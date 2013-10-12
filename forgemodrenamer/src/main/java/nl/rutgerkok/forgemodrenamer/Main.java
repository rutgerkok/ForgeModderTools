package nl.rutgerkok.forgemodrenamer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JarProvider;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.io.Files;

@Mojo(name = "forgemodrenamer")
public class Main extends AbstractMojo {

    /**
     * The Maven project, used to get information about the output file
     * location.
     */
    @Component
    private MavenProject project;

    @Parameter(defaultValue = "")
    private String mcpDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            execute0();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Moves the file, then remaps it and places it back at the origninal
     * location.
     * 
     * @throws IOException
     *             If something goes wrong reading or writing the things.
     */
    private void execute0() throws IOException {
        File outputFile = project.getArtifact().getFile();
        RemapProperties fallbacks = new RemapProperties();

        // Move to temporary file
        File tempFile = File.createTempFile("forgemodrenamer", ".jar");
        if (!outputFile.exists() || !outputFile.isFile()) {
            throw new IOException("No artifact to rename. Expecting " + tempFile.getAbsolutePath());
        }

        // Dependencies (for inheritance map)
        List<File> dependencies = new ArrayList<File>();
        dependencies.add(outputFile);
        for (Artifact dependency : project.getDependencyArtifacts()) {
            dependencies.add(dependency.getFile());
        }

        // Mappings
        JarMapping mapping = getSeargeToNumericMapping(fallbacks);
        Jar inJar = Jar.init(outputFile);
        mapping.setFallbackInheritanceProvider(new JarProvider(Jar.init(dependencies)));

        // Do the remap
        JarRemapper remapper = new JarRemapper(null, mapping);
        remapper.setGenerateAPI(false);
        remapper.remapJar(inJar, tempFile);

        // Let tempFile replace the unmapped file
        Files.copy(tempFile, outputFile);
        tempFile.delete();
        this.getLog().info("Remapped artifact " + outputFile.getAbsolutePath());
    }

    /**
     * Loads the mappings (Searge -> numeric) from an URL.
     * 
     * @return The mappings.
     * @throws IOException
     *             If the mappings cannot be loaded.
     */
    public JarMapping getSeargeToNumericMapping(RemapProperties fallbacks) throws IOException {
        JarMapping mapping = new JarMapping();
        mapping.loadMappings(print(fallbacks.withFallback(mcpDirectory, "mcpDirectory")), false, true, null, null);
        return mapping;
    }

    private String print(String string) {
        getLog().info("JarMapping: " + string);
        return string;
    }

}

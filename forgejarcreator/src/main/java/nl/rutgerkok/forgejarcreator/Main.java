package nl.rutgerkok.forgejarcreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import net.md_5.specialsource.util.FileLocator;
import nl.rutgerkok.forgejarcreator.patch.ForgePatcher;
import nl.rutgerkok.forgejarcreator.patch.MinecraftJar;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.io.Files;

@Mojo(name = "forgejarcreator")
public class Main extends AbstractMojo {
    /**
     * The Maven project, used to get information about the output file
     * location.
     */
    @Component
    private MavenProject project;

    /**
     * Online (or offline) location of the MCP mappings.
     */
    @Parameter(defaultValue = "${mcpDirectory}")
    private String mcpDirectory;

    /**
     * URL of the Minecraft client for the current {@link #minecrafVersion}.
     */
    @Parameter(defaultValue = "${minecraftClientUrl}")
    private String minecraftClientUrl;

    /**
     * URL of the Minecraft server for the current {@link #minecraftVersion}.
     */
    @Parameter(defaultValue = "${minecraftServerUrl}")
    private String minecraftServerUrl;

    /**
     * URL of the Forge universal mod for the current {@link #minecraftVersion}
     * and {@link #forgeVersion}.
     */
    @Parameter(defaultValue = "${forgeUrl}")
    private String forgeUrl;

    /**
     * Name of the binary patches file, found in the Forge zip.
     */
    @Parameter(defaultValue = "binpatches.pack.lzma")
    private String patchesFileInForge;

    /**
     * The side to compile for. Please note the the client also includes the
     * server files, but that the server doesn't include the client files.
     */
    @Parameter(defaultValue = "CLIENT")
    private String side;

    /**
     * The Forge Mod Loader access transformer file/URL to use.
     */
    @Parameter(defaultValue = "${fmlAccessTransformer}")
    private String fmlAccessTransformer;

    /**
     * The Forge access transformer file/URL to use.
     */
    @Parameter(defaultValue = "${forgeAccessTransformer}")
    private String forgeAccessTransformer;

    /**
     * Downloads the jars, patches them with the binary patches by Forge,
     * removes all method implementations and renames the classes, fields and
     * methods. Result: a Forge jar for people to build against.
     */
    public void execute() throws MojoExecutionException {
        try {
            execute0();
        } catch (IOException e) {
            // Convert exception
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void execute0() throws IOException {
        Side side = this.getSide();

        // Download files
        File mojangDownloadedJar = getMojangFile(side);
        File forgeJar = FileLocator.getFile(forgeUrl);

        // Move to other file
        File mojangTemporaryJarFile = new File("mojangtemp.jar");
        Files.copy(mojangDownloadedJar, mojangTemporaryJarFile);

        // Load mappings
        MCPDeobfuscator mappings = new MCPDeobfuscator(mcpDirectory, new String[] { fmlAccessTransformer, forgeAccessTransformer });

        // Add missing methods (apply patches extracted from Forge jar)
        MinecraftJar clientRenamedJar = new MinecraftJar(mappings, mojangTemporaryJarFile);
        ZipFile forgeZip = new ZipFile(forgeJar);
        ForgePatcher forgePatcher = new ForgePatcher();
        InputStream patchesStream = forgeZip.getInputStream(forgeZip.getEntry(patchesFileInForge));
        forgePatcher.patchJarFile(clientRenamedJar, patchesStream, side);
        patchesStream.close();
        clientRenamedJar.writeToFile(mojangTemporaryJarFile);
        getLog().info("Applied patches by Forge!");

        // Rename fields, methods and classes in file
        File outputJar = project.getArtifact().getFile();
        mappings.deobfuscate(forgeJar, mojangTemporaryJarFile, outputJar);
        System.err.println("But for us this is normal, as both the Forge and Minecraft jars have a Main class.");
        mojangTemporaryJarFile.deleteOnExit();
        getLog().info("Renamed!");
    }

    /**
     * Gets the side, based on the {@link #side} property.
     * 
     * @return The side.
     * @throws IOException
     *             If the side cannot be read.
     */
    private Side getSide() throws IOException {
        for (Side side : Side.values()) {
            if (side.toString().equalsIgnoreCase(this.side)) {
                return side;
            }
        }
        throw new IOException(this.side + " is not a valid side!");
    }

    /**
     * Downloads the appropriate file from Mojang's servers.
     * 
     * @param side
     *            The side to download.
     * @return The download file.
     * @throws IOException
     *             If the file cannot be downloaded.
     * @throws IllegalArgumentException
     *             If the side is not {@link Side#CLIENT} or {@link Side#SERVER}
     *             . This is impossible as long as the enum isn't extendee.
     */
    private File getMojangFile(Side side) throws IOException {
        switch (side) {
            case CLIENT:
                return FileLocator.getFile(minecraftClientUrl);
            case SERVER:
                return FileLocator.getFile(minecraftServerUrl);
        }
        throw new IllegalArgumentException("Unknown side " + side);
    }

}

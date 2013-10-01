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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.common.io.Files;

@Mojo(name = "forgejarcreator")
public class Main extends AbstractMojo {

    /**
     * Online (or offline) location of the MCP mappings.
     */
    @Parameter(property = "forgejarcreator.mcpDirectory")
    private String mcpDirectory;

    /**
     * URL of the Minecraft client for the current {@link #minecrafVersion}.
     */
    @Parameter(property = "forgejarcreator.minecraftClientUrl")
    private String minecraftClientUrl;

    /**
     * URL of the Minecraft server for the current {@link #minecraftVersion}.
     */
    @Parameter(property = "forgejarcreator.minecraftServerUrl")
    private String minecraftServerUrl;

    /**
     * URL of the Forge universal mod for the current {@link #minecraftVersion}
     * and {@link #forgeVersion}.
     */
    @Parameter(property = "forgejarcreator.forgeUrl")
    private String forgeUrl;

    /**
     * Name of the binary patches file, found in the Forge zip.
     */
    @Parameter(property = "forgejarcreator.patchesFileInForge", defaultValue = "binpatches.pack.lzma")
    private String patchesFileInForge;

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
        // Download
        File clientDownloadedJar = FileLocator.getFile(minecraftClientUrl);
        // File serverJar = FileLocator.getFile(minecraftServerUrl);
        File forgeJar = FileLocator.getFile(forgeUrl);
        System.out.println("Downloaded!");

        // Move to other file
        File clientTemporaryJarFile = new File("clienttemp.jar");
        Files.copy(clientDownloadedJar, clientTemporaryJarFile);

        // Load mappings
        MCPDeobfuscator mappings = new MCPDeobfuscator(mcpDirectory);

        // Add missing methods (apply patches extracted from Forge jar)
        MinecraftJar clientRenamedJar = new MinecraftJar(mappings, clientTemporaryJarFile);
        ZipFile forgeZip = new ZipFile(forgeJar);
        ForgePatcher forgePatcher = new ForgePatcher();
        InputStream patchesStream = forgeZip.getInputStream(forgeZip.getEntry(patchesFileInForge));
        forgePatcher.patchJarFile(clientRenamedJar, patchesStream, Side.CLIENT);
        patchesStream.close();
        clientRenamedJar.writeToFile(clientTemporaryJarFile);
        System.out.println("Applied patches by Forge!");

        // Rename fields, methods and classes in file
        mappings.deobfuscate(forgeJar, clientTemporaryJarFile, new File("client.jar"));
        System.err.println("But for us this is normal, as both the Forge and Minecraft jars have a Main class.");
        clientTemporaryJarFile.deleteOnExit();
        System.out.println("Renamed!");
    }

}

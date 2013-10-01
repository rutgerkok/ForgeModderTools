package nl.rutgerkok.forgejarcreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import net.md_5.specialsource.util.FileLocator;
import nl.rutgerkok.forgejarcreator.patch.ForgePatcher;
import nl.rutgerkok.forgejarcreator.patch.MinecraftJar;

import com.google.common.io.Files;

@Mojo(name = "forgejarcreator")
public class Main extends AbstractMojo {

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
        File clientDownloadedJar = FileLocator.getFile(Constants.MINECRAFT_CLIENT_URL);
        // File serverJar = FileLocator.getFile(Constants.MINECRAFT_SERVER_URL);
        File forgeJar = FileLocator.getFile(Constants.FORGE_URL);
        System.out.println("Downloaded!");

        // Move to other file
        File clientTemporaryJarFile = new File("clienttemp.jar");
        Files.copy(clientDownloadedJar, clientTemporaryJarFile);

        // Load mappings
        MCPDeobfuscator mappings = new MCPDeobfuscator();

        // Add missing methods (apply patches extracted from Forge jar)
        MinecraftJar clientRenamedJar = new MinecraftJar(mappings, clientTemporaryJarFile);
        ZipFile forgeZip = new ZipFile(forgeJar);
        ForgePatcher forgePatcher = new ForgePatcher();
        InputStream patchesStream = forgeZip.getInputStream(forgeZip.getEntry(Constants.PATCHES_FILE_IN_FORGE));
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

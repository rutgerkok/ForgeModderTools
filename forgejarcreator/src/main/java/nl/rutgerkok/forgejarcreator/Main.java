package nl.rutgerkok.forgejarcreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import net.md_5.specialsource.util.FileLocator;
import nl.rutgerkok.forgejarcreator.patch.ForgePatcher;
import nl.rutgerkok.forgejarcreator.patch.MinecraftJar;

import com.google.common.io.Files;

public class Main {

    /**
     * Single-threaded commandline application to create a file for Forge mods
     * to build against.
     * <p>
     * First, the needed files are downloaded, then they are renamed, then the
     * missing methods added by Forge are added and finally all method
     * implementations are removed, making the outputted files safer to redistribute.
     * 
     * @param args
     *            Ignored.
     * @throws IOException
     *             If something goes wrong. (Usually network problems.)
     */
    public static void main(String[] args) throws IOException {       
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

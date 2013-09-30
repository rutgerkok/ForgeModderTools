package nl.rutgerkok.forgejarcreator.patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import nl.rutgerkok.forgejarcreator.MCPDeobfuscator;
import nl.rutgerkok.forgejarcreator.com.nothome.delta.GDiffPatcher;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

/**
 * Stores all classes of a minecraft(_server).jar in memory, to be able to read
 * and patch them.
 * 
 */
public class MinecraftJar {
    private final Map<String, byte[]> data;
    private final GDiffPatcher patcher;
    private final Map<String, String> mappings;

    /**
     * Stores the contents of a minecraft(_server).jar in memory.
     * 
     * @param file
     *            The minecraft(_server).jar to store.
     * @throws IOException
     *             When the file is corrupted or not readable.
     */
    public MinecraftJar(MCPDeobfuscator obfuscator, File file) throws IOException {
        this.mappings = MapReverse.execute(obfuscator.getObfuscatedToSeargeMapping().classes);
        data = new HashMap<String, byte[]>();
        patcher = new GDiffPatcher();

        // Read all entries in the zip file
        ZipInputStream input = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;

        while ((entry = input.getNextEntry()) != null) {
            // Read an entry and put it in memory
            data.put(entry.getName(), ByteStreams.toByteArray(input));
            input.closeEntry();
        }

        // Don't forget to close the zip file
        input.close();
    }

    public void patch(ClassPatch patch) throws IOException {
        // Make name correct for usage by Minecraft
        String patchName = patch.targetClassName;
        patchName = patchName.replace('.', '/');
        
        // Try to get obfuscated name (not all classes are obfuscated)
        String patchNameObfuscated = mappings.get(patchName);
        if (patchNameObfuscated != null) {
            patchName = patchNameObfuscated;
        } else {
            System.err.println("Missing mapping for " + patch.targetClassName + ", cannot patch.");
        }
        
        // Append .class to get file name
        patchName = patchName + ".class";

        // Apply patch
        byte[] source = data.get(patchName);
        if (source == null) {
            System.out.println("Cannot apply patch for " + patchName + ", nothing to patch.");
            return;
        }
        byte[] patched = testAndPatch(source, patch);
        data.put(patchName, patched);
    }

    private byte[] testAndPatch(byte[] inputData, ClassPatch patch) {
        if (!patch.existsAtTarget && (inputData == null || inputData.length == 0)) {
            inputData = new byte[0];
        } else if (!patch.existsAtTarget) {
            System.err.println(String.format("Patcher expecting empty class data file for %s, but received non-empty", patch.targetClassName));
        } else {
            int inputChecksum = Hashing.adler32().hashBytes(inputData).asInt();
            if (patch.inputChecksum != inputChecksum) {
                String error = "There is a binary discrepency between the expected input class %s and the actual class."
                        + "Checksum on disk is %x, in patch %x. Things are probably about to go very wrong.";
                System.err.println(String.format(error, patch.name, inputChecksum, patch.inputChecksum));
                return inputData;
            }
        }
        try {
            inputData = patcher.patch(inputData, patch.patch);
        } catch (IOException e) {
            System.err.println(String.format("Encountered problem runtime patching class %s", patch.name));

        }
        return inputData;
    }

    /**
     * Writes the contents of this jar file to disk.
     * 
     * @param file
     *            The file to write.
     * @throws IOException
     *             If an IO error occurs.
     */
    public void writeToFile(File file) throws IOException {
        // Make sure file exists
        file.getAbsoluteFile().getParentFile().mkdirs();
        file.createNewFile();

        // Open file
        ZipOutputStream toFile = new ZipOutputStream(new FileOutputStream(file));
        toFile.setMethod(ZipOutputStream.DEFLATED);
        toFile.setLevel(6);

        // Write each entry
        for (Entry<String, byte[]> entry : data.entrySet()) {
            ZipEntry zipEntry = new ZipEntry(entry.getKey());
            toFile.putNextEntry(zipEntry);
            toFile.write(entry.getValue());
            toFile.closeEntry();
        }

        // Done!
        toFile.close();
    }
}

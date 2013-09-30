package nl.rutgerkok.forgejarcreator.patch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import nl.rutgerkok.forgejarcreator.Side;
import LZMA.LzmaInputStream;

/**
 * Forge adds a lot of methods to Minecraft's base classes. They need to be
 * added too.
 * 
 */
public class ForgePatcher {
    private static final String CLIENT_PATCH_PREFIX = "binpatch/client/";
    private static final String SERVER_PATCH_PREFIX = "binpatch/server/";
    
    public ForgePatcher() {
    }

    /**
     * Patches the minecraft(_server).jar with the given binary patch stream.
     * The stream must be compressed with LZMA compression.
     * 
     * @param toPatch
     *            The file to patch.
     * @param binpatchesCompressed
     *            The compressed patch stream.
     * @throws IOException
     *             If an IO error occures.
     */
    public void patchJarFile(MinecraftJar toPatch, InputStream binpatchesCompressed, Side side) throws IOException {
        // Wrap in LZMA input stream
        LzmaInputStream patchesZip = new LzmaInputStream(binpatchesCompressed);

        // Open a stream to write a jar file full of patches to a byte array
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        JarOutputStream jarOutputStream = new JarOutputStream(byteOutputStream);

        // Write the patches to the virtual jar file
        Pack200.newUnpacker().unpack(patchesZip, jarOutputStream);

        // Close the streams
        patchesZip.close();
        jarOutputStream.close();

        // Read the virtual patch jar file, apply patches to the actual file
        JarInputStream jarStream = new JarInputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()));
        combineJarFiles(jarStream, toPatch, side);
        jarStream.close();
    }

    /**
     * Applies all patches from the stream to the Jar file.
     * 
     * @param patchStream
     *            The uncompressed stream to read patches from.
     * @param fileToPatch
     *            The file that should be patched.
     * @throws IOException
     *             If an IO error occurs.
     */
    private void combineJarFiles(JarInputStream patchStream, MinecraftJar fileToPatch, Side side) throws IOException {
        JarEntry replacingClass = patchStream.getNextJarEntry();
        while (replacingClass != null) {

            // Read each entry, ignoring directories
            if (!replacingClass.isDirectory()) {
                String patchFileName = replacingClass.getName();
                
                if(isPatchOnSide(patchFileName, side)) {
                    // Only patch when on the corret side
                    ClassPatch patch = readPatch(patchStream);
                    fileToPatch.patch(patch);
                }
            }

            // On to the next entry
            replacingClass = patchStream.getNextJarEntry();
        }
    }

    /**
     * Gets whether the patch is on the correct side. For example, if the patch is made for serves it returns false when the side parameter is set to {@link Side#CLIENT}.
     * @param patchFileName Name of the patch file.
     * @param side The side the patch should be on.
     * @return True if the patch is actually on the correct side, false otherwise.
     */
    private boolean isPatchOnSide(String patchFileName, Side side) {
        if(side == Side.CLIENT) {
            return patchFileName.startsWith(CLIENT_PATCH_PREFIX);
        } else {
            return patchFileName.startsWith(SERVER_PATCH_PREFIX);
        }
    }

    /**
     * Reads a patch. Method is a copy of the one found in Forge ModLoader.
     * 
     * @param jis
     *            The stream to read the patch from.
     * @return The patch.
     * @throws IOException
     *             If an IO error occurs.
     */
    private ClassPatch readPatch(JarInputStream jis) throws IOException {
        DataInputStream input;
        input = new DataInputStream(jis);

        // Basic metadata
        String name = input.readUTF();
        String sourceClassName = input.readUTF();
        String targetClassName = input.readUTF();

        // Checksum
        boolean exists = input.readBoolean();
        int inputChecksum = 0;
        if (exists) {
            inputChecksum = input.readInt();
        }

        // Patch bytes
        int patchLength = input.readInt();
        byte[] patchBytes = new byte[patchLength];
        input.readFully(patchBytes);

        return new ClassPatch(name, sourceClassName, targetClassName, exists, inputChecksum, patchBytes);
    }
}

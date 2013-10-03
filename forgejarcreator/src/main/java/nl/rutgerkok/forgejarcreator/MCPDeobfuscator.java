package nl.rutgerkok.forgejarcreator;

import java.io.File;
import java.io.IOException;

import net.md_5.specialsource.AccessMap;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.RemapperPreprocessor;
import net.md_5.specialsource.provider.JarProvider;

import com.google.common.collect.Lists;

/**
 * Deobfucates all classes, fields and methods in Minecraft's client or server
 * JAR file using the mappings created by the MCP team.
 * 
 */
public class MCPDeobfuscator {
    private final String url;
    private String[] accessTransformers;
    private JarMapping obfuscatedToSeargeMapping;
    
    /**
     * Creates a new deobfuscator.
     * 
     * @param url
     *            The url of the directory to load the mappings from.
     * @param accessTransformers
     *            The access transformers.
     */
    public MCPDeobfuscator(String url, String[] accessTransformers) {
        this.url = url;
        this.accessTransformers = accessTransformers;
    }

    /**
     * Converts the file from the obfuscated mappings to Searge mappings.
     * 
     * @param forgeJar
     *            The jar file of Forge, also needed.
     * @param from
     *            Input file.
     * @param to
     *            Output file.
     * @throws IOException
     *             If something goes wrong.
     */
    public void deobfuscate(File forgeJar, File from, File to) throws IOException {
        // Remap
        try {
            Jar inJar = Jar.init(Lists.newArrayList(forgeJar, from));

            // Mappings
            JarMapping mapping = getObfuscatedToSeargeMapping();
            mapping.setFallbackInheritanceProvider(new JarProvider(inJar));

            // Access transformers
            RemapperPreprocessor preprocessor = null;
            if (accessTransformers != null) {
                AccessMap accessMap = new AccessMap();

                for (String filename : accessTransformers) {
                    if (filename != null && filename.length() != 0) {
                        accessMap.loadAccessTransformer(filename);
                    }
                }

                preprocessor = new RemapperPreprocessor(null, null, accessMap);
            }

            // Do the remap
            JarRemapper remapper = new JarRemapper(preprocessor, mapping);
            remapper.setGenerateAPI(true);
            remapper.remapJar(inJar, to);
        } catch (Exception e) {
            throw new RuntimeException("Error creating remapped jar at " + to + ": " + e.getMessage(), e);
        }
    }

    /**
     * Gets the (possibly cached) mappings for obfuscated to Searge conversion.
     * 
     * @return The mappings.
     * @throws IOException
     *             If the mappings cannot be read.
     */
    public JarMapping getObfuscatedToSeargeMapping() throws IOException {
        if (obfuscatedToSeargeMapping == null) {
            // Not loaded yet, load
            obfuscatedToSeargeMapping = loadMappings(false, false);
        }
        return obfuscatedToSeargeMapping;
    }

    /**
     * Loads the mappings.
     * 
     * @param reverse
     *            True to reverse the mappings.
     * @param numeric
     *            True to get searge->numeric, false to get obfuscated->searge.
     * @return The mappings.
     * @throws IOException
     *             If the mappings could not be read.
     */
    private JarMapping loadMappings(boolean reverse, boolean numeric) throws IOException {
        JarMapping mapping = new JarMapping();
        mapping.loadMappings(url, reverse, numeric, null, null);
        return mapping;
    }
}

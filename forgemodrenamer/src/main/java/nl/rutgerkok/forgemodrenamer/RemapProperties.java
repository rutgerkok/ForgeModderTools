package nl.rutgerkok.forgemodrenamer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RemapProperties {
    private final Properties properties;

    /**
     * loads the properties from the default location, the remap.properties file
     * included in the jar file.
     * 
     * @throws IOException
     *             If the remap.properties file is missing or invalid.
     */
    public RemapProperties() throws IOException {
        this(RemapProperties.class.getResourceAsStream("/remap.properties"));
    }

    /**
     * Loads the properties from the stream.
     * 
     * @param stream
     *            The stream to read from.
     * @throws IOException
     *             If the stream is invalid.
     */
    public RemapProperties(InputStream stream) throws IOException {
        properties = new Properties();
        properties.load(stream);
    }

    /**
     * Returns the value given as parameter. However, if the given value is null
     * or empty, the fallback is read and returned from the settings file. If
     * there is no fallback, null is returned.
     * 
     * @param value
     *            The value to return if not null or empty.
     * @param fallbackKey
     *            The key to lookup in this map if the value is null or empty.
     * @return The given value, or the value belonging to the fallbackKey.
     */
    public String withFallback(String value, String fallbackKey) {
        if (value == null || value.isEmpty()) {
            return properties.getProperty(fallbackKey);
        }
        return value;
    }
}

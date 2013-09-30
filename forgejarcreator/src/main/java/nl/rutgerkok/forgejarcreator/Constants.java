package nl.rutgerkok.forgejarcreator;

/**
 * This class contains all constants used to deobfuscate the jars.
 * 
 * When updating to the latest Minecraft or Forge version, update the
 * {@link #FORGE_VERSION}, the {@link #MINECRAFT_VERSION} and the
 * {@link #MCP_DIRECTORY}. Other constants only need to be updated when the URL
 * scheme of Mojang or Forge changes.
 * 
 */
public class Constants {
    /**
     * Version of Forge, Major.Minor.Revision.Build.
     */
    public static final String FORGE_VERSION = "9.11.0.884";
    /**
     * Version of Minecraft, Major.Minor.Revision.
     */
    public static final String MINECRAFT_VERSION = "1.6.4";
    /**
     * Online (or offline) location of the MCP mappings.
     */
    public static final String MCP_DIRECTORY = "https://raw.github.com/MinecraftForge/FML/25981706ef12654b6c2baccc80fa2298bb5afb4a/conf/";

    /**
     * URL of the Minecraft client for the current {@link #MINECRAFT_VERSION}.
     */
    public static final String MINECRAFT_CLIENT_URL = "http://s3.amazonaws.com/Minecraft.Download/versions/" + MINECRAFT_VERSION + "/" + MINECRAFT_VERSION + ".jar";
    /**
     * URL of the Minecraft server for the current {@link #MINECRAFT_VERSION}.
     */
    public static final String MINECRAFT_SERVER_URL = "http://s3.amazonaws.com/Minecraft.Download/versions/" + MINECRAFT_VERSION + "/minecraft_server." + MINECRAFT_VERSION + ".jar";
    /**
     * URL of the Forge universal mod for the current {@link #MINECRAFT_VERSION} and {@link #FORGE_VERSION}.
     */
    public static final String FORGE_URL = "http://files.minecraftforge.net/minecraftforge/minecraftforge-universal-" + MINECRAFT_VERSION + "-" + FORGE_VERSION + ".jar";
    /**
     * Name of the binary patches file, found in the Forge zip.
     */
    public static final String PATCHES_FILE_IN_FORGE = "binpatches.pack.lzma";
    
}

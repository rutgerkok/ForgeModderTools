## ForgeModderTools

This repo will contain some tools to assist people compiling mods for Minecraft Forge.

### ForgeJarCreator
This little program downloads Minecraft, Forge and the MCP mappings and outputs a .jar file.
The jar file only contains empty methods, so it should be safe to redistribute.

Forge mods need to add this to their build path, either manually or using Maven. (For now Maven doesn't work yet.)

ForgeJarCreator contains a modified version of [Javaxdelta](http://javaxdelta.sourceforge.net/).

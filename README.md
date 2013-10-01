## ForgeModderTools

This repo will contain some tools to assist people compiling mods for Minecraft Forge.

*This project is in no way affliated with Forge or the Forge team.*

### ForgeJars
ForgeJars contains one file: the `pom.xml` file. In this file, all settings are present to generate
the Forge jar using ForgeJarCreator. The resulting jar file contains only empty methods, so it
should be safe to build against. The file won't run.

### ForgeJarCreator
This program does the actual work. It downloads Minecraft, Forge and the MCP mappings and outputs
a .jar file. It needs to be run as a Maven plugin.

ForgeJarCreator relies on [SpecialSource](https://github.com/md-5/SpecialSource), 
[Minecraft Forge](http://www.minecraftforge.net/) and the [MCP Mappings](http://mcp.ocean-labs.de/).

ForgeJarCreator contains a modified version of [Javaxdelta](http://javaxdelta.sourceforge.net/).

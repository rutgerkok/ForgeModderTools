# ForgeModderTools

## Introduction

First of all, *this project is in no way affliated with Forge or the Forge team.* Thanks.

This project is aimed at modders using Minecraft Forge who want to use Maven to build their mod.

### Background
Most people use MCP to build their mod. MCP does a wonderful job: using its mappings, it translates the obfuscated Minecraft jar into readable source code. However, if you just want to build a mod that works with Minecraft Forge, you can
simplify the building process. We'll still use the mappings of MCP, but not the scripts.

To build mods for Forge, only two things are needed:

1. An deobfuscated Minecraft jar with all fields, methods and classes added by Forge present.
2. Something that translates your compiled mod so that it works again with the obfuscated fields,
   methods and classes in Minecraft.

As you might have guessed by now, this repo provides both.

### Credits
The amount of code you will find in this repo is rather limited. ForgeModderTools heavily relies on other projects.

[SpecialSource](https://github.com/md-5/SpecialSource) is used internally to both deobfuscate the
minecraft.jar and reobfuscate your mod. It does this using the [MCP](http://mcp.ocean-labs.de/) mappings.

It also uses a modified version of [Javaxdelta](http://javaxdelta.sourceforge.net/). Javaxdelta is used to
apply the binary patches of Minecraft Forge to the minecraft.jar, so that methods and fields added by Forge to Minecrafts
classes become available.

## Usage
Add the following repository to the `pom.xml`:

```xml
	<repositories>
		<repository>
			<id>rutger-repo</id>
			<url>http://www.rutgerkok.nl/repo</url>
		</repository>
	</repositories>
```

This repository is needed for ForgeJars. You'll also need to add this plugin repo:

```xml
	<pluginRepositories>
		<pluginRepository>
			<id>rutger-repo</id>
			<url>http://www.rutgerkok.nl/repo</url>
		</pluginRepository>
	</pluginRepositories>
```

This repo is needed for the Maven plugin ForgeModRenamer. Maven uses separate repositories for dependencies and plugins, so you'll need to add both.

Now you'll need to add ForgeJars as a dependency:

```xml
	<dependencies>
		<dependency>
			<groupId>nl.rutgerkok</groupId>
			<artifactId>forgejars</artifactId>
			<version>VERSION</version>
		</dependency>
	</dependencies>
```

This will be the jar you'll build against. Make sure to replace the `VERSION` with a valid Forge version. See [here](pom.xml#L7) for the latest version. You'll also need to add the ForgeModRenamer plugin:

```xml
	<build>
		<plugins>
			<plugin>
				<groupId>nl.rutgerkok</groupId>
				<artifactId>forgemodrenamer-maven-plugin</artifactId>
				<version>VERSION</version>
				<executions>
					<execution>
						<id>remap-mod</id>
						<phase>package</phase>
						<goals>
							<goal>forgemodrenamer</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

This Maven plugin remaps your mod so that it works with the official minecraft.jar. Again, replace the `VERSION` with a valid Forge version.

This should be everything you need. See the `pom.xml` of [ExampleMod](examplemod/pom.xml) for a complete example pom.

## Contents of each directory
If you're interested in contributing to ForgeModderTools, this will give you some information.

### ForgeJars
ForgeJars contains one file: the `pom.xml` file. In this file, all settings are present to generate
an emtpy Forge jar using ForgeJarCreator: all method headers, classes and fields are present, but the
methods lack any kind of implementation. This means that you can build against ForgeJars, but you can't
run such an empty Forge jar. Place your mod in the `mods` folder in the `.minecraft` directory to run it.

### ForgeJarCreator
This program does the actual work. It is executed by ForgeJars, but you can easily fork ForgeJars to get
a customized file. It downloads Minecraft, Forge and the MCP mappings and outputs a .jar file. It 
needs to be run as a Maven plugin during the `package` phase. The correct goal is `forgejarcreator`.

It has the following parameters:

* `side` (Optional, default is `CLIENT`) Whether you want to get the `CLIENT` or `SERVER` jar decompiled.
  Please note that the client jar includes all server classes, methods and fields, but that the server jar
  doesn't have the client stuff.
* `mcpDirectory` The directory to load the MCP mappings from. May be an internet address.
* `minecraftClientUrl` The url to download the Minecraft client from.
* `minecraftServerUrl` The url to download the Minecraft server from.
* `forgeUrl` The url to download the Minecraft Forge universal jar from.
* `patchesFileInForge` (Optional) The name of the binary patches file, found in the forge universal jar.
* `fmlAccessTransformer` Url to the AccessTransformer file of Forge ModLoader.
* `forgeAccessTransformer` Url to the AccessTransformer file of Forge.

Parameters can be supplied the usual way:

```xml
<plugin>
  <groupId>nl.rutgerkok</groupId>
  <artifactId>forgejarcreator-maven-plugin</artifactId>
  ...
  <configuration>
    <parameterName>parameterValue</parameterName>
  </configuration>
</plugin>
```

All parameters that *aren't optional* can also be supplied in the `<properties` tags:

```xml
<properties>
	<parameterName>parameterValue</parameterName>
</properties>
```

### ForgeModRenamer
The tool that renames your mod. This is a Maven plugin too. Run the goal `forgemodrenamer` during
the `package` phase. ForgeModRenamer has just one parameter:

* `mcpDirectory` (Optional) The directory to load the MCP mappings from. May be an internet address.
  When omitted, the default address is used for the current version of ForgeModRenamer, which should be fine.

### ExampleMod
Tiny example mod with just one class. You can use its [pom.xml](examplemod/pom.xml) as an example for your mod.

ForgeModderTools isn't needed anymore, as Forge now has a [Gradle plugin](http://www.minecraftforge.net/forum/index.php?board=118.0). The Gradle plugin does much more than ForgeModderTools ever did: it allows you to run mods from your IDE, as well as letting you view the deobfuscated Minecraft source code in your IDE.

If you really need to use Maven, try [this Gradle --> Maven bridge](https://github.com/if6was9/gradle-maven-plugin).

Although attempts were made to update ForgeModderTools to Minecraft 1.7, those weren't really successful: ForgeModderTools has problems with many inner classes in Minecraft. As ForgeGradle would be a much better alternative, even when ForgeModderTools didn't have these bugs, I'm not going to do any more work on ForgeModderTools.

[Original README.md](https://github.com/rutgerkok/ForgeModderTools/blob/b6771281cf5a613e82a7d66e22ec6155a1f28389/README.md)

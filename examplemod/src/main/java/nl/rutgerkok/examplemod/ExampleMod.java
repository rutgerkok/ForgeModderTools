package nl.rutgerkok.examplemod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "ExampleMod", name = "ExampleMod", version = "1.0")
public class ExampleMod {

    @Instance("ExampleMod")
    public static ExampleMod instance;

    @EventHandler
    public void load(FMLInitializationEvent event) {
        // This is the place where the mod starts loading
        System.out.println("Hello world! Example mod here!");

        // Just test some things that should get remapped
        System.out.println("Name of ice is " + Blocks.ice.func_149739_a());
        TestBlock testBlock = new TestBlock();
        GameRegistry.registerBlock(testBlock, "testBlock");

        // Calling inherited methods should work too
        testBlock.quantityDropped(0, 0, new Random());
    }

    public static class TestBlock extends Block {

        public TestBlock() {
            super(Material.field_151576_e);
        }

        @Override
        public int quantityDropped(int i, int j, Random random) {
            return 2;
        }

    }

}

package nl.rutgerkok.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
        System.out.println("Slipperiness of ice is " + Block.ice.slipperiness);

        GameRegistry.registerBlock(new TestBlock(), "testBlock");

        // Previous line will be remapped to something like this
        // System.out.println("Slipperiness of ice is " + Block.field_72036_aT.field_72016_cq);
    }

    public static class TestBlock extends Block {

        public TestBlock() {
            super(3000, Material.rock);
            this.getTickRandomly();
        }

        @Override
        public boolean getTickRandomly() {
            // Methods can be overriden - they will be renamed automatically
            return false;
        }

    }

}

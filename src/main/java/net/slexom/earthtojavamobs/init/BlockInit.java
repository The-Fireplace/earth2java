package net.slexom.earthtojavamobs.init;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.slexom.earthtojavamobs.EarthtojavamobsMod;
import net.slexom.earthtojavamobs.block.CarvedMelonBlock;
import net.slexom.earthtojavamobs.block.MudBlock;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, EarthtojavamobsMod.MOD_ID);


//    public static final RegistryObject<RainbowCarpetBlock> RAINBOW_CARPET = BLOCKS.register(
//            "rainbow_carpet",
//            () ->   new RainbowCarpetBlock(DyeColor.WHITE, Block.Properties.create(Material.CARPET, MaterialColor.SNOW).hardnessAndResistance(0.1F).sound(SoundType.CLOTH))
//    );
//
//    public static final RegistryObject<RainbowBedBlock> RAINBOW_BED = BLOCKS.register(
//            "rainbow_bed",
//            () -> new RainbowBedBlock(Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid())
//    );
//
//    public static final RegistryObject<Block> RAINBOW_WOOL = BLOCKS.register(
//            "rainbow_wool",
//            () -> new Block(Block.Properties.create(Material.WOOL, MaterialColor.SNOW).hardnessAndResistance(0.8F).sound(SoundType.CLOTH))
//    );

    public static final RegistryObject<Block> CARVED_MELON = BLOCKS.register(
            "carved_melon",
            () -> new CarvedMelonBlock(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD))
    );
    public static final RegistryObject<Block> MELON_LANTERN = BLOCKS.register(
            "melon_lantern",
            () -> new CarvedMelonBlock(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD).lightValue(15))
    );
    public static final RegistryObject<Block> MELON_GOLEM_HEAD_BLINK = BLOCKS.register(
            "melon_golem_blink",
            () -> new CarvedMelonBlock(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD))
    );
    public static final RegistryObject<Block> MELON_GOLEM_HEAD_SHOOT = BLOCKS.register(
            "melon_golem_shoot",
            () -> new CarvedMelonBlock(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD))
    );

    public static final RegistryObject<FlowingFluidBlock> MUD_BLOCK = BLOCKS.register("mud_fluid",
            () -> new MudBlock(
                    FluidInit.MUD_FLUID_STILL,
                    Block.Properties.create(Material.WATER, MaterialColor.BROWN)
                            .doesNotBlockMovement()
                            .hardnessAndResistance(100.0F)
                            .speedFactor(0.75F)
                            .noDrops()
            )
    );

}

package cliffordha.totvw.registry.blocks;

import cliffordha.totvw.block.*;
import cliffordha.totvw.block.custom.StorageBlock;
import cliffordha.totvw.block.custom.VerdantSpruceLeavesBlock;
import cliffordha.totvw.world.tree.VWTreeGrowers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import static cliffordha.totvw.registry.VWBlocks.Util.*;

public class VWBlocksVerdant extends Blocks {
    public VWBlocksVerdant() {}

    public static final BlockSetType VERDANT_SPRUCE_SET = registerBlockSetType("verdant_spruce", BlockSetType.SPRUCE);
    public static final WoodType VERDANT_SPRUCE_WOOD_TYPE = registerWoodType("verdant_spruce", WoodType.SPRUCE, VERDANT_SPRUCE_SET);

    public static final Block VERDANT_SPRUCE_LEAVES = registerBlock("verdant_spruce_leaves",
            properties -> new VerdantSpruceLeavesBlock( 0.00f, ParticleTypes.ASH, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES),
            true
    );
    public static final Block VERDANT_SPRUCE_SAPLING = registerBlock("verdant_spruce_sapling",
            properties -> new VWSaplingBlock(VWTreeGrowers.VERDANT, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING),
            true
    );
    public static final Block POTTED_VERDANT_SPRUCE_SAPLING = registerBlock( "potted_verdant_spruce_sapling",
            properties -> new FlowerPotBlock(VERDANT_SPRUCE_SAPLING, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_SPRUCE_SAPLING),
            false
    );
    public final static Block VERDANT_MOSS_BLOCK = registerBlock("verdant_moss_block",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK),
            true
    );
    public final static Block VERDANT_SPRUCE_PLANKS = registerBlock("verdant_spruce_planks",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_SLAB = registerBlock("verdant_spruce_slab",
            SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_STAIRS = registerBlock("verdant_spruce_stairs",
            properties -> new StairBlock(VERDANT_SPRUCE_PLANKS.defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_STAIRS),
            true
    );
    public final static Block VERDANT_SPRUCE_BUTTON = registerBlock("verdant_spruce_button",
            properties -> new ButtonBlock(VERDANT_SPRUCE_SET, 10, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_BUTTON),
            true
    );
    public final static Block VERDANT_SPRUCE_PRESSURE_PLATE = registerBlock("verdant_spruce_pressure_plate",
            properties -> new PressurePlateBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PRESSURE_PLATE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE = registerBlock("verdant_spruce_fence",
            FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE_GATE = registerBlock("verdant_spruce_fence_gate",
            properties -> new FenceGateBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE_GATE),
            true
    );
    public final static Block VERDANT_SPRUCE_TRAPDOOR = registerBlock("verdant_spruce_trapdoor",
            properties -> new TrapDoorBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_TRAPDOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_DOOR = registerBlock("verdant_spruce_door",
            properties -> new DoorBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_DOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_LOG = registerBlock("verdant_spruce_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG),
            true
    );
    public final static Block VERDANT_SPRUCE_WOOD = registerBlock("verdant_spruce_wood",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WOOD),
            true
    );
    public final static Block STRIPPED_VERDANT_SPRUCE_LOG = registerBlock("stripped_verdant_spruce_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_LOG),
            true
    );
    public final static Block STRIPPED_VERDANT_SPRUCE_WOOD = registerBlock("stripped_verdant_spruce_wood",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_WOOD),
            true
    );
    public static final Block VERDANT_SPRUCE_SIGN = registerBlock("verdant_spruce_sign",
            properties -> new VWStandingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN),
            false
    );
    public static final Block VERDANT_SPRUCE_HANGING_SIGN = registerBlock("verdant_spruce_hanging_sign",
            properties -> new VWCeilingHangingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN),
            false
    );
    public static final Block VERDANT_SPRUCE_WALL_SIGN = registerBlock("verdant_spruce_wall_sign",
            properties -> new VWWallSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_SIGN)
                    .overrideDescription(VERDANT_SPRUCE_SIGN.getDescriptionId())
                    .overrideLootTable(VERDANT_SPRUCE_SIGN.getLootTable()),
            false
    );
    public static final Block VERDANT_SPRUCE_WALL_HANGING_SIGN = registerBlock("verdant_spruce_wall_hanging_sign",
            properties -> new VWWallHangingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN)
                    .overrideDescription(VERDANT_SPRUCE_HANGING_SIGN.getDescriptionId())
                    .overrideLootTable(VERDANT_SPRUCE_HANGING_SIGN.getLootTable()),
            false
    );
    public static final Block VERDANT_SPRUCE_SHELF = registerBlock("verdant_spruce_shelf",
            VWShelfBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SHELF),
            true
    );
    public static final Block VERDANT_SPRUCE_STORAGE_BOX = registerBlock("verdant_spruce_storage_box",
            StorageBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL),
            true
    );

    public static final BlockFamily VERDANT_SPRUCE_FAMILY = BlockFamilies.familyBuilder(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS)
            .stairs(VERDANT_SPRUCE_STAIRS)
            .slab(VERDANT_SPRUCE_SLAB)
            .fence(VERDANT_SPRUCE_FENCE)
            .fenceGate(VERDANT_SPRUCE_FENCE_GATE)
            .button(VERDANT_SPRUCE_BUTTON)
            .pressurePlate(VERDANT_SPRUCE_PRESSURE_PLATE)
            .sign(VERDANT_SPRUCE_SIGN, VERDANT_SPRUCE_WALL_SIGN)
            .door(VERDANT_SPRUCE_DOOR)
            .trapdoor(VERDANT_SPRUCE_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static void registerVerdantBlocks() {}
}

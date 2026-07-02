package cliffordha.totvw.registry.blocks;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.block.*;
import cliffordha.totvw.block.custom.VerdantSpruceLeavesBlock;
import cliffordha.totvw.world.tree.ModTreeGrowers;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VerdantBlocks extends Blocks {
    public VerdantBlocks() {}

    public static final BlockSetType VERDANT_SPRUCE_SET = BlockSetTypeBuilder.copyOf(BlockSetType.SPRUCE)
            .register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce"));
    
    public static final WoodType VERDANT_SPRUCE_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.SPRUCE)
            .register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce"), VERDANT_SPRUCE_SET);

    public static final Block VERDANT_SPRUCE_LEAVES = TOTVW.registerBlock("verdant_spruce_leaves",
            properties -> new VerdantSpruceLeavesBlock( 0.00f, ParticleTypes.ASH, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES),
            true
    );
    public static final Block VERDANT_SPRUCE_SAPLING = TOTVW.registerBlock("verdant_spruce_sapling",
            properties -> new ModSaplingBlock(ModTreeGrowers.VERDANT, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING),
            true
    );
    public static final Block POTTED_VERDANT_SPRUCE_SAPLING = TOTVW.registerBlock( "potted_verdant_spruce_sapling",
            properties -> new FlowerPotBlock(VERDANT_SPRUCE_SAPLING, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_SPRUCE_SAPLING),
            false
    );
    public final static Block VERDANT_MOSS_BLOCK = TOTVW.registerBlock("verdant_moss_block",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK),
            true
    );
    public final static Block VERDANT_SPRUCE_PLANKS = TOTVW.registerBlock("verdant_spruce_planks",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_SLAB = TOTVW.registerBlock("verdant_spruce_slab",
            SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_STAIRS = TOTVW.registerBlock("verdant_spruce_stairs",
            properties -> new StairBlock(VERDANT_SPRUCE_PLANKS.defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_STAIRS),
            true
    );
    public final static Block VERDANT_SPRUCE_BUTTON = TOTVW.registerBlock("verdant_spruce_button",
            properties -> new ButtonBlock(VERDANT_SPRUCE_SET, 10, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_BUTTON),
            true
    );
    public final static Block VERDANT_SPRUCE_PRESSURE_PLATE = TOTVW.registerBlock("verdant_spruce_pressure_plate",
            properties -> new PressurePlateBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PRESSURE_PLATE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE = TOTVW.registerBlock("verdant_spruce_fence",
            FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE_GATE = TOTVW.registerBlock("verdant_spruce_fence_gate",
            properties -> new FenceGateBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE_GATE),
            true
    );
    public final static Block VERDANT_SPRUCE_TRAPDOOR = TOTVW.registerBlock("verdant_spruce_trapdoor",
            properties -> new TrapDoorBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_TRAPDOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_DOOR = TOTVW.registerBlock("verdant_spruce_door",
            properties -> new DoorBlock(VERDANT_SPRUCE_SET, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_DOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_LOG = TOTVW.registerBlock("verdant_spruce_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG),
            true
    );
    public final static Block VERDANT_SPRUCE_WOOD = TOTVW.registerBlock("verdant_spruce_wood",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WOOD),
            true
    );
    public final static Block STRIPPED_VERDANT_SPRUCE_LOG = TOTVW.registerBlock("stripped_verdant_spruce_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_LOG),
            true
    );
    public final static Block STRIPPED_VERDANT_SPRUCE_WOOD = TOTVW.registerBlock("stripped_verdant_spruce_wood",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_WOOD),
            true
    );
    public static final Block VERDANT_SPRUCE_SIGN = TOTVW.registerBlock("verdant_spruce_sign",
            properties -> new ModStandingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN),
            false
    );
    public static final Block VERDANT_SPRUCE_HANGING_SIGN = TOTVW.registerBlock("verdant_spruce_hanging_sign",
            properties -> new ModCeilingHangingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN),
            false
    );
    public static final Block VERDANT_SPRUCE_WALL_SIGN = TOTVW.registerBlock("verdant_spruce_wall_sign",
            properties -> new ModWallSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_SIGN)
                    .overrideDescription(VERDANT_SPRUCE_SIGN.getDescriptionId())
                    .overrideLootTable(VERDANT_SPRUCE_SIGN.getLootTable()),
            false
    );
    public static final Block VERDANT_SPRUCE_WALL_HANGING_SIGN = TOTVW.registerBlock("verdant_spruce_wall_hanging_sign",
            properties -> new ModWallHangingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN)
                    .overrideDescription(VERDANT_SPRUCE_HANGING_SIGN.getDescriptionId())
                    .overrideLootTable(VERDANT_SPRUCE_HANGING_SIGN.getLootTable()),
            false
    );
    public static final Block VERDANT_SPRUCE_SHELF = TOTVW.registerBlock("verdant_spruce_shelf",
            ModShelfBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SHELF),
            true
    );
    public static final Block VERDANT_SPRUCE_STORAGE_BOX = TOTVW.registerBlock("verdant_spruce_storage_box",
            ModStorageBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL),
            true
    );

    public static final BlockFamily VERDANT_SPRUCE_FAMILY = BlockFamilies.familyBuilder(VerdantBlocks.VERDANT_SPRUCE_PLANKS)
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

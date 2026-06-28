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
import net.minecraft.world.level.material.PushReaction;

public class VerdantBlocks extends Blocks {
    public VerdantBlocks() {}


    public static final BlockSetType VERDANT_SPRUCE_SET = BlockSetTypeBuilder.copyOf(BlockSetType.SPRUCE)
            .register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce"));
    
    public static final WoodType VERDANT_SPRUCE_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.SPRUCE)
            .register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce"), VERDANT_SPRUCE_SET);

    public static final float wood = 2.0f;

    public static final Block VERDANT_SPRUCE_LEAVES = TOTVW.registerBlock("verdant_spruce_leaves",
            properties -> new VerdantSpruceLeavesBlock( 0.00f, ParticleTypes.ASH, properties
                    .strength(0.2F, 0.5F)
                    .sound(SoundType.GRASS)
                    .requiresCorrectToolForDrops()
                    .noOcclusion().isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating(Blocks::never)
                    .isViewBlocking(Blocks::never)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(Blocks::never)),
            BlockBehaviour.Properties.of(),
            true);

    public static final Block VERDANT_SPRUCE_SAPLING = TOTVW.registerBlock("verdant_spruce_sapling",
            properties -> new ModSaplingBlock(ModTreeGrowers.VERDANT, properties
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .ignitedByLava()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING),
            true);

    public static final Block POTTED_VERDANT_SPRUCE_SAPLING = TOTVW.registerBlock( "potted_verdant_spruce_sapling",
            properties -> new FlowerPotBlock(VERDANT_SPRUCE_SAPLING, properties
                    .noOcclusion()
                    .strength(0.75F, 0.5F)
                    .pushReaction(PushReaction.DESTROY)),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_SPRUCE_SAPLING),
            false);

    public final static Block VERDANT_MOSS_BLOCK = TOTVW.registerBlock("verdant_moss_block",
            properties -> new Block( properties
                    .sound(SoundType.MOSS)
                    .ignitedByLava()
                    .strength(0.1F)
                    .isValidSpawn((_, _, _, entityType) -> entityType.getCategory().isFriendly())
                    .explosionResistance(5F) ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK),
            true);

    public final static Block VERDANT_SPRUCE_PLANKS = TOTVW.registerBlock("verdant_spruce_planks",
            properties -> new Block(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_SLAB = TOTVW.registerBlock("verdant_spruce_slab",
            properties -> new SlabBlock(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_STAIRS = TOTVW.registerBlock("verdant_spruce_stairs",
            properties -> new StairBlock(VERDANT_SPRUCE_PLANKS.defaultBlockState(), properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_BUTTON = TOTVW.registerBlock("verdant_spruce_button",
            properties -> new ButtonBlock(VERDANT_SPRUCE_SET, 10, properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_PRESSURE_PLATE = TOTVW.registerBlock("verdant_spruce_pressure_plate",
            properties -> new PressurePlateBlock(VERDANT_SPRUCE_SET, properties
                    .sound(SoundType.WOOD)
                    .strength(0.5f)
                    .noCollision()
                    .pushReaction(PushReaction.DESTROY)),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_FENCE = TOTVW.registerBlock("verdant_spruce_fence",
            properties -> new FenceBlock(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_FENCE_GATE = TOTVW.registerBlock("verdant_spruce_fence_gate",
            properties -> new FenceGateBlock(VERDANT_SPRUCE_WOOD_TYPE, properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_TRAPDOOR = TOTVW.registerBlock("verdant_spruce_trapdoor",
            properties -> new TrapDoorBlock(VERDANT_SPRUCE_SET, properties
                    .sound(SoundType.WOOD)
                    .strength(3.0f)
                    .noOcclusion()
                    .ignitedByLava()
                    .isValidSpawn(Blocks::never)
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_DOOR = TOTVW.registerBlock("verdant_spruce_door",
            properties -> new DoorBlock(VERDANT_SPRUCE_SET, properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_LOG = TOTVW.registerBlock("verdant_spruce_log",
            properties -> new RotatedPillarBlock(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block VERDANT_SPRUCE_WOOD = TOTVW.registerBlock("verdant_spruce_wood",
            properties -> new RotatedPillarBlock(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true);

    public final static Block STRIPPED_VERDANT_SPRUCE_LOG = TOTVW.registerBlock("stripped_verdant_spruce_log",
            properties -> new RotatedPillarBlock(properties
                    .sound(SoundType.WOOD)
                    .strength(wood)
                    .ignitedByLava()
            ),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block STRIPPED_VERDANT_SPRUCE_WOOD = TOTVW.registerBlock("stripped_verdant_spruce_wood",
            properties -> new RotatedPillarBlock(properties
                    //.sound(SoundType.WOOD)
                    //.strength(wood)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_WOOD),
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
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SHELF)
                    .sound(SoundType.WOOD)
                    .strength(wood),
            true
    );
    public static final Block VERDANT_STORAGE_BOX = TOTVW.registerBlock("verdant_storage_box",
            ModStorageBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .sound(SoundType.WOOD)
                    .strength(wood),
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

    public static void registerModBlocks() {}
}

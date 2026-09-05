package cliffordha.totvw.registry;

import cliffordha.totvw.block.*;
import cliffordha.totvw.block.custom.*;
import cliffordha.totvw.TOTVW;
import cliffordha.totvw.world.tree.VWTreeGrowers;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Consumer;
import java.util.function.Function;

import static cliffordha.totvw.registry.VWBlocks.Util.*;

public class VWBlocks {

    public static final BlockSetType VERDANT_SPRUCE_SET = registerBlockSetType("verdant_spruce", BlockSetType.SPRUCE);
    public static final WoodType VERDANT_SPRUCE_WOOD_TYPE = registerWoodType("verdant_spruce", WoodType.SPRUCE, VERDANT_SPRUCE_SET);

    public final static Block VERIXIUM_DEEPSLATE_ORE = registerBlock("verixium_deepslate_ore",
            properties -> new VerixiumOreBlock(properties
                    .sound(SoundType.DEEPSLATE)
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.5F, 60F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_STONE_ORE = registerBlock("verixium_stone_ore",
            properties -> new VerixiumOreBlock(properties
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.0F, 30F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_POWDER_BLOCK = registerBlock("verixium_powder_block",
            properties -> new VerixiumPowderBlock(new ColorRGBA(VWColors.VERDANT_WIND), properties
                    .sound(SoundType.SAND)
                    .mapColor(MapColor.DIAMOND)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(_ -> 15)
                    .pushReaction(PushReaction.DESTROY)
                    .strength(1.5F, 90F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public static final Block VERIXIUM_FLUID = registerBlock("verixium_fluid",
            (properties) -> new LiquidBlock(VWFluids.VERIXIUM_FLUID, properties
                    .mapColor(DyeColor.CYAN)
                    .lightLevel(_ -> 14)
                    .noLootTable()
                    .liquid()
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.WATER),
            false
    );
    public static final Block VERDANT_SPRUCE_LEAVES = registerBlock("verdant_spruce_leaves",
            properties -> new VerdantSpruceLeavesBlock( 0.00f, ParticleTypes.ASH, properties
                    .mapColor(MapColor.WARPED_WART_BLOCK)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES),
            true
    );
    public static final Block VERDANT_SPRUCE_SAPLING = registerBlock("verdant_spruce_sapling",
            properties -> new VWSaplingBlock(VWTreeGrowers.VERDANT, properties
                    .mapColor(MapColor.WARPED_WART_BLOCK)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING),
            true
    );
    public static final Block POTTED_VERDANT_SPRUCE_SAPLING = registerBlock( "potted_verdant_spruce_sapling",
            properties -> new FlowerPotBlock(VERDANT_SPRUCE_SAPLING, properties
                    .mapColor(MapColor.WARPED_WART_BLOCK)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_SPRUCE_SAPLING),
            false
    );
    public final static Block VERDANT_MOSS_BLOCK = registerBlock("verdant_moss_block",
            properties -> new GrassBlock(properties
                    .mapColor(MapColor.WARPED_WART_BLOCK)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK),
            true
    );
    public final static Block VERDANT_SPRUCE_PLANKS = registerBlock("verdant_spruce_planks",
            properties -> new Block(properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_SLAB = registerBlock("verdant_spruce_slab",
            properties -> new SlabBlock(properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(VERDANT_SPRUCE_PLANKS),
            true
    );
    public final static Block VERDANT_SPRUCE_STAIRS = registerBlock("verdant_spruce_stairs",
            properties -> new StairBlock(VERDANT_SPRUCE_PLANKS.defaultBlockState(), properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_STAIRS),
            true
    );
    public final static Block VERDANT_SPRUCE_BUTTON = registerBlock("verdant_spruce_button",
            properties -> new ButtonBlock(VERDANT_SPRUCE_SET, 10, properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_BUTTON),
            true
    );
    public final static Block VERDANT_SPRUCE_PRESSURE_PLATE = registerBlock("verdant_spruce_pressure_plate",
            properties -> new PressurePlateBlock(VERDANT_SPRUCE_SET, properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PRESSURE_PLATE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE = registerBlock("verdant_spruce_fence",
            properties -> new FenceBlock(properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE),
            true
    );
    public final static Block VERDANT_SPRUCE_FENCE_GATE = registerBlock("verdant_spruce_fence_gate",
            properties -> new FenceGateBlock(VERDANT_SPRUCE_WOOD_TYPE, properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE_GATE),
            true
    );
    public final static Block VERDANT_SPRUCE_TRAPDOOR = registerBlock("verdant_spruce_trapdoor",
            properties -> new TrapDoorBlock(VERDANT_SPRUCE_SET, properties
                    .mapColor(MapColor.WARPED_NYLIUM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_TRAPDOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_DOOR = registerBlock("verdant_spruce_door",
            properties -> new DoorBlock(VERDANT_SPRUCE_SET, properties
                    .mapColor(MapColor.WARPED_NYLIUM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_DOOR),
            true
    );
    public final static Block VERDANT_SPRUCE_LOG = registerBlock("verdant_spruce_log",
            properties -> new RotatedPillarBlock(properties
                    .mapColor(MapColor.WARPED_NYLIUM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG),
            true
    );
    public final static Block VERDANT_SPRUCE_WOOD = registerBlock("verdant_spruce_wood",
            properties -> new RotatedPillarBlock(properties
                    .mapColor(MapColor.WARPED_NYLIUM)
            ),
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
            properties -> new VWStandingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN),
            false
    );
    public static final Block VERDANT_SPRUCE_HANGING_SIGN = registerBlock("verdant_spruce_hanging_sign",
            properties -> new VWCeilingHangingSignBlock(VERDANT_SPRUCE_WOOD_TYPE, properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
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
            properties -> new VWShelfBlock(properties
                    .mapColor(MapColor.WARPED_STEM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SHELF),
            true
    );
    public static final Block VERDANT_SPRUCE_STORAGE_BOX = registerBlock("verdant_spruce_storage_box",
            properties -> new StorageBlock(properties
                    .mapColor(MapColor.WARPED_NYLIUM)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL),
            true
    );

    public final static Block IRIDESCENT_GLASS = registerBlock("iridescent_glass",
            properties -> new TransparentBlock(properties
                    .lightLevel(_ -> 9)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS),
            true
    );
    public final static Block IRIDESCENT_GLASS_PANE = registerBlock("iridescent_glass_pane",
            properties -> new StainedGlassPaneBlock(DyeColor.CYAN, properties
                    .lightLevel(_ -> 9)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE),
            true
    );
    public final static Block LODESTONE_WIND_CORE = registerBlock("lodestone_wind_core",
            properties -> new LodestoneWindCoreBlock(properties
                    .strength(50.0f, 100.0f)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)
                    .pushReaction(PushReaction.IGNORE)
                    .lightLevel((state) -> state.getValue(LodestoneWindCoreBlock.ACTIVE) ? 15 : 0)
            ),
            BlockBehaviour.Properties.of(),
            true
    );


    public static final BlockFamily VERDANT_SPRUCE_FAMILY = BlockFamilies.familyBuilder(VERDANT_SPRUCE_PLANKS)
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


    public static void register() {
        TOTVW.sendClassRegisterLog("Blocks");
    }
    
    public static class Util {
        public static BlockSetType registerBlockSetType(String name, BlockSetType blockSetType) {
            return BlockSetTypeBuilder.copyOf(blockSetType).register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
        }
        public static WoodType registerWoodType(String name, WoodType woodType, BlockSetType blockSetType) {
            return WoodTypeBuilder.copyOf(woodType).register(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), blockSetType);
        }

        public static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean registerBlock, Component... tooltips) {
            ResourceKey<Block> blockKey = keyOfBlock(name);
            Block block = blockFactory.apply(settings.setId(blockKey));
            if (registerBlock) {
                ResourceKey<Item> itemKey = keyOfItem(name);
                BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()){
                    @Override
                    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                        for (var component : tooltips) {
                            builder.accept(component);
                        }
                        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
                    }
                };
                Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);}
            return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);}

        private static ResourceKey<Block> keyOfBlock(String name) {
            return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
        }
        private static ResourceKey<Item> keyOfItem(String name) {
            return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
        }
    }
}
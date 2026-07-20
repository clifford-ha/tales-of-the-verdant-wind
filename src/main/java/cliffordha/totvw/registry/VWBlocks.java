package cliffordha.totvw.registry;

import cliffordha.totvw.block.custom.LodestoneWindCore;
import cliffordha.totvw.block.custom.VerixiumPowderBlock;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import cliffordha.totvw.TOTVW;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Consumer;
import java.util.function.Function;

import static cliffordha.totvw.registry.VWBlocks.Util.registerBlock;

public class VWBlocks {
    public final static Block VERIXIUM_DEEPSLATE_ORE = registerBlock("verixium_deepslate_ore",
            properties -> new Block( properties
                    .sound(SoundType.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.5F, 60F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_STONE_ORE = registerBlock("verixium_stone_ore",
            properties -> new Block( properties
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.0F, 30F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_POWDER_BLOCK = registerBlock("verixium_powder_block",
            properties -> new VerixiumPowderBlock(new ColorRGBA(VWColors.VERDANT_WIND), properties
                    .sound(SoundType.SAND)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(_ -> 15)
                    .pushReaction(PushReaction.DESTROY)
                    .strength(1.5F, 90F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public static final Block VERIXIUM_FLUID = registerBlock("verixium_fluid",
            (props) -> new LiquidBlock(VWFluids.VERIXIUM_FLUID, props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                    .mapColor(DyeColor.CYAN)
                    .lightLevel(_ -> 14)
                    .noLootTable()
                    .liquid(),
            false
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
            properties -> new LodestoneWindCore(properties
                    .strength(10.0f, 50.0f)
                    .sound(SoundType.STONE)
                    .lightLevel((state) -> state.getValue(LodestoneWindCore.ACTIVE) ? 15 : 0)
            ),
            BlockBehaviour.Properties.of(),
            true
    );


    // separate blocks of the same family when >= 10 items
    // my forgetful ahh will remember better that way
    public static void register() {
        VWBlocksVerdant.registerVerdantBlocks();
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
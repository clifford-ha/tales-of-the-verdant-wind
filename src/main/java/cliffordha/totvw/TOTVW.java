package cliffordha.totvw;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TOTVW {
    public static final String MOD_ID = "tales-of-the-verdant-wind";
    public static final String MOD_NAME = "Tales of the Verdant Wind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static void sendLog(String className) {
        TOTVW.LOGGER.info(MOD_NAME + " - {} registered!", className);
    }
    
    private static final int secTick = 20;
    private static final int minTick = secTick * 60;



    public static int setTime(int min,  int sec) {return ((min * minTick) + (sec * secTick));}
    public static int setSec(int sec) {return (sec * secTick);}
    public static int setMin(int sec) {return (sec * minTick);}
    public static boolean getGameTime(ServerLevel world, int min, int sec) {
        long gameTime = world.getGameTime();
        int finalTotal = ((min * minTick) + (sec * secTick));
        if (finalTotal <= 0) return false;
        return gameTime % finalTotal == 0;
    }
    
    public static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name))))); }

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
    
    private static ResourceKey<Block> keyOfBlock(String name) { return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
    private static ResourceKey<Item> keyOfItem(String name) { return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}

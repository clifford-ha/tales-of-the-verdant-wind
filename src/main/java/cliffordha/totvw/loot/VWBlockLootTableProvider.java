package cliffordha.totvw.loot;

import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.VWBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;

public class VWBlockLootTableProvider extends FabricBlockLootSubProvider {
    public VWBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(VWBlocks.VERIXIUM_STONE_ORE, createOreDrop(
                VWBlocks.VERIXIUM_STONE_ORE,
                VWItems.VERIXIUM_CHUNK
        ));
        add(VWBlocks.VERIXIUM_DEEPSLATE_ORE, createOreDrop(
                VWBlocks.VERIXIUM_DEEPSLATE_ORE,
                VWItems.VERIXIUM_CHUNK
        ));
        add(VWBlocks.VERIXIUM_POWDER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(VWItems.VERIXIUM_POWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 9)))
                                .when(doesNotHaveSilkTouch())
                        )
                        .add(LootItem.lootTableItem(VWBlocks.VERIXIUM_POWDER_BLOCK)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .when(hasSilkTouch())
                        )
                        .add(LootItem.lootTableItem(VWItems.VERIXIUM_POWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 9)))
                                .when(ExplosionCondition.survivesExplosion())
                        )
                )
        );
        dropSelf(VWBlocks.VERDANT_MOSS_BLOCK);
        add(VWBlocks.VERDANT_SPRUCE_LEAVES, createLeavesDrops(
                VWBlocks.VERDANT_SPRUCE_LEAVES,
                VWBlocks.VERDANT_SPRUCE_SAPLING,
                NORMAL_LEAVES_SAPLING_CHANCES
        ));

        dropSelf(VWBlocks.VERDANT_SPRUCE_SAPLING);
        dropPottedContents(VWBlocks.POTTED_VERDANT_SPRUCE_SAPLING);

        dropSelf(VWBlocks.VERDANT_SPRUCE_PLANKS);
        dropSelf(VWBlocks.VERDANT_SPRUCE_LOG);
        dropSelf(VWBlocks.VERDANT_SPRUCE_WOOD);
        dropSelf(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        dropSelf(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
        add(VWBlocks.VERDANT_SPRUCE_SLAB, createSlabItemTable(VWBlocks.VERDANT_SPRUCE_SLAB));
        dropSelf(VWBlocks.VERDANT_SPRUCE_STAIRS);
        dropSelf(VWBlocks.VERDANT_SPRUCE_FENCE);
        dropSelf(VWBlocks.VERDANT_SPRUCE_FENCE_GATE);
        dropSelf(VWBlocks.VERDANT_SPRUCE_BUTTON);
        dropSelf(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);
        add(VWBlocks.VERDANT_SPRUCE_DOOR, createDoorTable(VWBlocks.VERDANT_SPRUCE_DOOR));
        dropSelf(VWBlocks.VERDANT_SPRUCE_TRAPDOOR);
        dropSelf(VWBlocks.VERDANT_SPRUCE_SIGN);
        dropSelf(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN);
        dropSelf(VWBlocks.VERDANT_SPRUCE_SHELF);
        dropSelf(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX);

        dropSelf(VWBlocks.IRIDESCENT_GLASS);
        dropSelf(VWBlocks.IRIDESCENT_GLASS_PANE);
        dropSelf(VWBlocks.LODESTONE_WIND_CORE);
    }
}

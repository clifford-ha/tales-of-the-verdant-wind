package cliffordha.totvw.loot;

import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
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
        dropSelf(VWBlocksVerdant.VERDANT_MOSS_BLOCK);
        add(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES, createLeavesDrops(
                VWBlocksVerdant.VERDANT_SPRUCE_LEAVES,
                VWBlocksVerdant.VERDANT_SPRUCE_SAPLING,
                NORMAL_LEAVES_SAPLING_CHANCES
        ));

        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING);
        dropPottedContents(VWBlocksVerdant.POTTED_VERDANT_SPRUCE_SAPLING);

        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_LOG);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_WOOD);
        dropSelf(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);
        dropSelf(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD);
        add(VWBlocksVerdant.VERDANT_SPRUCE_SLAB, createSlabItemTable(VWBlocksVerdant.VERDANT_SPRUCE_SLAB));
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_FENCE);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE);
        add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR, createDoorTable(VWBlocksVerdant.VERDANT_SPRUCE_DOOR));
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_TRAPDOOR);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_SIGN);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_SHELF);
        dropSelf(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX);

        dropSelf(VWBlocks.IRIDESCENT_GLASS);
        dropSelf(VWBlocks.IRIDESCENT_GLASS_PANE);
    }
}

package cliffordha.totvw.loot;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.registry.ModBlocks;
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

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
    public ModBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(ModBlocks.VERIXIUM_STONE_ORE, createOreDrop(
                ModBlocks.VERIXIUM_STONE_ORE,
                ModItems.VERIXIUM_CHUNK
        ));
        add(ModBlocks.VERIXIUM_DEEPSLATE_ORE, createOreDrop(
                ModBlocks.VERIXIUM_DEEPSLATE_ORE,
                ModItems.VERIXIUM_CHUNK
        ));
        add(ModBlocks.VERIXIUM_POWDER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.VERIXIUM_POWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 9)))
                                .when(doesNotHaveSilkTouch())
                        )
                        .add(LootItem.lootTableItem(ModBlocks.VERIXIUM_POWDER_BLOCK)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .when(hasSilkTouch())
                        )
                        .add(LootItem.lootTableItem(ModItems.VERIXIUM_POWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 9)))
                                .when(ExplosionCondition.survivesExplosion())
                        )
                )
        );
        dropSelf(VerdantBlocks.VERDANT_MOSS_BLOCK);
        add(VerdantBlocks.VERDANT_SPRUCE_LEAVES, createLeavesDrops(
                VerdantBlocks.VERDANT_SPRUCE_LEAVES,
                VerdantBlocks.VERDANT_SPRUCE_SAPLING,
                NORMAL_LEAVES_SAPLING_CHANCES
        ));

        dropSelf(VerdantBlocks.VERDANT_SPRUCE_SAPLING);
        dropPottedContents(VerdantBlocks.POTTED_VERDANT_SPRUCE_SAPLING);

        dropSelf(VerdantBlocks.VERDANT_SPRUCE_PLANKS);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_LOG);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_WOOD);
        dropSelf(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        dropSelf(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
        add(VerdantBlocks.VERDANT_SPRUCE_SLAB, createSlabItemTable(VerdantBlocks.VERDANT_SPRUCE_SLAB));
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_STAIRS);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_FENCE);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_BUTTON);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);
        add(VerdantBlocks.VERDANT_SPRUCE_DOOR, createDoorTable(VerdantBlocks.VERDANT_SPRUCE_DOOR));
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_TRAPDOOR);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_SIGN);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_SHELF);
        dropSelf(VerdantBlocks.VERDANT_SPRUCE_STORAGE_BOX);

        dropSelf(ModBlocks.IRIDESCENT_GLASS);
        dropSelf(ModBlocks.IRIDESCENT_GLASS_PANE);
    }
}

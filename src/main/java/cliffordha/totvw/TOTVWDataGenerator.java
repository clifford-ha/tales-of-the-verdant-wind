package cliffordha.totvw;

import cliffordha.totvw.datagen.*;
import cliffordha.totvw.datagen.villager.VWVillagerTrades;
import cliffordha.totvw.loot.VWBlockLootTableProvider;
import cliffordha.totvw.loot.VWChestLootProvider;
import cliffordha.totvw.tag.*;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.world.VWBiomes;
import cliffordha.totvw.worldgen.VWConfiguredFeatures;
import cliffordha.totvw.worldgen.VWPlacedFeatures;

import cliffordha.totvw.worldgen.dimension.VWDimensions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class TOTVWDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(VWItemTags::new);
		pack.addProvider(VWBlockTags::new);
		pack.addProvider(VWBiomeTags::new);
		pack.addProvider(VWFluidTags::new);
		pack.addProvider(VWEnchantmentTags::new);
		pack.addProvider(VWDamageTypeTags::new);
		pack.addProvider(VWEntityTypeTags::new);
		pack.addProvider(VWVillagerTradeTags::new);

		pack.addProvider(VWModelProvider::new);
		pack.addProvider(VWSoundsProvider::new);
		pack.addProvider(VWEngLangProvider::new);
		pack.addProvider(VWRegistryProvider::new);
		pack.addProvider(VWBlockLootTableProvider::new);
		pack.addProvider(VWChestLootProvider::new);
		pack.addProvider(VWRecipeProvider::new);

		pack.addProvider(VWAdvancements::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.ENCHANTMENT, VWEnchantments::bootstrap);
		registryBuilder.add(Registries.DAMAGE_TYPE, VWDamageTypes::bootstrap);

		registryBuilder.add(Registries.VILLAGER_TRADE, VWVillagerTrades::bootstrap);

		registryBuilder.add(Registries.WOLF_VARIANT, VWWolfVariants::bootstrap);

		registryBuilder.add(Registries.BIOME, VWBiomes::bootstrap);
		registryBuilder.add(Registries.CONFIGURED_FEATURE, VWConfiguredFeatures::configure);
		registryBuilder.add(Registries.PLACED_FEATURE, VWPlacedFeatures::configure);

		registryBuilder.add(Registries.LEVEL_STEM, VWDimensions::bootstrapStem);
		registryBuilder.add(Registries.DIMENSION_TYPE, VWDimensions::bootstrapType);
	}
}
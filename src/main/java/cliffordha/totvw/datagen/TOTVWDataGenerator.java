package cliffordha.totvw.datagen;

import cliffordha.totvw.loot.ModBlockLootTableProvider;
import cliffordha.totvw.tag.*;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.world.ModBiomes;
import cliffordha.totvw.worldgen.ModConfiguredFeatures;
import cliffordha.totvw.worldgen.ModPlacedFeatures;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class TOTVWDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModItemTags::new);
		pack.addProvider(ModBlockTags::new);
		pack.addProvider(ModBiomeTags::new);
		pack.addProvider(ModFluidTags::new);
		pack.addProvider(ModEnchantmentTags::new);
		pack.addProvider(ModSoundsProvider::new);
		pack.addProvider(ModEngLangProvider::new);
		pack.addProvider(ModRegistryProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModDyeSynthesizerRecipeProvider::new);
		pack.addProvider(ModAdvancements::new);

	}
	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::configure);
		registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::configure);
		registryBuilder.add(Registries.BIOME, ModBiomes::bootstrap);
		registryBuilder.add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);
		registryBuilder.add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap);
	}
}
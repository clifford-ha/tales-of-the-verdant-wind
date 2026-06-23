package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.fluid.VerixiumFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FlowingFluid;

public class ModFluids {
    public static final FlowingFluid FLOWING_VERIXIUM_FLUID = initialize("flowing_verixium_fluid", new VerixiumFluid.Flowing());
    public static final FlowingFluid VERIXIUM_FLUID = initialize("verixium_fluid", new VerixiumFluid.Source());

    private static FlowingFluid initialize(String name, FlowingFluid fluid) {
        return Registry.register(BuiltInRegistries.FLUID, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), fluid);
    }

    public static void registerModFluids() {
        TOTVW.sendLog("Fluids");
    }
}
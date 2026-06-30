package cliffordha.totvw.mixin.villager;

import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.tag.ModBiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerSpawnDataMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void totvw$setData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        var villager = (Villager) (Object) this;
        boolean inVerdant = villager.level().getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        if (spawnReason != EntitySpawnReason.SPAWN_ITEM_USE) {
            if (inVerdant) {
                villager.setAttached(ModAttachments.Villager.IS_VERDANT_TYPE, true);
            } else {
                villager.setAttached(ModAttachments.Villager.IS_VERDANT_TYPE, false);
            }
        }
    }
}

package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.*;
import cliffordha.totvw.util.ModUtil;
import cliffordha.totvw.tag.ModBiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;

@Mixin(Villager.class)
public class VillagerEntityMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Villager villager = (Villager) (Object) this;
        boolean inVerdant = level.getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        if (!inVerdant) return;
        villager.setAttached(ModAttachments.Villager.IS_VERDANT_TYPE, true);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void attackIfPresent(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        boolean inVerdant = villager.level().getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);

        if (villager.level().getGameTime() % 100 == 0) {
            if (inVerdant) {
                List<Wolf> wolves = villager.level().getEntities(
                        EntityType.WOLF,
                        villager.getBoundingBox().inflate(12),
                        wolf -> wolf.isAlive()
                                && wolf.getHealth() <= wolf.getMaxHealth() * 0.75f
                                && wolf.getAttachedOrElse(ModAttachments.Wolf.HAS_TRIED_PROTECTING_VILLAGER, false));
                if (wolves.isEmpty()) return;
                for (Wolf wolf : wolves) {
                    wolf.heal(villager.getHealth() * 0.5f);
                    wolf.removeAttached(ModAttachments.Wolf.HAS_TRIED_PROTECTING_VILLAGER);
                    ModParticleEffects.spawnBlessingParticlesEntity(wolf, 2);
                    ModParticleEffects.spawnBlessingParticlesEntity(villager, 2);
                }
            }
        }
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void villagerVerdantTrades(Player player, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;

        boolean verdantVillager = villager.getAttachedOrElse(ModAttachments.Villager.IS_VERDANT_TYPE, false);
        boolean villagerInVerdant = villager.level().getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);

        if (!verdantVillager) return;

        int rerollCD = villager.getAttachedOrElse(ModAttachments.Villager.CD_DISCOUNT_REROLL, 0);
        float modifier = villager.getAttachedOrElse(ModAttachments.Villager.DISCOUNT_MODIFIER, 0.0f);
        float additional = villagerInVerdant ? 0.25f : 0.0f;

        float MIN_MODIFIER = 0.0f;
        float MAX_MODIFIER = 0.75f;
        int REROLL_INTERVAL = ModUtil.min(24);

        if (rerollCD <= 0 || modifier <= 0.0f) {
            modifier = MIN_MODIFIER + villager.level().getRandom().nextFloat() * ((MAX_MODIFIER + additional) - MIN_MODIFIER);
            villager.setAttached(ModAttachments.Villager.DISCOUNT_MODIFIER, modifier);
            villager.setAttached(ModAttachments.Villager.CD_DISCOUNT_REROLL, REROLL_INTERVAL);
        }

        for (MerchantOffer offer : villager.getOffers()) {
            int costReduction = (int) Math.floor(modifier * (double) offer.getBaseCostA().getCount());
            offer.addToSpecialPriceDiff(-Math.max(costReduction, 1));
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void villagerInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager villager = (Villager) (Object) this;
        String villagerName = villager.getName().getString();
        ItemStack itemStack = player.getItemInHand(hand);

        boolean isMeVerdantType = villager.getAttachedOrElse(ModAttachments.Villager.IS_VERDANT_TYPE, false);

        if (isMeVerdantType) {
            if (itemStack.is(Items.STICK)) {
                notifyFromPlayer(player, ModColors.DEFAULT, villagerName + " is a Verdant Type");
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else if (itemStack.is(ModItems.VERIXIUM_SWORD)) {
                villager.removeAttached(ModAttachments.Villager.IS_VERDANT_TYPE);
                notifyFromPlayer(player, ModColors.DEFAULT, villagerName + " removed Verdant status");
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else if (itemStack.is(ModItems.VERIXIUM_AXE)) {
                villager.setAttached(ModAttachments.Villager.CD_DISCOUNT_REROLL, 0);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else if (!isMeVerdantType && itemStack.is(ModItems.VERIXIUM_SWORD)) {
            villager.setAttached(ModAttachments.Villager.IS_VERDANT_TYPE, true);
            notifyFromPlayer(player, ModColors.DEFAULT, villagerName + " added Verdant status");
        }
    }
}
package cliffordha.totvw.mixin.villager;

import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.tag.ModBiomeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerVerdantTradesMixin {

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void totvw$VillagerVerdantTrades(Player player, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;

        boolean verdantVillager = villager.getAttachedOrElse(ModAttachments.Villager.IS_VERDANT_TYPE, false) == true;
        boolean villagerInVerdant = villager.level().getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);

        if (verdantVillager) {
            if (villagerInVerdant) {
                for(MerchantOffer offer : villager.getOffers()) {
                    double modifier = 0.3 + 0.0625 * 3;
                    int costReduction = (int)Math.floor(modifier * (double)offer.getBaseCostA().getCount());
                    offer.addToSpecialPriceDiff(-Math.max(costReduction, 1));
                }
            }
        }
    }
}

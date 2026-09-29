package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.registry.VWEnchantments;
import net.minecraft.world.entity.animal.wolf.Wolf;

//Yes, I know I am lazy...
public record WolfEnchants(
        boolean hasBenediction,
        boolean hasEnhancementKit,

        int getIgnition,
        int getPoisoning,
        int getWithering,
        int getLifting,
        int getBloodlust,
        int getMight,
        int getOozing,
        int getGnawing,

        //from vanilla
        int getProtection,
        int getFireProtection,
        int getProjectileProtection,
        int getBlastProtection,
        int getMending
) {
    static WolfEnchants of(Wolf wolf) {
        return new WolfEnchants(
                VWEnchantments.getBenediction(wolf),
                VWEnchantments.getEnhancementKit(wolf),

                VWEnchantments.getIgnition(wolf),
                VWEnchantments.getPoisoning(wolf),
                VWEnchantments.getWithering(wolf),
                VWEnchantments.getLifting(wolf),
                VWEnchantments.getBloodLust(wolf),
                VWEnchantments.getMight(wolf),
                VWEnchantments.getOozing(wolf),
                VWEnchantments.getGnawing(wolf),

                VWEnchantments.getProtection(wolf),
                VWEnchantments.getFireProtection(wolf),
                VWEnchantments.getProjectileProtection(wolf),
                VWEnchantments.getBlastProtection(wolf),
                VWEnchantments.getMending(wolf)
        );
    }

    boolean hasIgnition() {
        return getIgnition > 0;
    }
    boolean hasPoisoning() {
        return getPoisoning > 0;
    }
    boolean hasWithering() {
        return getWithering > 0;
    }
    boolean hasLifting() {
        return getLifting > 0;
    }
    boolean hasBloodLust() {
        return getBloodlust > 0;
    }
    boolean hasMight() {
        return getMight > 0;
    }
    boolean hasOozing() {
        return getOozing > 0;
    }
    boolean hasGnawing() {
        return getGnawing > 0;
    }
    boolean hasProtection() {
        return getProtection > 0;
    }
    boolean hasFireProtection() {
        return getFireProtection > 0;
    }
    boolean hasProjectileProtection() {
        return getProjectileProtection > 0;
    }
    boolean hasBlastProtection() {
        return getBlastProtection > 0;
    }
    boolean hasMending() {
        return getMending > 0;
    }
}

package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.registry.VWEnchantments;
import net.minecraft.world.entity.animal.wolf.Wolf;

//Yes, I know I'm lazy af :)
public record WolfEnchantments(
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
        int getArcticWinds,

        //from vanilla
        int getProtection,
        int getFireProtection,
        int getProjectileProtection,
        int getBlastProtection,
        int getMending
) {
    public static WolfEnchantments of(Wolf wolf) {
        return new WolfEnchantments(
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
                VWEnchantments.getArcticWinds(wolf),

                VWEnchantments.getProtection(wolf),
                VWEnchantments.getFireProtection(wolf),
                VWEnchantments.getProjectileProtection(wolf),
                VWEnchantments.getBlastProtection(wolf),
                VWEnchantments.getMending(wolf)
        );
    }

    public boolean hasIgnition() {
        return getIgnition > 0;
    }
    public boolean hasPoisoning() {
        return getPoisoning > 0;
    }
    public boolean hasWithering() {
        return getWithering > 0;
    }
    public boolean hasLifting() {
        return getLifting > 0;
    }
    public boolean hasBloodLust() {
        return getBloodlust > 0;
    }
    public boolean hasMight() {
        return getMight > 0;
    }
    public boolean hasOozing() {
        return getOozing > 0;
    }
    public boolean hasGnawing() {
        return getGnawing > 0;
    }
    public boolean hasArcticWinds() {
        return getArcticWinds > 0;
    }
    public boolean hasProtection() {
        return getProtection > 0;
    }
    public boolean hasFireProtection() {
        return getFireProtection > 0;
    }
    public boolean hasProjectileProtection() {
        return getProjectileProtection > 0;
    }
    public boolean hasBlastProtection() {
        return getBlastProtection > 0;
    }
    public boolean hasMending() {
        return getMending > 0;
    }
}

package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.TOTVW;

import cliffordha.totvw.config.TOTVWConfig;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

@FunctionalInterface
public interface WolfCondition {
    boolean test(Wolf wolf, ServerLevel level);

    default WolfCondition and(WolfCondition other) {return (wolf, level) -> this.test(wolf, level) && other.test(wolf, level);}
    default WolfCondition or(WolfCondition other) {return (wolf, level) -> this.test(wolf, level) || other.test(wolf, level);}
    default WolfCondition negate() {
        return (wolf, level) -> !this.test(wolf, level);
    }



    static WolfCondition alwaysTrue() {return (wolf, level) -> true;}

    static WolfCondition isTamed() {
        return (wolf, _) -> wolf.isTame();
    }

    static WolfCondition isWild() {
        return (wolf, _) -> !wolf.isTame();
    }

    static WolfCondition healthBelow(float fraction) { return (wolf, _) -> wolf.getHealth() <= wolf.getMaxHealth() * fraction; }

    static WolfCondition healthAbove(float fraction) { return (wolf, _) -> wolf.getHealth() >= wolf.getMaxHealth() * fraction; }

    static WolfCondition companionIsCritical(Supplier<Float> healthSupplier, Supplier<Integer> distanceSupplier) {
        return (wolf, _) -> {
            if (!wolf.isTame()) return false;
            var companion = wolf.getOwner();
            var distance = distanceSupplier.get();
            var health = healthSupplier.get();
            return companion != null && companion.getHealth() <= companion.getMaxHealth() * health && wolf.distanceTo(companion) <= distance * 16;
        };
    }

    static WolfCondition hasBodyArmor() { return (wolf, _) -> wolf.isWearingBodyArmor(); }

    static WolfCondition ownerInCreative() {
        return (wolf, _) -> {
            if (!wolf.isTame()) return false;
            var owner = wolf.getOwner();
            if (owner == null) {
                return false;
            } else {
            return owner.isInvulnerable(); }}; }

    static WolfCondition ownerNotInCreative() { return ownerInCreative().negate(); }

    static WolfCondition isAngry() { return (wolf, _) -> wolf.isAngry(); }

    static WolfCondition isSitting() { return (wolf, _) -> wolf.isOrderedToSit(); }

    static WolfCondition isInWater() { return (wolf, _) -> wolf.isInWater(); }

    static WolfCondition isLeashed() { return (wolf, _) -> wolf.isLeashed(); }

    static WolfCondition isUnderWater() { return (wolf, _) -> wolf.isUnderWater(); }

    static WolfCondition unableToTeleport() { return (wolf, _) -> wolf.unableToMoveToOwner(); }

    static WolfCondition airSupplyLowerThan(float fraction) { return (wolf, _) -> wolf.getAirSupply() <= wolf.getMaxAirSupply() * fraction; }

    static WolfCondition isPassenger() { return (wolf, _) -> wolf.isPassenger(); }

    static WolfCondition checkBiome(ResourceKey<Biome> biome) { return (wolf, _) -> wolf.level().getBiome(wolf.blockPosition()).is(biome) ;}

    static WolfCondition checkBiomeTag(TagKey<Biome> biomeTag) { return (wolf, _) -> wolf.level().getBiome(wolf.blockPosition()).is(biomeTag) ;}

    static WolfCondition tick(int min, int sec) {
        return (wolf, world) -> TOTVW.getGameTime(world, min, sec);
    }

    static WolfCondition randomChance(float value) {
        return (wolf, level) -> level.getRandom().nextFloat() == value;
    }

    static WolfCondition newSoundsEnable() {
        return (wolf, level) -> TOTVWConfig.get().newSounds;
    }


    static WolfCondition checkNoAttached(AttachmentType<Integer> type) { return (wolf, level) -> wolf.getAttachedOrElse(type, 0) == 0; }

    static WolfCondition ownerWithin(double radius) {
        return (wolf, _) -> {
            if (!wolf.isTame()) return false;
            var owner = wolf.getOwner();
            return owner != null && wolf.distanceTo(owner) <= radius;
        };
    }

    static WolfCondition ownerFarther(double radius) { return ownerWithin(radius).negate().and(isTamed()); }
}

package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.player.PlayerCondition;
import cliffordha.totvw.registry.VWAttachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

@FunctionalInterface
public interface WolfCondition {
    boolean test(Wolf wolf, ServerLevel level);

    default WolfCondition and(WolfCondition other) {return (wolf, level) -> this.test(wolf, level) && other.test(wolf, level);}
    default WolfCondition or(WolfCondition other) {return (wolf, level) -> this.test(wolf, level) || other.test(wolf, level);}
    default WolfCondition negate() {
        return (wolf, level) -> !this.test(wolf, level);
    }



    static WolfCondition alwaysTrue() {return (_, _) -> true;}

    static WolfCondition isTamed() {
        return (wolf, _) -> wolf.isTame();
    }

    static WolfCondition healthBelow(float fraction) {
        return (wolf, _) -> wolf.getHealth() <= wolf.getMaxHealth() * fraction;
    }

    static WolfCondition companionIsCritical(Supplier<Float> healthSupplier, Supplier<Integer> distanceSupplier) {
        return (wolf, _) -> {
            if (!wolf.isTame()) return false;
            var companion = wolf.getOwner();
            var distance = distanceSupplier.get();
            var health = healthSupplier.get();
            return companion != null && companion.getHealth() <= companion.getMaxHealth() * health && wolf.distanceTo(companion) <= distance * 16;
        };
    }

    static WolfCondition hasBodyArmor() {
        return (wolf, _) -> wolf.isWearingBodyArmor();
    }

    static WolfCondition hasArmorWithEnchantment(ResourceKey<Enchantment> enchantment) {
        return (wolf, level) -> {
            ItemStack bodyArmor = wolf.getBodyArmorItem();
            if (bodyArmor.isEmpty()) return false;
            Registry<Enchantment> enchantmentRegistry = wolf.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int playerHasEnchantment = bodyArmor.getEnchantments().getLevel( enchantmentRegistry.getOrThrow(enchantment));
            return playerHasEnchantment > 0;
        };
    }

    static WolfCondition checkConfig(boolean configValue) {
        return (_, _) -> configValue;
    }

    static WolfCondition isUnderWater() { return (wolf, _) -> wolf.isUnderWater(); }

    static WolfCondition unableToTeleport() { return (wolf, _) -> wolf.unableToMoveToOwner(); }

    static WolfCondition airSupplyLowerThan(float fraction) { return (wolf, _) -> wolf.getAirSupply() <= wolf.getMaxAirSupply() * fraction; }

    static WolfCondition tick(int min, int sec) {
        int finalTotal = Math.max(((min * (20 * 60)) + (sec * 20)), 0);
        return (_, level) -> level.getGameTime() % finalTotal == 0;
    }
    static WolfCondition tick() {
        return (_, world) -> world.getGameTime() % 20 == 0;
    }

    static WolfCondition halfTick() {
        return (_, world) -> world.getGameTime() % 10 == 0;
    }

    static WolfCondition quarterTick() {
        return (_, world) -> world.getGameTime() % 5 == 0;
    }

    static WolfCondition newSoundsEnable() {
        return (wolf, level) -> TOTVWConfig.get().CLIENT_MOD_SOUNDS;
    }

    static WolfCondition noAttachment(AttachmentType<Integer> type) { return (wolf, level) -> wolf.getAttachedOrElse(type, 0) == 0; }

    static WolfCondition ownerWithin(double radius) {
        return (wolf, _) -> {
            if (!wolf.isTame()) return false;
            var owner = wolf.getOwner();
            return owner != null && wolf.distanceTo(owner) <= radius;
        };
    }

    static WolfCondition ownerFarther(double radius) { return ownerWithin(radius).negate().and(isTamed()); }
}

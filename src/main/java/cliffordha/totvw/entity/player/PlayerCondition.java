package cliffordha.totvw.entity.player;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

public interface PlayerCondition {
    boolean test(Player player, ServerLevel level);

    default PlayerCondition and(PlayerCondition other) {return (player, level) -> this.test(player, level) && other.test(player, level);}
    default PlayerCondition or(PlayerCondition other) {return (player, level) -> this.test(player, level) || other.test(player, level);}
    default PlayerCondition negate() {
        return (player, level) -> !this.test(player, level);
    }


    static PlayerCondition hasBodyArmor() { return (player, _) -> player.hasItemInSlot(EquipmentSlot.CHEST) ;}

    static PlayerCondition checkBiomeTag(TagKey<Biome> biomeTag) { return (player, _) -> player.level().getBiome(player.blockPosition()).is(biomeTag) ;}

    static PlayerCondition hasArmorWithEnchantment(EquipmentSlot equipmentSlot, ResourceKey<Enchantment> enchantment) {
        return (player, level) -> {
            ItemStack bodyArmor = player.getItemBySlot(equipmentSlot);
            if (bodyArmor.isEmpty()) return false;
            Registry<Enchantment> enchantmentRegistry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int playerHasEnchantment = bodyArmor.getEnchantments().getLevel( enchantmentRegistry.getOrThrow(enchantment));
            return playerHasEnchantment > 0;
        };
    }

    static PlayerCondition checkNoAttached(AttachmentType<Integer> type) {
        return (wolf, level) -> wolf.getAttachedOrElse(type, 0) == 0;
    }

    static PlayerCondition tick(int min, int sec) {
        var finalTotal = Math.max(((min * (20 * 60)) + (sec * 20)), 0);
        return (_, level) -> level.getGameTime() % finalTotal == 0;
    }
    static PlayerCondition tick() {
        return (player, world) -> world.getGameTime() % 20 == 0;
    }
    static PlayerCondition halfTick() {
        return (_, world) -> world.getGameTime() % 10 == 0;
    }
    static PlayerCondition quarterTick() {
        return (_, world) -> world.getGameTime() % 5 == 0;
    }
}

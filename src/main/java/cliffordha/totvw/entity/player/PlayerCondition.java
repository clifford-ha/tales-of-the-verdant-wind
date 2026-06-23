package cliffordha.totvw.entity.player;

import cliffordha.totvw.TOTVW;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.wolf.WolfCondition;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
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




    static PlayerCondition alwaysTrue() {
        return (player, level) -> true;
    }

    static PlayerCondition healthBelow(float fraction) {
        return (player, _) -> player.getHealth() <= player.getMaxHealth() * fraction ;}

    static PlayerCondition healthAbove(float fraction) {
        return (player, _) -> player.getHealth() >= player.getMaxHealth() * fraction ;}

    static PlayerCondition hasBodyArmor() { return (player, _) -> player.hasItemInSlot(EquipmentSlot.CHEST) ;}

    static PlayerCondition checkBiome(ResourceKey<Biome> biome) { return (player, _) -> player.level().getBiome(player.blockPosition()).is(biome) ;}

    static PlayerCondition checkBiomeTag(TagKey<Biome> biomeTag) { return (player, _) -> player.level().getBiome(player.blockPosition()).is(biomeTag) ;}

    static PlayerCondition hasArmorWithEnchantment(EquipmentSlot equipmentSlot, ResourceKey<Enchantment> enchantment) {
        return (player, level) -> {
            ItemStack bodyArmor = player.getItemBySlot(equipmentSlot);
            if (bodyArmor.isEmpty()) return false;
            Registry<Enchantment> enchantmentRegistry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int playerHasEnchantment = bodyArmor.getEnchantments().getLevel( enchantmentRegistry.getOrThrow(enchantment));
            return playerHasEnchantment > 1;
        };
    }

    static PlayerCondition tick(int min, int sec) {
        return (player, world) -> TOTVW.getGameTime(world, min, sec);
    }
    static PlayerCondition checkNoAttached(AttachmentType<Integer> type) {
        return (wolf, level) -> wolf.getAttachedOrElse(type, 0) == 0;
    }
}

package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class HavocRunestonePlate extends Item {
    public HavocRunestonePlate(Properties properties) {
        super(properties);
    }
    private static final Component HAVOC = Component.literal("§lHavoc Runestone Plate§r").withColor(VWColors.RUNESTONE_HAVOC);
    private static boolean ACTIVE = false;

    @Override
    public Component getName(ItemStack itemStack) {
        return ACTIVE ? HAVOC : super.getName(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        ACTIVE = owner instanceof LivingEntity entity && entity.hasEffect(VWEffects.HAVOC);
        super.inventoryTick(itemStack, level, owner, slot);
    }
}

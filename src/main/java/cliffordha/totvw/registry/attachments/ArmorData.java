package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ArmorData(
        Component component,
        Identifier id,
        int maxDurability,
        int durability,
        EquipmentSlot slot,
        double baseATK,
        double totalATK
) {
    private static final Identifier NULL_ID = TOTVW.registerID("null");
    public static final Codec<Component> COMPONENT_CODEC = Codec.STRING.xmap(Component::literal, Component::getString);
    public static final Codec<ArmorData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    COMPONENT_CODEC.fieldOf("component").forGetter(ArmorData::component),
                    Identifier.CODEC.fieldOf("id").forGetter(ArmorData::id),
                    Codec.INT.fieldOf("max_durability").forGetter(ArmorData::maxDurability),
                    Codec.INT.fieldOf("durability").forGetter(ArmorData::durability),
                    EquipmentSlot.CODEC.fieldOf("slot").forGetter(ArmorData::slot),
                    Codec.DOUBLE.fieldOf("base_attack").forGetter(ArmorData::baseATK),
                    Codec.DOUBLE.fieldOf("total_attack").forGetter(ArmorData::totalATK)
            ).apply(instance, ArmorData::new)
    );
    public static final ArmorData NONE = new ArmorData(Component.literal(""), NULL_ID, 0, 0, EquipmentSlot.MAINHAND, 0, 0);

    public String getName() {
        return component.getString();
    }

    private ItemStack getItemStack(Identifier id) {
        Identifier get = Identifier.tryParse(String.valueOf(id));
        if (get != null) {
            return BuiltInRegistries.ITEM.getValue(get).getDefaultInstance();
        }
        return VWItems.EMPTY.getDefaultInstance();
    }
    public double getAddedATK() {
        return this.totalATK - this.baseATK;
    }
    public static ArmorData of(Player player) {
        ItemStack stack = player.getMainHandItem();
        AttributeMap map = player.getAttributes();
        return new ArmorData(
                stack.getItemName(),
                ArmorData.getID(stack),
                stack.getMaxDamage(), stack.getDamageValue(),
                EquipmentSlot.MAINHAND,
                map.getBaseValue(Attributes.ATTACK_DAMAGE), map.getValue(Attributes.ATTACK_DAMAGE)
                );
    }
    public static ArmorData of(Wolf wolf) {
        ItemStack armor = wolf.getBodyArmorItem();
        AttributeMap map = wolf.getAttributes();
        return new ArmorData(
                armor.getItemName(), ArmorData.getID(armor),
                armor.getMaxDamage(), armor.getDamageValue(),
                EquipmentSlot.BODY,
                map.getBaseValue(Attributes.ATTACK_DAMAGE),
                map.getValue(Attributes.ATTACK_DAMAGE));
    }

    public ItemStack getItemStack() {
        Identifier get = Identifier.tryParse(String.valueOf(id));
        if (get != null) {
            return BuiltInRegistries.ITEM.getValue(get).getDefaultInstance();
        }
        return VWItems.EMPTY.getDefaultInstance();
    }

    public boolean isNull(Item item) {
        if (item == null) {
            return false;
        }
        return item.getDefaultInstance().is(VWItems.EMPTY) && this.isSame(item);
    }

    public boolean isNull() {
        return this.equals(ArmorData.NONE);
    }

    public boolean isEmpty() {
        return this.isNull();
    }

    public boolean isSame(Item stack) {
        return component.equals(stack.getDefaultInstance().getDisplayName());
    }

    public static Identifier getID(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.getItem());
    }
}

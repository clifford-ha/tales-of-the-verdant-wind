package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ArmorData(Component component, Identifier id, int maxDurability, int durability, EquipmentSlot slot) {
    private static final Identifier NULL_ID = TOTVW.registerID("null");
    public static final Codec<Component> COMPONENT_CODEC = Codec.STRING.xmap(Component::literal, Component::getString);
    public static final Codec<ArmorData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    COMPONENT_CODEC.fieldOf("component").forGetter(ArmorData::component),
                    Identifier.CODEC.fieldOf("id").forGetter(ArmorData::id),
                    Codec.INT.fieldOf("maxDurability").forGetter(ArmorData::maxDurability),
                    Codec.INT.fieldOf("durability").forGetter(ArmorData::durability),
                    EquipmentSlot.CODEC.fieldOf("slot").forGetter(ArmorData::slot)
            ).apply(instance, ArmorData::new)
    );
    public static final ArmorData NONE = new ArmorData(Component.literal(""), NULL_ID, 0, 0, EquipmentSlot.MAINHAND);

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

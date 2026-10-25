package cliffordha.totvw.util;

import cliffordha.totvw.registry.VWItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BasicArmorData {
    public static final Codec<Component> COMPONENT_CODEC = Codec.STRING.xmap(Component::literal, Component::getString);
    public static final Codec<BasicArmorData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    COMPONENT_CODEC.fieldOf("component").forGetter(BasicArmorData::getComponent),
                    Identifier.CODEC.fieldOf("id").forGetter(BasicArmorData::getId),
                    Codec.INT.fieldOf("maxDurability").forGetter(BasicArmorData::getMaxDurability),
                    Codec.INT.fieldOf("durability").forGetter(BasicArmorData::getDurability),
                    EquipmentSlot.CODEC.fieldOf("slot").forGetter(BasicArmorData::getSlot)
            ).apply(instance, BasicArmorData::new)
    );
    public static final BasicArmorData NONE = new BasicArmorData(Component.literal(""), null, 0, 0, EquipmentSlot.MAINHAND);
    private final Component component;
    private final Identifier id;
    private final int maxDurability;
    private final int durability;
    private final EquipmentSlot slot;

    public BasicArmorData(Component component, Identifier id, int maxDurability, int durability, EquipmentSlot slot) {
        this.component = component;
        this.id = id;
        this.maxDurability = maxDurability;
        this.durability = durability;
        this.slot = slot;
    }

    public Component getComponent() {
        return component;
    }
    public String getName() {
        return component.getString();
    }
    public Identifier getId() {
        return id;
    }
    public int getMaxDurability() {
        return maxDurability;
    }
    public int getDurability() {
        return durability;
    }
    public EquipmentSlot getSlot() {
        return slot;
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
        return this.equals(BasicArmorData.NONE);
    }
    public boolean isSame(Item stack) {
        return component.equals(stack.getDefaultInstance().getDisplayName());
    }
    public static Identifier getID(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.getItem());
    }
}

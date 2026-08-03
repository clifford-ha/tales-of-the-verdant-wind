package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.block.entity.VWHangingSignBlockEntity;
import cliffordha.totvw.block.entity.VWShelfBlockEntity;
import cliffordha.totvw.block.entity.VWSignBlockEntity;
import cliffordha.totvw.block.entity.custom.StorageBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Set;

public class VWBlockEntityTypes {
    public static final ArrayList<Pair<Identifier, BlockEntityType<?>>> BLOCK_ENTITY_TYPES = new ArrayList<>();

    public static final BlockEntityType<VWSignBlockEntity> SIGN = create("sign",
            new BlockEntityType<>(VWSignBlockEntity::new,
                    Set.of(VWBlocks.VERDANT_SPRUCE_SIGN, VWBlocks.VERDANT_SPRUCE_WALL_SIGN)));

    public static final BlockEntityType<VWHangingSignBlockEntity> HANGING_SIGN = create("hanging_sign",
            new BlockEntityType<>(VWHangingSignBlockEntity::new,
                    Set.of(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN, VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)));

    public static final BlockEntityType<VWShelfBlockEntity> SHELF = create("shelf",
            new BlockEntityType<>(VWShelfBlockEntity::new,
                    Set.of(VWBlocks.VERDANT_SPRUCE_SHELF)));

    public static final BlockEntityType<StorageBlockEntity> STORAGE_BOX = create("storage_box",
            new BlockEntityType<>(StorageBlockEntity::new,
                    Set.of(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX)));

    public static <T extends BlockEntityType<?>> T create(String name, T blockEntityType) {
        BLOCK_ENTITY_TYPES.add(new Pair<>(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), blockEntityType));
        return blockEntityType;
    }

    public static void register() {
        for (Pair<Identifier, BlockEntityType<?>> entry : BLOCK_ENTITY_TYPES) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, entry.getA(), entry.getB());
        }
    }
}
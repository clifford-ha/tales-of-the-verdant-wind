package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.block.entity.ModHangingSignBlockEntity;
import cliffordha.totvw.block.entity.ModShelfBlockEntity;
import cliffordha.totvw.block.entity.ModSignBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Set;

public class ModBlockEntityTypes {
    public static void register() {
        for (Pair<Identifier, BlockEntityType<?>> registry : BLOCK_ENTITY_TYPES) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, registry.getA(), registry.getB());
        }
    }

    public static <T extends BlockEntityType<?>> T createBlockEntityType(String name, T blockEntity) {
        BLOCK_ENTITY_TYPES.add(new Pair<>(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), blockEntity));
        return blockEntity;
    }

    public static final ArrayList<Pair<Identifier, BlockEntityType<?>>> BLOCK_ENTITY_TYPES = new ArrayList<>();

    public static final BlockEntityType<ModSignBlockEntity> SIGN = createBlockEntityType("sign", new BlockEntityType<>(ModSignBlockEntity::new,
            Set.of(VerdantBlocks.VERDANT_SPRUCE_SIGN, VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN)
    ));
    public static final BlockEntityType<ModHangingSignBlockEntity> HANGING_SIGN = createBlockEntityType("hanging_sign", new BlockEntityType<>(ModHangingSignBlockEntity::new,
            Set.of(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN, VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)
    ));
    public static final BlockEntityType<ModShelfBlockEntity> SHELF = createBlockEntityType("shelf", new BlockEntityType<>(ModShelfBlockEntity::new,
            Set.of(VerdantBlocks.VERDANT_SPRUCE_SHELF)
    ));
}

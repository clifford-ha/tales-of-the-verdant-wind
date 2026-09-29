package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.VWBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FarmlandBlockPlacer extends Block {
    public FarmlandBlockPlacer(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tick(state, level, pos, random);
        initiateTickToSides(level, pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 0);

            if (level.getGameTime() % 20 == 0) {
                initiateTickToSides(level, pos);
                BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();

                if (isAir(pos.north(), level)) {
                    level.setBlockAndUpdate(pos, deepslate);

                } else if (isAir(pos.south(), level)) {
                    level.setBlockAndUpdate(pos, deepslate);

                } else if (isAir(pos.east(), level)) {
                    level.setBlockAndUpdate(pos, deepslate);

                } else if (isAir(pos.west(), level)) {
                    level.setBlockAndUpdate(pos, deepslate);

                } else if (level.getBlockState(pos.above()).isAir()) {

                    if (random.nextFloat() < 0.1f) {
                        convertToWaterSource(level, pos);
                    } else {
                        List<Block> lanterns = List.of(
                                Blocks.LANTERN,
                                Blocks.SOUL_LANTERN,
                                Blocks.COPPER_LANTERN.weathered(),
                                Blocks.COPPER_LANTERN.waxedWeathered()
                        );
                        if (random.nextFloat() < 0.009f) {
                            level.setBlockAndUpdate(pos.above(), lanterns.get(random.nextInt(lanterns.size())).defaultBlockState());
                            level.setBlockAndUpdate(pos, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState());
                        } else {
                            level.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, random.nextInt(7)));
                            level.setBlockAndUpdate(pos.above(), Blocks.WHEAT.defaultBlockState());
                        }
                    }

                    CropBlock crop = level.getBlockState(pos.above()).getBlock() instanceof CropBlock cropBlock ? cropBlock : null;

                    if (crop != null) {
                        List<Block> randomList = List.of(
                                Blocks.WHEAT,
                                Blocks.BEETROOTS,
                                Blocks.POTATOES,
                                Blocks.TORCHFLOWER_CROP,
                                Blocks.CARROTS
                        );
                        CropBlock randomCrop = (CropBlock) randomList.get(random.nextInt(randomList.size()));
                        level.setBlockAndUpdate(pos.above(), randomCrop.defaultBlockState());

                        for (int i = 0; i < random.nextInt(randomCrop.getMaxAge()); i++) {
                            randomCrop.performBonemeal(level, random, pos.above(), randomCrop.defaultBlockState());
                        }
                    }
                } else {
                    level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                }
            }
        }
    }
    private static void convertToWaterSource(ServerLevel level, BlockPos pos) {
        boolean surroundedByBlock = !isAir(pos.north(), level) && !isAir(pos.south(), level) && !isAir(pos.east(), level) && !isAir(pos.west(), level);
        if (surroundedByBlock) {
            BlockState state = level.getRandom().nextFloat() < 0.7f ?
                    VWBlocks.VERDANT_SPRUCE_LEAVES
                            .defaultBlockState()
                            .setValue(LeavesBlock.WATERLOGGED, true)
                            .setValue(LeavesBlock.PERSISTENT, true)
                    : VWBlocks.VERIXIUM_FLUID.defaultBlockState();
            level.setBlockAndUpdate(pos, state);
        }
    }
    private static FarmlandBlockPlacer getCropBlock(BlockPos pos, ServerLevel level) {
        return level.getBlockState(pos).getBlock() instanceof FarmlandBlockPlacer ? (FarmlandBlockPlacer) level.getBlockState(pos).getBlock() : null;
    }
    private static boolean isAir(BlockPos pos, ServerLevel level) {
        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof VegetationBlock) {
            return true;
        }
        return level.getBlockState(pos).isAir();
    }
    private static void initiateTickToSides(ServerLevel level, BlockPos pos) {
        if (level.getRandom().nextBoolean()) {
            FarmlandBlockPlacer cropAbove = getCropBlock(pos.above(), level);
            forceTick(cropAbove, level, pos.above());

            FarmlandBlockPlacer cropNorth = getCropBlock(pos.north(), level);
            forceTick(cropNorth, level, pos.north());

            FarmlandBlockPlacer cropNorthE = getCropBlock(pos.north().east(), level);
            forceTick(cropNorthE, level, pos.north().east());

            FarmlandBlockPlacer cropEast = getCropBlock(pos.east(), level);
            forceTick(cropEast, level, pos.east());

            FarmlandBlockPlacer cropEastS = getCropBlock(pos.east().south(), level);
            forceTick(cropEastS, level, pos.east().south());

            FarmlandBlockPlacer cropSouth = getCropBlock(pos.south(), level);
            forceTick(cropSouth, level, pos.south());

            FarmlandBlockPlacer cropSouthW = getCropBlock(pos.south().west(), level);
            forceTick(cropSouthW, level, pos.south().west());

            FarmlandBlockPlacer cropWest = getCropBlock(pos.west(), level);
            forceTick(cropWest, level, pos.west());

            FarmlandBlockPlacer cropWestN = getCropBlock(pos.west().north(), level);
            forceTick(cropWestN, level, pos.west().north());
        }
    }
    private static void forceTick(FarmlandBlockPlacer block, ServerLevel level, BlockPos pos) {
        if (block == null) return;
        level.scheduleTick(pos, block, 0);
    }
}

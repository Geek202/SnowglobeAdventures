package me.geek.tom.snowglobeadventures.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Locale;

public class BaubleBlock extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 10, 6, 10, 14, 10),
            Block.createCuboidShape(7.5, 14, 7.5, 8.5, 16, 8.5),
            BooleanBiFunction.OR
    );

    public BaubleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d offset = state.getModelOffset(world, pos);
        return SHAPE.offset(offset.x, offset.y, offset.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    public enum Colour implements StringIdentifiable {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE;

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}

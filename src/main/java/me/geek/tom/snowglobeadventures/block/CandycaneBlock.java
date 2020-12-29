package me.geek.tom.snowglobeadventures.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CandycaneBlock extends Block {

    private static final VoxelShape STALK_SHAPE = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
    private static final VoxelShape TOP_SHAPE = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 0, 6, 10, 7, 10),
            Block.createCuboidShape(6, 5, 8, 10, 10, 16),
            BooleanBiFunction.OR
    );

    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public CandycaneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        BlockState above = world.getBlockState(pos.up());
        boolean top = above.isAir() && !ctx.shouldCancelInteraction();

        return this.getDefaultState().with(TYPE, top ? Type.TOP : Type.STALK);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d offset = state.getModelOffset(world, pos);
        VoxelShape ret;
        switch (state.get(TYPE)) {
            case STALK:
                ret = STALK_SHAPE;
                break;
            case TOP:
                ret = TOP_SHAPE;
                break;
            default:
                ret = super.getOutlineShape(state, world, pos, context);
                break;
        }
        return ret.offset(offset.x, offset.y, offset.z);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(TYPE));
    }

    public enum Type implements StringIdentifiable {
        STALK, TOP;

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}

package me.geek.tom.snowglobeadventures.block;

import me.geek.tom.snowglobeadventures.Registration;
import me.geek.tom.snowglobeadventures.SnowglobeAdventures;
import me.geek.tom.snowglobeadventures.world.generator.SnowglobeStructurePlacer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class SnowglobeBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(
            3,  0, 3,
            13, 9, 13);

    public static final EnumProperty<SnowglobeStructurePlacer.DecorationType> DECORATION_TYPE = EnumProperty.of("decoration", SnowglobeStructurePlacer.DecorationType.class);
    public static final EnumProperty<SnowglobeStructurePlacer.TreeType> TREE_TYPE = EnumProperty.of("tree", SnowglobeStructurePlacer.TreeType.class);
    public static final EnumProperty<SnowglobeStructurePlacer.HouseType> HOUSE_TYPE = EnumProperty.of("house", SnowglobeStructurePlacer.HouseType.class);


    public SnowglobeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(DECORATION_TYPE, TREE_TYPE, HOUSE_TYPE));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Random rand = Objects.requireNonNull(ctx.getPlayer(), "ctx.getPlayer()").getRandom();
        return Objects.requireNonNull(super.getPlacementState(ctx), "super.getPlacementState()")
                .with(DECORATION_TYPE, Util.getRandom(SnowglobeStructurePlacer.DecorationType.values(), rand))
                .with(TREE_TYPE, Util.getRandom(SnowglobeStructurePlacer.TreeType.values(), rand))
                .with(HOUSE_TYPE, Util.getRandom(SnowglobeStructurePlacer.HouseType.values(), rand));
    }

    public SnowglobeStructurePlacer toPlacer(BlockPos pos, BlockState state) {
        return new SnowglobeStructurePlacer(state.get(HOUSE_TYPE), state.get(DECORATION_TYPE), state.get(TREE_TYPE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity p, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        // We are on the server, it is now safe to cast the player.
        ServerPlayerEntity player = (ServerPlayerEntity) p;
        SnowglobeAdventures.worldManager.enterSnowglobe(player.getServer(), player, pos, world.getRegistryKey(), this.toPlacer(pos, state));
        return ActionResult.CONSUME;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

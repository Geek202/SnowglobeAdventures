package me.geek.tom.snowglobeadventures.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.LanternBlock.HANGING;

@Mixin(LanternBlock.class)
public abstract class MixinLanternBlock extends Block {
    // Dummy
    public MixinLanternBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void hookCanPlaceAt_allowLeaves(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Direction direction = state.get(HANGING) ? Direction.UP : Direction.DOWN;
        if (world.getBlockState(pos.offset(direction)).isIn(BlockTags.LEAVES)) { // allow lanterns to be placed on leaves
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}

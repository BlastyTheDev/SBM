package dev.blasty.sbm.client.macro;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

import static dev.blasty.sbm.client.SbmClient.CONFIG;

public class FarmingMacro extends Macro {
    private boolean movingLeft = CONFIG.get().farmingMoveLeftFirst;

    @Override
    public void run() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (CONFIG.get().farmingWarpOnStart) {
            runCommand("warp garden");
            sleep(CONFIG.get().farmingWarpContinueDelay);
        }
        setHotbarSlot(CONFIG.get().getFarmingToolSlot());

        if (mc.player.getAbilities().flying) {
            pressKey(opts.sneakKey);
        }
        while (mc.player.getAbilities().flying) {
            sleep(1);
            checkPaused();
            if (wasPaused) {
                pressKey(opts.sneakKey);
            }
        }
        releaseKey(opts.sneakKey);

        for (int run = 0; run < CONFIG.get().farmingRuns; run++) {
            for (int row = 0; row < CONFIG.get().farmingRows; row++) {
                pressKey(opts.forwardKey);
                pressKey(opts.attackKey);
                pressKey(movingLeft ? opts.leftKey : opts.rightKey);

                waitToFinishRow(true);
                sleep(1);

                releaseKey(opts.leftKey);
                releaseKey(opts.rightKey);
                movingLeft = !movingLeft;

                waitToFinishRow(false);
                sleep(1);
            }
            releaseKeys();
            if (run + 1 < CONFIG.get().farmingRuns) {
                sleep(CONFIG.get().farmingWarpExecutionDelay);
                runCommand("warp garden");
                sleep(CONFIG.get().farmingWarpContinueDelay);
            }
        }
    }

    @Override
    public void pause() {
        releaseKeys();
        super.pause();
    }

    private void releaseKeys() {
        releaseKey(opts.forwardKey);
        releaseKey(opts.leftKey);
        releaseKey(opts.rightKey);
        releaseKey(opts.attackKey);
        releaseKey(opts.sneakKey);
    }

    private void waitToFinishRow(boolean horizontal) {
        AtomicReference<BlockState> blockInFront = new AtomicReference<>();
        mc.executeTask(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            assert mc.world != null;
            assert mc.player != null;
            blockInFront.set(mc.world.getBlockState(getBlockInFrontPos(mc.player.getEntityPos())));
        });
        while (horizontal ^ (blockInFront.get().isAir() || blockInFront.get().getFluidState().getFluid() == Fluids.WATER)) {
            sleep(1);
            mc.executeTask(() -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                assert mc.world != null;
                assert mc.player != null;
                blockInFront.set(mc.world.getBlockState(getBlockInFrontPos(mc.player.getEntityPos())));
            });

            checkPaused();
            if (wasPaused) {
                pressKey(opts.forwardKey);
                pressKey(opts.attackKey);
                if (horizontal) {
                    pressKey(movingLeft ? opts.leftKey : opts.rightKey);
                }
                wasPaused = false;
            }
        }
    }

    private @NotNull BlockPos getBlockInFrontPos(Position pos) {
        int xOffset = 0;
        int zOffset = 0;
        assert mc.player != null;
        Direction horizontalFacing = mc.player.getHorizontalFacing();
        if (horizontalFacing == Direction.NORTH) {
            zOffset = -1;
        } else if (horizontalFacing == Direction.EAST) {
            xOffset = 1;
        } else if (horizontalFacing == Direction.SOUTH) {
            zOffset = 1;
        } else if (horizontalFacing == Direction.WEST) {
            xOffset = -1;
        }
        return BlockPos.ofFloored(pos.getX() + xOffset, pos.getY(), pos.getZ() + zOffset);
    }
}

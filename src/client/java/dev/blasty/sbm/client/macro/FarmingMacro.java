package dev.blasty.sbm.client.macro;

import dev.blasty.sbm.client.mixin.MinecraftClientAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static dev.blasty.sbm.client.SbmClient.CONFIG;

public class FarmingMacro extends Macro {
    private static final int ACCEPT_OFFER = 29;
    private static final int REFUSE_OFFER = 33;

    private boolean movingLeft = CONFIG.get().farmingMoveLeftFirst;

    private final AtomicInteger visitorCount = new AtomicInteger(-1);
    private volatile boolean checkVisitors;

    @Override
    public void run() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        setHotbarSlot(CONFIG.get().getFarmingToolSlot());

        if (CONFIG.get().farmingWarpOnStart) {
            runCommand("warp garden");
            sleep(CONFIG.get().farmingWarpContinueDelay);
        }

        if (mc.player.getAbilities().flying) {
            pressKey(opts.sneakKey);
        }
        while (mc.player.getAbilities().flying) {
            sleep(1);
            checkPaused();
            pressKey(opts.sneakKey);
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
                movingLeft = CONFIG.get().farmingMoveLeftFirst;
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

            if (CONFIG.get().farmingServeVisitors && checkVisitors && visitorCount.get() >= 0) {
                if (visitorCount.get() >= 4) { // tablist takes time to update
                    serveVisitors();
                }
                checkVisitors = false;
                visitorCount.set(-1);
            }

            checkPaused();
            pressKey(opts.forwardKey);
            pressKey(opts.attackKey);
            if (horizontal) {
                pressKey(movingLeft ? opts.leftKey : opts.rightKey);
            }
        }
    }

    private void serveVisitors() {
        releaseKeys();
        sleep(CONFIG.get().farmingWarpExecutionDelay);
        runCommand("tptoplot barn");
        sleep(CONFIG.get().farmingWarpContinueDelay);

        for (int i = 0; i < 1; i++) {
            // double click to open visitor inv faster
            ((MinecraftClientAccessor) mc).leftClick();
            sleep(3);
            ((MinecraftClientAccessor) mc).leftClick();
            // wait for inv to open
            while (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                sleep(1);
            }
            Inventory visitorMenu = screen.getScreenHandler().getInventory();
            boolean immediatelyAcceptable = false;
            boolean readAllRequiredItems = false;
            List<String> requiredItems = new ArrayList<>();
            int copperReward = 0;
            int loreLinesIndex = 0;
            for (Text t : Objects.requireNonNull(visitorMenu.getStack(ACCEPT_OFFER).get(DataComponentTypes.LORE)).lines()) {
                String s = t.getString();
                if (s.isBlank()) {
                    readAllRequiredItems = true;
                }
                if (loreLinesIndex > 0 && !readAllRequiredItems) {
                    requiredItems.add(s);
                }
                if (s.contains("Copper")) {
                    copperReward = Integer.parseInt(s.strip().split(" ")[0].substring(1));
                }
                if (!immediatelyAcceptable && s.equals("Click to give!")) {
                    immediatelyAcceptable = true;
                }
                loreLinesIndex++;
            }

            assert mc.interactionManager != null;
            // i guess at least 20 copper is "worth it"
            if (copperReward < 20) {
                mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, REFUSE_OFFER, 0, SlotActionType.PICKUP, mc.player);
                continue;
            }
            // TODO: 1. Make min copper reward configurable, 2. Implement buying items, 3. Make player jump onto desk so visitors are reachable
            if (!immediatelyAcceptable) {
                // buy items
            }

            mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, ACCEPT_OFFER, 0, SlotActionType.PICKUP, mc.player);
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

    private void checkVisitorCount() {
        mc.executeTask(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            assert mc.player != null;
            for (PlayerListEntry entry : mc.player.networkHandler.getPlayerList()) {
                Text entryName = entry.getDisplayName();
                if (entryName == null) {
                    continue;
                }
                String entryNameStr = entryName.getString();
                if (entryNameStr.contains("Visitors: (")) {
                    int substringIndex = entryNameStr.indexOf(')') - 1;
                    visitorCount.set(Integer.parseInt(entryNameStr.substring(substringIndex, substringIndex + 1)));
                    break;
                }
            }
        });
    }

    public void queueVisitorCheck() {
        checkVisitors = true;
        checkVisitorCount();
    }
}

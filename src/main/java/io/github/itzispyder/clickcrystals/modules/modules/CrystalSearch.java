package io.github.itzispyder.clickcrystals.modules.modules;

import io.github.itzispyder.clickcrystals.events.EventHandler;
import io.github.itzispyder.clickcrystals.events.Listener;
import io.github.itzispyder.clickcrystals.events.events.PacketSendEvent;
import io.github.itzispyder.clickcrystals.modules.Module;
import io.github.itzispyder.clickcrystals.util.BlockUtils;
import io.github.itzispyder.clickcrystals.util.HotbarUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CrystalSearch extends Module implements Listener {

    public CrystalSearch() {
        super("CrystalSearch","The opposite of ObsidianSearch.");
    }

    @Override
    protected void onEnable() {
        system.addListener(this);
    }

    @Override
    protected void onDisable() {
        system.removeListener(this);
    }

    @EventHandler
    private void onSendPacket(PacketSendEvent e) {
        if (e.getPacket() instanceof PlayerActionC2SPacket packet) {
            if (packet.getAction() != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return;
            BlockPos pos = packet.getPos();
            if (!(BlockUtils.matchBlock(pos, Blocks.OBSIDIAN) || BlockUtils.matchBlock(pos,Blocks.BEDROCK))) return;

            if (HotbarUtils.nameContains("obsidian") || HotbarUtils.nameContains("totem") || HotbarUtils.nameContains("sword")) {
                e.setCancelled(true);
                ItemStack item = mc.player.getStackInHand(mc.player.getActiveHand());
                Item type = item.getItem();

                HotbarUtils.search(Items.END_CRYSTAL);
                BlockUtils.interact(pos,Direction.UP);
                HotbarUtils.search(type);
            }
        }
    }
}

package io.github.itzispyder.clickcrystals.gui_beta.hud.moveables;

import io.github.itzispyder.clickcrystals.gui_beta.hud.TextHud;
import io.github.itzispyder.clickcrystals.modules.Module;
import io.github.itzispyder.clickcrystals.modules.modules.clickcrystals.InGameHuds;
import io.github.itzispyder.clickcrystals.util.PlayerUtils;

public class FpsRelativeHud extends TextHud {

    public FpsRelativeHud() {
        super("fps-hud", 10, 90, 50, 12);
    }

    @Override
    public String getText() {
        return PlayerUtils.getFps() + " fps";
    }

    @Override
    public boolean canRender() {
        return super.canRender() && Module.getFrom(InGameHuds.class, m -> m.hudFps.getVal());
    }
}

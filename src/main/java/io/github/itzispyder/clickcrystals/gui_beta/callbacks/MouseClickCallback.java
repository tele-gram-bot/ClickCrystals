package io.github.itzispyder.clickcrystals.gui_beta.callbacks;

import io.github.itzispyder.clickcrystals.gui_beta.ClickType;

@FunctionalInterface
public interface MouseClickCallback {

    void handleMouse(double mouseX, double mouseY, int button, ClickType click);
}

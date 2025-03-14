package io.github.itzispyder.clickcrystals.gui_beta.elements.client.module;

import io.github.itzispyder.clickcrystals.gui_beta.GuiElement;
import io.github.itzispyder.clickcrystals.gui_beta.elements.AbstractElement;
import io.github.itzispyder.clickcrystals.gui_beta.misc.Gray;
import io.github.itzispyder.clickcrystals.gui_beta.misc.Tex;
import io.github.itzispyder.clickcrystals.modules.ModuleSetting;
import io.github.itzispyder.clickcrystals.util.RenderUtils;
import io.github.itzispyder.clickcrystals.util.StringUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class SettingElement<T extends ModuleSetting<?>> extends GuiElement {

    protected final T setting;
    protected boolean shouldUnderline;

    public SettingElement(T setting, int x, int y) {
        super(x, y, 270, 40);
        this.shouldUnderline = false;
        this.setting = setting;
    }

    public void renderSettingDetails(DrawContext context) {
        int caret = y + 5;
        RenderUtils.drawText(context, setting.getName(), x + 5, caret, 0.6F, false);
        for (String line : StringUtils.wrapLines(setting.getDescription(), 55, true)) {
            caret += 8;
            RenderUtils.drawText(context, "§7" + line, x + 5, caret, 0.6F, false);
        }
        caret += 5;

        this.setHeight(caret - y);

        if (shouldUnderline) {
            caret += 10;
            RenderUtils.drawHorizontalLine(context, x + 5, caret, width - 10, 1, Gray.DARK_GRAY.argb);
        }
    }

    public void createResetButton() {
        int drawY = y + 5;
        int drawX = x + width - 5;

        this.addChild(AbstractElement.create()
                .pos(drawX, drawY)
                .dimensions(10, 10)
                .onPress(button -> this.revertSettingValue())
                .onRender((context, mouseX, mouseY, button) -> {
                    RenderUtils.drawTexture(context, Tex.Icons.RESET, button.x, button.y, button.width, button.height);
                })
                .build()
        );
    }

    public void revertSettingValue() {
        setting.setVal(setting.getDef());
    }

    public void setShouldUnderline(boolean shouldUnderline) {
        this.shouldUnderline = shouldUnderline;
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        int bx = x + width / 4 * 3 - 5;
        int bxw = x + width - 5;
        int by = y + height / 2 - 4;
        int byh = y + height / 2 + 12;
        return rendering && mouseX > bx && mouseX < bxw && mouseY > by && mouseY < byh;
    }
}

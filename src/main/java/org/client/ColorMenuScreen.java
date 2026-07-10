package org.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public class ColorMenuScreen extends net.minecraft.client.gui.screens.Screen {
    private static final ColorCode[] COLOR_CODES = {
            new ColorCode("&0 = ", ChatFormatting.BLACK, "Black"),
            new ColorCode("&1 = ", ChatFormatting.DARK_BLUE, "Dark Blue"),
            new ColorCode("&2 = ", ChatFormatting.DARK_GREEN, "Dark Green"),
            new ColorCode("&3 = ", ChatFormatting.DARK_AQUA, "Dark Aqua"),
            new ColorCode("&4 = ", ChatFormatting.DARK_RED, "Dark Red"),
            new ColorCode("&5 = ", ChatFormatting.DARK_PURPLE, "Dark Purple"),
            new ColorCode("&6 = ", ChatFormatting.GOLD, "Gold"),
            new ColorCode("&7 = ", ChatFormatting.GRAY, "Gray"),
            new ColorCode("&k = ", ChatFormatting.OBFUSCATED, "aaaa"),
            new ColorCode("&l = ", ChatFormatting.BOLD, "Bold"),
            new ColorCode("&m = ", ChatFormatting.STRIKETHROUGH, "Strikethrough"),
            new ColorCode("&8 = ", ChatFormatting.DARK_GRAY, "Dark Gray"),
            new ColorCode("&9 = ", ChatFormatting.BLUE, "Blue"),
            new ColorCode("&a = ", ChatFormatting.GREEN, "Green"),
            new ColorCode("&b = ", ChatFormatting.AQUA, "Aqua"),
            new ColorCode("&c = ", ChatFormatting.RED, "Red"),
            new ColorCode("&d = ", ChatFormatting.LIGHT_PURPLE, "Light Purple"),
            new ColorCode("&e = ", ChatFormatting.YELLOW, "Yellow"),
            new ColorCode("&f = ", ChatFormatting.WHITE, "White"),
            new ColorCode("&n = ", ChatFormatting.UNDERLINE, "Underline"),
            new ColorCode("&o = ", ChatFormatting.ITALIC, "Italic"),
            new ColorCode("&r = ", ChatFormatting.RESET, "Reset"),
    };

    public ColorMenuScreen() {
        super(Component.literal("Color Codes"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(this.width / 2 - 50, this.height / 2 + 102, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {

        int panelWidth = 220;
        int panelHeight = 170;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2 + 56;

        context.fill(panelX, panelY - 76, panelX + panelWidth, panelY + panelHeight - 44, 0x4FFFFFFF);
        drawCodes(context, panelX + 10, panelY - 70);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawCodes(GuiGraphics context, int startX, int startY) {
        for (int index = 0; index < ColorMenuScreen.COLOR_CODES.length; index++) {
            int column = index / 11;
            int row = index % 11;
            int x = startX + column * 112;
            int y = startY + row * 18;
            ColorCode code = ColorMenuScreen.COLOR_CODES[index];

            context.drawString(this.font, code.text(), x, y, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyEvent)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    private record ColorCode(String code, ChatFormatting formatting, String color) {
        private Component text() {
            return Component.literal(this.code).append(Component.literal(color).withStyle(formatting));
        }
    }
}

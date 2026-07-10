package org.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.event.KeyEvent;

public class ColorMenuScreen extends Screen {
    private static final ColorCode[] COLOR_CODES = {
            new ColorCode("&0 = ", Formatting.BLACK, "Black"),
            new ColorCode("&1 = ", Formatting.DARK_BLUE, "Dark Blue"),
            new ColorCode("&2 = ", Formatting.DARK_GREEN, "Dark Green"),
            new ColorCode("&3 = ", Formatting.DARK_AQUA, "Dark Aqua"),
            new ColorCode("&4 = ", Formatting.DARK_RED,  "Dark Red"),
            new ColorCode("&5 = ", Formatting.DARK_PURPLE, "Dark Purple"),
            new ColorCode("&6 = ", Formatting.GOLD,  "Gold"),
            new ColorCode("&7 = ", Formatting.GRAY,  "Gray"),
            new ColorCode("&k = ", Formatting.OBFUSCATED, "aaaa"),
            new ColorCode("&l = ", Formatting.BOLD, "Bold"),
            new ColorCode("&m = ", Formatting.STRIKETHROUGH,  "Strikethrough"),
            new ColorCode("&8 = ", Formatting.DARK_GRAY,   "Dark Gray"),
            new ColorCode("&9 = ", Formatting.BLUE, "Blue"),
            new ColorCode("&a = ", Formatting.GREEN, "Green"),
            new ColorCode("&b = ", Formatting.AQUA,  "Aqua"),
            new ColorCode("&c = ", Formatting.RED, "Red"),
            new ColorCode("&d = ", Formatting.LIGHT_PURPLE, "Light Purple"),
            new ColorCode("&e = ", Formatting.YELLOW,  "Yellow"),
            new ColorCode("&f = ", Formatting.WHITE,  "White"),
            new ColorCode("&n = ", Formatting.UNDERLINE,   "Underline"),
            new ColorCode("&o = ", Formatting.ITALIC,   "Italic"),
            new ColorCode("&r = ", Formatting.RESET, "Reset"),
    };

    public ColorMenuScreen() {
        super(Text.literal("Color Codes"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 50, this.height / 2 + 102, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        int panelWidth = 220;
        int panelHeight = 170;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2 + 56;

        context.fill(panelX, panelY -76, panelX + panelWidth, panelY + panelHeight-44, 0x4FFFFFFF);
        drawCodes(context, panelX + 10, panelY - 70);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (this.client != null && this.client.options.inventoryKey.matchesKey(keyInput)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean shouldPause(){return false;}

    private void drawCodes(DrawContext context, int startX, int startY) {
        for (int index = 0; index < ColorMenuScreen.COLOR_CODES.length; index++) {
            int column = index / 11;
            int row = index % 11;
            int x = startX + column * 112;
            int y = startY + row * 18;
            ColorCode code = ColorMenuScreen.COLOR_CODES[index];

            context.drawTextWithShadow(this.textRenderer, code.text(), x, y, 0xFFFFFFFF);
        }
    }

    private record ColorCode(String code, Formatting formatting, String color) {
        private Text text() {
            return Text.literal(this.code).append(Text.literal(color).formatted(formatting));
        }
    }
}

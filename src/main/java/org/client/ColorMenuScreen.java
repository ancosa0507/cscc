package org.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
//        this.renderBackground(context, mouseX, mouseY, deltaTicks);

        int panelWidth = 220;
        int panelHeight = 170;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2 + 56;

        context.fill(panelX, panelY -76, panelX + panelWidth, panelY + panelHeight-44, 0x4FFFFFFF);
//        context.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFF475E82);
//        context.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFF475E82);
//        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 12, 0xFFFFFFFF);

//        drawSectionDivider(context, panelX + 14, panelY -82, panelWidth - 28);
        drawCodes(context, panelX + 10, panelY - 70);

//        drawSectionDivider(context, panelX + 14, panelY + 76, panelWidth - 28);
//        drawCodes(context, FORMAT_CODES, panelX + 28, panelY + 88, 2, 3, 112, 18);

        super.render(context, mouseX, mouseY, deltaTicks);
    }

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

//    private void drawSectionDivider(DrawContext context, int x, int y, int width) {
//        context.fill(x, y, x + width, y + 1, 0xFFAA2222);
//        context.fill(x, y + 3, x + width, y + 4, 0xFFAA2222);
//    }

    private record ColorCode(String code, Formatting formatting, String color) {
        private Text text() {
            return Text.literal(this.code).append(Text.literal(color).formatted(formatting));
        }
    }
}

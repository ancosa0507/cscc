package org.mixin.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.client.clientsidecolorcodesClient.COLOR_CODE_PATTERN;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {

    @Redirect(
            method = "renderSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"
            )
    )
    private int redirectGetWidth(TextRenderer textRenderer, String text) {
        if (text != null && text.contains("&")) {
            return textRenderer.getWidth(colorizeCodeString(text));
        }
        return textRenderer.getWidth(text);
    }

    @Redirect(
            method = "renderSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"
            )
    )
    private int redirectDrawText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
        if (text != null && text.contains("&")) {
            return context.drawText(textRenderer, colorizeCodeString(text), x, y, color, shadow);
        }
        return context.drawText(textRenderer, text, x, y, color, shadow);
    }

    @Unique
    private static Text colorizeCodeString(String text) {
        MutableText root = Text.literal("");
        Style currentStyle = Style.EMPTY;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '&' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (COLOR_CODE_PATTERN.matcher("&" + next).matches()) {
                    Formatting formatting = Formatting.byCode(next);
                    if (formatting != null) {
                        currentStyle = formatting == Formatting.RESET ? Style.EMPTY : currentStyle.withFormatting(formatting);
                        root.append(Text.literal("&" + next).setStyle(currentStyle));
                        i++;
                        continue;
                    }
                }
                root.append(Text.literal(String.valueOf(c)).setStyle(currentStyle));
            } else {
                root.append(Text.literal(String.valueOf(c)).setStyle(currentStyle));
            }
        }
        return root;
    }
}
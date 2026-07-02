package org.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Wir importieren das Pattern aus deiner Hauptklasse
import static org.client.clientsidecolorcodesClient.COLOR_CODE_PATTERN;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {

    /**
     * Ersetzt die Breitenberechnung des Textes für die zentrierte Ausrichtung (int x1 = -this.font.width(line) / 2;)
     * sowie für die Cursor-Berechnungen in 'extractSignText'.
     */
    @Redirect(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I"
            )
    )
    private int redirectGetWidth(Font font, String str) {
        if (str != null && str.contains("&")) {
            // Wir messen die Breite des farbigen Components
            return font.width(colorizeCodeString(str));
        }
        return font.width(str);
    }

    /**
     * Leitet das eigentliche Zeichnen des Texts um, damit anstelle des rohen Strings
     * unser farbiges Component an den GuiGraphicsExtractor übergeben wird.
     */
    @Redirect(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    // In 26.1.2 nimmt die Methode 'text' den GuiGraphicsExtractor, Font, Component, x, y, color, dropShadow
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"
            )
    )
    private void redirectDrawText(GuiGraphicsExtractor instance, Font font, String str, int x, int y, int color, boolean dropShadow) {
        if (str != null && str.contains("&")) {
            instance.text(font, colorizeCodeString(str), x, y, color, dropShadow);
        } else {
            instance.text(font, str, x, y, color, dropShadow);
        }
    }

    @Unique
    private static Component colorizeCodeString(String text) {
        MutableComponent root = Component.literal("");
        Style currentStyle = Style.EMPTY;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '&' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (COLOR_CODE_PATTERN.matcher("&" + next).matches()) {
                    ChatFormatting formatting = ChatFormatting.getByCode(next);
                    if (formatting != null) {
                        // In 26.1.2 heißt es wieder applyFormatting
                        currentStyle = formatting == ChatFormatting.RESET ? Style.EMPTY : currentStyle.applyFormat(formatting);
                        root.append(Component.literal("&" + next).withStyle(currentStyle));
                        i++;
                        continue;
                    }
                }
                root.append(Component.literal(String.valueOf(c)).withStyle(currentStyle));
            } else {
                root.append(Component.literal(String.valueOf(c)).withStyle(currentStyle));
            }
        }
        return root;
    }
}
package org.mixin.client;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {

    @Shadow
    @Final
    private List<TextFieldWidget.Formatter> formatters;

    @Inject(
            method = "format(Ljava/lang/String;I)Lnet/minecraft/text/OrderedText;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onFormatText(String string, int firstCharacterIndex,
                              CallbackInfoReturnable<OrderedText> cir) {
        if (string == null || !string.contains("&")) return;

        boolean hasValidCode = false;
        for (int j = 0; j < string.length() - 1; j++) {
            if (string.charAt(j) == '&') {
                char code = string.charAt(j + 1);
                if (!Character.isUpperCase(code)
                        && Formatting.byCode(code) != null) {
                    hasValidCode = true;
                    break;
                }
            }
        }
        if (!hasValidCode) return;

        OrderedText originalSequence = null;
        for (TextFieldWidget.Formatter formatter : formatters) {
            OrderedText result = formatter.format(string, firstCharacterIndex);
            if (result != null) {
                originalSequence = result;
                break;
            }
        }
        if (originalSequence == null) {
            originalSequence = OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
        }
        java.util.Map<Integer, Style> originalStyles = new java.util.HashMap<>();
        final int[] idx = {0};
        originalSequence.accept((charIndex, style, codePoint) -> {
            originalStyles.put(idx[0]++, style);
            return true;
        });
        MutableText result = Text.literal("");
        Style currentStyle = Style.EMPTY;
        boolean overriding = false;
        int charPos = 0;
        int i = 0;

        while (i < string.length()) {
            char c = string.charAt(i);

            if (c == '&' && i + 1 < string.length()) {
                char code = string.charAt(i + 1);
                if (!Character.isUpperCase(code)) {
                    Formatting formatting = Formatting.byCode(code);
                    if (formatting != null) {
                        overriding = true;
                        if (formatting.isColor() || formatting == Formatting.RESET) {
                            currentStyle = Style.EMPTY.withFormatting(formatting);
                        } else {
                            currentStyle = currentStyle.withFormatting(formatting);
                        }
                        result.append(Text.literal("&" + code).setStyle(currentStyle));
                        i += 2;
                        continue;
                    }
                }
            }
            Style styleForChar;
            if (overriding) {
                styleForChar = currentStyle;
            } else {
                styleForChar = originalStyles.getOrDefault(charPos, Style.EMPTY);
            }
            result.append(Text.literal(String.valueOf(c)).setStyle(styleForChar));
            charPos++;
            i++;
        }
        cir.setReturnValue(result.asOrderedText());
    }
}

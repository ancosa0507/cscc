package org.mixin.client;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {

    @Redirect(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object redirectRenderTextProvider(BiFunction<String, Integer, OrderedText> instance, Object stringObj, Object indexObj) {
        String string = (String) stringObj;
        int firstCharacterIndex = (Integer) indexObj;
        OrderedText customResult = processFormatting(string, firstCharacterIndex);
        if (customResult != null) {
            return customResult;
        }
        return instance.apply(string, firstCharacterIndex);
    }

    @Unique
    private OrderedText processFormatting(String string, int firstCharacterIndex) {
        if (string == null) return null;

        String fullText = ((TextFieldWidget) (Object) this).getText();
        if (!fullText.contains("&") && !string.contains("&")) return null;

        Style currentStyle = Style.EMPTY;
        int i = 0;
        while (i < firstCharacterIndex && i < fullText.length()) {
            char c = fullText.charAt(i);
            if (c == '&' && i + 1 < fullText.length()) {
                char code = fullText.charAt(i + 1);
                if (!Character.isUpperCase(code)) {
                    Formatting formatting = Formatting.byCode(code);
                    if (formatting != null) {
                        if (formatting.isColor() || formatting == Formatting.RESET) {
                            currentStyle = Style.EMPTY.withFormatting(formatting);
                        } else {
                            currentStyle = currentStyle.withFormatting(formatting);
                        }
                        i += 2;
                        continue;
                    }
                }
            }
            i++;
        }

        MutableText combinedText = Text.empty();
        i = 0;
        while (i < string.length()) {
            char c = string.charAt(i);
            if (c == '&' && i + 1 < string.length()) {
                char code = string.charAt(i + 1);
                if (Character.isUpperCase(code)) {
                    combinedText.append(Text.literal(String.valueOf(c)).setStyle(currentStyle));
                    i++;
                    continue;
                }
                Formatting formatting = Formatting.byCode(code);
                if (formatting != null) {
                    if (formatting.isColor() || formatting == Formatting.RESET) {
                        currentStyle = Style.EMPTY.withFormatting(formatting);
                    } else {
                        currentStyle = currentStyle.withFormatting(formatting);
                    }
                    combinedText.append(Text.literal("&" + code).setStyle(currentStyle));
                    i += 2;
                    continue;
                }
            }
            combinedText.append(Text.literal(String.valueOf(c)).setStyle(currentStyle));
            i++;
        }

        return combinedText.asOrderedText();
    }
}

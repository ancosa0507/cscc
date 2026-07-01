package org.mixin.client;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {

    @Inject(
            method = "format(Ljava/lang/String;I)Lnet/minecraft/text/OrderedText;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onFormatText(String string, int firstCharacterIndex, CallbackInfoReturnable<OrderedText> cir) {
        if (string == null) return;
        String fullText = ((TextFieldWidget)(Object)this).getText();
        if (!fullText.contains("&") && !string.contains("&")) return;
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
        cir.setReturnValue(combinedText.asOrderedText());
    }
}

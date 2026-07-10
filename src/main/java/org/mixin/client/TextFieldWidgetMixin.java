package org.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EditBox.class)
public class TextFieldWidgetMixin {

    @Final
    @Shadow
    private List<EditBox.TextFormatter> formatters;

    @Inject(
            method = "applyFormat",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onFormatText(String string, int firstCharacterIndex,
                              CallbackInfoReturnable<FormattedCharSequence> cir) {
        if (string == null || !string.contains("&")) return;

        boolean hasValidCode = false;
        for (int j = 0; j < string.length() - 1; j++) {
            if (string.charAt(j) == '&') {
                char code = string.charAt(j + 1);
                if (!Character.isUpperCase(code)
                        && ChatFormatting.getByCode(code) != null) {
                    hasValidCode = true;
                    break;
                }
            }
        }
        if (!hasValidCode) return;

        FormattedCharSequence originalSequence = null;
        for (EditBox.TextFormatter formatter : formatters) {
            FormattedCharSequence result = formatter.format(string, firstCharacterIndex);
            if (result != null) {
                originalSequence = result;
                break;
            }
        }
        if (originalSequence == null) {
            originalSequence = FormattedCharSequence.forward(string, Style.EMPTY);
        }
        java.util.Map<Integer, Style> originalStyles = new java.util.HashMap<>();
        final int[] idx = {0};
        originalSequence.accept((charIndex, style, codePoint) -> {
            originalStyles.put(idx[0]++, style);
            return true;
        });
        MutableComponent result = Component.literal("");
        Style currentStyle = Style.EMPTY;
        boolean overriding = false;
        int charPos = 0;
        int i = 0;

        while (i < string.length()) {
            char c = string.charAt(i);

            if (c == '&' && i + 1 < string.length()) {
                char code = string.charAt(i + 1);
                if (!Character.isUpperCase(code)) {
                    ChatFormatting formatting = ChatFormatting.getByCode(code);
                    if (formatting != null) {
                        overriding = true;
                        if (formatting.isColor() || formatting == ChatFormatting.RESET) {
                            currentStyle = Style.EMPTY.applyFormat(formatting);
                        } else {
                            currentStyle = currentStyle.applyFormat(formatting);
                        }
                        result.append(Component.literal("&" + code).withStyle(currentStyle));
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
            result.append(Component.literal(String.valueOf(c)).withStyle(styleForChar));
            charPos++;
            i++;
        }
        cir.setReturnValue(result.getVisualOrderText());
    }
}
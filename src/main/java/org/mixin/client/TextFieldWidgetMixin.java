package org.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class TextFieldWidgetMixin {

    @Inject(
            method = "applyFormat",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onFormatText(String string, int firstCharacterIndex, CallbackInfoReturnable<FormattedCharSequence> cir) {
        if (string == null) return;

        String fullText = ((EditBox) (Object) this).getValue();
        if (!fullText.contains("&") && !string.contains("&")) return;

        Style currentStyle = Style.EMPTY;
        int i = 0;
        while (i < firstCharacterIndex && i < fullText.length()) {
            char c = fullText.charAt(i);
            if (c == '&' && i + 1 < fullText.length()) {
                char code = fullText.charAt(i + 1);
                if (!Character.isUpperCase(code)) {
                    ChatFormatting formatting = ChatFormatting.getByCode(code);
                    if (formatting != null) {
                        if (formatting.isColor() || formatting == ChatFormatting.RESET) {
                            currentStyle = Style.EMPTY.applyFormat(formatting);
                        } else {
                            currentStyle = currentStyle.applyFormat(formatting);
                        }
                        i += 2;
                        continue;
                    }
                }
            }
            i++;
        }

        MutableComponent combinedText = Component.literal("");
        i = 0;
        while (i < string.length()) {
            char c = string.charAt(i);
            if (c == '&' && i + 1 < string.length()) {
                char code = string.charAt(i + 1);
                if (Character.isUpperCase(code)) {
                    combinedText.append(Component.literal(String.valueOf(c)).withStyle(currentStyle));
                    i++;
                    continue;
                }
                ChatFormatting formatting = ChatFormatting.getByCode(code);
                if (formatting != null) {
                    if (formatting.isColor() || formatting == ChatFormatting.RESET) {
                        currentStyle = Style.EMPTY.applyFormat(formatting);
                    } else {
                        currentStyle = currentStyle.applyFormat(formatting);
                    }
                    combinedText.append(Component.literal("&" + code).withStyle(currentStyle));
                    i += 2;
                    continue;
                }
            }
            combinedText.append(Component.literal(String.valueOf(c)).withStyle(currentStyle));
            i++;
        }

        cir.setReturnValue(combinedText.getVisualOrderText());
    }
}
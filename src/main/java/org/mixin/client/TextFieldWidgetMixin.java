package org.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
    private void onFormatText(String text, int offset, CallbackInfoReturnable<FormattedCharSequence> cir) {
        if (text == null) return;
        String fullText = ((EditBox)(Object)this).getValue();
        if (!fullText.contains("&") && !text.contains("&")) return;
        Style currentStyle = Style.EMPTY;
        int i = 0;
        while (i < offset && i < fullText.length()) {
            char c = fullText.charAt(i);
            if (c == '&' && i + 1 < fullText.length()) {
                char code = fullText.charAt(i + 1);
                if (!Character.isUpperCase(code)) {
                    ChatFormatting formatting = ChatFormatting.getByCode(code);
                    if (formatting != null) {
                        if (isColor(formatting) || formatting == ChatFormatting.RESET) {
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
        MutableComponent combinedText = Component.empty();
        i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if (c == '&' && i + 1 < text.length()) {
                char code = text.charAt(i + 1);
                if (Character.isUpperCase(code)) {
                    combinedText.append(Component.literal(String.valueOf(c)).setStyle(currentStyle));
                    i++;
                    continue;
                }
                ChatFormatting formatting = ChatFormatting.getByCode(code);
                if (formatting != null) {
                    if (isColor(formatting) || formatting == ChatFormatting.RESET) {
                        currentStyle = Style.EMPTY.applyFormat(formatting);
                    } else {
                        currentStyle = currentStyle.applyFormat(formatting);
                    }
                    combinedText.append(Component.literal("&" + code).setStyle(currentStyle));
                    i += 2;
                    continue;
                }
            }
            combinedText.append(Component.literal(String.valueOf(c)).setStyle(currentStyle));
            i++;
        }
        cir.setReturnValue(combinedText.getVisualOrderText());
    }
    @Unique
    private static boolean isColor(ChatFormatting formatting) {
        if (formatting == ChatFormatting.RESET) return false;
        return formatting != ChatFormatting.BOLD
                && formatting != ChatFormatting.ITALIC
                && formatting != ChatFormatting.UNDERLINE
                && formatting != ChatFormatting.STRIKETHROUGH
                && formatting != ChatFormatting.OBFUSCATED;
    }
}
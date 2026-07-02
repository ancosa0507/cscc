package org.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.client.clientsidecolorcodesClient.COLOR_CODE_PATTERN;

@Mixin(ChatComponent.class)
public class ChatHudMixin {

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Component convertColorCodesInChatHud(Component text) {
        if (text == null || !text.getString().contains("&")) return text;
        return processText(text);
    }

    @Unique
    private Component processText(Component text) {
        if (text == null) return null;
        Style style = text.getStyle();
        MutableComponent result;

        if (text.getContents() instanceof PlainTextContents plain) {
            String raw = plain.text();
            if (raw.contains("&")) {
                String converted = COLOR_CODE_PATTERN.matcher(raw).replaceAll("§$1");
                result = parseLegacyString(converted, style);
            } else {
                result = text.copy().setStyle(style);
            }
        } else if (text.getContents() instanceof TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Component argText) {
                    newArgs[i] = processText(argText);
                } else {
                    newArgs[i] = args[i];
                }
            }
            result = MutableComponent.create(new TranslatableContents(
                    translatable.getKey(),
                    translatable.getFallback(),
                    newArgs
            )).setStyle(style);
        } else {
            result = text.copy().setStyle(style);
        }

        for (Component sibling : text.getSiblings()) {
            result.append(processText(sibling));
        }
        return result;
    }

    @Unique
    private static MutableComponent parseLegacyString(String text, Style style) {
        MutableComponent root = MutableComponent.create(PlainTextContents.EMPTY).setStyle(style);
        Style currentStyle = style;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' && i + 1 < text.length()) {
                if (!current.isEmpty()) {
                    root.append(Component.literal(current.toString()).setStyle(currentStyle));
                    current.setLength(0);
                }
                char code = Character.toLowerCase(text.charAt(i + 1));
                ChatFormatting formatting = ChatFormatting.getByCode(code);
                if (formatting != null) {
                    currentStyle = formatting == ChatFormatting.RESET
                            ? style
                            : currentStyle.applyFormat(formatting);
                }
                i++;
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) {
            root.append(Component.literal(current.toString()).setStyle(currentStyle));
        }
        return root;
    }
}
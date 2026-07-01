package org.mixin.client;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.client.clientsidecolorcodesClient.COLOR_CODE_PATTERN;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text convertColorCodesInChatHud(Text text) {
        if (text == null || !text.getString().contains("&")) return text;
        return processText(text);
    }

    @Unique
    private Text processText(Text text) {
        if (text == null) return null;
        Style style = text.getStyle();
        MutableText result;
        if (text.getContent() instanceof PlainTextContent plain) {
            String raw = plain.string();
            if (raw.contains("&")) {
                String converted = COLOR_CODE_PATTERN.matcher(raw).replaceAll("§$1");
                result = parseLegacyString(converted, style);
            } else {
                result = text.copyContentOnly().setStyle(style);
            }
        } else if (text.getContent() instanceof TranslatableTextContent translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Text argText) {
                    newArgs[i] = processText(argText);
                } else {
                    newArgs[i] = args[i];
                }
            }
            result = MutableText.of(new TranslatableTextContent(
                    translatable.getKey(),
                    translatable.getFallback(),
                    newArgs
            )).setStyle(style);
        } else {
            result = text.copyContentOnly().setStyle(style);
        }
        for (Text sibling : text.getSiblings()) {
            result.append(processText(sibling));
        }
        return result;
    }

    @Unique
    private static MutableText parseLegacyString(String text, Style style) {
        MutableText root = MutableText.of(PlainTextContent.EMPTY).setStyle(style);
        Style currentStyle = style;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' && i + 1 < text.length()) {
                if (!current.isEmpty()) {
                    root.append(MutableText.of(new PlainTextContent.Literal(current.toString())).setStyle(currentStyle));
                    current.setLength(0);
                }
                char code = Character.toLowerCase(text.charAt(i + 1));
                Formatting formatting = Formatting.byCode(code);
                if (formatting != null) {
                    currentStyle = formatting == Formatting.RESET
                            ? style
                            : currentStyle.withFormatting(formatting);
                }
                i++;
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) {
            root.append(MutableText.of(new PlainTextContent.Literal(current.toString())).setStyle(currentStyle));
        }
        return root;
    }
}

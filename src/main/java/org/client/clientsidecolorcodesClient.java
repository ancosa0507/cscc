package org.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clientsidecolorcodesClient implements ClientModInitializer {
    public static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fk-or])");

    @Override
    public void onInitializeClient() {
            ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
                if (lines.isEmpty()) return;
                String rawName = lines.getFirst().getString();
                if (rawName.contains("&")) {
                    Matcher matcher = COLOR_CODE_PATTERN.matcher(rawName);
                    if (matcher.find()) {
                        rawName = Text.literal(matcher.replaceAll("§$1")).getString();
                    }
                    lines.set(0, Text.literal(rawName));
                }
            });
        }
    public static Text convertColorCodes(Text original) {
        String raw = original.getString();
        Matcher matcher = COLOR_CODE_PATTERN.matcher(raw);
        if (matcher.find()) {
            return Text.literal(matcher.replaceAll("§$1"));
        }
        return original;
    }
}

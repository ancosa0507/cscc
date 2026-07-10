package org.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clientsidecolorcodesClient implements ClientModInitializer {
    public static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fk-or])");
    public static int pendingMenuOpenTicks = 0;
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("cscc")
                    .executes(ctx->{
                        pendingMenuOpenTicks = 2;
                        return 1;
                    }));
        });
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
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pendingMenuOpenTicks > 0) {
                pendingMenuOpenTicks--;
                if (pendingMenuOpenTicks == 0) {
                    client.setScreen(new ColorMenuScreen());
                }
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

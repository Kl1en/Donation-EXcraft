package com.example.donationexcraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DonationHandler {

    private final JavaPlugin plugin;

    public DonationHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleDonation(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            double diamondThreshold = plugin.getConfig().getDouble("donation_thresholds.diamond", 10.0);
            double goldThreshold = plugin.getConfig().getDouble("donation_thresholds.gold", 5.0);
            double ironThreshold = plugin.getConfig().getDouble("donation_thresholds.iron", 2.0);

            if (amount >= diamondThreshold) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                player.sendMessage("Вы получили алмаз за ваш донат!");
            } else if (amount >= goldThreshold) {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
                player.sendMessage("Вы получили золотой слиток за ваш донат!");
            } else if (amount >= ironThreshold) {
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
                player.sendMessage("Вы получили железный слиток за ваш донат!");
            } else {
                player.sendMessage("Спасибо за ваш донат!");
            }
        }
    }
}

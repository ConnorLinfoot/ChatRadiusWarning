package com.connorlinfoot.chatradiuswarning;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatRadiusWarning extends JavaPlugin implements Listener {
	Plugin essentialsPlugin;
	File essFile = null;
	YamlConfiguration essConfig = null;
	int radius = 100;

	public void onEnable() {
		essentialsPlugin = getServer().getPluginManager().getPlugin("Essentials");
		if (essentialsPlugin == null) {
			getLogger().severe("Essentials not found disabling plugin!");
			getServer().getPluginManager().disablePlugin(this);
		} else {
			saveDefaultConfig();

			try {
				essFile = new File(this.essentialsPlugin.getDataFolder() + File.separator + "config.yml");
				essConfig = new YamlConfiguration();
				essConfig.load(this.essFile);
				radius = this.essConfig.getInt("chat.radius");
			} catch (IOException var2) {
				getLogger().severe("Could not read Essentials config!");
			} catch (InvalidConfigurationException var3) {
				getLogger().severe("Essentials config was invalid!");
			}

			getServer().getPluginManager().registerEvents(this, this);
			if (!getConfig().contains("madness")) {
				getConfig().set("show_madness_if_only_one_online", true);
				getConfig().set("madness", "&c[ChatRadiusWarning]&r Talking to yourself is the first sign of madness");
				saveConfig();
			}
		}

	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		int num = this.getServer().getOnlinePlayers().size();
		Player player = event.getPlayer();
		if (!player.hasPermission("crw.bypass") && this.radius > 0 && num > 1) {
			int count = 0;
			List entitylist = player.getNearbyEntities((double) this.radius, (double) this.radius, (double) this.radius);
			if (entitylist.contains(player)) {
				entitylist.remove(player);
			}

			for (Object anEntitylist : entitylist) {
				Entity e = (Entity) anEntitylist;
				if (e.getType() == EntityType.PLAYER && !e.hasMetadata("NPC")) {
					++count;
				}
			}

			if (count == 0) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', (this.getConfig().getString("message"))));
			}
		} else if (num == 1 && this.getConfig().getBoolean("show_madness_if_only_one_online")) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("madness")));
		}

	}

}

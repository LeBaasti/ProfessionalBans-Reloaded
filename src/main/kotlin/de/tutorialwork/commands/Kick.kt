package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.io.File
import java.io.IOException

class Kick(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.kick") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty() || args.size == 1) {
                    sender.sendMessage(Main.prefix + "/kick <Spieler> <Grund>")
                } else {
                    val tokick = ProxyServer.getInstance().getPlayer(args[0])
                    if (tokick != null) {
                        val config = File(Main.instance.dataFolder, "config.yml")
                        try {
                            val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                            var grund = ""
                            for (i in 1 until args.size) {
                                grund = grund + " " + args[i]
                            }
                            BanManager.sendNotify("KICK", tokick.name, sender.name, grund)
                            LogManager.createEntry(tokick.uniqueId.toString(), sender.uniqueId.toString(), "KICK", grund)
                            tokick.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK")
                                    .replace("%grund%", grund)))
                            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    } else {
                        sender.sendMessage(Main.prefix + "§cDieser Spieler ist nicht online")
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty() || args.size == 1) {
                console.sendMessage(Main.prefix + "/kick <Spieler> <Grund>")
            } else {
                val tokick = ProxyServer.getInstance().getPlayer(args[0])
                if (tokick != null) {
                    val config = File(Main.instance.dataFolder, "config.yml")
                    try {
                        val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                        var grund = ""
                        for (i in 1 until args.size) {
                            grund = grund + " " + args[i]
                        }
                        BanManager.sendNotify("KICK", tokick.name, "KONSOLE", grund)
                        LogManager.createEntry(tokick.uniqueId.toString(), "KONSOLE", "KICK", grund)
                        tokick.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK")
                                .replace("%grund%", grund)))
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    console.sendMessage(Main.prefix + "§cDieser Spieler ist nicht online")
                }
            }
        }
    }
}
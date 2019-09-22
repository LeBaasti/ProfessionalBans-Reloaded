package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.consoleName
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.ActionType
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.sendNotify
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
                    sender.sendMessage("$prefix/kick <Spieler> <Grund>")
                } else {
                    val tokick = ProxyServer.getInstance().getPlayer(args[0])
                    if (tokick != null) {
                        val config = File(Main.instance.dataFolder, "configFile.yml")
                        try {
                            val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                            var grund = ""
                            for (i in 1 until args.size) {
                                grund = grund + " " + args[i]
                            }
                            ActionType.Kick(grund).sendNotify(tokick.name, sender.name)
                            LogManager.createEntry(tokick.uniqueId.toString(), sender.uniqueId.toString(), ActionType.Kick(grund), grund)
                            tokick.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK")
                                    .replace("%grund%", grund)))
                            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    } else {
                        sender.sendMessage(prefix + "§cDieser Spieler ist nicht online")
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty() || args.size == 1) {
                console.sendMessage(prefix + "/kick <Spieler> <Grund>")
            } else {
                val tokick = ProxyServer.getInstance().getPlayer(args[0])
                if (tokick != null) {
                    val config = File(Main.instance.dataFolder, "configFile.yml")
                    try {
                        val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                        var grund = ""
                        for (i in 1 until args.size) {
                            grund = grund + " " + args[i]
                        }
                        ActionType.Kick(grund).sendNotify(tokick.name, consoleName)
                        LogManager.createEntry(tokick.uniqueId.toString(), consoleName, ActionType.Kick, grund)
                        tokick.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK")
                                .replace("%grund%", grund)))
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    console.sendMessage("$prefix§cDieser Spieler ist nicht online")
                }
            }
        }
    }
}
package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.LogManager
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.*

class Blacklist(name: String = Blacklist::class.java.simpleName) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.blacklist") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty() || args.size == 1) {
                    sender.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                    sender.msg(prefix + "/blacklist <add/del> <Wort>")
                } else if (args.size == 2) {
                    val blacklist = File(Main.instance.dataFolder, "blacklist.yml")
                    try {
                        val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(blacklist)

                        val tempblacklist = ArrayList<String>()

                        if (args[0].equals("add", ignoreCase = true)) {
                            val word = args[1]
                            for (congigstr in cfg.getStringList("BLACKLIST")) {
                                tempblacklist.add(congigstr)
                            }
                            tempblacklist.add(word)
                            Main.blacklist.add(word)
                            cfg.set("BLACKLIST", tempblacklist)
                            sender.msg(prefix + "§e§l" + word + " §7wurde zur Blacklist hinzugefügt")
                            LogManager.createEntry("", sender.uniqueId.toString(), "ADD_WORD_BLACKLIST", word)
                        } else if (args[0].equals("del", ignoreCase = true)) {
                            val word = args[1]
                            if (Main.blacklist.contains(word)) {
                                for (congigstr in cfg.getStringList("BLACKLIST")) {
                                    tempblacklist.add(congigstr)
                                }
                                tempblacklist.remove(word)
                                Main.blacklist.remove(word)
                                cfg.set("BLACKLIST", tempblacklist)
                                sender.msg(prefix + "§e§l" + word + " §7wurde von der Blacklist entfernt")
                                LogManager.createEntry("", sender.uniqueId.toString(), "DEL_WORD_BLACKLIST", word)
                            } else {
                                sender.msg(prefix + "§cDieses Wort steht nicht auf der Blacklist")
                            }
                        } else {
                            sender.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                            sender.msg(prefix + "/blacklist <add/del> <Wort>")
                        }

                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(cfg, blacklist)
                        tempblacklist.clear()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    sender.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                    sender.msg(prefix + "/blacklist <add/del> <Wort>")
                }
            } else {
                sender.msg(Main.noPerms)
            }
        } else {
            //KONSOLE
            if (args.isEmpty() || args.size == 1) {
                console.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                console.msg(prefix + "/blacklist <add/del> <Wort>")
            } else if (args.size == 2) {
                val blacklist = File(Main.instance.dataFolder, "blacklist.yml")
                val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(blacklist)

                val tempblacklist = ArrayList<String>()

                if (args[0].equals("add", ignoreCase = true)) {
                    val Wort = args[1]
                    for (congigstr in cfg.getStringList("BLACKLIST")) {
                        tempblacklist.add(congigstr)
                    }
                    tempblacklist.add(Wort)
                    Main.blacklist.add(Wort)
                    cfg.set("BLACKLIST", tempblacklist)
                    console.msg(prefix + "§e§l" + Wort + " §7wurde zur Blacklist hinzugefügt")
                    LogManager.createEntry("", "KONSOLE", "ADD_WORD_BLACKLIST", Wort)
                } else if (args[0].equals("del", ignoreCase = true)) {
                    val Wort = args[1]
                    if (Main.blacklist.contains(Wort)) {
                        for (congigstr in cfg.getStringList("BLACKLIST")) {
                            tempblacklist.add(congigstr)
                        }
                        tempblacklist.remove(Wort)
                        Main.blacklist.remove(Wort)
                        cfg.set("BLACKLIST", tempblacklist)
                        console.msg(prefix + "§e§l" + Wort + " §7wurde von der Blacklist entfernt")
                        LogManager.createEntry("", "KONSOLE", "DEL_WORD_BLACKLIST", Wort)
                    } else {
                        console.msg(prefix + "§cDieses Wort steht nicht auf der Blacklist")
                    }
                } else {
                    console.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                    console.msg(prefix + "/blacklist <add/del> <Wort>")
                }

                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(cfg, blacklist)
                tempblacklist.clear()

            } else {
                console.msg(prefix + "Derzeit sind §e§l" + Main.blacklist.size + " Wörter §7auf der Blacklist")
                console.msg(prefix + "/blacklist <add/del> <Wort>")
            }
        }
    }


}
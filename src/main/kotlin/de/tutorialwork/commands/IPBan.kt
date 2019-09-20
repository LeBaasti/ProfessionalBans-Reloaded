package de.tutorialwork.commands

import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.IPManager
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.UUIDFetcher
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.io.File
import java.io.IOException
import java.util.regex.Pattern

class IPBan(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.ipban") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty() || args.size == 1) {
                    for (zaehler in 1 until BanManager.countReasons()!! + 1) {
                        if (BanManager.isBanReason(zaehler)) {
                            sender.sendMessage("§7" + zaehler + " §8| §e" + BanManager.getReasonByID(zaehler))
                        }
                    }
                    sender.sendMessage(Main.prefix + "/ipban <IP/Spieler> <Grund-ID>")
                } else {
                    val ip = args[0]
                    val id = Integer.valueOf(args[1])
                    if (validate(ip)) {
                        if (IPManager.ipExists(ip)) {
                            IPManager.ban(ip, id, sender.uniqueId.toString())
                            IPManager.addBan(ip)
                            BanManager.sendNotify("IPBAN", ip, sender.name, BanManager.getReasonByID(id).toString())
                        } else {
                            IPManager.insertIP(ip, sender.uniqueId)
                            IPManager.ban(ip, id, sender.uniqueId.toString())
                            IPManager.addBan(ip)
                            BanManager.sendNotify("IPBAN", ip, sender.name, BanManager.getReasonByID(id).toString())
                        }
                        disconnectIPBannedPlayers(ip)
                        LogManager.createEntry("", sender.uniqueId.toString(), "IPBAN_IP", ip)
                    } else {
                        val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                        val dbip = IPManager.getIPFromPlayer(uuid)
                        IPManager.ban(dbip, id, sender.uniqueId.toString())
                        IPManager.addBan(dbip)
                        BanManager.sendNotify("IPBAN", dbip, sender.name, BanManager.getReasonByID(id).toString())
                        disconnectIPBannedPlayers(dbip)
                        LogManager.createEntry("", sender.uniqueId.toString(), "IPBAN_PLAYER", id.toString())
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        }
    }

    companion object {

        fun disconnectIPBannedPlayers(IP: String) {
            for (all in Main.instance.proxy.players) {
                if (all.address.hostString == IP) {
                    val config = File(Main.instance.dataFolder, "config.yml")
                    try {
                        val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)

                        if (IPManager.getRAWEnd(IP) == -1L) {
                            all.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.IPBAN").replace("%grund%", IPManager.getReasonString(IP)!!)))
                        } else {
                            if (System.currentTimeMillis() < IPManager.getRAWEnd(IP) ?: 0) {
                                var MSG = configcfg.getString("LAYOUT.TEMPIPBAN")
                                MSG = MSG.replace("%grund%", IPManager.getReasonString(IP)!!)
                                MSG = MSG.replace("%dauer%", IPManager.getEnd(IP))
                                all.disconnect(ChatColor.translateAlternateColorCodes('&', MSG))
                            } else {
                                IPManager.unban(IP)
                            }
                        }

                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                    } catch (e2: IOException) {
                        e2.printStackTrace()
                    }

                }
            }
        }

        private val PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")

        fun validate(ip: String): Boolean {
            return PATTERN.matcher(ip).matches()
        }
    }
}
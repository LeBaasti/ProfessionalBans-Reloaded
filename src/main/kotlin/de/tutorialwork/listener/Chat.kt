package de.tutorialwork.listener

import de.tutorialwork.global.*
import de.tutorialwork.sql.Chat
import de.tutorialwork.utils.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object Chat : Listener {

	@EventHandler
	fun onChat(event: ChatEvent) {
		if (event.message.startsWith("/")) return
		val player = event.sender as? ProxiedPlayer ?: return
		if (player in activeChats) {
			event.isCancelled = true
			val target = activeChats[player] ?: return
			val prefix = "§9§lSUPPORT §8• §c"
			target.msg(
					"$prefix§c${player.name} §8» ${event.message}",
					"$prefix§aDu §8» ${event.message}"
			)
			activeChats.keys.forEach { it.msg("$prefix§c${player.name} §8» ${event.message}") }
		}
		val uuid = player.uniqueId
		if (uuid.isMuted) {
			if (System.currentTimeMillis() >= uuid.rawEnd) {
				uuid.unMute()
				return
			}
			event.isCancelled = true
			player.sendMute()
		} else {
			insertMessage(uuid, event.message, player.server.info.name)
			if (player.hasPermission("${permissionPrefix}blacklist.bypass")) return
			val predicate: (String) -> Boolean = { it.toUpperCase() in event.message.toUpperCase() }
			when {
				config.getBoolean("AUTOMUTE.ENABLED") -> {
					val a = when {
						blacklist.any(predicate) -> ""
						adBlackList.any(predicate) && event.message.toUpperCase() !in adWhiteList -> "AD"
						else -> return
					}
					uuid.createLogEntry(consoleName, ActionType.Blacklist("AUTOMUTE_${a}BLACKLIST"))
					ActionType.Mute(config.getInt("AUTOMUTE.${a}MUTEID")).run {
						execute(uuid, consoleName)
						sendNotify(uuid.name, consoleName)
					}
				}
				config.getBoolean("AUTOMUTE.AUTOREPORT") -> {
					val reason: String = when {
						blacklist.any(predicate) -> {
							player.msg("${prefix}§cAchte auf deine Wortwahl")
							"VERHALTEN"
						}
						adBlackList.any(predicate) && event.message.toUpperCase() !in adWhiteList -> {
							player.msg("${prefix}§cDu darfst keine Werbung machen")
							"WERBUNG"
						}
						else -> return
					}
					val logId = createChatLog(uuid, consoleName)
					uuid.createReport(consoleName, reason, logId)
					ActionType.Report(reason).sendNotify(player.name, consoleName)
				}
				else -> return
			}
			event.isCancelled = true
		}
	}


	private fun insertMessage(uuid: UUID, message: String, server: String) {
		Chat.insert {
			it[this.uuid] = uuid.toString()
			it[this.server] = server
			it[this.message] = message
			it[this.sendDate] = System.currentTimeMillis()
		}
	}

	fun createChatLog(uuid: UUID, createdUUID: String): String {
		val query = Chat.getByUUID(uuid)
		val id = 20.randomString()
		val now = System.currentTimeMillis()
		val tenMinutes = 10 * 60 * 1000
		query.forEach {
			val tenAgo = System.currentTimeMillis() - tenMinutes
			val sendDate = it[Chat.sendDate]
			if (sendDate > tenAgo) {
				mysql.update("""INSERT INTO chatlog(LOGID, uuid, CREATOR_UUID, SERVER, MESSAGE, SENDDATE, CREATED_AT) VALUES ('$id' 
                            |,'$uuid', '$createdUUID', '${it[Chat.server]}', '${it[Chat.message]}'
                            |, '${sendDate}', '$now')""".trimMargin())
			}
		}
		return id
	}

	fun hasMessages(uuid: UUID): Boolean = Chat.getByUUID(uuid).any()

	private fun Chat.getByUUID(uuid: UUID) = select { Chat.uuid.eq(uuid.toString()) }

}
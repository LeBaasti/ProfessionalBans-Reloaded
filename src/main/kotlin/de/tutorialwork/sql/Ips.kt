package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:47.
 * @author Lars Artmann | LartyHD
 */
object Ips : Table() {

	var ip = varchar("ip", 15).primaryKey()
	val usedBy = varchar("used_by", 64)
	val usedAt = varchar("used_at", 64)
	val banned = integer("banned")
	val reason = integer("reason")
	val end = integer("end")
	val timeUuid = varchar("time_uuid", 64)
	val bans = integer("bans")

}
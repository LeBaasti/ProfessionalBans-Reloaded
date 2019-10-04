package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:47.
 * @author Lars Artmann | LartyHD
 */
object Bans : Table() {

	var uuid = varchar("uuid", 36).primaryKey()
	var name = varchar("username", 16)
	var banned = integer("banned")
	var muted = integer("muted")
	var reason = varchar("reason", 64)
	var end = long("end")
	var teamUuid = varchar("team_uuid", 64)
	var bans = integer("bans")
	var mutes = integer("mutes")
	var firstLogin = varchar("first_login", 255)
	var lastLogin = varchar("last_login", 255)

}
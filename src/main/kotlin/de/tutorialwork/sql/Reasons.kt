package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:46.
 * @author Lars Artmann | LartyHD
 */
object Reasons : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	val reason = varchar("", 255)
	val time = integer("time")
	val type = integer("type")
	val addedAt = varchar("added_at", 11)
	val bans = integer("bans")
	val perms = varchar("perms", 255)
	val sortIndex = integer("sort_index")

}
package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:47.
 * @author Lars Artmann | LartyHD
 */
object Reports : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	var uuid = varchar("uuid", 36)
	var reporter = varchar("reporter", 36)
	var team = varchar("team", 36)
	var reason = varchar("reason", 64)
	var log = varchar("log", 64)
	var status = integer("status")
	var createdAt = long("created_at")
	
}
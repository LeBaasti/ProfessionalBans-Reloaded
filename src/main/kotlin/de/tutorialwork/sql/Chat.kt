package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

object Chat : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	var uuid = varchar("uuid", 36)
	var server = varchar("server", 64)
	var message = varchar("message", 2500)
	var sendDate = long("send_date")

}
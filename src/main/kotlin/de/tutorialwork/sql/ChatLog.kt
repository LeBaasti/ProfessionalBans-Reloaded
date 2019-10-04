package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:47.
 * @author Lars Artmann | LartyHD
 */
object ChatLog : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	val logId = varchar("log_id", 255)
	val uuid = varchar("uuid", 36)
	val creatorUuid = varchar("creator_uuid", 36)
	var server = varchar("server", 64)
	var message = varchar("message", 2500)
	var sendDate = long("send_date")
	var createdAt = Reports.long("created_at")

}
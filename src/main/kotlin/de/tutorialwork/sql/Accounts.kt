package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:46.
 * @author Lars Artmann | LartyHD
 */
object Accounts : Table() {
	var uuid = varchar("uuid", 36).primaryKey()
	var username = varchar("username", 16)
	var password = varchar("password", 255)
	var rank = integer("rank")
	var googleAuth = varchar("google_auth", 255)
	var authCode = varchar("auth_code", 255)
	var authStatus = integer("auth_status")
}
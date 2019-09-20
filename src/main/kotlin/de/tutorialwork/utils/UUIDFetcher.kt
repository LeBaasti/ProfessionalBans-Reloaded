package de.tutorialwork.utils

import com.google.gson.JsonParser
import java.io.IOException
import java.net.URL
import java.util.*

object UUIDFetcher {
    private val uuidCache = mutableMapOf<String, UUID>()

    fun getUUID(username: String): UUID? {
        if (uuidCache.containsKey(username)) return uuidCache[username]
        try {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
            val stream = url.openStream()

            val result = stream.reader().readText()


            val element = JsonParser().parse(result)
            val obj = element.asJsonObject
            var api = obj.get("id").toString()
                    .substring(1)
            api = api.substring(0, api.length - 1)
            val sbu = StringBuffer(api)
            sbu.insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-")
            val uuid = UUID.fromString(sbu.toString())
            uuidCache[username] = uuid
            return uuid
        } catch (localIOException: IOException) {
        } catch (localIOException: IllegalStateException) {
        }

        return null
    }
}
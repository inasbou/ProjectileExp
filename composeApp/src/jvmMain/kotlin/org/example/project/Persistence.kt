package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class StoredState(
    val masse: String,
    val gravite: String,
    val vitesse0: String,
    val alpha0: String
)

object Persistence {

    private val file = File("projectile_state.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun save(state: StoredState) {
        file.writeText(json.encodeToString(StoredState.serializer(), state))
    }

    fun load(): StoredState? {
        if (!file.exists()) return null
        return try {
            json.decodeFromString(StoredState.serializer(), file.readText())
        } catch (e: Exception) {
            null
        }
    }
}

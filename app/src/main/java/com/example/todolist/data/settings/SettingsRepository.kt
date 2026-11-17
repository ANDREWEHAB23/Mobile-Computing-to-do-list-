package com.example.todolist.data.settings

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

enum class SortOrder { DATE, PRIORITY }

data class UserSettings(
    val darkMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.DATE
)

//just one datastore instance per app
private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }

    val settings: Flow<UserSettings> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            UserSettings(
                darkMode = prefs[Keys.DARK_MODE] ?: false,
                sortOrder = prefs[Keys.SORT_ORDER]
                    ?.let { runCatching { SortOrder.valueOf(it) }.getOrNull() }
                    ?: SortOrder.DATE
            )
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }
}
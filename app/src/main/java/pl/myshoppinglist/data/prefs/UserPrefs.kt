package pl.myshoppinglist.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

object UserPrefsKeys { val DARK_MODE = booleanPreferencesKey("dark_mode") }

class UserPrefs(private val context: Context) {
    val isDark = context.dataStore.data.map { it[UserPrefsKeys.DARK_MODE] ?: false }
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[UserPrefsKeys.DARK_MODE] = enabled }
    }
}

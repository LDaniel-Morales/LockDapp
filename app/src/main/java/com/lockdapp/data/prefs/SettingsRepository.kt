package com.lockdapp.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lockdapp.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lockdapp_settings"
)

private object Keys {
    val ACTIVE_THEME       = stringPreferencesKey("active_theme")
    val ESCAPE_ENABLED     = booleanPreferencesKey("escape_enabled")
    val ESCAPE_DELAY_SECS  = intPreferencesKey("escape_delay_seconds")
    val FIRST_RUN_DONE     = booleanPreferencesKey("first_run_done")
}

class SettingsRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    val activeTheme: Flow<AppTheme> = store.data.map { prefs ->
        AppTheme.valueOf(prefs[Keys.ACTIVE_THEME] ?: AppTheme.MASTIL.name)
    }

    val escapeEnabled: Flow<Boolean> = store.data.map { prefs ->
        prefs[Keys.ESCAPE_ENABLED] ?: true
    }

    val escapeDelaySeconds: Flow<Int> = store.data.map { prefs ->
        prefs[Keys.ESCAPE_DELAY_SECS] ?: 30
    }

    val isFirstRunDone: Flow<Boolean> = store.data.map { prefs ->
        prefs[Keys.FIRST_RUN_DONE] ?: false
    }

    suspend fun setActiveTheme(theme: AppTheme) {
        store.edit { it[Keys.ACTIVE_THEME] = theme.name }
    }

    suspend fun setEscapeEnabled(enabled: Boolean) {
        store.edit { it[Keys.ESCAPE_ENABLED] = enabled }
    }

    suspend fun setEscapeDelaySeconds(seconds: Int) {
        store.edit { it[Keys.ESCAPE_DELAY_SECS] = seconds }
    }

    suspend fun markFirstRunDone() {
        store.edit { it[Keys.FIRST_RUN_DONE] = true }
    }
}

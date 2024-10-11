package com.example.finance.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeywordsDataStore(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keywords_prefs")

    companion object {
        val KEYWORDS_KEY = stringSetPreferencesKey("keywords")
    }

    val keywordsFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[KEYWORDS_KEY] ?: emptySet()
        }

    suspend fun saveKeywords(keywords: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[KEYWORDS_KEY] = keywords
        }
    }
}

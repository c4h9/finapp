package com.example.finance.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeywordsDataStore private constructor(context: Context) {

    // Singleton instance of DataStore
    private val dataStore: DataStore<Preferences> = context.applicationContext.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keywords_prefs")
        val KEYWORDS_KEY = stringSetPreferencesKey("keywords")

        @Volatile
        private var INSTANCE: KeywordsDataStore? = null

        fun getInstance(context: Context): KeywordsDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KeywordsDataStore(context).also { INSTANCE = it }
            }
        }
    }

    val keywordsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[KEYWORDS_KEY] ?: emptySet()
        }

    suspend fun saveKeywords(keywords: Set<String>) {
        dataStore.edit { preferences ->
            preferences[KEYWORDS_KEY] = keywords
        }
    }
}
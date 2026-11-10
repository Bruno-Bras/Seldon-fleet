package br.com.seldonfleet.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.seldonfleet.domain.FavoritesRepository
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

class FavoritesStore(private val context: Context) : FavoritesRepository {
    private val key = stringSetPreferencesKey("device_ids")
    override val favorites = context.favoritesDataStore.data.map { it[key] ?: emptySet() }

    override suspend fun toggle(id: String) {
        context.favoritesDataStore.edit { preferences ->
            val current = preferences[key] ?: emptySet()
            preferences[key] = if (id in current) current - id else current + id
        }
    }
}

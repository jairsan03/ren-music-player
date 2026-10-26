package com.dn0ne.player.core.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.miniPlayerDataStore by preferencesDataStore(name = "mini-player-settings")

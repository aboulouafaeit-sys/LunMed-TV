package com.lunmed.tv.core.ui.text

import androidx.annotation.StringRes
import com.lunmed.tv.core.ui.R

/**
 * Single source of truth for the category vocabulary shared by Home, App Library and Settings.
 *
 * Category ids are persisted in the database, so they must stay stable ASCII keys; only the
 * human-readable label is localised here and resolved with `stringResource` at render time.
 */
object LunmedCategories {

    const val ALL = "all"
    const val FAVORITES = "favorites"
    const val MEDIA = "media"
    const val GAMES = "games"
    const val UTILITIES = "utilities"
    const val SYSTEM = "system"
    const val OTHER = "other"
    const val HIDDEN = "hidden"

    @StringRes
    fun labelRes(categoryId: String?): Int = when (categoryId) {
        null, ALL -> R.string.cat_all
        FAVORITES -> R.string.cat_favorites
        HIDDEN -> R.string.cat_hidden
        MEDIA -> R.string.cat_media
        GAMES -> R.string.cat_games
        UTILITIES -> R.string.cat_tools
        SYSTEM -> R.string.cat_system
        else -> R.string.cat_other
    }
}

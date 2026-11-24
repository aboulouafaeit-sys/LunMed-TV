package com.lunmed.tv.feature.apps.model

import androidx.annotation.StringRes
import com.lunmed.tv.core.ui.R

enum class AppSortOrder(@StringRes val displayNameRes: Int) {
    NAME_ASC(R.string.sort_name_asc),
    NAME_DESC(R.string.sort_name_desc),
    MOST_USED(R.string.sort_most_used),
    RECENTLY_USED(R.string.sort_recently_used),
    CUSTOM(R.string.sort_custom),
}

data class CategoryFilterItem(
    val id: String,
    @StringRes val titleRes: Int,
    val count: Int = 0,
)



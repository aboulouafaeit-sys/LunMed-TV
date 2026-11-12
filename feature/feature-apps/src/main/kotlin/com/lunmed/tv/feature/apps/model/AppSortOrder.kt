package com.lunmed.tv.feature.apps.model

enum class AppSortOrder(val displayName: String) {
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)"),
    MOST_USED("Most Used"),
    RECENTLY_USED("Recently Used"),
    CUSTOM("Custom Order"),
}

data class CategoryFilterItem(
    val id: String,
    val title: String,
    val count: Int = 0,
)


package com.lunmed.tv.core.navigation.focus

/**
 * Pure, deterministic focus memory and overlay-restoration stack.
 * Remembers the last focused item per section/rail so vertical D-pad
 * navigation and modal dismissal never lose the user's place.
 */
class FocusMemoryRegistry {

    data class FocusAnchor(
        val sectionId: String,
        val itemId: String,
    )

    private val sectionMemory = LinkedHashMap<String, String>()
    private val overlayStack = ArrayDeque<FocusAnchor>()

    var activeSection: String? = null
        private set

    fun recordFocus(sectionId: String, itemId: String) {
        activeSection = sectionId
        sectionMemory[sectionId] = itemId
    }

    fun getLastFocusedItem(sectionId: String): String? = sectionMemory[sectionId]

    fun getActiveAnchor(): FocusAnchor? {
        val section = activeSection ?: return null
        val item = sectionMemory[section] ?: return null
        return FocusAnchor(section, item)
    }

    fun pushOverlayAnchor() {
        getActiveAnchor()?.let { overlayStack.addLast(it) }
    }

    fun popOverlayAnchor(): FocusAnchor? =
        if (overlayStack.isEmpty()) null else overlayStack.removeLast()

    fun pruneMissingItems(sectionId: String, validItemIds: Set<String>) {
        val current = sectionMemory[sectionId]
        if (current != null && current !in validItemIds) {
            sectionMemory.remove(sectionId)
        }
    }

    fun clear() {
        sectionMemory.clear()
        overlayStack.clear()
        activeSection = null
    }
}


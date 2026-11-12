package com.lunmed.tv.core.ui.artwork

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppArtwork(
    val bitmap: ImageBitmap,
    val isBanner: Boolean,
)

/**
 * Memory-bounded LRU cache for TV app banners and launcher icons.
 */
object AppArtworkCache {
    private const val MAX_ENTRIES = 96
    private const val MAX_BANNER_WIDTH = 320
    private const val MAX_BANNER_HEIGHT = 180
    private const val MAX_ICON_SIZE = 144

    private val cache = LruCache<String, AppArtwork>(MAX_ENTRIES)

    suspend fun load(
        context: Context,
        packageName: String,
        activityName: String,
    ): AppArtwork? {
        val key = "$packageName/$activityName"
        cache.get(key)?.let { return it }

        return withContext(Dispatchers.IO) {
            cache.get(key)?.let { return@withContext it }

            val pm = context.packageManager
            val banner = loadBannerDrawable(pm, packageName, activityName)
            if (banner != null) {
                val bmp = drawableToBitmap(banner, MAX_BANNER_WIDTH, MAX_BANNER_HEIGHT)
                val artwork = AppArtwork(bmp.asImageBitmap(), isBanner = true)
                cache.put(key, artwork)
                return@withContext artwork
            }

            val icon = runCatching { pm.getApplicationIcon(packageName) }.getOrNull()
                ?: return@withContext null
            val bmp = drawableToBitmap(icon, MAX_ICON_SIZE, MAX_ICON_SIZE)
            val artwork = AppArtwork(bmp.asImageBitmap(), isBanner = false)
            cache.put(key, artwork)
            artwork
        }
    }

    private fun loadBannerDrawable(
        pm: PackageManager,
        packageName: String,
        activityName: String,
    ): Drawable? {
        if (activityName.isNotBlank()) {
            val activityBanner = runCatching {
                pm.getActivityBanner(ComponentName(packageName, activityName))
            }.getOrNull()
            if (activityBanner != null) return activityBanner
        }
        return runCatching { pm.getApplicationBanner(packageName) }.getOrNull()
    }

    private fun drawableToBitmap(
        drawable: Drawable,
        maxWidth: Int,
        maxHeight: Int,
    ): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val src = drawable.bitmap
            if (src.width <= maxWidth && src.height <= maxHeight) {
                return src
            }
            val scale = minOf(
                maxWidth.toFloat() / src.width.coerceAtLeast(1),
                maxHeight.toFloat() / src.height.coerceAtLeast(1),
            )
            return Bitmap.createScaledBitmap(
                src,
                (src.width * scale).toInt().coerceAtLeast(1),
                (src.height * scale).toInt().coerceAtLeast(1),
                true,
            )
        }

        val intrinsicW = drawable.intrinsicWidth.takeIf { it > 0 } ?: maxWidth
        val intrinsicH = drawable.intrinsicHeight.takeIf { it > 0 } ?: maxHeight
        val scale = minOf(
            maxWidth.toFloat() / intrinsicW,
            maxHeight.toFloat() / intrinsicH,
            1f,
        )
        val width = (intrinsicW * scale).toInt().coerceAtLeast(1)
        val height = (intrinsicH * scale).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
}


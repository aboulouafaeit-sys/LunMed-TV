# ==============================================================================
# LUNMED TV Commercial ProGuard & R8 Configuration
# Optimized for 10-foot Television Hardware, AOSP TV Boxes & Leanback Devices
# ==============================================================================

# Debug & Line Number Attributes for Remote Crash Analytics
-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# Android TV & AOSP Receiver Components
-keep class com.lunmed.tv.receiver.LunmedBootReceiver { *; }
-keep class com.lunmed.tv.receiver.LunmedDeviceAdminReceiver { *; }
-keep class com.lunmed.tv.service.LunmedHomeWatchdogService { *; }

# AndroidX Room
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Proto & DataStore (consumer rules cover reflection needs; avoid keeping unused protobuf bloat)
-dontwarn androidx.datastore.**

# Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.flow.**

# Dagger / Hilt Dependency Injection
-keep class com.lunmed.tv.core.data.di.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Compose for TV & Material 3 (let R8 tree-shake unused widgets via consumer rules)
-dontwarn androidx.compose.**
-dontwarn coil.**

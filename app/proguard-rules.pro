############################################
# GLOBAL
############################################

-dontpreverify
-allowaccessmodification

# Keep debugging info for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

############################################
# GOOGLE SIGN-IN (CRITICAL – MINIMAL)
############################################

# Google Sign-In core
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

-dontwarn com.google.android.gms.auth.api.signin.**

############################################
# FIREBASE
############################################

-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }

-dontwarn com.google.firebase.**

############################################
# KOTLIN & COROUTINES
############################################

-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature

# Coroutines internals
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

############################################
# KOTLIN SERIALIZATION
############################################

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

############################################
# COMPOSE
############################################

-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }

-dontwarn androidx.compose.**

############################################
# HILT
############################################

-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

############################################
# ROOM
############################################

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

############################################
# KTOR
############################################

-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

-dontwarn kotlinx.atomicfu.**

############################################
# ARROW
############################################

-keep class arrow.core.** { *; }
-keep class arrow.** { *; }

############################################
# GLANCE (WIDGETS)
############################################

-keep class androidx.glance.** { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidget
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver

############################################
# ANDROID SYSTEM
############################################

# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Custom Views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

############################################
# REMOVE LOGGING IN RELEASE
############################################

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

############################################
# ENUMS & NATIVE
############################################

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

############################################
# WARNING SUPPRESSION (SAFE)
############################################

-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.**
-dontwarn org.slf4j.impl.**

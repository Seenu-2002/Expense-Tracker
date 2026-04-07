# ============================================================
# ProGuard / R8 rules for Expense Tracker
# ============================================================

# --- Hilt ---
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.ajay.seenu.expensetracker.**$$serializer { *; }
-keepclassmembers class com.ajay.seenu.expensetracker.** {
    *** Companion;
}
-keepclasseswithmembers class com.ajay.seenu.expensetracker.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- SQLDelight ---
-keep class app.cash.sqldelight.** { *; }
-keep class com.ajay.seenu.expensetracker.ExpenseDatabase { *; }
-keep class com.ajay.seenu.expensetracker.ExpenseDatabaseQueries { *; }
-keep class com.ajay.seenu.expensetracker.*Entity { *; }
-keep class com.ajay.seenu.expensetracker.Get* { *; }

# --- Vico Charts ---
-keep class com.patrykandpatrick.vico.** { *; }

# --- Coil ---
-keep class coil.** { *; }

# --- Timber ---
-dontwarn org.jetbrains.annotations.**

# --- Kotlin ---
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# --- General Android ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


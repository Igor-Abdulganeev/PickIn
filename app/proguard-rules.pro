-keep class kotlin.text.** { *; }
-dontwarn kotlin.text.**

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
-keep class kotlinx.serialization.json.** { *; }

-keepclassmembers class * {
    *** Companion;
    *** serializer(...);
}

# Модели данных
-keep class ru.gorinih.pickin.data.parsers.models.** { *; }
-keep class ru.gorinih.pickin.data.remote.models.** { *; }


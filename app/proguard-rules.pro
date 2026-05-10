# Keep kotlinx.serialization classes (they're accessed via reflection/generated code)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class dev.matejgroombridge.streaks.**$$serializer { *; }
-keepclassmembers class dev.matejgroombridge.streaks.** {
    *** Companion;
}
-keepclasseswithmembers class dev.matejgroombridge.streaks.** {
    kotlinx.serialization.KSerializer serializer(...);
}

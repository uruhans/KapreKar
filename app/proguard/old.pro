-dontwarn com.squareup.otto.**
-keep class com.squareup.otto.** { *; }
-keep interface com.squareup.otto.** { *; }



# Retain generated JsonAdapters if annotated type is retained.
#-if @com.squareup.moshi.JsonClass class *
-keep class <1>JsonAdapter {
    <init>(...);
    <fields>;
}


-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
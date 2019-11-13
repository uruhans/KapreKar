-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-keep interface com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep class com.google.protobuf.** { *; }
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class io.fabric.** { *; }
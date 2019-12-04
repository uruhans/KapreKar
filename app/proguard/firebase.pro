-keep class com.firebase.** { *; }
-keep interface com.firebase.** { *; }

-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }

-keep class org.apache.** { *; }
-keep interface org.apache.** { *; }

-keepnames class com.shaded.fasterxml.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**


-keep,allowshrinking class com.google.android.gms.common.api.internal.BasePendingResult.** {
    <fields>;
    <methods>;
}
-dontwarn com.google.android.gms.common.api.internal.BasePendingResult
-dontwarn com.google.android.gms.measurement.AppMeasurement

-keep,allowshrinking class com.google.android.gms.measurement.AppMeasurement.** {
    <fields>;
    <methods>;
}

-keepclassmembers class com.google.android.gms.measurement.AppMeasurement {
    public <fields>;
    public <methods>;
}

-keepattributes Signature

-dontwarn com.firebase.ui.auth.**

-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn org.xmlpull.v1.**
-dontnote org.xmlpull.v1.**
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }
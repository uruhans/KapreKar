# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

## Square Otto specific rules ##
## https://square.github.io/otto/ ##

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

-dontwarn okhttp3.internal.**
-keep class okhttp3.internal.** { *; }
-keep interface okhttp3.internal.** { *; }

-dontwarn com.android.org.**
-keep class com.android.org.** { *; }

-dontwarn sun.security.ssl.**
-keep class sun.security.ssl.** { *; }

-dontwarn org.conscrypt.**
-keep class org.conscrypt.** { *; }

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }



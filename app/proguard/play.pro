-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

-dontwarn com.google.android.gms.internal.**
-keep class com.google.android.gms.internal.** { *; }

-dontwarn com.google.android.gms.measurement.internal.**
-keep class com.google.android.gms.measurement.internal.** { *; }

-dontwarn kotlin.internal.**
-keep class kotlin.internal.** { *; }

-dontwarn com.google.android.gms.ads.AdView.**
-keep class com.google.android.gms.ads.AdView.** { *; }

-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}
-keep class com.trifork.gaj.BuildConfig

-keepclassmembers class com.trifork.gaj.shared.** {
  <init>(...);
  <fields>;
}

-keep class com.trifork.gaj.shared.** { *; }

-keepclassmembers class com.trifork.gaj.shared.entities.** {
  <init>(...);
  <fields>;
}

-keep class com.trifork.gaj.shared.entities.** { *; }


-keepclassmembers class com.trifork.gaj.datalayer.entities.** {
  <init>(...);
  <fields>;
}

-keep class com.trifork.gaj.datalayer.entities.** { *; }

-keepclassmembers class com.trifork.gaj.datalayer.growthchart.** {
  <init>(...);
  <fields>;
}

-keep class com.trifork.gaj.datalayer.growthchart.** { *; }

-optimizations !class/unboxing/enum

-dontskipnonpubliclibraryclasses

-keep class com.trifork.gaj.notifications.** { *; }

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class **.R$*
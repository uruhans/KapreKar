# #########
# RxJava 2
# #########
-keep class io.reactivex.schedulers {
    public static <methods>;
}

-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}

-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}

-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}

-keep class io.reactivex.disposables.Disposable
-keep class io.reactivex.functions.** {*;}
-keep class io.reactivex.** { *; }
-keep class org.reactivestreams.** { *; }
#-keep class io.reactivex.MaybeObserver
#-keep class io.reactivex.CompletableObserver
#-keep class io.reactivex.SingleObserver

-keepclassmembers class io.reactivex.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keep,includedescriptorclasses class rx.internal.util.** { *; }
-keep,includedescriptorclasses class rx.schedulers.** { *; }

-keep,includedescriptorclasses class rx.internal.** { *; }
-keep,includedescriptorclasses class io.reactivex.** { *; }


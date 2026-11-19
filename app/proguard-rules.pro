# The speech engine's native code looks these classes and their fields up by name.
-keep class com.k2fsa.sherpa.onnx.** { *; }

# The ads SDK brings in WorkManager, whose Room database is created by reflection
# from its generated "_Impl" class. Without these the release build crashes on launch.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.work.impl.WorkDatabase_Impl { *; }

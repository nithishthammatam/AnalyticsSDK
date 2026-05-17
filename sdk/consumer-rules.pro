# sdk/consumer-rules.pro
# Keep all SDK public classes
-keep class com.shopzen.analytics.** { *; }

# Keep Room entity classes
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep WorkManager worker
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep Retrofit service
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; } 

# Keep Gson models
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

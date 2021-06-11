# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.squareup.okhttp.**
-keep class org.apache.commons.codec.binary** { *; }
-keep class com.pusher.client** { *; }
-keep class com.pusher** { *; }
-keep class io.socket** { *; }
-keep class io.socket.client** { *; }
-keep class io.socket.client.emitter** { *; }
-keep class com.onesignal** { *; }
-dontwarn com.pushwoosh.**
-dontwarn com.arellomobile.**
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepattributes *Annotation*,EnclosingMethod,Signature,SourceFile,LineNumberTable
 -dontwarn com.fasterxml.jackson.databind.**
# My POJO class directory
-keep public class com.kubernet.bytefollow.model** {
  public void set*(***);
  public *** get*();
  public protected private *;
}
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
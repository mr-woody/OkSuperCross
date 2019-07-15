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
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.* { *;}
-dontwarn com.google.gson.**
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep class **.R_$* {
 *;
}
-keep @com.okay.supercross.annotation.RemoteServiceImpl class * {*;}
-keepnames class com.okay.supercross.apt.** { *; }
-keepnames class * implements com.okay.supercross.application.IApplicationLife { *; }
-keep public class * extends com.okay.supercross.processors.RemoteServiceProcessor { *; }
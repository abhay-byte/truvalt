# Add project specific ProGuard rules here.
-keep class com.ivarna.truvalt.** { *; }

# Keep BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Suppress missing javax.imageio SPI classes (jai-imageio transitive dep, not available on Android)
-dontwarn javax.imageio.spi.ImageInputStreamSpi
-dontwarn javax.imageio.spi.ImageOutputStreamSpi
-dontwarn javax.imageio.spi.ImageReaderSpi
-dontwarn javax.imageio.spi.ImageWriterSpi

# Keep SQLCipher
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Keep Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

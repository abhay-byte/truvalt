package com.ivarna.truvalt.core.crypto;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 -2\u00020\u0001:\u0001-B\t\b\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\f\u001a\u00020\u000bH\u0002J\u0018\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u0018\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u000eJ\u0016\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000eJ\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\u001c\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u001b2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u001d\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u000eJB\u0010\u001f\u001a\u00020\u000b2\b\b\u0002\u0010 \u001a\u00020!2\b\b\u0002\u0010\"\u001a\u00020#2\b\b\u0002\u0010$\u001a\u00020#2\b\b\u0002\u0010%\u001a\u00020#2\b\b\u0002\u0010&\u001a\u00020#2\b\b\u0002\u0010\'\u001a\u00020#J\u0006\u0010(\u001a\u00020)J\u000e\u0010*\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u000eJ\u000e\u0010+\u001a\u00020\u000e2\u0006\u0010,\u001a\u00020\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/ivarna/truvalt/core/crypto/CryptoManager;", "", "<init>", "()V", "keyStore", "Ljava/security/KeyStore;", "secureRandom", "Ljava/security/SecureRandom;", "deriveKeyFromPassword", "Lcom/ivarna/truvalt/core/crypto/DerivedKeys;", "password", "", "email", "deriveSalt", "", "hkdf", "inputKey", "info", "hmacSha256", "key", "data", "encryptVaultItem", "Lcom/ivarna/truvalt/core/crypto/EncryptedBlob;", "vaultKey", "decryptVaultItem", "blob", "encryptForExport", "Lcom/ivarna/truvalt/core/crypto/ExportedEncryptedBlob;", "decryptExport", "hashAuthKey", "authKey", "generateRandomPassword", "length", "", "useUppercase", "", "useLowercase", "useDigits", "useSymbols", "excludeAmbiguous", "getOrCreateKeystoreKey", "Ljavax/crypto/SecretKey;", "encryptWithKeystore", "decryptWithKeystore", "encryptedData", "Companion", "app_debug"})
public final class CryptoManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ANDROID_KEYSTORE = "AndroidKeyStore";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ALIAS = "truvalt_master_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String AES_MODE = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int ARGON2_MEMORY = 65536;
    private static final int ARGON2_ITERATIONS = 3;
    private static final int ARGON2_PARALLELISM = 4;
    private static final int KEY_LENGTH = 256;
    @org.jetbrains.annotations.NotNull()
    private final java.security.KeyStore keyStore = null;
    @org.jetbrains.annotations.NotNull()
    private final java.security.SecureRandom secureRandom = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.ivarna.truvalt.core.crypto.CryptoManager.Companion Companion = null;
    
    @javax.inject.Inject()
    public CryptoManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.core.crypto.DerivedKeys deriveKeyFromPassword(@org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String email) {
        return null;
    }
    
    private final byte[] deriveSalt(java.lang.String email) {
        return null;
    }
    
    private final byte[] hkdf(byte[] inputKey, byte[] info) {
        return null;
    }
    
    private final byte[] hmacSha256(byte[] key, byte[] data) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.core.crypto.EncryptedBlob encryptVaultItem(@org.jetbrains.annotations.NotNull()
    byte[] data, @org.jetbrains.annotations.NotNull()
    byte[] vaultKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] decryptVaultItem(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.core.crypto.EncryptedBlob blob, @org.jetbrains.annotations.NotNull()
    byte[] vaultKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.core.crypto.ExportedEncryptedBlob encryptForExport(@org.jetbrains.annotations.NotNull()
    byte[] data, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] decryptExport(@org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.core.crypto.ExportedEncryptedBlob blob, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlin.io.encoding.ExperimentalEncodingApi.class})
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hashAuthKey(@org.jetbrains.annotations.NotNull()
    byte[] authKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateRandomPassword(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSymbols, boolean excludeAmbiguous) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final javax.crypto.SecretKey getOrCreateKeystoreKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] encryptWithKeystore(@org.jetbrains.annotations.NotNull()
    byte[] data) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] decryptWithKeystore(@org.jetbrains.annotations.NotNull()
    byte[] encryptedData) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/ivarna/truvalt/core/crypto/CryptoManager$Companion;", "", "<init>", "()V", "ANDROID_KEYSTORE", "", "KEY_ALIAS", "AES_MODE", "GCM_IV_LENGTH", "", "GCM_TAG_LENGTH", "ARGON2_MEMORY", "ARGON2_ITERATIONS", "ARGON2_PARALLELISM", "KEY_LENGTH", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}
package com.ivarna.truvalt.core.utils;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0002\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003JB\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\tJ.\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u00052\b\b\u0002\u0010\u0011\u001a\u00020\t2\b\b\u0002\u0010\u0012\u001a\u00020\tR\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/ivarna/truvalt/core/utils/PasswordGenerator;", "", "<init>", "()V", "generate", "", "length", "", "useUppercase", "", "useLowercase", "useDigits", "useSymbols", "excludeAmbiguous", "generatePassphrase", "wordCount", "separator", "capitalize", "appendNumber", "EFF_WORDLIST", "", "Companion", "app_debug"})
public final class PasswordGenerator {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DIGITS = "0123456789";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String AMBIGUOUS = "0O1lI";
    @org.jetbrains.annotations.NotNull()
    private static final java.security.SecureRandom secureRandom = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> EFF_WORDLIST = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.ivarna.truvalt.core.utils.PasswordGenerator.Companion Companion = null;
    
    public PasswordGenerator() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generate(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSymbols, boolean excludeAmbiguous) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generatePassphrase(int wordCount, @org.jetbrains.annotations.NotNull()
    java.lang.String separator, boolean capitalize, boolean appendNumber) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/ivarna/truvalt/core/utils/PasswordGenerator$Companion;", "", "<init>", "()V", "UPPERCASE", "", "LOWERCASE", "DIGITS", "SYMBOLS", "AMBIGUOUS", "secureRandom", "Ljava/security/SecureRandom;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}
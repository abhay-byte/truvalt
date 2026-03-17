package com.ivarna.truvalt.core.utils;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001:\u0003\u0016\u0017\u0018B\t\b\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0010\u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0010\u0010\u000f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u001a\u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\u0011\u001a\u00020\tH\u0002J\u0010\u0010\u0012\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u00142\u0006\u0010\u0015\u001a\u00020\tH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService;", "", "<init>", "()V", "gson", "Lcom/google/gson/Gson;", "importData", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult;", "content", "", "format", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportFormat;", "importBitwardenJson", "importLastPassCsv", "importChromeCsv", "importFirefoxCsv", "importGenericCsv", "source", "importTruvaltExport", "parseCsvLine", "", "line", "ImportResult", "ImportFormat", "Quad", "app_debug"})
public final class ImportExportService {
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    
    @javax.inject.Inject()
    public ImportExportService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importData(@org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.NotNull()
    com.ivarna.truvalt.core.utils.ImportExportService.ImportFormat format) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importBitwardenJson(java.lang.String content) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importLastPassCsv(java.lang.String content) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importChromeCsv(java.lang.String content) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importFirefoxCsv(java.lang.String content) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importGenericCsv(java.lang.String content, java.lang.String source) {
        return null;
    }
    
    private final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult importTruvaltExport(java.lang.String content) {
        return null;
    }
    
    private final java.util.List<java.lang.String> parseCsvLine(java.lang.String line) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportFormat;", "", "<init>", "(Ljava/lang/String;I)V", "BITWARDEN_JSON", "LASTPASS_CSV", "CHROME_CSV", "FIREFOX_CSV", "GENERIC_CSV", "TRUVALT_EXPORT", "app_debug"})
    public static enum ImportFormat {
        /*public static final*/ BITWARDEN_JSON /* = new BITWARDEN_JSON() */,
        /*public static final*/ LASTPASS_CSV /* = new LASTPASS_CSV() */,
        /*public static final*/ CHROME_CSV /* = new CHROME_CSV() */,
        /*public static final*/ FIREFOX_CSV /* = new FIREFOX_CSV() */,
        /*public static final*/ GENERIC_CSV /* = new GENERIC_CSV() */,
        /*public static final*/ TRUVALT_EXPORT /* = new TRUVALT_EXPORT() */;
        
        ImportFormat() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.ivarna.truvalt.core.utils.ImportExportService.ImportFormat> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0004\u0005B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0002\u0006\u0007\u00a8\u0006\b"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult;", "", "<init>", "()V", "Success", "Error", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult$Error;", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult$Success;", "app_debug"})
    public static abstract class ImportResult {
        
        private ImportResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult$Error;", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.ivarna.truvalt.core.utils.ImportExportService.ImportResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J)\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\n\u00a8\u0006\u0016"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult$Success;", "Lcom/ivarna/truvalt/core/utils/ImportExportService$ImportResult;", "items", "", "Lcom/ivarna/truvalt/domain/model/VaultItem;", "errors", "", "<init>", "(Ljava/util/List;Ljava/util/List;)V", "getItems", "()Ljava/util/List;", "getErrors", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Success extends com.ivarna.truvalt.core.utils.ImportExportService.ImportResult {
            @org.jetbrains.annotations.NotNull()
            private final java.util.List<com.ivarna.truvalt.domain.model.VaultItem> items = null;
            @org.jetbrains.annotations.NotNull()
            private final java.util.List<java.lang.String> errors = null;
            
            public Success(@org.jetbrains.annotations.NotNull()
            java.util.List<com.ivarna.truvalt.domain.model.VaultItem> items, @org.jetbrains.annotations.NotNull()
            java.util.List<java.lang.String> errors) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.util.List<com.ivarna.truvalt.domain.model.VaultItem> getItems() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.util.List<java.lang.String> getErrors() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.util.List<com.ivarna.truvalt.domain.model.VaultItem> component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.util.List<java.lang.String> component2() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.ivarna.truvalt.core.utils.ImportExportService.ImportResult.Success copy(@org.jetbrains.annotations.NotNull()
            java.util.List<com.ivarna.truvalt.domain.model.VaultItem> items, @org.jetbrains.annotations.NotNull()
            java.util.List<java.lang.String> errors) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u0002*\u0004\b\u0002\u0010\u0003*\u0004\b\u0003\u0010\u00042\u00020\u0005B\'\u0012\u0006\u0010\u0006\u001a\u00028\u0000\u0012\u0006\u0010\u0007\u001a\u00028\u0001\u0012\u0006\u0010\b\u001a\u00028\u0002\u0012\u0006\u0010\t\u001a\u00028\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\u0012\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u0013\u001a\u00028\u0001H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u0014\u001a\u00028\u0002H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u0015\u001a\u00028\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJN\u0010\u0016\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u00002\b\b\u0002\u0010\u0006\u001a\u00028\u00002\b\b\u0002\u0010\u0007\u001a\u00028\u00012\b\b\u0002\u0010\b\u001a\u00028\u00022\b\b\u0002\u0010\t\u001a\u00028\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0005H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001R\u0013\u0010\u0006\u001a\u00028\u0000\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0007\u001a\u00028\u0001\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u000f\u0010\rR\u0013\u0010\b\u001a\u00028\u0002\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0010\u0010\rR\u0013\u0010\t\u001a\u00028\u0003\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0011\u0010\r\u00a8\u0006\u001f"}, d2 = {"Lcom/ivarna/truvalt/core/utils/ImportExportService$Quad;", "A", "B", "C", "D", "", "first", "second", "third", "fourth", "<init>", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", "getFirst", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getSecond", "getThird", "getFourth", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/ivarna/truvalt/core/utils/ImportExportService$Quad;", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class Quad<A extends java.lang.Object, B extends java.lang.Object, C extends java.lang.Object, D extends java.lang.Object> {
        private final A first = null;
        private final B second = null;
        private final C third = null;
        private final D fourth = null;
        
        public Quad(A first, B second, C third, D fourth) {
            super();
        }
        
        public final A getFirst() {
            return null;
        }
        
        public final B getSecond() {
            return null;
        }
        
        public final C getThird() {
            return null;
        }
        
        public final D getFourth() {
            return null;
        }
        
        public final A component1() {
            return null;
        }
        
        public final B component2() {
            return null;
        }
        
        public final C component3() {
            return null;
        }
        
        public final D component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.ivarna.truvalt.core.utils.ImportExportService.Quad<A, B, C, D> copy(A first, B second, C third, D fourth) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}
package com.lge.launcher3.backuprestore;

/* JADX INFO: loaded from: classes.dex */
public enum BackupErrorCode {
    NO_ERROR(0, "success"),
    INVALID_FRAMEWORK_API(200, "invalid framework api"),
    SECURITY_ERR(201, "security exception"),
    FRAMEWORK_ERR(202, "backup framework error"),
    INVALID_LBF(203, "invalid backup file"),
    INTERNAL_ERROR(204, "internal error");

    private final String message;
    private final int value;

    BackupErrorCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int value() {
        return this.value;
    }

    public String getMessage() {
        return this.message;
    }
}

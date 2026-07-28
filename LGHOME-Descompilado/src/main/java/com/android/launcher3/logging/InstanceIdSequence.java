package com.android.launcher3.logging;

import java.security.SecureRandom;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class InstanceIdSequence {
    protected final int mInstanceIdMax;
    private final Random mRandom;

    public InstanceIdSequence(int instanceIdMax) {
        this.mRandom = new SecureRandom();
        this.mInstanceIdMax = Math.min(Math.max(1, instanceIdMax), 1048576);
    }

    public InstanceIdSequence() {
        this(1048576);
    }

    public InstanceId newInstanceId() {
        return newInstanceIdInternal(this.mRandom.nextInt(this.mInstanceIdMax) + 1);
    }

    protected InstanceId newInstanceIdInternal(int id) {
        return new InstanceId(id);
    }
}

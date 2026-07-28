package com.android.launcher3.anim;

/* JADX INFO: loaded from: classes.dex */
public class SpringProperty {
    public static final SpringProperty DEFAULT = new SpringProperty();
    public static final int FLAG_CAN_SPRING_ON_END = 1;
    public static final int FLAG_CAN_SPRING_ON_START = 2;
    public final int flags;
    float mDampingRatio;
    float mStiffness;

    public SpringProperty() {
        this(0);
    }

    public SpringProperty(int flags) {
        this.mDampingRatio = 0.5f;
        this.mStiffness = 1500.0f;
        this.flags = flags;
    }

    public SpringProperty setDampingRatio(float dampingRatio) {
        this.mDampingRatio = dampingRatio;
        return this;
    }

    public SpringProperty setStiffness(float stiffness) {
        this.mStiffness = stiffness;
        return this;
    }
}

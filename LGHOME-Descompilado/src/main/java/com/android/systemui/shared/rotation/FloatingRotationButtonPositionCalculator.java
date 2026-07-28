package com.android.systemui.shared.rotation;

import kotlin.Metadata;

/* JADX INFO: compiled from: FloatingRotationButtonPositionCalculator.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\u0010B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0002\u0010\u0007J&\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rJ\u0018\u0010\u000f\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u0003H\u0002R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator;", "", "buttonMarginLeft", "", "buttonMarginBottom", "taskbarMarginLeft", "taskbarMarginBottom", "(IIII)V", "calculatePosition", "Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator$Position;", "rotation", "currentRotation", "taskbarVisible", "", "taskbarStashed", "resolveGravity", "Position", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class FloatingRotationButtonPositionCalculator {
    private final int buttonMarginBottom;
    private final int buttonMarginLeft;
    private final int taskbarMarginBottom;
    private final int taskbarMarginLeft;

    private final int resolveGravity(int rotation, int currentRotation) {
        if ((currentRotation == 0 && rotation == 1) || (currentRotation == 3 && rotation == 0)) {
            return 83;
        }
        if ((currentRotation == 0 && rotation == 3) || (currentRotation == 1 && rotation == 0)) {
            return 53;
        }
        return ((currentRotation == 1 && rotation == 3) || (currentRotation == 3 && rotation == 1)) ? 51 : 85;
    }

    public FloatingRotationButtonPositionCalculator(int i, int i2, int i3, int i4) {
        this.buttonMarginLeft = i;
        this.buttonMarginBottom = i2;
        this.taskbarMarginLeft = i3;
        this.taskbarMarginBottom = i4;
    }

    public final Position calculatePosition(int rotation, int currentRotation, boolean taskbarVisible, boolean taskbarStashed) {
        boolean z = false;
        if ((currentRotation == 0 || currentRotation == 1) && taskbarVisible && !taskbarStashed) {
            z = true;
        }
        int iResolveGravity = resolveGravity(rotation, currentRotation);
        int i = z ? this.taskbarMarginLeft : this.buttonMarginLeft;
        int i2 = z ? this.taskbarMarginBottom : this.buttonMarginBottom;
        if ((iResolveGravity & 5) == 5) {
            i = -i;
        }
        if ((iResolveGravity & 80) == 80) {
            i2 = -i2;
        }
        return new Position(iResolveGravity, i, i2);
    }

    /* JADX INFO: compiled from: FloatingRotationButtonPositionCalculator.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b¨\u0006\u0015"}, d2 = {"Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator$Position;", "", "gravity", "", "translationX", "translationY", "(III)V", "getGravity", "()I", "getTranslationX", "getTranslationY", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public static final /* data */ class Position {
        private final int gravity;
        private final int translationX;
        private final int translationY;

        public static /* synthetic */ Position copy$default(Position position, int i, int i2, int i3, int i4, Object obj) {
            if ((i4 & 1) != 0) {
                i = position.gravity;
            }
            if ((i4 & 2) != 0) {
                i2 = position.translationX;
            }
            if ((i4 & 4) != 0) {
                i3 = position.translationY;
            }
            return position.copy(i, i2, i3);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final int getGravity() {
            return this.gravity;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final int getTranslationX() {
            return this.translationX;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final int getTranslationY() {
            return this.translationY;
        }

        public final Position copy(int gravity, int translationX, int translationY) {
            return new Position(gravity, translationX, translationY);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Position)) {
                return false;
            }
            Position position = (Position) other;
            return this.gravity == position.gravity && this.translationX == position.translationX && this.translationY == position.translationY;
        }

        public int hashCode() {
            return (((Integer.hashCode(this.gravity) * 31) + Integer.hashCode(this.translationX)) * 31) + Integer.hashCode(this.translationY);
        }

        public String toString() {
            return "Position(gravity=" + this.gravity + ", translationX=" + this.translationX + ", translationY=" + this.translationY + ')';
        }

        public Position(int i, int i2, int i3) {
            this.gravity = i;
            this.translationX = i2;
            this.translationY = i3;
        }

        public final int getGravity() {
            return this.gravity;
        }

        public final int getTranslationX() {
            return this.translationX;
        }

        public final int getTranslationY() {
            return this.translationY;
        }
    }
}

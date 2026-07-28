package com.android.launcher3.graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatArrayEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.core.view.ViewCompat;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ClipPathView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.DDTUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public abstract class IconShape {
    public static final int DEFAULT_PATH_SIZE = 100;
    private static IconShape sInstance = new SimplePathShape(0.0f);
    private static float sNormalizationScale = 0.97f;
    private static Path sShapePath;
    private SparseArray<TypedValue> mAttrs;

    public abstract void addToPath(Path path, float offsetX, float offsetY, float radius);

    public abstract <T extends View & ClipPathView> Animator createRevealAnimator(T target, Rect startRect, Rect endRect, float endRadius, boolean isReversed);

    public abstract void drawShape(Canvas canvas, float offsetX, float offsetY, float radius, Paint paint);

    public boolean enableShapeDetection() {
        return false;
    }

    public static IconShape getShape() {
        return sInstance;
    }

    public static Path getShapePath() {
        if (sShapePath == null) {
            Path path = new Path();
            getShape().addToPath(path, 0.0f, 0.0f, 50.0f);
            sShapePath = path;
        }
        return sShapePath;
    }

    public static float getNormalizationScale() {
        return sNormalizationScale;
    }

    public TypedValue getAttrValue(int attr) {
        SparseArray<TypedValue> sparseArray = this.mAttrs;
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(attr);
    }

    private static abstract class SimpleRectShape extends IconShape {
        protected abstract float getStartRadius(Rect startRect);

        private SimpleRectShape() {
        }

        @Override // com.android.launcher3.graphics.IconShape
        public final <T extends View & ClipPathView> Animator createRevealAnimator(T target, Rect startRect, Rect endRect, float endRadius, boolean isReversed) {
            return new RoundedRectRevealOutlineProvider(getStartRadius(startRect), endRadius, startRect, endRect) { // from class: com.android.launcher3.graphics.IconShape.SimpleRectShape.1
                @Override // com.android.launcher3.anim.RoundedRectRevealOutlineProvider, com.android.launcher3.anim.RevealOutlineAnimation
                public boolean shouldRemoveElevationDuringAnimation() {
                    return true;
                }
            }.createRevealAnimator(target, isReversed);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static abstract class PathShape extends IconShape {
        private final Path mTmpPath;

        protected abstract ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect startRect, Rect endRect, float endRadius, Path outPath);

        private PathShape() {
            this.mTmpPath = new Path();
        }

        @Override // com.android.launcher3.graphics.IconShape
        public final void drawShape(Canvas canvas, float offsetX, float offsetY, float radius, Paint paint) {
            this.mTmpPath.reset();
            addToPath(this.mTmpPath, offsetX, offsetY, radius);
            canvas.drawPath(this.mTmpPath, paint);
        }

        @Override // com.android.launcher3.graphics.IconShape
        public final <T extends View & ClipPathView> Animator createRevealAnimator(final T target, Rect startRect, Rect endRect, float endRadius, boolean isReversed) {
            ValueAnimator valueAnimatorOfFloat;
            final Path path = new Path();
            final ValueAnimator.AnimatorUpdateListener animatorUpdateListenerNewUpdateListener = newUpdateListener(startRect, endRect, endRadius, path);
            float[] fArr = {0.0f, 1.0f};
            if (isReversed) {
                // fill-array-data instruction
                fArr[0] = 1.0f;
                fArr[1] = 0.0f;
                valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            } else {
                valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            }
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.graphics.IconShape.PathShape.1
                private ViewOutlineProvider mOldOutlineProvider;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    this.mOldOutlineProvider = target.getOutlineProvider();
                    target.setOutlineProvider(null);
                    View view = target;
                    view.setTranslationZ(-view.getElevation());
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    target.setTranslationZ(0.0f);
                    ((ClipPathView) target).setClipPath(null);
                    target.setOutlineProvider(this.mOldOutlineProvider);
                }
            });
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.graphics.-$$Lambda$IconShape$PathShape$c8k_g3fUH4pqVFd5dzjrABMFtJw
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    IconShape.PathShape.lambda$createRevealAnimator$0(path, animatorUpdateListenerNewUpdateListener, target, valueAnimator);
                }
            });
            return valueAnimatorOfFloat;
        }

        /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
        /* JADX WARN: Multi-variable type inference failed */
        static /* synthetic */ void lambda$createRevealAnimator$0(Path path, ValueAnimator.AnimatorUpdateListener animatorUpdateListener, View view, ValueAnimator valueAnimator) {
            path.reset();
            animatorUpdateListener.onAnimationUpdate(valueAnimator);
            ((ClipPathView) view).setClipPath(path);
        }
    }

    public static final class Circle extends SimpleRectShape {
        @Override // com.android.launcher3.graphics.IconShape
        public boolean enableShapeDetection() {
            return true;
        }

        public Circle() {
            super();
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void drawShape(Canvas canvas, float offsetX, float offsetY, float radius, Paint p) {
            canvas.drawCircle(offsetX + radius, offsetY + radius, radius, p);
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void addToPath(Path path, float offsetX, float offsetY, float radius) {
            path.addCircle(offsetX + radius, offsetY + radius, radius, Path.Direction.CW);
        }

        @Override // com.android.launcher3.graphics.IconShape.SimpleRectShape
        protected float getStartRadius(Rect startRect) {
            return startRect.width() / 2.0f;
        }
    }

    public static class RoundedSquare extends SimpleRectShape {
        private final float mRadiusRatio;

        public RoundedSquare(float radiusRatio) {
            super();
            this.mRadiusRatio = radiusRatio;
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void drawShape(Canvas canvas, float offsetX, float offsetY, float radius, Paint p) {
            float f = offsetX + radius;
            float f2 = offsetY + radius;
            float f3 = radius * this.mRadiusRatio;
            canvas.drawRoundRect(f - radius, f2 - radius, f + radius, f2 + radius, f3, f3, p);
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void addToPath(Path path, float offsetX, float offsetY, float radius) {
            float f = offsetX + radius;
            float f2 = offsetY + radius;
            float f3 = radius * this.mRadiusRatio;
            path.addRoundRect(f - radius, f2 - radius, f + radius, f2 + radius, f3, f3, Path.Direction.CW);
        }

        @Override // com.android.launcher3.graphics.IconShape.SimpleRectShape
        protected float getStartRadius(Rect startRect) {
            return (startRect.width() / 2.0f) * this.mRadiusRatio;
        }
    }

    public static class TearDrop extends PathShape {
        private final float mRadiusRatio;
        private final float[] mTempRadii;

        public TearDrop(float radiusRatio) {
            super();
            this.mTempRadii = new float[8];
            this.mRadiusRatio = radiusRatio;
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void addToPath(Path p, float offsetX, float offsetY, float r1) {
            float f = offsetX + r1;
            float f2 = offsetY + r1;
            p.addRoundRect(f - r1, f2 - r1, f + r1, f2 + r1, getRadiiArray(r1, this.mRadiusRatio * r1), Path.Direction.CW);
        }

        private float[] getRadiiArray(float r1, float r2) {
            float[] fArr = this.mTempRadii;
            fArr[7] = r1;
            fArr[6] = r1;
            fArr[3] = r1;
            fArr[2] = r1;
            fArr[1] = r1;
            fArr[0] = r1;
            fArr[5] = r2;
            fArr[4] = r2;
            return fArr;
        }

        @Override // com.android.launcher3.graphics.IconShape.PathShape
        protected ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect startRect, Rect endRect, float endRadius, final Path outPath) {
            float fWidth = startRect.width() / 2.0f;
            final float[] fArr = {startRect.left, startRect.top, startRect.right, startRect.bottom, fWidth, this.mRadiusRatio * fWidth};
            final float[] fArr2 = {endRect.left, endRect.top, endRect.right, endRect.bottom, endRadius, endRadius};
            final FloatArrayEvaluator floatArrayEvaluator = new FloatArrayEvaluator(new float[6]);
            return new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.graphics.-$$Lambda$IconShape$TearDrop$O64lnQ400gPNK3Q1TcfKfQYd8uo
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$newUpdateListener$0$IconShape$TearDrop(floatArrayEvaluator, fArr, fArr2, outPath, valueAnimator);
                }
            };
        }

        public /* synthetic */ void lambda$newUpdateListener$0$IconShape$TearDrop(FloatArrayEvaluator floatArrayEvaluator, float[] fArr, float[] fArr2, Path path, ValueAnimator valueAnimator) {
            float[] fArrEvaluate = floatArrayEvaluator.evaluate(((Float) valueAnimator.getAnimatedValue()).floatValue(), fArr, fArr2);
            path.addRoundRect(fArrEvaluate[0], fArrEvaluate[1], fArrEvaluate[2], fArrEvaluate[3], getRadiiArray(fArrEvaluate[4], fArrEvaluate[5]), Path.Direction.CW);
        }
    }

    public static class Squircle extends PathShape {
        private final float mRadiusRatio;

        public Squircle(float radiusRatio) {
            super();
            this.mRadiusRatio = radiusRatio;
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void addToPath(Path p, float offsetX, float offsetY, float r) {
            float f = offsetX + r;
            float f2 = offsetY + r;
            float f3 = r - (this.mRadiusRatio * r);
            p.moveTo(f, f2 - r);
            addLeftCurve(f, f2, r, f3, p);
            addRightCurve(f, f2, r, f3, p);
            float f4 = -r;
            float f5 = -f3;
            addLeftCurve(f, f2, f4, f5, p);
            addRightCurve(f, f2, f4, f5, p);
            p.close();
        }

        private void addLeftCurve(float cx, float cy, float r, float control, Path path) {
            float f = cx - r;
            path.cubicTo(cx - control, cy - r, f, cy - control, f, cy);
        }

        private void addRightCurve(float cx, float cy, float r, float control, Path path) {
            float f = cy + r;
            path.cubicTo(cx - r, cy + control, cx - control, f, cx, f);
        }

        @Override // com.android.launcher3.graphics.IconShape.PathShape
        protected ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect startRect, Rect endRect, final float endR, final Path outPath) {
            final float fExactCenterX = startRect.exactCenterX();
            final float fExactCenterY = startRect.exactCenterY();
            final float fWidth = startRect.width() / 2.0f;
            final float f = fWidth - (this.mRadiusRatio * fWidth);
            final float fExactCenterX2 = endRect.exactCenterX();
            final float fExactCenterY2 = endRect.exactCenterY();
            final float f2 = endR * 0.55191505f;
            final float fWidth2 = (endRect.width() / 2.0f) - endR;
            final float fHeight = (endRect.height() / 2.0f) - endR;
            final float f3 = 0.0f;
            final float f4 = 0.0f;
            return new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.graphics.-$$Lambda$IconShape$Squircle$zI8JW3WfGJyMZpvUtsCMTzoy14o
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$newUpdateListener$0$IconShape$Squircle(fExactCenterX, fExactCenterX2, fExactCenterY, fExactCenterY2, fWidth, endR, f, f2, f3, fWidth2, f4, fHeight, outPath, valueAnimator);
                }
            };
        }

        public /* synthetic */ void lambda$newUpdateListener$0$IconShape$Squircle(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, Path path, ValueAnimator valueAnimator) {
            float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f13 = 1.0f - fFloatValue;
            float f14 = (f13 * f) + (fFloatValue * f2);
            float f15 = (f13 * f3) + (fFloatValue * f4);
            float f16 = (f13 * f5) + (fFloatValue * f6);
            float f17 = (f13 * f7) + (fFloatValue * f8);
            float f18 = (f13 * f9) + (fFloatValue * f10);
            float f19 = (f13 * f11) + (fFloatValue * f12);
            float f20 = f15 - f19;
            path.moveTo(f14, f20 - f16);
            path.rLineTo(-f18, 0.0f);
            float f21 = f14 - f18;
            addLeftCurve(f21, f20, f16, f17, path);
            path.rLineTo(0.0f, f19 + f19);
            float f22 = f15 + f19;
            addRightCurve(f21, f22, f16, f17, path);
            path.rLineTo(f18 + f18, 0.0f);
            float f23 = f14 + f18;
            float f24 = -f16;
            float f25 = -f17;
            addLeftCurve(f23, f22, f24, f25, path);
            path.rLineTo(0.0f, (-f19) - f19);
            addRightCurve(f23, f20, f24, f25, path);
            path.close();
        }
    }

    public static class SimplePathShape extends IconShape {
        private boolean mIsSetClipPath = false;
        private final float mRadiusRatio;

        @Override // com.android.launcher3.graphics.IconShape
        public void addToPath(Path path, float offsetX, float offsetY, float radius) {
        }

        @Override // com.android.launcher3.graphics.IconShape
        public void drawShape(Canvas canvas, float offsetX, float offsetY, float radius, Paint paint) {
        }

        public SimplePathShape(float radiusRatio) {
            this.mRadiusRatio = radiusRatio;
        }

        @Override // com.android.launcher3.graphics.IconShape
        public final <T extends View & ClipPathView> Animator createRevealAnimator(final T target, Rect startRect, Rect endRect, float endRadius, boolean isReversed) {
            ValueAnimator valueAnimatorOfFloat;
            final Path mask = DDTUtils.getCurrentMaskInfo(target.getContext(), startRect, false).getMask();
            float[] fArr = {0.0f, 1.0f};
            if (isReversed) {
                // fill-array-data instruction
                fArr[0] = 1.0f;
                fArr[1] = 0.0f;
                valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            } else {
                valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            }
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.graphics.IconShape.SimplePathShape.1
                private ViewOutlineProvider mOldOutlineProvider;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    this.mOldOutlineProvider = target.getOutlineProvider();
                    target.setOutlineProvider(null);
                    View view = target;
                    view.setTranslationZ(-view.getElevation());
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    target.setTranslationZ(0.0f);
                    ((ClipPathView) target).setClipPath(null);
                    target.setOutlineProvider(this.mOldOutlineProvider);
                    SimplePathShape.this.mIsSetClipPath = false;
                }
            });
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.graphics.-$$Lambda$IconShape$SimplePathShape$_oueO8sj3IOYvZBDe8JG7LaS8YM
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$createRevealAnimator$0$IconShape$SimplePathShape(target, mask, valueAnimator);
                }
            });
            return valueAnimatorOfFloat;
        }

        /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: android.view.View */
        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$createRevealAnimator$0$IconShape$SimplePathShape(View view, Path path, ValueAnimator valueAnimator) {
            if (this.mIsSetClipPath) {
                return;
            }
            ((ClipPathView) view).setClipPath(path);
            this.mIsSetClipPath = true;
        }
    }

    public static void init(Context context) {
        if (Utilities.ATLEAST_OREO) {
            pickBestShape(context);
        }
    }

    private static IconShape getShapeDefinition(String type, float radius) {
        type.hashCode();
        switch (type) {
            case "TearDrop":
                return new TearDrop(radius);
            case "Squircle":
                return new Squircle(radius);
            case "RoundedSquare":
                return new RoundedSquare(radius);
            case "Circle":
                return new Circle();
            default:
                throw new IllegalArgumentException("Invalid shape type: " + type);
        }
    }

    private static List<IconShape> getAllShapes(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            XmlResourceParser xml = context.getResources().getXml(R.xml.folder_shapes);
            do {
                try {
                    int next = xml.next();
                    if (next == 3 || next == 1) {
                        break;
                    }
                } finally {
                }
            } while (!"shapes".equals(xml.getName()));
            int depth = xml.getDepth();
            int[] iArr = {R.attr.folderIconRadius};
            IntArray intArray = new IntArray(0);
            while (true) {
                int next2 = xml.next();
                if ((next2 == 3 && xml.getDepth() <= depth) || next2 == 1) {
                    break;
                }
                if (next2 == 2) {
                    AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xml);
                    TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSetAsAttributeSet, iArr);
                    IconShape shapeDefinition = getShapeDefinition(xml.getName(), typedArrayObtainStyledAttributes.getFloat(0, 1.0f));
                    typedArrayObtainStyledAttributes.recycle();
                    shapeDefinition.mAttrs = Themes.createValueMap(context, attributeSetAsAttributeSet, intArray);
                    arrayList.add(shapeDefinition);
                }
            }
            if (xml != null) {
                xml.close();
            }
            return arrayList;
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void pickBestShape(Context context) {
        Region region = new Region(0, 0, 200, 200);
        Region region2 = new Region();
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        adaptiveIconDrawable.setBounds(0, 0, 200, 200);
        region2.setPath(adaptiveIconDrawable.getIconMask(), region);
        Path path = new Path();
        Region region3 = new Region();
        int i = Integer.MAX_VALUE;
        IconShape iconShape = null;
        for (IconShape iconShape2 : getAllShapes(context)) {
            path.reset();
            iconShape2.addToPath(path, 0.0f, 0.0f, 100.0f);
            region3.setPath(path, region);
            region3.op(region2, Region.Op.XOR);
            int area = GraphicsUtils.getArea(region3);
            if (area < i) {
                iconShape = iconShape2;
                i = area;
            }
        }
        if (iconShape != null) {
            sInstance = iconShape;
        }
        adaptiveIconDrawable.setBounds(0, 0, 100, 100);
        sShapePath = new Path(adaptiveIconDrawable.getIconMask());
        sNormalizationScale = com.android.launcher3.icons.IconNormalizer.normalizeAdaptiveIcon(adaptiveIconDrawable, 200, null);
    }
}

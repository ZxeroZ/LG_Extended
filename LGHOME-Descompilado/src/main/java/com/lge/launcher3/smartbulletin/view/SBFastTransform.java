package com.lge.launcher3.smartbulletin.view;

import android.graphics.RenderNode;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class SBFastTransform {
    RenderNode mRenderNode;
    Method mSetAlpha;
    View mView;

    public SBFastTransform(View view) {
        this.mView = null;
        this.mView = view;
        try {
            Field declaredField = Class.forName("android.view.View").getDeclaredField("mRenderNode");
            declaredField.setAccessible(true);
            this.mRenderNode = (RenderNode) declaredField.get(this.mView);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (NoSuchFieldException e4) {
            e4.printStackTrace();
        }
        try {
            Method declaredMethod = Class.forName("android.view.View").getDeclaredMethod("setAlphaNoInvalidation", Float.TYPE);
            this.mSetAlpha = declaredMethod;
            declaredMethod.setAccessible(true);
        } catch (ClassNotFoundException e5) {
            e5.printStackTrace();
        } catch (NoSuchMethodException e6) {
            e6.printStackTrace();
        }
    }

    public void setTranslationX(float translationX) {
        RenderNode renderNode = this.mRenderNode;
        if (renderNode == null) {
            this.mView.setTranslationX(translationX);
        } else if (Float.compare(translationX, renderNode.getTranslationY()) != 0) {
            this.mRenderNode.setTranslationX(translationX);
        }
    }

    public void setTranslationY(float translationY) {
        RenderNode renderNode = this.mRenderNode;
        if (renderNode == null) {
            this.mView.setTranslationY(translationY);
        } else if (Float.compare(translationY, renderNode.getTranslationY()) != 0) {
            this.mRenderNode.setTranslationY(translationY);
        }
    }

    public void setTranslationZ(float translationZ) {
        RenderNode renderNode = this.mRenderNode;
        if (renderNode == null) {
            this.mView.setTranslationZ(translationZ);
        } else if (Float.compare(translationZ, renderNode.getTranslationY()) != 0) {
            this.mRenderNode.setTranslationZ(translationZ);
        }
    }

    public void setScaleX(float scaleX) {
        RenderNode renderNode = this.mRenderNode;
        if (renderNode == null) {
            this.mView.setScaleX(scaleX);
        } else if (Float.compare(scaleX, renderNode.getScaleX()) != 0) {
            this.mRenderNode.setScaleX(scaleX);
            this.mView.notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void setScaleY(float scaleY) {
        RenderNode renderNode = this.mRenderNode;
        if (renderNode == null) {
            this.mView.setScaleY(scaleY);
        } else if (Float.compare(scaleY, renderNode.getScaleY()) != 0) {
            this.mRenderNode.setScaleY(scaleY);
            this.mView.notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void setAlpha(float alpha) {
        if (this.mSetAlpha == null) {
            this.mView.setAlpha(alpha);
            return;
        }
        if (Float.compare(alpha, this.mRenderNode.getAlpha()) != 0) {
            try {
                this.mSetAlpha.invoke(this.mView, Float.valueOf(alpha));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        }
    }
}

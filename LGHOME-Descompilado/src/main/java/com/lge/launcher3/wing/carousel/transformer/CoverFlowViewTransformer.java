package com.lge.launcher3.wing.carousel.transformer;

import android.view.View;
import com.android.launcher3.Launcher;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager;
import com.lge.launcher3.wing.carousel.widget.CarouselView;

/* JADX INFO: loaded from: classes2.dex */
public class CoverFlowViewTransformer extends ParameterizableViewTransformer {
    private double mYProjection = 35.0d;
    private float mEditModeScaleXFactor = -0.1f;
    private float mEditModeScaleYFactor = -0.13f;
    private float mEditModemOffsetXPercent = 1.05f;

    public CoverFlowViewTransformer() {
        setScaleXFactor(-0.15f);
        setScaleYFactor(-0.15f);
        setOffsetXPercent(1.05f);
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer, com.lge.launcher3.wing.carousel.widget.CarouselView.ViewTransformer
    public void onAttach(CarouselLayoutManager layoutManager) {
        layoutManager.setDrawOrder(CarouselView.DrawOrder.CenterFront);
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer
    public void setOffsetXPercent(float offsetXPercent) {
        super.setOffsetXPercent(offsetXPercent);
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer
    public float getOffsetXPercent() {
        return super.getOffsetXPercent();
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer
    public float getScaleYFactor() {
        return super.getScaleYFactor();
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer
    public void setScaleYFactor(float scaleYFactor) {
        super.setScaleYFactor(scaleYFactor);
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer
    public void setScaleXFactor(float scaleXFactor) {
        super.setScaleXFactor(scaleXFactor);
    }

    public double getYProjection() {
        return this.mYProjection;
    }

    public void setYProjection(double yProjectionDegree) {
        this.mYProjection = yProjectionDegree;
    }

    @Override // com.lge.launcher3.wing.carousel.transformer.ParameterizableViewTransformer, com.lge.launcher3.wing.carousel.widget.CarouselView.ViewTransformer
    public void transform(View view, float position) {
        float fSignum;
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        float f = measuredWidth;
        view.setPivotX(f / 2.0f);
        view.setPivotY(measuredHeight / 3.0f);
        float f2 = 0.0f;
        if (view.getContext() instanceof Launcher) {
            Launcher launcher = (Launcher) view.getContext();
            if (launcher.getWorkspace() != null && launcher.getWorkspace().isInOverviewMode()) {
                float f3 = f * position;
                float left = view.getLeft() + (this.mEditModemOffsetXPercent * f3) + (f3 * Math.abs(position) * ((this.mEditModeScaleXFactor * Math.abs(position)) + 1.0f) * (-0.07f));
                view.layout(Math.round(left), view.getTop(), Math.round(left + f), view.getBottom());
                view.setRotationY(0.0f);
                view.setScaleX((this.mEditModeScaleXFactor * Math.abs(position)) + 1.0f);
                view.setScaleY((this.mEditModeScaleYFactor * Math.abs(position)) + 1.0f);
                return;
            }
        }
        if (Math.abs(position) > 1.0f) {
            fSignum = Math.signum(position);
            f2 = -((float) (Math.log(Math.abs(position)) * 0.9d * 0.14000000059604645d));
        } else {
            fSignum = position;
        }
        if (OrientationUtils.isPortrait(view.getContext())) {
            this.mYProjection = 65.0d;
            setScaleXFactor(-0.2f);
            setScaleYFactor(-0.2f);
            setOffsetXPercent(0.6f);
            f2 = -f2;
        } else {
            this.mYProjection = 35.0d;
            setScaleXFactor(-0.15f);
            setScaleYFactor(-0.15f);
            setOffsetXPercent(1.09f);
        }
        float f4 = f * position;
        float left2 = view.getLeft() + (this.mOffsetXPercent * f4) + (f4 * Math.abs(position) * (-0.11f)) + (fSignum * f * f2);
        view.layout(Math.round(left2), view.getTop(), Math.round(left2 + f), view.getBottom());
        view.setRotationY(Math.signum(position) * ((float) ((Math.log(Math.abs(fSignum) + 1.0f) / Math.log(3.0d)) * (-this.mYProjection))));
        view.setScaleX((this.mScaleXFactor * Math.abs(position)) + 1.0f);
        view.setScaleY((this.mScaleYFactor * Math.abs(position)) + 1.0f);
    }
}

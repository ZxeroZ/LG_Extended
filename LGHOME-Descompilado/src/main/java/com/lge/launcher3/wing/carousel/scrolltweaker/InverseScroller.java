package com.lge.launcher3.wing.carousel.scrolltweaker;

import com.lge.launcher3.wing.carousel.widget.CarouselView;

/* JADX INFO: loaded from: classes2.dex */
public class InverseScroller implements CarouselView.Scroller {
    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public int inverseTweakScrollDx(int dx) {
        return -dx;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public int inverseTweakScrollDy(int dy) {
        return -dy;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public float tweakScrollDx(float dx) {
        return -dx;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public int tweakScrollDx(int dx) {
        return -dx;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public float tweakScrollDy(float dy) {
        return -dy;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.Scroller
    public int tweakScrollDy(int dy) {
        return -dy;
    }
}

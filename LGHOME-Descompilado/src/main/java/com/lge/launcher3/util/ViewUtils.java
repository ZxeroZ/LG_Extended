package com.lge.launcher3.util;

import android.view.ViewGroup;
import android.view.ViewParent;

/* JADX INFO: loaded from: classes.dex */
public class ViewUtils {
    public static void traverseToSetClip(ViewGroup view, boolean clip, int recursiveLevel) {
        ViewParent parent;
        if (view == null) {
            return;
        }
        if (recursiveLevel > 0 && (parent = view.getParent()) != null && (parent instanceof ViewGroup)) {
            traverseToSetClip((ViewGroup) parent, clip, recursiveLevel - 1);
        }
        view.setClipChildren(false);
        view.setClipToPadding(false);
    }
}

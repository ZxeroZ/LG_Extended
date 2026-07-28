package com.zxerox.lg_extended.hooks;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class RecentsHook {

    private static final float STACK_GAP = 160f;
    private static final Map<View, Float> lastScaleMap = new WeakHashMap<>();

    public void hook(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.lge.launcher3") && !lpparam.packageName.equals("com.android.launcher3")) {
            return;
        }

        // Corner Radius
        hookSilently(lpparam.classLoader, "com.android.quickstep.util.TaskCornerRadius", "get", android.content.Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                android.content.Context ctx = (android.content.Context) param.args[0];
                float density = ctx.getResources().getDisplayMetrics().density;
                param.setResult(26f * density); // More rounded corners
            }
        });

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.TaskView", "setFullscreenProgress", float.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                float original = (float) param.args[0];
                View view = (View) param.thisObject;

                Float baseScale = lastScaleMap.get(view);
                float from = (baseScale != null) ? baseScale : 1.0f;
                // Soft interpolation
                float interpolated = from + (1.0f - from) * original;

                view.setScaleX(interpolated);
                view.setScaleY(interpolated);
            }
        });

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.TaskView", "setDimAlpha", float.class, new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam param) { param.args[0] = 0.0f; }
        });

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.TaskView", "onFinishInflate", new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) { ((View) param.thisObject).setElevation(0.0f); }
        });

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.TaskView", "onTaskListVisibilityChanged", boolean.class, new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam param) { param.args[0] = true; }
        });

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.RecentsView", "updateStackLayout", new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam param) { param.setResult(null); }
        });

        XC_MethodHook scrollHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ViewGroup rv = (ViewGroup) param.thisObject;
                int count = rv.getChildCount();
                int scrollX = rv.getScrollX();
                float width = rv.getWidth();

                if (count == 0 || width == 0) return;

                float nativeSpacing = width;
                View firstTask = null;
                View secondTask = null;
                for (int i = 0; i < count; i++) {
                    View v = rv.getChildAt(i);
                    if (v != null && v.getClass().getSimpleName().equals("TaskView")) {
                        if (firstTask == null) firstTask = v;
                        else if (secondTask == null) {
                            secondTask = v;
                            break;
                        }
                    }
                }
                if (firstTask != null && secondTask != null) {
                    nativeSpacing = Math.abs(secondTask.getLeft() - firstTask.getLeft());
                }
                if (nativeSpacing <= 0) nativeSpacing = width;

                float stackGapAhead = nativeSpacing * 0.65f;
                float screenCenter = scrollX + (width / 2f);

                List<View> taskViews = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    View v = rv.getChildAt(i);
                    if (v != null && v.getClass().getSimpleName().equals("TaskView")) {
                        taskViews.add(v);
                    }
                }
                taskViews.sort((a, b) -> Float.compare(b.getLeft(), a.getLeft()));

                Map<View, Integer> rankMap = new HashMap<>();
                for (int r = 0; r < taskViews.size(); r++) {
                    rankMap.put(taskViews.get(r), r);
                }

                for (int i = 0; i < count; i++) {
                    View child = rv.getChildAt(i);
                    if (child == null || !child.getClass().getSimpleName().equals("TaskView")) continue;

                    float childCenter = child.getLeft() + (child.getWidth() / 2f);
                    float distanceToCenter = childCenter - screenCenter;
                    float progress = distanceToCenter / nativeSpacing;

                    if (distanceToCenter < 0) {
                        float targetScreenPos = progress * STACK_GAP;
                        child.setTranslationX(targetScreenPos - distanceToCenter);
                    } else if (distanceToCenter > 0) {
                        float targetScreenPos = progress * stackGapAhead;
                        child.setTranslationX(targetScreenPos - distanceToCenter);
                    } else {
                        child.setTranslationX(0f);
                    }

                    int rank = rankMap.getOrDefault(child, 0);
                    float scale;
                    if (rank <= 3) {
                        scale = 1.0f - (rank * 0.01f);
                    } else {
                        scale = 1.0f - (3 * 0.01f); // Mantener escala fija después de la 3ra tarjeta
                    }
                    scale = Math.max(0.70f, scale);
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    child.setTranslationZ(child.getLeft() * 0.01f);

                    try {
                        ViewGroup headerView = (ViewGroup) XposedHelpers.getObjectField(child, "mHeaderView");
                        if (headerView != null) {
                            // Fades out much faster so only the focused card (progress ~ 0) shows the title
                            float titleAlpha = Math.max(0.0f, 1.0f - (Math.abs(progress) * 2.5f));
                            
                            headerView.setScaleX(0.85f);
                            headerView.setScaleY(0.85f);
                            headerView.setTranslationY(15f);
                            
                            for (int j = 0; j < headerView.getChildCount(); j++) {
                                View hChild = headerView.getChildAt(j);
                                if (hChild instanceof TextView) {
                                    hChild.setAlpha(titleAlpha);
                                }
                            }
                        }
                    } catch (Throwable t) {}
                }
            }
        };

        hookSilently(lpparam.classLoader, "com.android.quickstep.views.RecentsView", "updateStackProperties", scrollHook);
        hookSilently(lpparam.classLoader, "com.android.quickstep.views.RecentsView", "updateCurveProperties", scrollHook);
        hookSilently(lpparam.classLoader, "com.android.quickstep.views.RecentsView", "updateCurveProperties", boolean.class, scrollHook);
    }

    private void hookSilently(ClassLoader classLoader, String className, String methodName, Object... params) {
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, params);
        } catch (Throwable t) {}
    }
}
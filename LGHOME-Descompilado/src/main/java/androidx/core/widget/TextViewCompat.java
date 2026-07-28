package androidx.core.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build;
import android.text.Editable;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class TextViewCompat {
    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
    private static final int LINES = 1;
    private static final String LOG_TAG = "TextViewCompat";
    private static Field sMaxModeField;
    private static boolean sMaxModeFieldFetched;
    private static Field sMaximumField;
    private static boolean sMaximumFieldFetched;
    private static Field sMinModeField;
    private static boolean sMinModeFieldFetched;
    private static Field sMinimumField;
    private static boolean sMinimumFieldFetched;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoSizeTextType {
    }

    private TextViewCompat() {
    }

    private static Field retrieveField(String fieldName) {
        Field declaredField = null;
        try {
            declaredField = TextView.class.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException unused) {
            Log.e(LOG_TAG, "Could not retrieve " + fieldName + " field.");
            return declaredField;
        }
    }

    private static int retrieveIntFromField(Field field, TextView textView) {
        try {
            return field.getInt(textView);
        } catch (IllegalAccessException unused) {
            Log.d(LOG_TAG, "Could not retrieve value of " + field.getName() + " field.");
            return -1;
        }
    }

    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            boolean z = textView.getLayoutDirection() == 1;
            Drawable drawable = z ? end : start;
            if (!z) {
                start = end;
            }
            textView.setCompoundDrawables(drawable, top, start, bottom);
            return;
        }
        textView.setCompoundDrawables(start, top, end, bottom);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            boolean z = textView.getLayoutDirection() == 1;
            Drawable drawable = z ? end : start;
            if (!z) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, top, start, bottom);
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            boolean z = textView.getLayoutDirection() == 1;
            int i = z ? end : start;
            if (!z) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(i, top, start, bottom);
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
    }

    public static int getMaxLines(TextView textView) {
        if (Build.VERSION.SDK_INT >= 16) {
            return textView.getMaxLines();
        }
        if (!sMaxModeFieldFetched) {
            sMaxModeField = retrieveField("mMaxMode");
            sMaxModeFieldFetched = true;
        }
        Field field = sMaxModeField;
        if (field == null || retrieveIntFromField(field, textView) != 1) {
            return -1;
        }
        if (!sMaximumFieldFetched) {
            sMaximumField = retrieveField("mMaximum");
            sMaximumFieldFetched = true;
        }
        Field field2 = sMaximumField;
        if (field2 != null) {
            return retrieveIntFromField(field2, textView);
        }
        return -1;
    }

    public static int getMinLines(TextView textView) {
        if (Build.VERSION.SDK_INT >= 16) {
            return textView.getMinLines();
        }
        if (!sMinModeFieldFetched) {
            sMinModeField = retrieveField("mMinMode");
            sMinModeFieldFetched = true;
        }
        Field field = sMinModeField;
        if (field == null || retrieveIntFromField(field, textView) != 1) {
            return -1;
        }
        if (!sMinimumFieldFetched) {
            sMinimumField = retrieveField("mMinimum");
            sMinimumFieldFetched = true;
        }
        Field field2 = sMinimumField;
        if (field2 != null) {
            return retrieveIntFromField(field2, textView);
        }
        return -1;
    }

    public static void setTextAppearance(TextView textView, int resId) {
        if (Build.VERSION.SDK_INT >= 23) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    public static Drawable[] getCompoundDrawablesRelative(TextView textView) {
        if (Build.VERSION.SDK_INT >= 18) {
            return textView.getCompoundDrawablesRelative();
        }
        if (Build.VERSION.SDK_INT >= 17) {
            boolean z = textView.getLayoutDirection() == 1;
            Drawable[] compoundDrawables = textView.getCompoundDrawables();
            if (z) {
                Drawable drawable = compoundDrawables[2];
                Drawable drawable2 = compoundDrawables[0];
                compoundDrawables[0] = drawable;
                compoundDrawables[2] = drawable2;
            }
            return compoundDrawables;
        }
        return textView.getCompoundDrawables();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setAutoSizeTextTypeWithDefaults(TextView textView, int autoSizeTextType) {
        if (Build.VERSION.SDK_INT >= 27) {
            textView.setAutoSizeTextTypeWithDefaults(autoSizeTextType);
        } else if (textView instanceof AutoSizeableTextView) {
            ((AutoSizeableTextView) textView).setAutoSizeTextTypeWithDefaults(autoSizeTextType);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
        if (Build.VERSION.SDK_INT >= 27) {
            textView.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
        } else if (textView instanceof AutoSizeableTextView) {
            ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] presetSizes, int unit) throws IllegalArgumentException {
        if (Build.VERSION.SDK_INT >= 27) {
            textView.setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
        } else if (textView instanceof AutoSizeableTextView) {
            ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static int getAutoSizeTextType(TextView textView) {
        if (Build.VERSION.SDK_INT >= 27) {
            return textView.getAutoSizeTextType();
        }
        if (textView instanceof AutoSizeableTextView) {
            return ((AutoSizeableTextView) textView).getAutoSizeTextType();
        }
        return 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static int getAutoSizeStepGranularity(TextView textView) {
        if (Build.VERSION.SDK_INT >= 27) {
            return textView.getAutoSizeStepGranularity();
        }
        if (textView instanceof AutoSizeableTextView) {
            return ((AutoSizeableTextView) textView).getAutoSizeStepGranularity();
        }
        return -1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static int getAutoSizeMinTextSize(TextView textView) {
        if (Build.VERSION.SDK_INT >= 27) {
            return textView.getAutoSizeMinTextSize();
        }
        if (textView instanceof AutoSizeableTextView) {
            return ((AutoSizeableTextView) textView).getAutoSizeMinTextSize();
        }
        return -1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static int getAutoSizeMaxTextSize(TextView textView) {
        if (Build.VERSION.SDK_INT >= 27) {
            return textView.getAutoSizeMaxTextSize();
        }
        if (textView instanceof AutoSizeableTextView) {
            return ((AutoSizeableTextView) textView).getAutoSizeMaxTextSize();
        }
        return -1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static int[] getAutoSizeTextAvailableSizes(TextView textView) {
        if (Build.VERSION.SDK_INT >= 27) {
            return textView.getAutoSizeTextAvailableSizes();
        }
        return textView instanceof AutoSizeableTextView ? ((AutoSizeableTextView) textView).getAutoSizeTextAvailableSizes() : new int[0];
    }

    public static void setCustomSelectionActionModeCallback(final TextView textView, final ActionMode.Callback callback) {
        textView.setCustomSelectionActionModeCallback(wrapCustomSelectionActionModeCallback(textView, callback));
    }

    public static ActionMode.Callback wrapCustomSelectionActionModeCallback(final TextView textView, final ActionMode.Callback callback) {
        return (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT > 27 || (callback instanceof OreoCallback) || callback == null) ? callback : new OreoCallback(callback, textView);
    }

    public static ActionMode.Callback unwrapCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        return (!(callback instanceof OreoCallback) || Build.VERSION.SDK_INT < 26) ? callback : ((OreoCallback) callback).getWrappedCallback();
    }

    private static class OreoCallback implements ActionMode.Callback {
        private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
        private final ActionMode.Callback mCallback;
        private boolean mCanUseMenuBuilderReferences;
        private boolean mInitializedMenuBuilderReferences = false;
        private Class<?> mMenuBuilderClass;
        private Method mMenuBuilderRemoveItemAtMethod;
        private final TextView mTextView;

        OreoCallback(ActionMode.Callback callback, TextView textView) {
            this.mCallback = callback;
            this.mTextView = textView;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mCallback.onCreateActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            recomputeProcessTextMenuItems(menu);
            return this.mCallback.onPrepareActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mCallback.onActionItemClicked(mode, item);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            this.mCallback.onDestroyActionMode(mode);
        }

        ActionMode.Callback getWrappedCallback() {
            return this.mCallback;
        }

        private void recomputeProcessTextMenuItems(final Menu menu) {
            Method declaredMethod;
            Context context = this.mTextView.getContext();
            PackageManager packageManager = context.getPackageManager();
            if (!this.mInitializedMenuBuilderReferences) {
                this.mInitializedMenuBuilderReferences = true;
                try {
                    Class<?> cls = Class.forName("com.android.internal.view.menu.MenuBuilder");
                    this.mMenuBuilderClass = cls;
                    this.mMenuBuilderRemoveItemAtMethod = cls.getDeclaredMethod("removeItemAt", Integer.TYPE);
                    this.mCanUseMenuBuilderReferences = true;
                } catch (ClassNotFoundException | NoSuchMethodException unused) {
                    this.mMenuBuilderClass = null;
                    this.mMenuBuilderRemoveItemAtMethod = null;
                    this.mCanUseMenuBuilderReferences = false;
                }
            }
            try {
                if (this.mCanUseMenuBuilderReferences && this.mMenuBuilderClass.isInstance(menu)) {
                    declaredMethod = this.mMenuBuilderRemoveItemAtMethod;
                } else {
                    declaredMethod = menu.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                }
                for (int size = menu.size() - 1; size >= 0; size--) {
                    MenuItem item = menu.getItem(size);
                    if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                        declaredMethod.invoke(menu, Integer.valueOf(size));
                    }
                }
                List<ResolveInfo> supportedActivities = getSupportedActivities(context, packageManager);
                for (int i = 0; i < supportedActivities.size(); i++) {
                    ResolveInfo resolveInfo = supportedActivities.get(i);
                    menu.add(0, 0, i + 100, resolveInfo.loadLabel(packageManager)).setIntent(createProcessTextIntentForResolveInfo(resolveInfo, this.mTextView)).setShowAsAction(1);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused2) {
            }
        }

        private List<ResolveInfo> getSupportedActivities(final Context context, final PackageManager packageManager) {
            ArrayList arrayList = new ArrayList();
            if (!(context instanceof Activity)) {
                return arrayList;
            }
            for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(createProcessTextIntent(), 0)) {
                if (isSupportedActivity(resolveInfo, context)) {
                    arrayList.add(resolveInfo);
                }
            }
            return arrayList;
        }

        private boolean isSupportedActivity(final ResolveInfo info, final Context context) {
            if (context.getPackageName().equals(info.activityInfo.packageName)) {
                return true;
            }
            if (info.activityInfo.exported) {
                return info.activityInfo.permission == null || context.checkSelfPermission(info.activityInfo.permission) == 0;
            }
            return false;
        }

        private Intent createProcessTextIntentForResolveInfo(final ResolveInfo info, final TextView textView11) {
            return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !isEditable(textView11)).setClassName(info.activityInfo.packageName, info.activityInfo.name);
        }

        private boolean isEditable(final TextView textView11) {
            return (textView11 instanceof Editable) && textView11.onCheckIsTextEditor() && textView11.isEnabled();
        }

        private Intent createProcessTextIntent() {
            return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
        }
    }

    public static void setFirstBaselineToTopHeight(final TextView textView, final int firstBaselineToTopHeight) {
        int i;
        Preconditions.checkArgumentNonnegative(firstBaselineToTopHeight);
        if (Build.VERSION.SDK_INT >= 28) {
            textView.setFirstBaselineToTopHeight(firstBaselineToTopHeight);
            return;
        }
        Paint.FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        if (Build.VERSION.SDK_INT < 16 || textView.getIncludeFontPadding()) {
            i = fontMetricsInt.top;
        } else {
            i = fontMetricsInt.ascent;
        }
        if (firstBaselineToTopHeight > Math.abs(i)) {
            textView.setPadding(textView.getPaddingLeft(), firstBaselineToTopHeight + i, textView.getPaddingRight(), textView.getPaddingBottom());
        }
    }

    public static void setLastBaselineToBottomHeight(final TextView textView, int lastBaselineToBottomHeight) {
        int i;
        Preconditions.checkArgumentNonnegative(lastBaselineToBottomHeight);
        Paint.FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        if (Build.VERSION.SDK_INT < 16 || textView.getIncludeFontPadding()) {
            i = fontMetricsInt.bottom;
        } else {
            i = fontMetricsInt.descent;
        }
        if (lastBaselineToBottomHeight > Math.abs(i)) {
            textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), lastBaselineToBottomHeight - i);
        }
    }

    public static int getFirstBaselineToTopHeight(final TextView textView) {
        return textView.getPaddingTop() - textView.getPaint().getFontMetricsInt().top;
    }

    public static int getLastBaselineToBottomHeight(final TextView textView) {
        return textView.getPaddingBottom() + textView.getPaint().getFontMetricsInt().bottom;
    }

    public static void setLineHeight(final TextView textView, int lineHeight) {
        Preconditions.checkArgumentNonnegative(lineHeight);
        if (lineHeight != textView.getPaint().getFontMetricsInt(null)) {
            textView.setLineSpacing(lineHeight - r0, 1.0f);
        }
    }

    public static PrecomputedTextCompat.Params getTextMetricsParams(final TextView textView) {
        if (Build.VERSION.SDK_INT >= 28) {
            return new PrecomputedTextCompat.Params(textView.getTextMetricsParams());
        }
        PrecomputedTextCompat.Params.Builder builder = new PrecomputedTextCompat.Params.Builder(new TextPaint(textView.getPaint()));
        if (Build.VERSION.SDK_INT >= 23) {
            builder.setBreakStrategy(textView.getBreakStrategy());
            builder.setHyphenationFrequency(textView.getHyphenationFrequency());
        }
        if (Build.VERSION.SDK_INT >= 18) {
            builder.setTextDirection(getTextDirectionHeuristic(textView));
        }
        return builder.build();
    }

    public static void setTextMetricsParams(TextView textView, PrecomputedTextCompat.Params params) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setTextDirection(getTextDirection(params.getTextDirection()));
        }
        if (Build.VERSION.SDK_INT < 23) {
            float textScaleX = params.getTextPaint().getTextScaleX();
            textView.getPaint().set(params.getTextPaint());
            if (textScaleX == textView.getTextScaleX()) {
                textView.setTextScaleX((textScaleX / 2.0f) + 1.0f);
            }
            textView.setTextScaleX(textScaleX);
            return;
        }
        textView.getPaint().set(params.getTextPaint());
        textView.setBreakStrategy(params.getBreakStrategy());
        textView.setHyphenationFrequency(params.getHyphenationFrequency());
    }

    public static void setPrecomputedText(TextView textView, PrecomputedTextCompat precomputed) {
        if (Build.VERSION.SDK_INT >= 29) {
            textView.setText(precomputed.getPrecomputedText());
        } else {
            if (!getTextMetricsParams(textView).equalsWithoutTextDirection(precomputed.getParams())) {
                throw new IllegalArgumentException("Given text can not be applied to TextView.");
            }
            textView.setText(precomputed);
        }
    }

    private static TextDirectionHeuristic getTextDirectionHeuristic(TextView textView) {
        if (textView.getTransformationMethod() instanceof PasswordTransformationMethod) {
            return TextDirectionHeuristics.LTR;
        }
        if (Build.VERSION.SDK_INT >= 28 && (textView.getInputType() & 15) == 3) {
            byte directionality = Character.getDirectionality(DecimalFormatSymbols.getInstance(textView.getTextLocale()).getDigitStrings()[0].codePointAt(0));
            if (directionality == 1 || directionality == 2) {
                return TextDirectionHeuristics.RTL;
            }
            return TextDirectionHeuristics.LTR;
        }
        boolean z = textView.getLayoutDirection() == 1;
        switch (textView.getTextDirection()) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                if (!z) {
                }
                break;
        }
        return TextDirectionHeuristics.LTR;
    }

    private static int getTextDirection(TextDirectionHeuristic heuristic) {
        if (heuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL || heuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            return 1;
        }
        if (heuristic == TextDirectionHeuristics.ANYRTL_LTR) {
            return 2;
        }
        if (heuristic == TextDirectionHeuristics.LTR) {
            return 3;
        }
        if (heuristic == TextDirectionHeuristics.RTL) {
            return 4;
        }
        if (heuristic == TextDirectionHeuristics.LOCALE) {
            return 5;
        }
        if (heuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            return 6;
        }
        return heuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL ? 7 : 1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setCompoundDrawableTintList(TextView textView, ColorStateList tint) {
        Preconditions.checkNotNull(textView);
        if (Build.VERSION.SDK_INT >= 24) {
            textView.setCompoundDrawableTintList(tint);
        } else if (textView instanceof TintableCompoundDrawablesView) {
            ((TintableCompoundDrawablesView) textView).setSupportCompoundDrawablesTintList(tint);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static ColorStateList getCompoundDrawableTintList(TextView textView) {
        Preconditions.checkNotNull(textView);
        if (Build.VERSION.SDK_INT >= 24) {
            return textView.getCompoundDrawableTintList();
        }
        if (textView instanceof TintableCompoundDrawablesView) {
            return ((TintableCompoundDrawablesView) textView).getSupportCompoundDrawablesTintList();
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setCompoundDrawableTintMode(TextView textView, PorterDuff.Mode tintMode) {
        Preconditions.checkNotNull(textView);
        if (Build.VERSION.SDK_INT >= 24) {
            textView.setCompoundDrawableTintMode(tintMode);
        } else if (textView instanceof TintableCompoundDrawablesView) {
            ((TintableCompoundDrawablesView) textView).setSupportCompoundDrawablesTintMode(tintMode);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.TextView */
    /* JADX WARN: Multi-variable type inference failed */
    public static PorterDuff.Mode getCompoundDrawableTintMode(TextView textView) {
        Preconditions.checkNotNull(textView);
        if (Build.VERSION.SDK_INT >= 24) {
            return textView.getCompoundDrawableTintMode();
        }
        if (textView instanceof TintableCompoundDrawablesView) {
            return ((TintableCompoundDrawablesView) textView).getSupportCompoundDrawablesTintMode();
        }
        return null;
    }
}

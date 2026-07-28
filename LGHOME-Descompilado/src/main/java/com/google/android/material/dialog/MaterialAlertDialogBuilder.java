package com.google.android.material.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialAttributes;
import com.google.android.material.shape.MaterialShapeDrawable;

/* JADX INFO: loaded from: classes.dex */
public class MaterialAlertDialogBuilder extends AlertDialog.Builder {
    private static final int DEF_STYLE_ATTR = R.attr.alertDialogStyle;
    private static final int DEF_STYLE_RES = R.style.MaterialAlertDialog_MaterialComponents;
    private static final int MATERIAL_ALERT_DIALOG_THEME_OVERLAY = R.attr.materialAlertDialogTheme;
    private Drawable background;
    private final Rect backgroundInsets;

    private static int getMaterialAlertDialogThemeOverlay(Context context) {
        TypedValue typedValueResolve = MaterialAttributes.resolve(context, MATERIAL_ALERT_DIALOG_THEME_OVERLAY);
        if (typedValueResolve == null) {
            return 0;
        }
        return typedValueResolve.data;
    }

    private static Context createMaterialAlertDialogThemedContext(Context context) {
        int materialAlertDialogThemeOverlay = getMaterialAlertDialogThemeOverlay(context);
        Context contextCreateThemedContext = ThemeEnforcement.createThemedContext(context, null, DEF_STYLE_ATTR, DEF_STYLE_RES);
        return materialAlertDialogThemeOverlay == 0 ? contextCreateThemedContext : new ContextThemeWrapper(contextCreateThemedContext, materialAlertDialogThemeOverlay);
    }

    private static int getOverridingThemeResId(Context context, int i) {
        return i == 0 ? getMaterialAlertDialogThemeOverlay(context) : i;
    }

    public MaterialAlertDialogBuilder(Context context) {
        this(context, 0);
    }

    public MaterialAlertDialogBuilder(Context context, int i) {
        super(createMaterialAlertDialogThemedContext(context), getOverridingThemeResId(context, i));
        Context context2 = getContext();
        Resources.Theme theme = context2.getTheme();
        int i2 = DEF_STYLE_ATTR;
        int i3 = DEF_STYLE_RES;
        this.backgroundInsets = MaterialDialogs.getDialogBackgroundInsets(context2, i2, i3);
        int color = MaterialColors.getColor(context2, R.attr.colorSurface, getClass().getCanonicalName());
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(context2, null, i2, i3);
        materialShapeDrawable.initializeElevationOverlay(context2);
        materialShapeDrawable.setFillColor(ColorStateList.valueOf(color));
        if (Build.VERSION.SDK_INT >= 28) {
            TypedValue typedValue = new TypedValue();
            theme.resolveAttribute(android.R.attr.dialogCornerRadius, typedValue, true);
            float dimension = typedValue.getDimension(getContext().getResources().getDisplayMetrics());
            if (typedValue.type == 5 && dimension >= 0.0f) {
                materialShapeDrawable.setCornerRadius(dimension);
            }
        }
        this.background = materialShapeDrawable;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog create() {
        AlertDialog alertDialogCreate = super.create();
        Window window = alertDialogCreate.getWindow();
        View decorView = window.getDecorView();
        Drawable drawable = this.background;
        if (drawable instanceof MaterialShapeDrawable) {
            ((MaterialShapeDrawable) drawable).setElevation(ViewCompat.getElevation(decorView));
        }
        window.setBackgroundDrawable(MaterialDialogs.insetDrawable(this.background, this.backgroundInsets));
        decorView.setOnTouchListener(new InsetDialogOnTouchListener(alertDialogCreate, this.backgroundInsets));
        return alertDialogCreate;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public MaterialAlertDialogBuilder setBackground(Drawable drawable) {
        this.background = drawable;
        return this;
    }

    public MaterialAlertDialogBuilder setBackgroundInsetStart(int i) {
        if (Build.VERSION.SDK_INT >= 17 && getContext().getResources().getConfiguration().getLayoutDirection() == 1) {
            this.backgroundInsets.right = i;
        } else {
            this.backgroundInsets.left = i;
        }
        return this;
    }

    public MaterialAlertDialogBuilder setBackgroundInsetTop(int i) {
        this.backgroundInsets.top = i;
        return this;
    }

    public MaterialAlertDialogBuilder setBackgroundInsetEnd(int i) {
        if (Build.VERSION.SDK_INT >= 17 && getContext().getResources().getConfiguration().getLayoutDirection() == 1) {
            this.backgroundInsets.left = i;
        } else {
            this.backgroundInsets.right = i;
        }
        return this;
    }

    public MaterialAlertDialogBuilder setBackgroundInsetBottom(int i) {
        this.backgroundInsets.bottom = i;
        return this;
    }

    /* JADX DEBUG: Method merged with bridge method: setTitle(I)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setTitle(int i) {
        return (MaterialAlertDialogBuilder) super.setTitle(i);
    }

    /* JADX DEBUG: Method merged with bridge method: setTitle(Ljava/lang/CharSequence;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setTitle(CharSequence charSequence) {
        return (MaterialAlertDialogBuilder) super.setTitle(charSequence);
    }

    /* JADX DEBUG: Method merged with bridge method: setCustomTitle(Landroid/view/View;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setCustomTitle(View view) {
        return (MaterialAlertDialogBuilder) super.setCustomTitle(view);
    }

    /* JADX DEBUG: Method merged with bridge method: setMessage(I)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setMessage(int i) {
        return (MaterialAlertDialogBuilder) super.setMessage(i);
    }

    /* JADX DEBUG: Method merged with bridge method: setMessage(Ljava/lang/CharSequence;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setMessage(CharSequence charSequence) {
        return (MaterialAlertDialogBuilder) super.setMessage(charSequence);
    }

    /* JADX DEBUG: Method merged with bridge method: setIcon(I)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setIcon(int i) {
        return (MaterialAlertDialogBuilder) super.setIcon(i);
    }

    /* JADX DEBUG: Method merged with bridge method: setIcon(Landroid/graphics/drawable/Drawable;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setIcon(Drawable drawable) {
        return (MaterialAlertDialogBuilder) super.setIcon(drawable);
    }

    /* JADX DEBUG: Method merged with bridge method: setIconAttribute(I)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setIconAttribute(int i) {
        return (MaterialAlertDialogBuilder) super.setIconAttribute(i);
    }

    /* JADX DEBUG: Method merged with bridge method: setPositiveButton(ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setPositiveButton(i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setPositiveButton(charSequence, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setPositiveButtonIcon(Landroid/graphics/drawable/Drawable;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setPositiveButtonIcon(Drawable drawable) {
        return (MaterialAlertDialogBuilder) super.setPositiveButtonIcon(drawable);
    }

    /* JADX DEBUG: Method merged with bridge method: setNegativeButton(ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setNegativeButton(i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setNegativeButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setNegativeButton(charSequence, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setNegativeButtonIcon(Landroid/graphics/drawable/Drawable;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNegativeButtonIcon(Drawable drawable) {
        return (MaterialAlertDialogBuilder) super.setNegativeButtonIcon(drawable);
    }

    /* JADX DEBUG: Method merged with bridge method: setNeutralButton(ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setNeutralButton(i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setNeutralButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setNeutralButton(charSequence, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setNeutralButtonIcon(Landroid/graphics/drawable/Drawable;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setNeutralButtonIcon(Drawable drawable) {
        return (MaterialAlertDialogBuilder) super.setNeutralButtonIcon(drawable);
    }

    /* JADX DEBUG: Method merged with bridge method: setCancelable(Z)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setCancelable(boolean z) {
        return (MaterialAlertDialogBuilder) super.setCancelable(z);
    }

    /* JADX DEBUG: Method merged with bridge method: setOnCancelListener(Landroid/content/DialogInterface$OnCancelListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        return (MaterialAlertDialogBuilder) super.setOnCancelListener(onCancelListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setOnDismissListener(Landroid/content/DialogInterface$OnDismissListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        return (MaterialAlertDialogBuilder) super.setOnDismissListener(onDismissListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setOnKeyListener(Landroid/content/DialogInterface$OnKeyListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        return (MaterialAlertDialogBuilder) super.setOnKeyListener(onKeyListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setItems(ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setItems(int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setItems(i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setItems([Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setItems(charSequenceArr, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setAdapter(Landroid/widget/ListAdapter;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setAdapter(listAdapter, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setCursor(Landroid/database/Cursor;Landroid/content/DialogInterface$OnClickListener;Ljava/lang/String;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setCursor(Cursor cursor, DialogInterface.OnClickListener onClickListener, String str) {
        return (MaterialAlertDialogBuilder) super.setCursor(cursor, onClickListener, str);
    }

    /* JADX DEBUG: Method merged with bridge method: setMultiChoiceItems(I[ZLandroid/content/DialogInterface$OnMultiChoiceClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setMultiChoiceItems(int i, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        return (MaterialAlertDialogBuilder) super.setMultiChoiceItems(i, zArr, onMultiChoiceClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setMultiChoiceItems([Ljava/lang/CharSequence;[ZLandroid/content/DialogInterface$OnMultiChoiceClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setMultiChoiceItems(CharSequence[] charSequenceArr, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        return (MaterialAlertDialogBuilder) super.setMultiChoiceItems(charSequenceArr, zArr, onMultiChoiceClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setMultiChoiceItems(Landroid/database/Cursor;Ljava/lang/String;Ljava/lang/String;Landroid/content/DialogInterface$OnMultiChoiceClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setMultiChoiceItems(Cursor cursor, String str, String str2, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        return (MaterialAlertDialogBuilder) super.setMultiChoiceItems(cursor, str, str2, onMultiChoiceClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setSingleChoiceItems(IILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setSingleChoiceItems(int i, int i2, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setSingleChoiceItems(i, i2, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setSingleChoiceItems(Landroid/database/Cursor;ILjava/lang/String;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setSingleChoiceItems(Cursor cursor, int i, String str, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setSingleChoiceItems(cursor, i, str, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setSingleChoiceItems([Ljava/lang/CharSequence;ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setSingleChoiceItems(CharSequence[] charSequenceArr, int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setSingleChoiceItems(charSequenceArr, i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setSingleChoiceItems(Landroid/widget/ListAdapter;ILandroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setSingleChoiceItems(ListAdapter listAdapter, int i, DialogInterface.OnClickListener onClickListener) {
        return (MaterialAlertDialogBuilder) super.setSingleChoiceItems(listAdapter, i, onClickListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setOnItemSelectedListener(Landroid/widget/AdapterView$OnItemSelectedListener;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        return (MaterialAlertDialogBuilder) super.setOnItemSelectedListener(onItemSelectedListener);
    }

    /* JADX DEBUG: Method merged with bridge method: setView(I)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setView(int i) {
        return (MaterialAlertDialogBuilder) super.setView(i);
    }

    /* JADX DEBUG: Method merged with bridge method: setView(Landroid/view/View;)Landroidx/appcompat/app/AlertDialog$Builder; */
    @Override // androidx.appcompat.app.AlertDialog.Builder
    public MaterialAlertDialogBuilder setView(View view) {
        return (MaterialAlertDialogBuilder) super.setView(view);
    }
}

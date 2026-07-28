package com.google.android.material.navigation;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.customview.view.AbsSavedState;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuPresenter;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.shape.MaterialShapeDrawable;

/* JADX INFO: loaded from: classes.dex */
public class NavigationView extends ScrimInsetsFrameLayout {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int PRESENTER_NAVIGATION_VIEW_ID = 1;
    OnNavigationItemSelectedListener listener;
    private final int maxWidth;
    private final NavigationMenu menu;
    private MenuInflater menuInflater;
    private final NavigationMenuPresenter presenter;

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, com.google.android.material.R.attr.navigationViewStyle);
    }

    public NavigationView(Context context, AttributeSet attributeSet, int i) {
        ColorStateList colorStateListCreateDefaultColorStateList;
        int resourceId;
        boolean z;
        super(context, attributeSet, i);
        NavigationMenuPresenter navigationMenuPresenter = new NavigationMenuPresenter();
        this.presenter = navigationMenuPresenter;
        NavigationMenu navigationMenu = new NavigationMenu(context);
        this.menu = navigationMenu;
        TintTypedArray tintTypedArrayObtainTintedStyledAttributes = ThemeEnforcement.obtainTintedStyledAttributes(context, attributeSet, com.google.android.material.R.styleable.NavigationView, i, com.google.android.material.R.style.Widget_Design_NavigationView, new int[0]);
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_android_background)) {
            ViewCompat.setBackground(this, tintTypedArrayObtainTintedStyledAttributes.getDrawable(com.google.android.material.R.styleable.NavigationView_android_background));
        }
        if (getBackground() == null || (getBackground() instanceof ColorDrawable)) {
            Drawable background = getBackground();
            MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
            if (background instanceof ColorDrawable) {
                materialShapeDrawable.setFillColor(ColorStateList.valueOf(((ColorDrawable) background).getColor()));
            }
            materialShapeDrawable.initializeElevationOverlay(context);
            ViewCompat.setBackground(this, materialShapeDrawable);
        }
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_elevation)) {
            setElevation(tintTypedArrayObtainTintedStyledAttributes.getDimensionPixelSize(com.google.android.material.R.styleable.NavigationView_elevation, 0));
        }
        setFitsSystemWindows(tintTypedArrayObtainTintedStyledAttributes.getBoolean(com.google.android.material.R.styleable.NavigationView_android_fitsSystemWindows, false));
        this.maxWidth = tintTypedArrayObtainTintedStyledAttributes.getDimensionPixelSize(com.google.android.material.R.styleable.NavigationView_android_maxWidth, 0);
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_itemIconTint)) {
            colorStateListCreateDefaultColorStateList = tintTypedArrayObtainTintedStyledAttributes.getColorStateList(com.google.android.material.R.styleable.NavigationView_itemIconTint);
        } else {
            colorStateListCreateDefaultColorStateList = createDefaultColorStateList(R.attr.textColorSecondary);
        }
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_itemTextAppearance)) {
            resourceId = tintTypedArrayObtainTintedStyledAttributes.getResourceId(com.google.android.material.R.styleable.NavigationView_itemTextAppearance, 0);
            z = true;
        } else {
            resourceId = 0;
            z = false;
        }
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_itemIconSize)) {
            setItemIconSize(tintTypedArrayObtainTintedStyledAttributes.getDimensionPixelSize(com.google.android.material.R.styleable.NavigationView_itemIconSize, 0));
        }
        ColorStateList colorStateList = tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_itemTextColor) ? tintTypedArrayObtainTintedStyledAttributes.getColorStateList(com.google.android.material.R.styleable.NavigationView_itemTextColor) : null;
        if (!z && colorStateList == null) {
            colorStateList = createDefaultColorStateList(R.attr.textColorPrimary);
        }
        Drawable drawable = tintTypedArrayObtainTintedStyledAttributes.getDrawable(com.google.android.material.R.styleable.NavigationView_itemBackground);
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_itemHorizontalPadding)) {
            navigationMenuPresenter.setItemHorizontalPadding(tintTypedArrayObtainTintedStyledAttributes.getDimensionPixelSize(com.google.android.material.R.styleable.NavigationView_itemHorizontalPadding, 0));
        }
        int dimensionPixelSize = tintTypedArrayObtainTintedStyledAttributes.getDimensionPixelSize(com.google.android.material.R.styleable.NavigationView_itemIconPadding, 0);
        setItemMaxLines(tintTypedArrayObtainTintedStyledAttributes.getInt(com.google.android.material.R.styleable.NavigationView_itemMaxLines, 1));
        navigationMenu.setCallback(new MenuBuilder.Callback() { // from class: com.google.android.material.navigation.NavigationView.1
            @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
            public void onMenuModeChange(MenuBuilder menuBuilder) {
            }

            @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                return NavigationView.this.listener != null && NavigationView.this.listener.onNavigationItemSelected(menuItem);
            }
        });
        navigationMenuPresenter.setId(1);
        navigationMenuPresenter.initForMenu(context, navigationMenu);
        navigationMenuPresenter.setItemIconTintList(colorStateListCreateDefaultColorStateList);
        if (z) {
            navigationMenuPresenter.setItemTextAppearance(resourceId);
        }
        navigationMenuPresenter.setItemTextColor(colorStateList);
        navigationMenuPresenter.setItemBackground(drawable);
        navigationMenuPresenter.setItemIconPadding(dimensionPixelSize);
        navigationMenu.addMenuPresenter(navigationMenuPresenter);
        addView((View) navigationMenuPresenter.getMenuView(this));
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_menu)) {
            inflateMenu(tintTypedArrayObtainTintedStyledAttributes.getResourceId(com.google.android.material.R.styleable.NavigationView_menu, 0));
        }
        if (tintTypedArrayObtainTintedStyledAttributes.hasValue(com.google.android.material.R.styleable.NavigationView_headerLayout)) {
            inflateHeaderView(tintTypedArrayObtainTintedStyledAttributes.getResourceId(com.google.android.material.R.styleable.NavigationView_headerLayout, 0));
        }
        tintTypedArrayObtainTintedStyledAttributes.recycle();
    }

    @Override // android.view.View
    public void setElevation(float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.setElevation(f);
        }
        Drawable background = getBackground();
        if (background instanceof MaterialShapeDrawable) {
            ((MaterialShapeDrawable) background).setElevation(f);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuState = new Bundle();
        this.menu.savePresenterStates(savedState.menuState);
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.menu.restorePresenterStates(savedState.menuState);
    }

    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        this.listener = onNavigationItemSelectedListener;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            i = View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), this.maxWidth), 1073741824);
        } else if (mode == 0) {
            i = View.MeasureSpec.makeMeasureSpec(this.maxWidth, 1073741824);
        }
        super.onMeasure(i, i2);
    }

    @Override // com.google.android.material.internal.ScrimInsetsFrameLayout
    protected void onInsetsChanged(WindowInsetsCompat windowInsetsCompat) {
        this.presenter.dispatchApplyWindowInsets(windowInsetsCompat);
    }

    public void inflateMenu(int i) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(i, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(false);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public View inflateHeaderView(int i) {
        return this.presenter.inflateHeaderView(i);
    }

    public void addHeaderView(View view) {
        this.presenter.addHeaderView(view);
    }

    public void removeHeaderView(View view) {
        this.presenter.removeHeaderView(view);
    }

    public int getHeaderCount() {
        return this.presenter.getHeaderCount();
    }

    public View getHeaderView(int i) {
        return this.presenter.getHeaderView(i);
    }

    public ColorStateList getItemIconTintList() {
        return this.presenter.getItemTintList();
    }

    public void setItemIconTintList(ColorStateList colorStateList) {
        this.presenter.setItemIconTintList(colorStateList);
    }

    public ColorStateList getItemTextColor() {
        return this.presenter.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList colorStateList) {
        this.presenter.setItemTextColor(colorStateList);
    }

    public Drawable getItemBackground() {
        return this.presenter.getItemBackground();
    }

    public void setItemBackgroundResource(int i) {
        setItemBackground(ContextCompat.getDrawable(getContext(), i));
    }

    public void setItemBackground(Drawable drawable) {
        this.presenter.setItemBackground(drawable);
    }

    public int getItemHorizontalPadding() {
        return this.presenter.getItemHorizontalPadding();
    }

    public void setItemHorizontalPadding(int i) {
        this.presenter.setItemHorizontalPadding(i);
    }

    public void setItemHorizontalPaddingResource(int i) {
        this.presenter.setItemHorizontalPadding(getResources().getDimensionPixelSize(i));
    }

    public int getItemIconPadding() {
        return this.presenter.getItemIconPadding();
    }

    public void setItemIconPadding(int i) {
        this.presenter.setItemIconPadding(i);
    }

    public void setItemIconPaddingResource(int i) {
        this.presenter.setItemIconPadding(getResources().getDimensionPixelSize(i));
    }

    public void setCheckedItem(int i) {
        MenuItem menuItemFindItem = this.menu.findItem(i);
        if (menuItemFindItem != null) {
            this.presenter.setCheckedItem((MenuItemImpl) menuItemFindItem);
        }
    }

    public void setCheckedItem(MenuItem menuItem) {
        MenuItem menuItemFindItem = this.menu.findItem(menuItem.getItemId());
        if (menuItemFindItem != null) {
            this.presenter.setCheckedItem((MenuItemImpl) menuItemFindItem);
            return;
        }
        throw new IllegalArgumentException("Called setCheckedItem(MenuItem) with an item that is not in the current menu.");
    }

    public MenuItem getCheckedItem() {
        return this.presenter.getCheckedItem();
    }

    public void setItemTextAppearance(int i) {
        this.presenter.setItemTextAppearance(i);
    }

    public void setItemIconSize(int i) {
        this.presenter.setItemIconSize(i);
    }

    public void setItemMaxLines(int i) {
        this.presenter.setItemMaxLines(i);
    }

    public int getItemMaxLines() {
        return this.presenter.getItemMaxLines();
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    private ColorStateList createDefaultColorStateList(int i) {
        TypedValue typedValue = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            return null;
        }
        ColorStateList colorStateList = AppCompatResources.getColorStateList(getContext(), typedValue.resourceId);
        if (!getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
            return null;
        }
        int i2 = typedValue.data;
        int defaultColor = colorStateList.getDefaultColor();
        int[] iArr = DISABLED_STATE_SET;
        return new ColorStateList(new int[][]{iArr, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{colorStateList.getColorForState(iArr, defaultColor), i2, defaultColor});
    }

    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: com.google.android.material.navigation.NavigationView.SavedState.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;Ljava/lang/ClassLoader;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public Bundle menuState;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.menuState = parcel.readBundle(classLoader);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeBundle(this.menuState);
        }
    }
}

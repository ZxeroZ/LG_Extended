package com.google.android.material.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.core.view.MarginLayoutParamsCompat;
import com.google.android.material.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.shape.ShapeAppearanceModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MaterialButtonToggleGroup extends RelativeLayout {
    private static final String LOG_TAG = "MaterialButtonToggleGroup";
    private int checkedId;
    private final CheckedStateTracker checkedStateTracker;
    private final ArrayList<MaterialButton> childrenInOrder;
    private final LinkedHashSet<OnButtonCheckedListener> onButtonCheckedListeners;
    private final ArrayList<CornerData> originalCornerData;
    private final PressedStateTracker pressedStateTracker;
    private boolean singleSelection;
    private boolean skipCheckedStateTracker;

    public interface OnButtonCheckedListener {
        void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup, int i, boolean z);
    }

    public MaterialButtonToggleGroup(Context context) {
        this(context, null);
    }

    public MaterialButtonToggleGroup(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.materialButtonToggleGroupStyle);
    }

    public MaterialButtonToggleGroup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.childrenInOrder = new ArrayList<>();
        this.originalCornerData = new ArrayList<>();
        this.checkedStateTracker = new CheckedStateTracker();
        this.pressedStateTracker = new PressedStateTracker();
        this.onButtonCheckedListeners = new LinkedHashSet<>();
        this.skipCheckedStateTracker = false;
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R.styleable.MaterialButtonToggleGroup, i, R.style.Widget_MaterialComponents_MaterialButtonToggleGroup, new int[0]);
        setSingleSelection(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialButtonToggleGroup_singleSelection, false));
        this.checkedId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MaterialButtonToggleGroup_checkedButton, -1);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        int i = this.checkedId;
        if (i != -1) {
            checkForced(i);
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (!(view instanceof MaterialButton)) {
            Log.e(LOG_TAG, "Child views must be of type MaterialButton.");
            return;
        }
        MaterialButton materialButton = (MaterialButton) view;
        setGeneratedIdIfNeeded(materialButton);
        int childCount = i >= 0 ? i : getChildCount();
        super.addView(materialButton, i, new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height));
        this.childrenInOrder.add(childCount, materialButton);
        setupButtonChild(materialButton);
        if (materialButton.isChecked()) {
            updateCheckedStates(materialButton.getId(), true);
            setCheckedId(materialButton.getId());
        }
        ShapeAppearanceModel shapeAppearanceModel = materialButton.getShapeAppearanceModel();
        this.originalCornerData.add(new CornerData(shapeAppearanceModel.getTopLeftCorner().getCornerSize(), shapeAppearanceModel.getTopRightCorner().getCornerSize(), shapeAppearanceModel.getBottomRightCorner().getCornerSize(), shapeAppearanceModel.getBottomLeftCorner().getCornerSize()));
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (view instanceof MaterialButton) {
            MaterialButton materialButton = (MaterialButton) view;
            materialButton.removeOnCheckedChangeListener(this.checkedStateTracker);
            materialButton.setOnPressedChangeListenerInternal(null);
        }
        int iIndexOf = this.childrenInOrder.indexOf(view);
        if (iIndexOf >= 0) {
            this.childrenInOrder.remove(view);
            this.originalCornerData.remove(iIndexOf);
        }
        updateChildShapes();
        adjustChildMarginsAndUpdateLayout();
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        updateChildShapes();
        adjustChildMarginsAndUpdateLayout();
        super.onMeasure(i, i2);
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return MaterialButtonToggleGroup.class.getName();
    }

    public void check(int i) {
        if (i == this.checkedId) {
            return;
        }
        checkForced(i);
    }

    public void clearChecked() {
        this.skipCheckedStateTracker = true;
        for (int i = 0; i < getChildCount(); i++) {
            MaterialButton materialButton = this.childrenInOrder.get(i);
            materialButton.setChecked(false);
            dispatchOnButtonChecked(materialButton.getId(), false);
        }
        this.skipCheckedStateTracker = false;
        setCheckedId(-1);
    }

    public int getCheckedButtonId() {
        if (this.singleSelection) {
            return this.checkedId;
        }
        return -1;
    }

    public List<Integer> getCheckedButtonIds() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < getChildCount(); i++) {
            MaterialButton materialButton = this.childrenInOrder.get(i);
            if (materialButton.isChecked()) {
                arrayList.add(Integer.valueOf(materialButton.getId()));
            }
        }
        return arrayList;
    }

    public void addOnButtonCheckedListener(OnButtonCheckedListener onButtonCheckedListener) {
        this.onButtonCheckedListeners.add(onButtonCheckedListener);
    }

    public void removeOnButtonCheckedListener(OnButtonCheckedListener onButtonCheckedListener) {
        this.onButtonCheckedListeners.remove(onButtonCheckedListener);
    }

    public void clearOnButtonCheckedListeners() {
        this.onButtonCheckedListeners.clear();
    }

    public boolean isSingleSelection() {
        return this.singleSelection;
    }

    public void setSingleSelection(boolean z) {
        if (this.singleSelection != z) {
            this.singleSelection = z;
            clearChecked();
        }
    }

    public void setSingleSelection(int i) {
        setSingleSelection(getResources().getBoolean(i));
    }

    private void setCheckedStateForView(int i, boolean z) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById instanceof MaterialButton) {
            this.skipCheckedStateTracker = true;
            ((MaterialButton) viewFindViewById).setChecked(z);
            this.skipCheckedStateTracker = false;
        }
    }

    private void setCheckedId(int i) {
        this.checkedId = i;
        dispatchOnButtonChecked(i, true);
    }

    private void adjustChildMarginsAndUpdateLayout() {
        for (int i = 1; i < getChildCount(); i++) {
            MaterialButton materialButton = this.childrenInOrder.get(i);
            MaterialButton materialButton2 = this.childrenInOrder.get(i - 1);
            int iMin = Math.min(materialButton.getStrokeWidth(), materialButton2.getStrokeWidth());
            RelativeLayout.LayoutParams layoutParamsBuildEndAlignLayoutParams = buildEndAlignLayoutParams(materialButton2, materialButton);
            MarginLayoutParamsCompat.setMarginEnd(layoutParamsBuildEndAlignLayoutParams, 0);
            int i2 = iMin * (-1);
            if (MarginLayoutParamsCompat.getMarginStart(layoutParamsBuildEndAlignLayoutParams) != i2) {
                MarginLayoutParamsCompat.setMarginStart(layoutParamsBuildEndAlignLayoutParams, i2);
            }
            materialButton.setLayoutParams(layoutParamsBuildEndAlignLayoutParams);
        }
        resetFirstChildMargin();
    }

    private void resetFirstChildMargin() {
        if (this.childrenInOrder.isEmpty()) {
            return;
        }
        MaterialButton materialButton = this.childrenInOrder.get(0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) materialButton.getLayoutParams();
        MarginLayoutParamsCompat.setMarginEnd(layoutParams, 0);
        MarginLayoutParamsCompat.setMarginStart(layoutParams, 0);
        materialButton.setLayoutParams(layoutParams);
    }

    private void updateChildShapes() {
        int childCount = getChildCount();
        if (childCount >= 1) {
            for (int i = 0; i < childCount; i++) {
                MaterialButton materialButton = this.childrenInOrder.get(i);
                if (materialButton.getShapeAppearanceModel() != null) {
                    ShapeAppearanceModel shapeAppearanceModel = materialButton.getShapeAppearanceModel();
                    CornerData cornerData = this.originalCornerData.get(i);
                    if (childCount == 1) {
                        shapeAppearanceModel.setCornerRadii(cornerData.topLeft, cornerData.topRight, cornerData.bottomRight, cornerData.bottomLeft);
                    } else if (i == (ViewUtils.isLayoutRtl(this) ? childCount - 1 : 0)) {
                        shapeAppearanceModel.setCornerRadii(cornerData.topLeft, 0.0f, 0.0f, cornerData.bottomLeft);
                    } else if (i != 0 && i < childCount - 1) {
                        shapeAppearanceModel.setCornerRadius(0.0f);
                    } else if (i == (ViewUtils.isLayoutRtl(this) ? 0 : childCount - 1)) {
                        shapeAppearanceModel.setCornerRadii(0.0f, cornerData.topRight, cornerData.bottomRight, 0.0f);
                    }
                    materialButton.setShapeAppearanceModel(shapeAppearanceModel);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedStates(int i, boolean z) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            MaterialButton materialButton = this.childrenInOrder.get(i2);
            if (materialButton.isChecked()) {
                if (this.singleSelection && z && materialButton.getId() != i) {
                    setCheckedStateForView(materialButton.getId(), false);
                    dispatchOnButtonChecked(materialButton.getId(), false);
                } else {
                    materialButton.bringToFront();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnButtonChecked(int i, boolean z) {
        Iterator<OnButtonCheckedListener> it = this.onButtonCheckedListeners.iterator();
        while (it.hasNext()) {
            it.next().onButtonChecked(this, i, z);
        }
    }

    private void checkForced(int i) {
        setCheckedStateForView(i, true);
        updateCheckedStates(i, true);
        setCheckedId(i);
    }

    private void setGeneratedIdIfNeeded(MaterialButton materialButton) {
        int iHashCode;
        if (materialButton.getId() == -1) {
            if (Build.VERSION.SDK_INT >= 17) {
                iHashCode = View.generateViewId();
            } else {
                iHashCode = materialButton.hashCode();
            }
            materialButton.setId(iHashCode);
        }
    }

    private void setupButtonChild(MaterialButton materialButton) {
        materialButton.setMaxLines(1);
        materialButton.setEllipsize(TextUtils.TruncateAt.END);
        materialButton.setCheckable(true);
        materialButton.addOnCheckedChangeListener(this.checkedStateTracker);
        materialButton.setOnPressedChangeListenerInternal(this.pressedStateTracker);
        materialButton.setShouldDrawSurfaceColorStroke(true);
    }

    private RelativeLayout.LayoutParams buildEndAlignLayoutParams(View view, View view2) {
        ViewGroup.LayoutParams layoutParams = view2.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
        if (view == null) {
            return layoutParams2;
        }
        layoutParams2.addRule(!ViewUtils.isLayoutRtl(this) ? 1 : 0, view.getId());
        return layoutParams2;
    }

    private class CheckedStateTracker implements MaterialButton.OnCheckedChangeListener {
        private CheckedStateTracker() {
        }

        @Override // com.google.android.material.button.MaterialButton.OnCheckedChangeListener
        public void onCheckedChanged(MaterialButton materialButton, boolean z) {
            if (MaterialButtonToggleGroup.this.skipCheckedStateTracker) {
                return;
            }
            if (MaterialButtonToggleGroup.this.singleSelection) {
                MaterialButtonToggleGroup.this.checkedId = z ? materialButton.getId() : -1;
            }
            MaterialButtonToggleGroup.this.dispatchOnButtonChecked(materialButton.getId(), z);
            MaterialButtonToggleGroup.this.updateCheckedStates(materialButton.getId(), z);
        }
    }

    private class PressedStateTracker implements MaterialButton.OnPressedChangeListener {
        private PressedStateTracker() {
        }

        @Override // com.google.android.material.button.MaterialButton.OnPressedChangeListener
        public void onPressedChanged(MaterialButton materialButton, boolean z) {
            if (!z) {
                MaterialButtonToggleGroup.this.updateCheckedStates(materialButton.getId(), materialButton.isChecked());
            } else {
                materialButton.bringToFront();
            }
        }
    }

    private static class CornerData {
        final float bottomLeft;
        final float bottomRight;
        final float topLeft;
        final float topRight;

        CornerData(float f, float f2, float f3, float f4) {
            this.topLeft = f;
            this.topRight = f2;
            this.bottomRight = f3;
            this.bottomLeft = f4;
        }
    }
}

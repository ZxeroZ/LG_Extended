package com.google.android.material.picker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.picker.MaterialCalendar;
import com.google.android.material.picker.selector.GridSelector;
import com.google.android.material.resources.MaterialAttributes;
import java.text.SimpleDateFormat;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public abstract class MaterialPickerDialogFragment<S> extends DialogFragment {
    private static final String CALENDAR_BOUNDS_KEY = "CALENDAR_BOUNDS_KEY";
    public static final Object CANCEL_BUTTON_TAG;
    public static final Object CONFIRM_BUTTON_TAG;
    public static final CalendarBounds DEFAULT_BOUNDS;
    public static final Month DEFAULT_END;
    public static final Month DEFAULT_START;
    private static final String GRID_SELECTOR_KEY = "GRID_SELECTOR_KEY";
    private static final String THEME_RES_ID_KEY = "THEME_RES_ID";
    private CalendarBounds calendarBounds;
    private GridSelector<S> gridSelector;
    private TextView header;
    private MaterialCalendar<S> materialCalendar;
    private S selection;
    private SimpleDateFormat simpleDateFormat;
    private int themeResId;

    protected abstract GridSelector<S> createGridSelector();

    protected abstract int getDefaultThemeAttr();

    protected abstract String getHeaderText(S s);

    static {
        Month monthCreate = Month.create(1900, 0);
        DEFAULT_START = monthCreate;
        Month monthCreate2 = Month.create(2100, 11);
        DEFAULT_END = monthCreate2;
        DEFAULT_BOUNDS = CalendarBounds.create(monthCreate, monthCreate2);
        CONFIRM_BUTTON_TAG = "CONFIRM_BUTTON_TAG";
        CANCEL_BUTTON_TAG = "CANCEL_BUTTON_TAG";
    }

    protected static void addArgsToBundle(Bundle bundle, int i, CalendarBounds calendarBounds) {
        bundle.putInt(THEME_RES_ID_KEY, i);
        bundle.putParcelable(CALENDAR_BOUNDS_KEY, calendarBounds);
    }

    private static int getThemeResource(Context context, int i, int i2) {
        return i2 != 0 ? i2 : MaterialAttributes.resolveOrThrow(context, i, MaterialPickerDialogFragment.class.getCanonicalName());
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(THEME_RES_ID_KEY, this.themeResId);
        bundle.putParcelable(GRID_SELECTOR_KEY, this.gridSelector);
        bundle.putParcelable(CALENDAR_BOUNDS_KEY, this.calendarBounds);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.mtrl_picker_date_format), Locale.getDefault());
        if (bundle == null) {
            bundle = getArguments();
        }
        this.themeResId = getThemeResource(getContext(), getDefaultThemeAttr(), bundle.getInt(THEME_RES_ID_KEY));
        this.gridSelector = (GridSelector) bundle.getParcelable(GRID_SELECTOR_KEY);
        this.calendarBounds = (CalendarBounds) bundle.getParcelable(CALENDAR_BOUNDS_KEY);
        setStyle(2, this.themeResId);
        if (this.gridSelector == null) {
            this.gridSelector = createGridSelector();
        }
        this.materialCalendar = MaterialCalendar.newInstance(this.gridSelector, this.themeResId, this.calendarBounds);
    }

    @Override // androidx.fragment.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        return new Dialog(requireContext(), this.themeResId);
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.mtrl_picker_dialog, viewGroup);
        this.header = (TextView) viewInflate.findViewById(R.id.date_picker_header_title);
        MaterialButton materialButton = (MaterialButton) viewInflate.findViewById(R.id.confirm_button);
        materialButton.setTag(CONFIRM_BUTTON_TAG);
        materialButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.picker.MaterialPickerDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MaterialPickerDialogFragment materialPickerDialogFragment = MaterialPickerDialogFragment.this;
                materialPickerDialogFragment.selection = materialPickerDialogFragment.materialCalendar.getSelection();
                MaterialPickerDialogFragment.this.dismiss();
            }
        });
        MaterialButton materialButton2 = (MaterialButton) viewInflate.findViewById(R.id.cancel_button);
        materialButton2.setTag(CANCEL_BUTTON_TAG);
        materialButton2.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.picker.MaterialPickerDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MaterialPickerDialogFragment.this.selection = null;
                MaterialPickerDialogFragment.this.dismiss();
            }
        });
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentTransaction fragmentTransactionBeginTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.calendar_frame, this.materialCalendar);
        fragmentTransactionBeginTransaction.commit();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateHeader(this.materialCalendar.getSelection());
        this.materialCalendar.addOnSelectionChangedListener(new MaterialCalendar.OnSelectionChangedListener<S>() { // from class: com.google.android.material.picker.MaterialPickerDialogFragment.3
            @Override // com.google.android.material.picker.MaterialCalendar.OnSelectionChangedListener
            public void onSelectionChanged(S s) {
                MaterialPickerDialogFragment.this.updateHeader(s);
            }
        });
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        this.materialCalendar.clearOnSelectionChangedListeners();
        super.onStop();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialogInterface) {
        ViewGroup viewGroup = (ViewGroup) getView();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        super.onDismiss(dialogInterface);
    }

    public final S getSelection() {
        return this.selection;
    }

    public final void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public final SimpleDateFormat getSimpleDateFormat() {
        return this.simpleDateFormat;
    }

    public final MaterialCalendar<? extends S> getMaterialCalendar() {
        return this.materialCalendar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeader(S s) {
        this.header.setText(getHeaderText(s));
    }
}

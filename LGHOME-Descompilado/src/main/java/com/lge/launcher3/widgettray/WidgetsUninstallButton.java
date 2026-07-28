package com.lge.launcher3.widgettray;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Launcher;
import com.android.launcher3.widget.WidgetsContainerView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsUninstallButton extends ImageButton implements View.OnClickListener {
    MODE mCurMode;
    private ViewPager mViewPager;
    private WidgetsViewPagerAdapter mViewPagerAdapter;
    private WidgetContainerCallbacks mWidgetContainerCallbacks;

    enum MODE {
        NORMAL,
        UNINSTALL
    }

    public WidgetsUninstallButton(Context context) {
        this(context, null);
    }

    public WidgetsUninstallButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsUninstallButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCurMode = MODE.NORMAL;
        setOnClickListener(this);
    }

    public void setmWidgetContainerCallbacks(WidgetContainerCallbacks callbacks) {
        this.mWidgetContainerCallbacks = callbacks;
    }

    public void init(WidgetsContainerView wcv, Launcher launcher, WidgetsViewPagerAdapter adapter) {
        this.mViewPagerAdapter = adapter;
        this.mViewPager = (ViewPager) wcv.findViewById(R.id.widgets_list_view);
        setColorFilter(Utilities.sWhite);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.widgettray.WidgetsUninstallButton$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$widgettray$WidgetsUninstallButton$MODE;

        static {
            int[] iArr = new int[MODE.values().length];
            $SwitchMap$com$lge$launcher3$widgettray$WidgetsUninstallButton$MODE = iArr;
            try {
                iArr[MODE.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$widgettray$WidgetsUninstallButton$MODE[MODE.UNINSTALL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$widgettray$WidgetsUninstallButton$MODE[this.mCurMode.ordinal()];
        if (i == 1) {
            setUninstallMode(true);
        } else {
            if (i != 2) {
                return;
            }
            setUninstallMode(false);
        }
    }

    public void setUninstallMode(boolean turnOn) {
        WidgetContainerCallbacks widgetContainerCallbacks = this.mWidgetContainerCallbacks;
        if (widgetContainerCallbacks != null) {
            widgetContainerCallbacks.onChange(turnOn);
        }
        if (turnOn) {
            this.mCurMode = MODE.UNINSTALL;
            setImageDrawable(getContext().getDrawable(R.drawable.ic_t_done));
            setContentDescription(getResources().getString(R.string.delete_target_delete_label) + getResources().getString(R.string.disable_target_label));
            this.mViewPagerAdapter.setUninstallBadge(this.mViewPager, true);
            return;
        }
        this.mCurMode = MODE.NORMAL;
        setImageDrawable(getContext().getDrawable(R.drawable.ic_t_delete));
        setContentDescription(getResources().getString(R.string.delete_target_delete_label));
        this.mViewPagerAdapter.setUninstallBadge(this.mViewPager, false);
    }

    public boolean isUninstallMode() {
        return this.mCurMode != MODE.NORMAL;
    }
}

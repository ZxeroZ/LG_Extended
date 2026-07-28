package com.android.launcher3.shortcuts;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupItemView;
import com.android.launcher3.popup.PopupPopulator;
import com.android.launcher3.popup.SystemShortcut;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutsItemView extends PopupItemView implements View.OnLongClickListener, View.OnTouchListener {
    private final List<DeepShortcutView> mDeepShortcutViews;
    private final Point mIconLastTouchPos;
    private final Point mIconShift;
    private Launcher mLauncher;
    private final List<View> mShortcutViews;
    private LinearLayout mShortcutsLayout;
    private LinearLayout mSystemShortcutIcons;
    private final List<View> mSystemShortcutViews;

    public ShortcutsItemView(Context context) {
        this(context, null, 0);
    }

    public ShortcutsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortcutsItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIconShift = new Point();
        this.mIconLastTouchPos = new Point();
        this.mDeepShortcutViews = new ArrayList();
        this.mShortcutViews = new ArrayList();
        this.mSystemShortcutViews = new ArrayList();
        this.mLauncher = Launcher.getLauncher(context);
    }

    @Override // com.android.launcher3.popup.PopupItemView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mShortcutsLayout = (LinearLayout) findViewById(R.id.deep_shortcuts);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent ev) {
        int action = ev.getAction();
        if (action != 0 && action != 2) {
            return false;
        }
        this.mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
        return false;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (!(v.getParent() instanceof DeepShortcutView) || !this.mLauncher.isDraggingEnabled() || this.mLauncher.getDragController().isDragging()) {
            return false;
        }
        DeepShortcutView deepShortcutView = (DeepShortcutView) v.getParent();
        deepShortcutView.setWillDrawIcon(false);
        this.mIconShift.x = this.mIconLastTouchPos.x - deepShortcutView.getIconCenter().x;
        this.mIconShift.y = this.mIconLastTouchPos.y - this.mLauncher.getDeviceProfile().iconSizePx;
        AbstractFloatingView.closeOpenContainer(this.mLauncher, 1);
        return false;
    }

    public void addShortcutView(View shortcutView, PopupPopulator.Item shortcutType) {
        addShortcutView(shortcutView, shortcutType, -1);
    }

    private void addShortcutView(View shortcutView, PopupPopulator.Item shortcutType, int index) {
        int i;
        if (shortcutType == PopupPopulator.Item.SHORTCUT || ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) && shortcutType == PopupPopulator.Item.SHORTCUT_SWIVEL)) {
            this.mDeepShortcutViews.add((DeepShortcutView) shortcutView);
        } else {
            this.mSystemShortcutViews.add(shortcutView);
        }
        if (shortcutType == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON || ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) && shortcutType == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON_SWIVEL)) {
            if (this.mSystemShortcutIcons == null) {
                LayoutInflater layoutInflater = this.mLauncher.getLayoutInflater();
                if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                    i = R.layout.system_shortcut_icons_swivel;
                } else {
                    i = Utilities.isLGUI10_0() ? R.layout.system_shortcut_icons_ux10_0 : R.layout.system_shortcut_icons;
                }
                LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(i, (ViewGroup) this.mShortcutsLayout, false);
                this.mSystemShortcutIcons = linearLayout;
                this.mShortcutsLayout.addView(linearLayout, 0);
            }
            this.mSystemShortcutIcons.addView(shortcutView, index);
            return;
        }
        if (this.mShortcutsLayout.getChildCount() > 0) {
            View childAt = this.mShortcutsLayout.getChildAt(r5.getChildCount() - 1);
            if (childAt instanceof DeepShortcutView) {
                childAt.findViewById(R.id.divider).setVisibility(0);
            }
        }
        this.mShortcutsLayout.addView(shortcutView, index);
    }

    public List<DeepShortcutView> getDeepShortcutViews(boolean reverseOrder) {
        if (reverseOrder) {
            Collections.reverse(this.mDeepShortcutViews);
        }
        return this.mDeepShortcutViews;
    }

    public List<View> getSystemShortcutViews(boolean reverseOrder) {
        if (reverseOrder || this.mSystemShortcutIcons != null) {
            Collections.reverse(this.mSystemShortcutViews);
        }
        return this.mSystemShortcutViews;
    }

    public int getSystemShortcutViewsSize() {
        if (this.mSystemShortcutIcons == null) {
            return 0;
        }
        return this.mSystemShortcutViews.size();
    }

    public void enableWidgetsIfExist(final BubbleTextView originalIcon) {
        View next;
        PopupPopulator.Item item;
        SystemShortcut<T> shortcut = SystemShortcut.WIDGETS.getShortcut(this.mLauncher, (ItemInfo) originalIcon.getTag());
        Iterator<View> it = this.mSystemShortcutViews.iterator();
        while (true) {
            if (!it.hasNext()) {
                next = null;
                break;
            } else {
                next = it.next();
                if (next.getTag() instanceof SystemShortcut.Widgets) {
                    break;
                }
            }
        }
        if (this.mSystemShortcutIcons == null) {
            item = PopupPopulator.Item.SYSTEM_SHORTCUT;
        } else if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            item = PopupPopulator.Item.SYSTEM_SHORTCUT_ICON_SWIVEL;
        } else {
            item = PopupPopulator.Item.SYSTEM_SHORTCUT_ICON;
        }
        if (next == null) {
            View viewInflate = this.mLauncher.getLayoutInflater().inflate(item.layoutId, (ViewGroup) this, false);
            PopupPopulator.initializeSystemShortcut(getContext(), viewInflate, shortcut);
            viewInflate.setOnClickListener(shortcut);
            if (item == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON || ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) && item == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON_SWIVEL)) {
                addShortcutView(viewInflate, item, 0);
                return;
            } else {
                ((PopupContainerWithArrow) getParent()).close(false);
                PopupContainerWithArrow.showForIcon(originalIcon);
                return;
            }
        }
        if (next != null) {
            if (item == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON || ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) && item == PopupPopulator.Item.SYSTEM_SHORTCUT_ICON_SWIVEL)) {
                this.mSystemShortcutViews.remove(next);
                this.mSystemShortcutIcons.removeView(next);
            } else {
                ((PopupContainerWithArrow) getParent()).close(false);
                PopupContainerWithArrow.showForIcon(originalIcon);
            }
        }
    }

    @Override // com.android.launcher3.popup.PopupItemView
    public Animator createOpenAnimation(boolean isContainerAboveIcon, boolean pivotLeft) {
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        animatorSetCreateAnimatorSet.play(super.createOpenAnimation(isContainerAboveIcon, pivotLeft));
        for (int i = 0; i < this.mShortcutsLayout.getChildCount(); i++) {
            if (this.mShortcutsLayout.getChildAt(i) instanceof DeepShortcutView) {
                View iconView = ((DeepShortcutView) this.mShortcutsLayout.getChildAt(i)).getIconView();
                iconView.setScaleX(0.0f);
                iconView.setScaleY(0.0f);
                animatorSetCreateAnimatorSet.play(LauncherAnimUtils.ofPropertyValuesHolder(iconView, new PropertyListBuilder().scale(1.0f).build()));
            }
        }
        return animatorSetCreateAnimatorSet;
    }

    @Override // com.android.launcher3.popup.PopupItemView
    public Animator createCloseAnimation(boolean isContainerAboveIcon, boolean pivotLeft, long duration) {
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        animatorSetCreateAnimatorSet.play(super.createCloseAnimation(isContainerAboveIcon, pivotLeft, duration));
        for (int i = 0; i < this.mShortcutsLayout.getChildCount(); i++) {
            if (this.mShortcutsLayout.getChildAt(i) instanceof DeepShortcutView) {
                View iconView = ((DeepShortcutView) this.mShortcutsLayout.getChildAt(i)).getIconView();
                iconView.setScaleX(1.0f);
                iconView.setScaleY(1.0f);
                animatorSetCreateAnimatorSet.play(LauncherAnimUtils.ofPropertyValuesHolder(iconView, new PropertyListBuilder().scale(0.0f).build()));
            }
        }
        return animatorSetCreateAnimatorSet;
    }
}

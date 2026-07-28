package com.lge.launcher3.uninstallmode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.dragndrop.ConeShortcut;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TalkBackUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeleteItemDialog {
    public static final String TAG = "DeleteItemDialog";

    public static void showDialogFragment(Launcher launcher, ItemInfo itemInfo, View view, DropTarget.DragObject dragObject) {
        if (dragObject == null || !(dragObject.dragSource instanceof ConeShortcut)) {
            String simpleName = DeleteDialogFragment.class.getSimpleName();
            FragmentManager fragmentManager = launcher.getFragmentManager();
            FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
            Fragment fragmentFindFragmentByTag = fragmentManager.findFragmentByTag(simpleName);
            if (fragmentFindFragmentByTag != null) {
                fragmentTransactionBeginTransaction.remove(fragmentFindFragmentByTag);
            }
            if ((itemInfo instanceof AllAppsItemInfo) || (!LGHomeFeature.isDisableAllApps() && HomeSettingsSharedPreferences.getDeletePopupDialogDisable(launcher.getApplicationContext()))) {
                DeleteDropTarget.removeWorkspaceOrFolderItem(launcher, itemInfo, view);
                TalkBackUtils.sendAccessibilityEvent((Context) launcher, R.string.removed, true);
                if (dragObject != null) {
                    DragSource dragSource = dragObject.dragSource;
                    if (dragSource instanceof UninstallDropTarget.UninstallSource) {
                        ((UninstallDropTarget.UninstallSource) dragSource).onUninstallActivityReturned(true);
                        return;
                    }
                    return;
                }
                return;
            }
            try {
                new DeleteDialogFragment(launcher, itemInfo, view, dragObject).show(fragmentTransactionBeginTransaction, simpleName);
            } catch (IllegalStateException e) {
                Log.e(TAG, e.toString());
                if (dragObject != null) {
                    DragSource dragSource2 = dragObject.dragSource;
                    if (dragSource2 instanceof UninstallDropTarget.UninstallSource) {
                        ((UninstallDropTarget.UninstallSource) dragSource2).onUninstallActivityReturned(false);
                    }
                }
            }
        }
    }

    public static class DeleteDialogFragment extends DialogFragment {
        private CheckBox checkBox;
        private Dialog dialog;
        private DropTarget.DragObject mDragObject;
        private boolean mIsSuccess;
        private ItemInfo mItemInfo;
        private Launcher mLauncher;
        private View mView;

        public DeleteDialogFragment() {
            this.mLauncher = null;
            this.mItemInfo = null;
            this.mView = null;
            this.mIsSuccess = false;
            this.mDragObject = null;
            this.dialog = null;
        }

        public DeleteDialogFragment(Launcher launcher, ItemInfo itemInfo, View view, DropTarget.DragObject dragObject) {
            this.mLauncher = null;
            this.mItemInfo = null;
            this.mView = null;
            this.mIsSuccess = false;
            this.mDragObject = null;
            this.dialog = null;
            this.mLauncher = launcher;
            this.mItemInfo = itemInfo;
            this.mView = view;
            this.mDragObject = dragObject;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (this.mLauncher == null) {
                LGLog.e(DeleteItemDialog.TAG, "Ignore dialog since mLauncher is null");
                return null;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mLauncher);
            if (!LGHomeFeature.isDisableAllApps()) {
                if (GVNUtils.isGiovanna(getContext())) {
                    setDialogMessageWithImage(builder);
                } else {
                    setDialogMessage(builder);
                }
            } else {
                builder.setMessage(getMessageResId(this.mItemInfo));
            }
            builder.setPositiveButton(R.string.smartbulletin_remove, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.uninstallmode.DeleteItemDialog.DeleteDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    DeleteDropTarget.removeWorkspaceOrFolderItem(DeleteDialogFragment.this.mLauncher, DeleteDialogFragment.this.mItemInfo, DeleteDialogFragment.this.mView);
                    DeleteDialogFragment.this.mIsSuccess = true;
                    TalkBackUtils.sendAccessibilityEvent((Context) DeleteDialogFragment.this.mLauncher, R.string.removed, true);
                    if (LGHomeFeature.isDisableAllApps() || !DeleteDialogFragment.this.checkBox.isChecked()) {
                        return;
                    }
                    HomeSettingsSharedPreferences.setDeletePopupDialogDisable(DeleteDialogFragment.this.getContext(), true);
                }
            });
            builder.setNegativeButton(R.string.droptarget_cancel, (DialogInterface.OnClickListener) null);
            AlertDialog alertDialogCreate = builder.create();
            this.dialog = alertDialogCreate;
            return alertDialogCreate;
        }

        private void setDialogMessage(AlertDialog.Builder dialogBuilder) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View viewInflate = layoutInflater.inflate(33751061, (ViewGroup) null);
            LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(android.R.id.content);
            View viewInflate2 = layoutInflater.inflate(33751045, (ViewGroup) linearLayout, false);
            ((TextView) viewInflate2.findViewById(android.R.id.text1)).setText(getContext().getString(getMessageResId(this.mItemInfo)));
            View viewInflate3 = layoutInflater.inflate(33751052, (ViewGroup) linearLayout, false);
            CheckBox checkBox = (CheckBox) viewInflate3.findViewById(android.R.id.checkbox);
            this.checkBox = checkBox;
            checkBox.setText(R.string.sp_dont_show_again_NORMAL);
            linearLayout.addView(viewInflate2);
            linearLayout.addView(viewInflate3);
            dialogBuilder.setView(viewInflate);
        }

        private void setDialogMessageWithImage(AlertDialog.Builder dialogBuilder) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View viewInflate = layoutInflater.inflate(33751061, (ViewGroup) null);
            LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(android.R.id.content);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.gvn_dialog_remove));
            View viewInflate2 = layoutInflater.inflate(33751045, (ViewGroup) linearLayout, false);
            ((TextView) viewInflate2.findViewById(android.R.id.text1)).setText(getContext().getString(getMessageResId(this.mItemInfo)));
            View viewInflate3 = layoutInflater.inflate(33751052, (ViewGroup) linearLayout, false);
            CheckBox checkBox = (CheckBox) viewInflate3.findViewById(android.R.id.checkbox);
            this.checkBox = checkBox;
            checkBox.setText(R.string.sp_dont_show_again_NORMAL);
            linearLayout.addView(imageView);
            linearLayout.addView(viewInflate2);
            linearLayout.addView(viewInflate3);
            dialogBuilder.setView(viewInflate);
        }

        private int getMessageResId(ItemInfo itemInfo) {
            if ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && (itemInfo instanceof AppInfo)) || (itemInfo instanceof ShortcutInfo) || (itemInfo instanceof AllAppsItemInfo)) {
                return R.string.ask_remove_shortcut_message;
            }
            if (itemInfo instanceof LauncherAppWidgetInfo) {
                return R.string.ask_remove_widget_message;
            }
            if (itemInfo instanceof FolderInfo) {
                return R.string.ask_remove_folder_message;
            }
            return -1;
        }

        @Override // android.app.Fragment, android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            Dialog dialog = this.dialog;
            if (dialog != null) {
                onDismiss(dialog);
            }
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            DropTarget.DragObject dragObject = this.mDragObject;
            if (dragObject != null) {
                DragSource dragSource = dragObject.dragSource;
                if (dragSource instanceof UninstallDropTarget.UninstallSource) {
                    ((UninstallDropTarget.UninstallSource) dragSource).onUninstallActivityReturned(this.mIsSuccess);
                }
            }
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mLauncher = null;
            this.mItemInfo = null;
            this.mView = null;
            this.dialog = null;
        }
    }
}

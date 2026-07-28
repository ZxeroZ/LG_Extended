package com.lge.launcher3.folder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import com.android.launcher3.Launcher;
import com.android.launcher3.model.data.FolderInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGUserLog;
import com.lge.sui.widget.control.SUIColorPickerRuby;
import com.lge.sui.widget.control.color.SUIColor;
import com.lge.sui.widget.dialog.SUIColorPickerDialogRuby;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class FolderColorPickerDialog extends DialogFragment {
    private static final int UNKNOWN_COLOR = -1;
    private static FolderColorPickerDialog sFolderRename;
    private int mDefaultColor;
    private SUIColorPickerDialogRuby mDialog;
    private FolderInfo mFolderInfo;
    private Launcher mLauncher;
    private TypedArray talkbackList;
    private int curSelectedColor = -1;
    private int mOriginalColor = -1;
    private SUIColorPickerRuby.OnColorChangedListener mOnColorChangedListener = new SUIColorPickerRuby.OnColorChangedListener() { // from class: com.lge.launcher3.folder.FolderColorPickerDialog.1
        public void onColorChanged(int arg0) {
            FolderColorPickerDialog.this.curSelectedColor = arg0;
        }
    };

    public static FolderColorPickerDialog getInstance(Launcher launcher) {
        if (sFolderRename == null) {
            FolderColorPickerDialog folderColorPickerDialog = new FolderColorPickerDialog();
            sFolderRename = folderColorPickerDialog;
            folderColorPickerDialog.setLauncher(launcher);
        }
        return sFolderRename;
    }

    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void setFolderInfo(FolderInfo folderInfo) {
        this.mFolderInfo = folderInfo;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInsatnceState) {
        if (this.mFolderInfo == null) {
            return null;
        }
        this.mOriginalColor = FolderColorUtil.getFolderBGColor(getContext(), this.mFolderInfo.folderColor);
        this.mDefaultColor = getResources().getColor(R.color.folder_colorpicker_dialog_color);
        this.talkbackList = getResources().obtainTypedArray(R.array.lg_folder_color_list);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < FolderColorUtil.getColorMax(); i++) {
            arrayList.add(new SUIColor(FolderColorUtil.getFolderBGColor(getContext(), i), this.talkbackList.getResourceId(i, 0), true));
        }
        arrayList.add(new SUIColor(0, this.talkbackList.getResourceId(FolderColorUtil.getColorMax(), 0), true));
        SUIColorPickerDialogRuby sUIColorPickerDialogRubyBuild = new SUIColorPickerDialogRuby.Builder(getActivity(), this.mOnColorChangedListener).setType(1).setPreviewColor(this.mOriginalColor).setInitialColor(new SUIColor(this.mOriginalColor)).useRecentlyUsedColorsView(true, (String) null).setSeamlessSelectionEnabled(true).build();
        this.mDialog = sUIColorPickerDialogRubyBuild;
        sUIColorPickerDialogRubyBuild.setTitle(this.mFolderInfo.title);
        this.mDialog.setOnButtonHandler(new SUIColorPickerDialogRuby.OnButtonHandler() { // from class: com.lge.launcher3.folder.FolderColorPickerDialog.2
            public void onSet(SUIColorPickerRuby view) {
                if (FolderColorPickerDialog.this.mOriginalColor != FolderColorPickerDialog.this.curSelectedColor) {
                    FolderColorPickerDialog.this.mFolderInfo.changeFolderColor(FolderColorPickerDialog.this.curSelectedColor);
                    LGUserLog.send(FolderColorPickerDialog.this.mLauncher, LGUserLog.FEATURENAME_CHANGEFOLDERCOLOR);
                }
            }

            public void onCancel(SUIColorPickerRuby view) {
                if (FolderColorPickerDialog.this.mFolderInfo.folderColor != FolderColorPickerDialog.this.mOriginalColor) {
                    FolderColorPickerDialog.this.mFolderInfo.changeFolderColor(FolderColorPickerDialog.this.mOriginalColor);
                    FolderColorPickerDialog.this.mOriginalColor = -1;
                }
            }
        });
        return this.mDialog;
    }
}

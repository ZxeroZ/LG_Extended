package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsFolderPagedView extends FolderPagedView {
    Folder.DataModel mAllAppsModel;

    public AllAppsFolderPagedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Folder.DataModel dataModel = new Folder.DataModel() { // from class: com.lge.launcher3.allapps.AllAppsFolderPagedView.1
            @Override // com.android.launcher3.folder.Folder.DataModel
            public void addItemToDatabase(Context context2, ItemInfo item, long container, long screenId, int cellX, int cellY) {
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void addOrMoveItemInDatabase(Context context2, ItemInfo item, long container, long screenId, int cellX, int cellY) {
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void deleteItemFromDatabase(Context context2, ItemInfo item) {
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void moveItemsInDatabase(Context context2, ArrayList<ItemInfo> items, long container, int screen) {
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void updateItemInDatabase(Context context2, ItemInfo item) {
            }
        };
        this.mAllAppsModel = dataModel;
        setDataModel(dataModel);
    }

    @Override // com.android.launcher3.folder.FolderPagedView
    public View createAndAddViewForRank(ShortcutInfo item, int rank) {
        View viewCreateNewView = createNewView(item);
        UninstallModeManager.getInstance(getContext()).setUninstallTypeForBadgeViewAllApps(viewCreateNewView);
        addViewForRank(viewCreateNewView, item, rank);
        return viewCreateNewView;
    }
}

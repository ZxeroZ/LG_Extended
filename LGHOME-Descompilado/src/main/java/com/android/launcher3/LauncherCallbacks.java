package com.android.launcher3;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.android.launcher3.allapps.AllAppsSearchBarController;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.ComponentKey;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherCallbacks {
    void bindAllApplications(ArrayList<AppInfo> apps);

    void dump(String prefix, FileDescriptor fd, PrintWriter w, String[] args);

    void finishBindingItems(final boolean upgradePath);

    AllAppsSearchBarController getAllAppsSearchBarController();

    Intent getFirstRunActivity();

    View getIntroScreen();

    LauncherClient getLauncherClient();

    List<ComponentKey> getPredictedApps();

    View getQsbBar();

    boolean handleBackPressed();

    boolean hasCustomContentToLeft();

    boolean hasDismissableIntroScreen();

    boolean hasFirstRunActivity();

    boolean hasSettings();

    void hideLauncherOverlay(int duration);

    void hideLauncherOverlay(boolean animate);

    boolean isLauncherPreinstalled();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onAttachedToWindow();

    void onClickAddWidgetButton(View v);

    void onClickAllAppsButton(View v);

    void onClickAppShortcut(View v);

    void onClickFolderIcon(View v);

    @Deprecated
    void onClickPagedViewIcon(View v);

    void onClickSettingsButton(View v);

    void onClickSwivelSettingsButton(View v);

    void onClickWallpaperPicker(View v);

    void onCreate(Bundle savedInstanceState);

    void onDestroy();

    void onDetachedFromWindow();

    void onDragStarted(View view);

    void onHomeIntent(boolean internalStateHandled);

    void onInteractionBegin();

    void onInteractionEnd();

    void onLauncherProviderChange();

    void onNewIntent(Intent intent);

    void onPageSwitch(View newPage, int newPageIndex);

    void onPause();

    void onPostCreate(Bundle savedInstanceState);

    boolean onPrepareOptionsMenu(Menu menu);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onResume();

    void onSaveInstanceState(Bundle outState);

    void onStart();

    void onStop();

    void onTrimMemory(int level);

    void onWindowFocusChanged(boolean hasFocus);

    void onWorkspaceLockedChanged();

    boolean overrideWallpaperDimensions();

    void populateCustomContentContainer();

    void preOnCreate();

    void preOnResume();

    boolean providesSearch();

    void resetSwivelItemInitialized();

    void setLauncherOverlayLightNavigationBar(boolean enabled);

    void setLauncherSearchCallback(Object callbacks);

    boolean shouldMoveToDefaultScreenOnHomeIntent();

    boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds);

    void updateLauncherClient(boolean enableMinusOneScreen);
}

package com.lge.launcher3.adaptive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import com.android.launcher3.Workspace;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUtilFunctionReflect;

/* JADX INFO: loaded from: classes.dex */
public class AdaptiveTextManager {
    private static final String TAG = "AdaptiveTextManager";
    private static int sAdaptiveStatusBarColor = -1;
    private static int sAdaptiveTextColor = -1;
    public CalculateAdpativeTask mAdaptivetask;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPrefListerner;
    private Workspace mWorkspace;

    public AdaptiveTextManager(final Workspace workspace) {
        this.mAdaptivetask = null;
        this.mWorkspace = workspace;
        final Context context = workspace.getContext();
        SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.lge.launcher3.adaptive.AdaptiveTextManager.1
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR).equals(key) || SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.STATUS_BAR_COLOR).equals(key) || SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.SWIVEL_WEATHER_COLOR).equals(key)) {
                    if (SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR).equals(key)) {
                        int i = sharedPreferences.getInt(key, 0);
                        AdaptiveTextManager.sAdaptiveTextColor = i;
                        AdaptiveTextUtil.setAdaptiveTextColor(AdaptiveTextManager.this.mWorkspace, i);
                        LGLog.i(AdaptiveTextManager.TAG, String.format("onSharedPreferenceChanged(%s): %d(%s)", key, Integer.valueOf(i), Integer.toHexString(i)));
                        if (!LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() || AdaptiveTextManager.this.mWorkspace.getSBView() == null) {
                            return;
                        }
                        AdaptiveTextManager.this.mWorkspace.getSBView().setAdaptiveHeaderForSB(i);
                        return;
                    }
                    if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue() && SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.STATUS_BAR_COLOR).equals(key)) {
                        int i2 = sharedPreferences.getInt(key, 0);
                        AdaptiveTextManager.sAdaptiveStatusBarColor = i2;
                        AdaptiveTextUtil.adaptiveStatusBar(workspace, AdaptiveTextUtil.getAdaptiveStatusBarColor(context));
                        LGLog.i(AdaptiveTextManager.TAG, String.format("onSharedPreferenceChanged(%s): %d(%s)", key, Integer.valueOf(i2), Integer.toHexString(i2)));
                        return;
                    }
                    if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.SWIVEL_WEATHER_COLOR).equals(key)) {
                        int i3 = sharedPreferences.getInt(key, 0);
                        AdaptiveTextUtil.setAdaptiveTextColorForSwivel(workspace.getLauncher().getCarouselLayout());
                        LGLog.i(AdaptiveTextManager.TAG, String.format("onSharedPreferenceChanged(%s): %d(%s)", key, Integer.valueOf(i3), Integer.toHexString(i3)));
                    }
                }
            }
        };
        this.mSharedPrefListerner = onSharedPreferenceChangeListener;
        SharedPreferencesManager.registerOnSharedPreferenceChangeListener(context, 0, onSharedPreferenceChangeListener);
        if (!LGUtilFunctionReflect.WallpaperManagerExReflect.possibleToUseWallpapaerApi()) {
            sAdaptiveTextColor = AdaptiveTextUtil.getAdaptiveTextColor(context);
            if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
                sAdaptiveStatusBarColor = AdaptiveTextUtil.getAdaptiveStatusBarColor(context);
            } else {
                sAdaptiveStatusBarColor = sAdaptiveTextColor;
            }
            AdaptiveTextUtil.adaptiveStatusBar(workspace, sAdaptiveStatusBarColor);
            AdaptiveTextUtil.adaptiveNavigationBar(workspace, sAdaptiveTextColor);
            new Handler().postDelayed(new InitAdaptiveColor(context), 3000L);
            return;
        }
        CalculateAdpativeTask calculateAdpativeTask = new CalculateAdpativeTask(context, workspace);
        this.mAdaptivetask = calculateAdpativeTask;
        calculateAdpativeTask.execute(new Void[0]);
    }

    public class CalculateAdpativeTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        private Workspace mWorkspace;

        public CalculateAdpativeTask(Context context, Workspace workspace) {
            this.mContext = context;
            this.mWorkspace = workspace;
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                WallpaperColorInfoUtil.getInstance(this.mContext).makeInfo(this.mContext);
            }
            if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
                AdaptiveTextUtil.updateAdaptiveColorForStatusBar(this.mContext);
            }
            AdaptiveTextUtil.updateAdaptiveTextColor(this.mContext);
            return true;
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (result.booleanValue()) {
                if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
                    AdaptiveTextUtil.adaptiveStatusBar(this.mWorkspace, AdaptiveTextUtil.getAdaptiveStatusBarColor(this.mContext));
                } else {
                    AdaptiveTextUtil.adaptiveStatusBar(this.mWorkspace, AdaptiveTextManager.sAdaptiveTextColor);
                }
                if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                    AdaptiveTextUtil.updateAdaptiveColorForSwivel(this.mContext);
                }
                AdaptiveTextUtil.adaptiveNavigationBar(this.mWorkspace, AdaptiveTextManager.sAdaptiveTextColor);
            }
        }
    }

    private class InitAdaptiveColor implements Runnable {
        private Context mContext;

        public InitAdaptiveColor(Context context) {
            this.mContext = context;
        }

        @Override // java.lang.Runnable
        public void run() {
            AdaptiveTextUtil.runAdaptiveColor(this.mContext);
        }
    }

    public static int getAdaptiveTextColor() {
        return sAdaptiveTextColor;
    }

    public static void setAdaptiveTextColor(int color) {
        sAdaptiveTextColor = color;
    }

    public void destroy() {
        CalculateAdpativeTask calculateAdpativeTask = this.mAdaptivetask;
        if (calculateAdpativeTask != null) {
            calculateAdpativeTask.cancel(true);
        }
        SharedPreferencesManager.unregisterOnSharedPreferenceChangeListener(this.mWorkspace.getContext(), 0, this.mSharedPrefListerner);
    }
}

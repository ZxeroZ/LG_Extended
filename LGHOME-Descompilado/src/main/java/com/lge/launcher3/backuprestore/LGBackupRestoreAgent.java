package com.lge.launcher3.backuprestore;

import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.lge.bnr.framework.IBNRFrameworkAPI;
import com.lge.bnr.framework.LGBackupAgent;
import com.lge.bnr.framework.LGBackupException;
import com.lge.bnr.model.BNRFailItem;
import com.lge.launcher3.backuprestore.BackupRestoreImpl;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class LGBackupRestoreAgent extends LGBackupAgent {
    public static final String KEY_DEFAULT_HOME = "default_home";
    public static final String PREF_FOR_BACKUP = "pref_for_backup";
    public static final String PREF_FOR_RESTORE = "pref_for_restore";
    private static final String TAG = "LGBackupRestoreAgent";
    private final String mAllAppsWorkspaceFileForLGBackup;
    private final String mAllAppsWorkspaceNameForLGBackup;
    private final String mAllAppsWorkspaceNameForLGHome;
    public BackupRestoreImpl mBackupRestoreImpl;
    private final Context mContext;
    private final String mMenuFileForLGBackup;
    private final String mMenuNameForLGBackup;
    private final String mMenuNameForLGHome;
    private PackageInfo mPackageInfo;
    private String mPackageName;
    private final String mPreferenceFileForLGBackup;
    private final String mSuffixForLGBackup;
    private final String mSwivelMenuFileForLGBackup;
    private final String mSwivelMenuNameForLGBackup;
    private final String mSwivelMenuNameForLGHome;
    private final String mSwivelWallpaperFileForLGBackup;
    private final String mSwivelWallpaperNameForLGBackup;
    private final String mSwivelWallpaperNameForLGHome;
    private final String mSwivelWorkspaceFileForLGBackup;
    private final String mSwivelWorkspaceNameForLGBackup;
    private final String mSwivelWorkspaceNameForLGHome;
    private String mUnzipRootPath;
    private final String mWallpaperFileForLGBackup;
    private final String mWallpaperNameForLGBackup;
    private final String mWallpaperNameForLGHome;
    private final String mWorkspaceFileForLGBackup;
    private final String mWorkspaceNameForLGBackup;
    private final String mWorkspaceNameForLGHome;

    public LGBackupRestoreAgent(Context context, Intent intent) {
        super(context, intent);
        this.mSuffixForLGBackup = "_for_lgbackup";
        this.mWorkspaceNameForLGHome = "workspace";
        this.mAllAppsWorkspaceNameForLGHome = "allapps_workspace";
        this.mMenuNameForLGHome = "menu";
        this.mSwivelWorkspaceNameForLGHome = "swivel_workspace";
        this.mSwivelMenuNameForLGHome = "swivel_menu";
        this.mWallpaperNameForLGHome = "wallpaper";
        this.mSwivelWallpaperNameForLGHome = "swivel_wallpaper";
        this.mWorkspaceNameForLGBackup = "workspace_for_lgbackup";
        this.mAllAppsWorkspaceNameForLGBackup = "allapps_workspace_for_lgbackup";
        this.mMenuNameForLGBackup = "menu_for_lgbackup";
        this.mSwivelWorkspaceNameForLGBackup = "swivel_workspace_for_lgbackup";
        this.mSwivelMenuNameForLGBackup = "swivel_menu_for_lgbackup";
        this.mWallpaperNameForLGBackup = "wallpaper_for_lgbackup";
        this.mSwivelWallpaperNameForLGBackup = "swivel_wallpaper_for_lgbackup";
        this.mWorkspaceFileForLGBackup = "workspace_for_lgbackup.db";
        this.mAllAppsWorkspaceFileForLGBackup = "allapps_workspace_for_lgbackup.db";
        this.mMenuFileForLGBackup = "menu_for_lgbackup.db";
        this.mSwivelWorkspaceFileForLGBackup = "swivel_workspace_for_lgbackup.db";
        this.mSwivelMenuFileForLGBackup = "swivel_menu_for_lgbackup.db";
        this.mWallpaperFileForLGBackup = "wallpaper_for_lgbackup.dat";
        this.mSwivelWallpaperFileForLGBackup = "swivel_wallpaper_for_lgbackup.dat";
        this.mPreferenceFileForLGBackup = "pref_for_backup.xml";
        this.mPackageName = null;
        this.mPackageInfo = null;
        this.mBackupRestoreImpl = null;
        this.mUnzipRootPath = null;
        this.mBackupRestoreImpl = new BackupRestoreImpl(context);
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        this.mUnzipRootPath = context.getFilesDir().getAbsolutePath().replace("files", "backup");
        try {
            this.mPackageInfo = context.getPackageManager().getPackageInfo(this.mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onBackup(IBNRFrameworkAPI bnr) throws Throwable {
        LGLog.i(TAG, "Start backup");
        if (bnr == null) {
            LGLog.i(TAG, "parameter bnr is null");
            return;
        }
        BackupErrorCode backupErrorCode = BackupErrorCode.NO_ERROR;
        LGBackupAgent.isCancle = false;
        BNRFailItem bNRFailItem = new BNRFailItem();
        bnr.setBackupProgress(this.mPackageName, 0);
        Boolean boolBackupTo = this.mBackupRestoreImpl.backupTo("workspace_for_lgbackup", "allapps_workspace_for_lgbackup", "menu_for_lgbackup", "swivel_workspace_for_lgbackup", "swivel_menu_for_lgbackup", "wallpaper_for_lgbackup", "swivel_wallpaper_for_lgbackup");
        if (this.mBackupRestoreImpl.getBackupCancel()) {
            LGLog.i(TAG, "LGHome backup stop by remote");
            return;
        }
        LGLog.i(TAG, "lgbnr.backupTo success is " + boolBackupTo);
        bnr.setBackupProgress(this.mPackageName, 50);
        if (boolBackupTo.booleanValue()) {
            ArrayList<File> fileListInDirectory = getFileListInDirectory(this.mBackupRestoreImpl.getDatabaseInHomeDirectory());
            LGLog.i(TAG, "LGHome backup FileList=" + fileListInDirectory);
            try {
                try {
                    writeZip(this.mPackageName, fileListInDirectory, 50, 100);
                    bNRFailItem.setFailCode(0);
                    for (File file : fileListInDirectory) {
                        LGLog.i(TAG, "fileN : " + file);
                        if (file.exists()) {
                            file.delete();
                            LGLog.i(TAG, file + " is removed");
                        }
                    }
                } catch (SecurityException e) {
                    backupErrorCode = BackupErrorCode.SECURITY_ERR;
                    e.printStackTrace();
                } catch (LGBackupException e2) {
                    backupErrorCode = BackupErrorCode.FRAMEWORK_ERR;
                    e2.printStackTrace();
                }
            } finally {
                setBackupComplete(bnr, backupErrorCode);
            }
        }
    }

    public void onBackupCancel(IBNRFrameworkAPI bnr) {
        if (bnr == null) {
            LGLog.i(TAG, "parameter bnr is null");
            return;
        }
        LGLog.i(TAG, "LGHome backup cancel");
        this.mBackupRestoreImpl.setBackupCancel(true);
        LGBackupAgent.isCancle = true;
    }

    public void onRestore(IBNRFrameworkAPI bnr, int versionCode, String versionName, String Info) {
        LGLog.i(TAG, "Start restore");
        if (bnr == null) {
            LGLog.i(TAG, "parameter bnr is null");
            return;
        }
        BackupErrorCode backupErrorCode = BackupErrorCode.NO_ERROR;
        SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.BackupRestoreKey.ISRESTORING, true);
        try {
            try {
                ArrayList arrayListWriteUnzip = writeUnzip(this.mPackageName, this.mUnzipRootPath, 0, 80);
                if (arrayListWriteUnzip != null) {
                    LGLog.i(TAG, "LGHome restore FileList=" + arrayListWriteUnzip);
                    BackupRestoreImpl.Result resultRestoreFrom = this.mBackupRestoreImpl.restoreFrom(getFileFromPathList(arrayListWriteUnzip, "workspace_for_lgbackup.db"), getFileFromPathList(arrayListWriteUnzip, "allapps_workspace_for_lgbackup.db"), getFileFromPathList(arrayListWriteUnzip, "menu_for_lgbackup.db"), getFileFromPathList(arrayListWriteUnzip, "swivel_workspace_for_lgbackup.db"), getFileFromPathList(arrayListWriteUnzip, "swivel_menu_for_lgbackup.db"), getFileFromPathList(arrayListWriteUnzip, "wallpaper_for_lgbackup.dat"), getFileFromPathList(arrayListWriteUnzip, "swivel_wallpaper_for_lgbackup.dat"), getFileFromPathList(arrayListWriteUnzip, "pref_for_backup.xml"));
                    if (!resultRestoreFrom.succeeded) {
                        LGLog.w(TAG, "Failed to restore: " + resultRestoreFrom.errMsg, new int[0]);
                        backupErrorCode = BackupErrorCode.INTERNAL_ERROR;
                    }
                } else {
                    LGLog.w(TAG, "Backup file is empty", new int[0]);
                    backupErrorCode = BackupErrorCode.INVALID_LBF;
                }
                deleteFilesAfterRestore();
                bnr.setRestoreProgress(this.mPackageName, 100);
            } catch (SecurityException e) {
                backupErrorCode = BackupErrorCode.SECURITY_ERR;
                e.printStackTrace();
            } catch (LGBackupException e2) {
                backupErrorCode = BackupErrorCode.FRAMEWORK_ERR;
                e2.printStackTrace();
            }
            setRestoreComplete(bnr, backupErrorCode);
            SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.BackupRestoreKey.ISRESTORING, false);
        } catch (Throwable th) {
            setRestoreComplete(bnr, backupErrorCode);
            throw th;
        }
    }

    private File getFileFromPathList(ArrayList<String> filePathList, String filename) {
        Iterator<String> it = filePathList.iterator();
        while (it.hasNext()) {
            File file = new File(it.next());
            if (filename != null && filename.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }

    public void onRestoreOld(IBNRFrameworkAPI bnr, ArrayList<String> filePathList) throws Throwable {
        LGLog.i(TAG, "Start restoreOld");
        if (bnr == null) {
            LGLog.i(TAG, "parameter bnr is null");
            return;
        }
        LGLog.i(TAG, "LGHome restoreold start");
        LGLog.i(TAG, "LGHome restoreold filePathList=" + filePathList);
        if (filePathList != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            String databaseInHomeDirectory = this.mBackupRestoreImpl.getDatabaseInHomeDirectory();
            for (String str : filePathList) {
                LGLog.i(TAG, "fileN : " + str);
                File file = new File(str);
                if (!file.exists() || file.length() == 0) {
                    LGLog.i(TAG, str + " doesn't exist");
                } else {
                    String str2 = databaseInHomeDirectory + "/" + extractFile(str);
                    LGLog.i(TAG, "fout : " + str2);
                    String strChangeDBFileNameForLGBackup = changeDBFileNameForLGBackup(str2);
                    LGLog.i(TAG, "foutchange : " + strChangeDBFileNameForLGBackup);
                    File file2 = new File(this.mBackupRestoreImpl.getDatabaseInHomeDirectory());
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    File file3 = new File(strChangeDBFileNameForLGBackup);
                    if (file3.exists()) {
                        file3.delete();
                    }
                    writeFile(new File(str), new File(strChangeDBFileNameForLGBackup));
                    arrayList.add(strChangeDBFileNameForLGBackup);
                }
            }
            File fileFromPathList = getFileFromPathList(arrayList, "workspace_for_lgbackup.db");
            File fileFromPathList2 = getFileFromPathList(filePathList, "allapps_workspace_for_lgbackup.db");
            File fileFromPathList3 = getFileFromPathList(arrayList, "menu_for_lgbackup.db");
            File fileFromPathList4 = getFileFromPathList(arrayList, "swivel_workspace_for_lgbackup.db");
            File fileFromPathList5 = getFileFromPathList(arrayList, "swivel_menu_for_lgbackup.db");
            File fileFromPathList6 = getFileFromPathList(arrayList, "wallpaper_for_lgbackup.dat");
            File fileFromPathList7 = getFileFromPathList(arrayList, "swivel_wallpaper_for_lgbackup.dat");
            File fileFromPathList8 = getFileFromPathList(arrayList, "pref_for_backup.xml");
            if (fileFromPathList != null) {
                new AppWidgetHost(this.mContext, 1024).deleteHost();
                BackupRestoreImpl.Result resultRestoreFrom = this.mBackupRestoreImpl.restoreFrom(fileFromPathList, fileFromPathList2, fileFromPathList3, fileFromPathList4, fileFromPathList5, fileFromPathList6, fileFromPathList7, fileFromPathList8);
                bnr.setRestoreProgress(this.mPackageName, 100);
                LGLog.i(TAG, "lgbnr.restoreFrom success is " + resultRestoreFrom.succeeded);
                if (resultRestoreFrom.succeeded) {
                    deleteFilesAfterRestore();
                } else {
                    LGLog.w(TAG, resultRestoreFrom.errMsg, new int[0]);
                }
            } else {
                LGLog.i(TAG, "LGHome restoreold workspaceFile is null");
            }
            setRestoreComplete(bnr, BackupErrorCode.NO_ERROR);
            LGLog.i(TAG, "After onRestoreOld LGHome should exit");
            return;
        }
        LGLog.i(TAG, "LGHome restoreold filePathList is null");
    }

    private void deleteFilesAfterRestore() {
        deleteDirectory(this.mUnzipRootPath);
    }

    private void deleteDirectory(String dirPath) {
        File file = new File(dirPath);
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                if (file2.isDirectory()) {
                    deleteDirectory(file2.getAbsolutePath());
                } else {
                    file2.delete();
                }
            }
        }
        file.delete();
    }

    private BNRFailItem getBNRFailItem() {
        BNRFailItem bNRFailItem = new BNRFailItem();
        bNRFailItem.setPackageNm(this.mPackageName);
        bNRFailItem.setPackageVersion(this.mPackageInfo.versionName);
        bNRFailItem.setPackageVersionCode(this.mPackageInfo.versionCode);
        return bNRFailItem;
    }

    private void setBackupComplete(IBNRFrameworkAPI bnr, BackupErrorCode errorCode) {
        LGLog.i(TAG, "Backup complete: " + errorCode.getMessage());
        BNRFailItem bNRFailItem = getBNRFailItem();
        bNRFailItem.setFailCode(errorCode.value());
        bnr.setBackupComplete(this.mPackageName, bNRFailItem);
    }

    private void setRestoreComplete(IBNRFrameworkAPI bnr, BackupErrorCode errorCode) {
        LGLog.i(TAG, "Restore complete: " + errorCode.getMessage());
        BNRFailItem bNRFailItem = getBNRFailItem();
        bNRFailItem.setFailCode(errorCode.value());
        bnr.setRestoreComplete(this.mPackageName, bNRFailItem);
    }

    public long getDatabaseSize() {
        BackupRestoreImpl backupRestoreImpl = this.mBackupRestoreImpl;
        if (backupRestoreImpl != null) {
            return backupRestoreImpl.getDatabaseSize();
        }
        return 0L;
    }

    private ArrayList<File> getFileListInDirectory(String pPath) {
        String[] strArrSplit;
        ArrayList<File> arrayList = new ArrayList<>();
        if (pPath.contains("\"")) {
            strArrSplit = pPath.trim().substring(1, pPath.length() - 1).split("\" \"");
        } else {
            strArrSplit = pPath.trim().split(" ");
        }
        for (String str : strArrSplit) {
            arrayList.addAll(getOnlyLGBackupFileList(str));
        }
        return arrayList;
    }

    private ArrayList<File> getOnlyLGBackupFileList(String pPath) {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(pPath);
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles == null) {
            if (file.isFile()) {
                arrayList.add(file);
            }
            return arrayList;
        }
        for (File file2 : fileArrListFiles) {
            if (file2.isDirectory()) {
                arrayList.addAll(getOnlyLGBackupFileList(file2.getAbsolutePath()));
            } else {
                String strExtractFile = extractFile(file2.toString());
                if (strExtractFile.equals("workspace_for_lgbackup.db") || strExtractFile.equals("allapps_workspace_for_lgbackup.db") || strExtractFile.equals("menu_for_lgbackup.db") || strExtractFile.equals("swivel_workspace_for_lgbackup.db") || strExtractFile.equals("swivel_menu_for_lgbackup.db") || strExtractFile.equals("wallpaper_for_lgbackup.dat") || strExtractFile.equals("swivel_wallpaper_for_lgbackup.dat") || strExtractFile.equals("pref_for_backup.xml")) {
                    arrayList.add(file2);
                }
            }
        }
        return arrayList;
    }

    /* JADX DEBUG: Don't trust debug lines info. Repeating lines: [401=4, 403=4, 408=4] */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:15:0x0066 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:17:0x0068 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:19:0x006a */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:40:0x008a */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:57:0x00aa */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:72:0x0037 */
    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: java.io.File */
    /* JADX DEBUG: Multi-variable search result rejected for r6v11, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v12, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v13, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v14, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v17, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v19, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v2, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v3, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v4, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v5, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v6, resolved type: java.io.BufferedOutputStream */
    /* JADX DEBUG: Multi-variable search result rejected for r6v8, resolved type: java.io.BufferedOutputStream */
    /* JADX WARN: Can't wrap try/catch for region: R(12:72|3|85|4|(5:87|5|(1:7)(1:90)|74|56)|8|79|9|13|74|56|(2:(0)|(1:73))) */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x005e, code lost:
    
        r7 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x005f, code lost:
    
        r7.printStackTrace();
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v10, types: [java.io.BufferedOutputStream] */
    /* JADX WARN: Type inference failed for: r6v15 */
    /* JADX WARN: Type inference failed for: r6v16 */
    /* JADX WARN: Type inference failed for: r6v18 */
    /* JADX WARN: Type inference failed for: r6v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void writeFile(java.io.File r6, java.io.File r7) throws java.lang.Throwable {
        /*
            r5 = this;
            r0 = 524288(0x80000, float:7.34684E-40)
            byte[] r0 = new byte[r0]
            java.lang.String r1 = r6.getName()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "fin: "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            java.lang.String r2 = "LGBackupRestoreAgent"
            com.lge.launcher3.util.LGLog.i(r2, r1)
            java.lang.String r1 = r7.getName()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "fout: "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            com.lge.launcher3.util.LGLog.i(r2, r1)
            r1 = 0
            java.io.BufferedInputStream r2 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L78 java.io.IOException -> L7b java.io.FileNotFoundException -> L90
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L78 java.io.IOException -> L7b java.io.FileNotFoundException -> L90
            r3.<init>(r6)     // Catch: java.lang.Throwable -> L78 java.io.IOException -> L7b java.io.FileNotFoundException -> L90
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L78 java.io.IOException -> L7b java.io.FileNotFoundException -> L90
            java.io.BufferedOutputStream r6 = new java.io.BufferedOutputStream     // Catch: java.lang.Throwable -> L6c java.io.IOException -> L70 java.io.FileNotFoundException -> L74
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L6c java.io.IOException -> L70 java.io.FileNotFoundException -> L74
            r4 = 1
            r3.<init>(r7, r4)     // Catch: java.lang.Throwable -> L6c java.io.IOException -> L70 java.io.FileNotFoundException -> L74
            r6.<init>(r3)     // Catch: java.lang.Throwable -> L6c java.io.IOException -> L70 java.io.FileNotFoundException -> L74
        L4c:
            int r7 = r2.read(r0)     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68 java.io.FileNotFoundException -> L6a
            if (r7 <= 0) goto L57
            r1 = 0
            r6.write(r0, r1, r7)     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68 java.io.FileNotFoundException -> L6a
            goto L4c
        L57:
            r6.flush()     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68 java.io.FileNotFoundException -> L6a
            r2.close()     // Catch: java.io.IOException -> L5e
            goto L62
        L5e:
            r7 = move-exception
            r7.printStackTrace()
        L62:
            r6.close()     // Catch: java.io.IOException -> La5
            goto La9
        L66:
            r7 = move-exception
            goto L6e
        L68:
            r7 = move-exception
            goto L72
        L6a:
            r7 = move-exception
            goto L76
        L6c:
            r7 = move-exception
            r6 = r1
        L6e:
            r1 = r2
            goto Lab
        L70:
            r7 = move-exception
            r6 = r1
        L72:
            r1 = r2
            goto L7d
        L74:
            r7 = move-exception
            r6 = r1
        L76:
            r1 = r2
            goto L92
        L78:
            r7 = move-exception
            r6 = r1
            goto Lab
        L7b:
            r7 = move-exception
            r6 = r1
        L7d:
            r7.printStackTrace()     // Catch: java.lang.Throwable -> Laa
            if (r1 == 0) goto L8a
            r1.close()     // Catch: java.io.IOException -> L86
            goto L8a
        L86:
            r7 = move-exception
            r7.printStackTrace()
        L8a:
            if (r6 == 0) goto La9
            r6.close()     // Catch: java.io.IOException -> La5
            goto La9
        L90:
            r7 = move-exception
            r6 = r1
        L92:
            r7.printStackTrace()     // Catch: java.lang.Throwable -> Laa
            if (r1 == 0) goto L9f
            r1.close()     // Catch: java.io.IOException -> L9b
            goto L9f
        L9b:
            r7 = move-exception
            r7.printStackTrace()
        L9f:
            if (r6 == 0) goto La9
            r6.close()     // Catch: java.io.IOException -> La5
            goto La9
        La5:
            r6 = move-exception
            r6.printStackTrace()
        La9:
            return
        Laa:
            r7 = move-exception
        Lab:
            if (r1 == 0) goto Lb5
            r1.close()     // Catch: java.io.IOException -> Lb1
            goto Lb5
        Lb1:
            r0 = move-exception
            r0.printStackTrace()
        Lb5:
            if (r6 == 0) goto Lbf
            r6.close()     // Catch: java.io.IOException -> Lbb
            goto Lbf
        Lbb:
            r6 = move-exception
            r6.printStackTrace()
        Lbf:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.backuprestore.LGBackupRestoreAgent.writeFile(java.io.File, java.io.File):void");
    }

    private String extractFile(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    private String changeDBFileNameForLGBackup(String path) {
        if (path.endsWith(".db")) {
            return path.substring(0, path.length() - 3) + "_for_lgbackup.db";
        }
        if (!path.endsWith(".dat")) {
            return path;
        }
        return path.substring(0, path.length() - 14) + "wallpaper_for_lgbackup.dat";
    }
}

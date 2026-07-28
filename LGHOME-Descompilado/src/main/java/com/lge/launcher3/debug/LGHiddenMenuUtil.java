package com.lge.launcher3.debug;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.LGHomeFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class LGHiddenMenuUtil {
    public static final String HIDDENMENU_FILENAME = "hiddenmenu.data";
    public static Class<?>[] sFeatureClassList = {LGFeatureConfig.class, LGHomeFeature.Config.class};
    private static int keystatus = 0;

    public static class FunctionData implements Serializable {
        private static final long serialVersionUID = -2974413352741858948L;
        public int classNumber;
        public String featureFields;
        public boolean mValume;

        public String toString() {
            return this.featureFields + " " + this.mValume + " " + this.classNumber;
        }
    }

    public static class functionDataManager implements Serializable {
        private static final long serialVersionUID = -6950333645552845186L;
        private final ArrayList<FunctionData> mFeatureArray = new ArrayList<>();

        public FunctionData getData(int index) {
            return this.mFeatureArray.get(index);
        }

        public void addtData(FunctionData input) {
            this.mFeatureArray.add(input);
        }

        public ArrayList<String> getNameList() {
            ArrayList<String> arrayList = new ArrayList<>();
            Iterator<FunctionData> it = this.mFeatureArray.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().featureFields);
            }
            return arrayList;
        }

        public ArrayList<String> getNameList(ArrayList<Integer> filter) {
            ArrayList<String> arrayList = new ArrayList<>();
            Iterator<Integer> it = filter.iterator();
            while (it.hasNext()) {
                arrayList.add(this.mFeatureArray.get(it.next().intValue()).featureFields);
            }
            return arrayList;
        }

        public int size() {
            return this.mFeatureArray.size();
        }

        public ArrayList<FunctionData> getArray() {
            return this.mFeatureArray;
        }
    }

    public static boolean getItemValue(int classNumber, Field featureFields, Context input) {
        if (featureFields == null) {
            return false;
        }
        try {
            if (!"boolean".equals(featureFields.getType().getName())) {
                return false;
            }
            if (sFeatureClassList[classNumber].getName().equals(LGHomeFeature.class.getName())) {
                featureFields.setAccessible(true);
                return featureFields.getBoolean(LGHomeFeature.getInstance());
            }
            return featureFields.getBoolean(null);
        } catch (IllegalAccessException | IllegalArgumentException unused) {
            return false;
        }
    }

    public static void setValueAll(functionDataManager infManger, LGHomeFeature input) {
        Field declaredField;
        String name = LGFeatureConfig.class.getName();
        String name2 = LGHomeFeature.Config.class.getName();
        for (FunctionData functionData : infManger.getArray()) {
            for (Class<?> cls : sFeatureClassList) {
                try {
                    declaredField = cls.getDeclaredField(functionData.featureFields);
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
                }
                if (declaredField == null) {
                    return;
                }
                String name3 = cls.getName();
                String name4 = declaredField.getType().getName();
                if (name3.equals(name) && "boolean".equals(name4)) {
                    declaredField.setAccessible(true);
                    declaredField.setBoolean(input, functionData.mValume);
                } else if (name3.equals(name2)) {
                    LGHomeFeature.Config.valueOf(declaredField.getName()).setValue(functionData.mValume);
                } else if ("boolean".equals(name4)) {
                    declaredField.setBoolean(null, functionData.mValume);
                }
            }
        }
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:17:0x0044 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:32:0x005f */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:35:0x0063 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:50:0x001c */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v10, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r2v15 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r2v9 */
    public static void reLoadingFeature(Object obj, LGHomeFeature lGHomeFeature) throws Throwable {
        if (!(obj instanceof Activity)) {
            return;
        }
        ?? fileInputStream = ((Activity) obj).getFilesDir() + "/hiddenmenu.data";
        ObjectInputStream objectInputStream = null;
        try {
            try {
                File file = new File((String) fileInputStream);
                if (file.exists()) {
                    fileInputStream = new FileInputStream(file);
                    try {
                        ObjectInputStream objectInputStream2 = new ObjectInputStream(fileInputStream);
                        try {
                            functionDataManager functiondatamanager = (functionDataManager) objectInputStream2.readObject();
                            if (functiondatamanager != null) {
                                setValueAll(functiondatamanager, lGHomeFeature);
                            }
                            objectInputStream = objectInputStream2;
                            fileInputStream = fileInputStream;
                        } catch (Exception e) {
                            e = e;
                            objectInputStream = objectInputStream2;
                            e.printStackTrace();
                            if (objectInputStream != null) {
                                try {
                                    objectInputStream.close();
                                } catch (IOException unused) {
                                }
                            }
                            if (fileInputStream == 0) {
                                return;
                            }
                        } catch (Throwable th) {
                            th = th;
                            objectInputStream = objectInputStream2;
                            if (objectInputStream != null) {
                                try {
                                    objectInputStream.close();
                                } catch (IOException unused2) {
                                }
                            }
                            if (fileInputStream != 0) {
                                try {
                                    fileInputStream.close();
                                    throw th;
                                } catch (IOException unused3) {
                                    throw th;
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                    }
                } else {
                    fileInputStream = 0;
                }
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException unused4) {
                    }
                }
                if (fileInputStream == 0) {
                    return;
                }
            } catch (Exception e3) {
                e = e3;
                fileInputStream = 0;
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = 0;
            }
            try {
                fileInputStream.close();
            } catch (IOException unused5) {
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static void hiddenMenuRunKeyCondition(Activity mActivity, int keyCode, KeyEvent event) {
        if (mActivity == null || "user".equals(Build.TYPE) || event.getAction() != 0) {
            return;
        }
        if (keystatus > 5 && event.getKeyCode() == 25) {
            try {
                try {
                    Intent intent = new Intent(mActivity.getBaseContext(), Class.forName(LGHiddenMenuActivity.class.getName()));
                    intent.setFlags(545325056);
                    mActivity.startActivity(intent);
                    keystatus = 0;
                    mActivity.finish();
                } catch (ClassNotFoundException unused) {
                    return;
                }
            } catch (ActivityNotFoundException unused2) {
            }
        }
        if (event.getKeyCode() == 25) {
            keystatus = 1;
        } else {
            if (keystatus <= 0 || event.getKeyCode() != 24) {
                return;
            }
            keystatus++;
        }
    }
}

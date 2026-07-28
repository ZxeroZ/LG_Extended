package com.lge.launcher3.debug;

import android.content.Context;
import android.content.res.Resources;
import com.lge.launcher3.config.LauncherConst;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class LGResouceCheckTool {
    public static ResCheckClass[] sCeckClassList = {new ResCheckClass(LauncherConst.RESOURCE_IMAGE_TYPE) { // from class: com.lge.launcher3.debug.LGResouceCheckTool.1
        @Override // com.lge.launcher3.debug.LGResouceCheckTool.ResCheckClass
        public void check(Context context, Field field) throws Exception {
            context.getResources().getDrawableForDensity(field.getInt(null), 120, null);
        }
    }, new ResCheckClass("dimen") { // from class: com.lge.launcher3.debug.LGResouceCheckTool.2
        @Override // com.lge.launcher3.debug.LGResouceCheckTool.ResCheckClass
        public void check(Context context, Field field) throws Exception {
            context.getResources().getDimension(field.getInt(null));
        }
    }, new ResCheckClass("color") { // from class: com.lge.launcher3.debug.LGResouceCheckTool.3
        @Override // com.lge.launcher3.debug.LGResouceCheckTool.ResCheckClass
        public void check(Context context, Field field) throws Exception {
            context.getResources().getColor(field.getInt(null));
        }
    }, new ResCheckClass("bool") { // from class: com.lge.launcher3.debug.LGResouceCheckTool.4
        @Override // com.lge.launcher3.debug.LGResouceCheckTool.ResCheckClass
        public void check(Context context, Field field) throws Exception {
            context.getResources().getBoolean(field.getInt(null));
        }
    }, new ResCheckClass(LauncherConst.RESOURCE_INTEGER_TYPE) { // from class: com.lge.launcher3.debug.LGResouceCheckTool.5
        @Override // com.lge.launcher3.debug.LGResouceCheckTool.ResCheckClass
        public void check(Context context, Field field) throws Exception {
            context.getResources().getInteger(field.getInt(null));
        }
    }};

    private static abstract class ResCheckClass {
        String mClassName;

        public abstract void check(Context context, Field field) throws Exception;

        public ResCheckClass(String className) {
            this.mClassName = className;
        }
    }

    public static String checkAllRes(Context context) {
        String str;
        Class<?> cls;
        int i;
        Class<?> cls2;
        int i2;
        Field[] fieldArr;
        StringBuilder sb = new StringBuilder();
        String str2 = "com.lge.launcher3.R";
        try {
            Class<?> cls3 = Class.forName("com.lge.launcher3.R");
            ResCheckClass[] resCheckClassArr = sCeckClassList;
            int length = resCheckClassArr.length;
            int i3 = 0;
            int i4 = 0;
            while (i4 < length) {
                ResCheckClass resCheckClass = resCheckClassArr[i4];
                Class<?>[] classes = cls3.getClasses();
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                int length2 = classes.length;
                int i5 = 0;
                while (true) {
                    if (i5 >= length2) {
                        str = str2;
                        cls = cls3;
                        i = i3;
                        cls2 = null;
                        break;
                    }
                    Class<?> cls4 = classes[i5];
                    String name = cls4.getName();
                    String str3 = resCheckClass.mClassName;
                    cls = cls3;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(str2);
                    str = str2;
                    sb4.append("$");
                    sb4.append(str3);
                    if (name.equals(sb4.toString())) {
                        int i6 = i3 + 1;
                        sb2.append(i6 + ". " + cls4.getSimpleName() + " check : ");
                        i = i6;
                        cls2 = cls4;
                        break;
                    }
                    i5++;
                    cls3 = cls;
                    str2 = str;
                }
                if (cls2 == null) {
                    break;
                }
                Field[] declaredFields = cls2.getDeclaredFields();
                int length3 = declaredFields.length;
                int length4 = declaredFields.length;
                int i7 = 0;
                int i8 = 0;
                while (i7 < length4) {
                    Field field = declaredFields[i7];
                    try {
                        resCheckClass.check(context, field);
                    } catch (Resources.NotFoundException unused) {
                        i8++;
                        String name2 = field.getName();
                        i2 = i;
                        StringBuilder sb5 = new StringBuilder();
                        fieldArr = declaredFields;
                        sb5.append("  - Error: ");
                        sb5.append(name2);
                        sb5.append("\n");
                        sb3.append(sb5.toString());
                    } catch (Exception e) {
                        i8++;
                        e.printStackTrace();
                    }
                    i2 = i;
                    fieldArr = declaredFields;
                    i7++;
                    i = i2;
                    declaredFields = fieldArr;
                }
                int i9 = i;
                sb2.append("Total: " + length3).append(" Error : " + i8).append("\n");
                sb.append((CharSequence) sb2).append((CharSequence) sb3);
                i4++;
                i3 = i9;
                cls3 = cls;
                str2 = str;
            }
            return sb.toString();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
            return sb.toString();
        }
    }
}

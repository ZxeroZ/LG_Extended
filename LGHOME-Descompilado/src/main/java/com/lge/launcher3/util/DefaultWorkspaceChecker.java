package com.lge.launcher3.util;

import android.content.Context;
import android.os.SystemProperties;
import com.android.systemui.plugins.OverscrollPlugin;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DefaultWorkspaceChecker {
    private static HashMap<String, CheckingInterface> sCheckClassHash;
    private static String sGid;
    private static String sIccid;
    private static boolean sInitUsimInfo;
    private static String sMcc;
    private static String sMnc;
    private static int sSp;
    private static String sSpn;

    public interface CheckingInterface {
        boolean isMatching(Context context, String input);
    }

    public static CheckingInterface getCheckClass(String key) {
        HashMap<String, CheckingInterface> map = sCheckClassHash;
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    static class Mcc implements CheckingInterface {
        Mcc() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return input.equals(DefaultWorkspaceChecker.sMcc);
        }
    }

    static class Mnc implements CheckingInterface {
        Mnc() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return input.equals(DefaultWorkspaceChecker.sMnc);
        }
    }

    static class Gid implements CheckingInterface {
        Gid() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return DefaultWorkspaceChecker.sGid != null && DefaultWorkspaceChecker.sGid.toUpperCase(Locale.getDefault()).startsWith(input.toUpperCase(Locale.getDefault()));
        }
    }

    static class Sp implements CheckingInterface {
        Sp() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            try {
                return DefaultWorkspaceChecker.sSp == Integer.valueOf(input).intValue();
            } catch (NumberFormatException unused) {
                return false;
            }
        }
    }

    static class Spn implements CheckingInterface {
        Spn() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return DefaultWorkspaceChecker.sSpn != null && input.equals(DefaultWorkspaceChecker.sSpn.replaceAll(" ", ""));
        }
    }

    static class UscIccid implements CheckingInterface {
        UscIccid() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return DefaultWorkspaceChecker.sIccid != null && DefaultWorkspaceChecker.sIccid.charAt(8) == input.charAt(0);
        }
    }

    static class SpStart implements CheckingInterface {
        SpStart() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            try {
                return Integer.valueOf(input).intValue() <= DefaultWorkspaceChecker.sSp;
            } catch (NumberFormatException unused) {
                return false;
            }
        }
    }

    static class SpEnd implements CheckingInterface {
        SpEnd() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            try {
                return DefaultWorkspaceChecker.sSp <= Integer.valueOf(input).intValue();
            } catch (NumberFormatException unused) {
                return false;
            }
        }
    }

    static class SprintId implements CheckingInterface {
        SprintId() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return SystemProperties.get("ro.chameleon.mobileid", "0").equals(input);
        }
    }

    static class NetworkCode implements CheckingInterface {
        NetworkCode() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return SystemProperties.get("ro.cdma.home.operator.numeric", OverscrollPlugin.DEVICE_STATE_UNKNOWN).equals(input);
        }
    }

    static class SktMode implements CheckingInterface {
        SktMode() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            int defaultPhoneMode = TPhoneModeUtils.getDefaultPhoneMode(context);
            SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE, defaultPhoneMode);
            SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.TPhoneMode.T_PHONE_MODE_APPDRAWER, defaultPhoneMode);
            return Boolean.valueOf(input).booleanValue() == (defaultPhoneMode == 1);
        }
    }

    static class FiSimMode implements CheckingInterface {
        FiSimMode() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return Boolean.valueOf(input).booleanValue() == SystemProperties.get(com.lge.launcher3.receiver.FiSimMode.PROPERTY_PERSIST_SYS_SIM_OPERATOR_GOOGLEFI, "0").equals("1");
        }
    }

    static class Signature implements CheckingInterface {
        Signature() {
        }

        @Override // com.lge.launcher3.util.DefaultWorkspaceChecker.CheckingInterface
        public boolean isMatching(Context context, String input) {
            return "signature".equalsIgnoreCase(SystemProperties.get(LauncherConst.PROPERTY_RO_LGE_HYDRA, ""));
        }
    }

    public static void init(Context context) {
        if (!sInitUsimInfo) {
            try {
                LGUsimInfo lGUsimInfo = LGUsimInfo.getInstance(context);
                sMcc = lGUsimInfo.getMcc();
                sMnc = lGUsimInfo.getMnc();
                sGid = lGUsimInfo.getGid();
                sSpn = lGUsimInfo.getSpn();
                sIccid = lGUsimInfo.getIccid();
                sSp = lGUsimInfo.getSP();
                LGLog.d("init", lGUsimInfo.toString());
                lGUsimInfo.onDestroy();
            } catch (NoClassDefFoundError unused) {
                LGLog.w("init", "NoClassDefFoundError", new int[0]);
            }
            sInitUsimInfo = true;
        }
        if (sCheckClassHash == null) {
            sCheckClassHash = new HashMap<>();
            for (Class<?> cls : DefaultWorkspaceChecker.class.getDeclaredClasses()) {
                String simpleName = cls.getSimpleName();
                try {
                    Object objNewInstance = cls.newInstance();
                    if (objNewInstance instanceof CheckingInterface) {
                        sCheckClassHash.put(simpleName.toLowerCase(), (CheckingInterface) objNewInstance);
                    }
                } catch (IllegalAccessException | InstantiationException unused2) {
                    LGLog.w("CheckClass", "not found", new int[0]);
                } catch (NullPointerException e) {
                    LGLog.w("DefaultWorkspaceChecker", String.format("NullPointerException(%s)", e.getMessage()), new int[0]);
                }
            }
        }
    }

    public static void destory() {
        sMcc = null;
        sMnc = null;
        sGid = null;
        sSpn = null;
        sIccid = null;
        sSp = 0;
        sInitUsimInfo = false;
        sCheckClassHash = null;
    }
}

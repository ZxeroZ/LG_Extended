package com.zxerox.lg_extended.root;

import android.os.Build;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class DeviceInfoProvider {

    public static class DeviceData {
        public String deviceModel = "Unknown";
        public String androidVersion = "Unknown";
        public String buildNumber = "Unknown";
        public String kernelVersion = "Unknown";
        public String rootManager = "Unknown";
        public String arch = "Unknown";
        public boolean hasRoot = false;
    }

    public interface Callback {
        void onResult(DeviceData data);
    }

    public static void fetch(Callback callback) {
        new Thread(() -> {
            DeviceData d = new DeviceData();
            d.deviceModel = Build.MANUFACTURER + " " + Build.MODEL;
            d.androidVersion = Build.VERSION.RELEASE;
            d.buildNumber = Build.DISPLAY;
            d.arch = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "Unknown";
            d.hasRoot = Shell.getShell().isRoot();

            if (d.hasRoot) {
                Shell.Result kernelResult = Shell.cmd("uname -r").exec();
                d.kernelVersion = firstLine(kernelResult.getOut(), "Unknown");

                d.rootManager = detectRootManager();
            }

            callback.onResult(d);
        }).start();
    }

    private static String detectRootManager() {
        Shell.Result suV = Shell.cmd("su -v").exec();
        String out = firstLine(suV.getOut(), "").toLowerCase().trim();
        if (!out.isEmpty()) {
            if (out.contains("magisk")) return "Magisk";
            if (out.contains("kernelsu") || out.contains("ksu")) return "KernelSU";
            if (out.contains("apatch")) return "APatch";
        }

        Shell.Result ksuDir = Shell.cmd("[ -d /data/adb/ksu ] && echo ksu").exec();
        if ("ksu".equals(firstLine(ksuDir.getOut(), "").trim())) return "KernelSU";

        Shell.Result apDir = Shell.cmd("[ -d /data/adb/ap ] && echo apatch").exec();
        if ("apatch".equals(firstLine(apDir.getOut(), "").trim())) return "APatch";

        Shell.Result magiskDir = Shell.cmd("[ -d /data/adb/magisk ] && echo magisk").exec();
        if ("magisk".equals(firstLine(magiskDir.getOut(), "").trim())) return "Magisk";

        Shell.Result suVersion = Shell.cmd("su --version").exec();
        String version = firstLine(suVersion.getOut(), "").trim();
        if (!version.isEmpty()) return "Root (" + version + ")";

        return "Unknown";
    }

    private static String firstLine(List<String> lines, String fallback) {
        if (lines != null && !lines.isEmpty()) {
            return lines.get(0);
        }
        return fallback;
    }
}


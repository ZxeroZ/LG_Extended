package com.zxerox.lg_extended.root;

import com.topjohnwu.superuser.Shell;

public class RootUtils {

    public static boolean tieneRoot() {
        return Shell.getShell().isRoot();
    }

    public static void reiniciarSystemUI(Runnable onDone) {
        Shell.cmd("killall com.android.systemui").submit(result -> {
            if (onDone != null) onDone.run();
        });
    }
}
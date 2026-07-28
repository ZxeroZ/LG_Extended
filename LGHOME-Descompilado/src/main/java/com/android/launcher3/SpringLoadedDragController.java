package com.android.launcher3;

/* JADX INFO: loaded from: classes.dex */
public class SpringLoadedDragController implements OnAlarmListener {
    Alarm mAlarm;
    private Launcher mLauncher;
    private CellLayout mScreen;
    final long ENTER_SPRING_LOAD_HOVER_TIME = 500;
    final long ENTER_SPRING_LOAD_CANCEL_HOVER_TIME = 950;
    final long EXIT_SPRING_LOAD_HOVER_TIME = 200;

    public SpringLoadedDragController(Launcher launcher) {
        this.mLauncher = launcher;
        Alarm alarm = new Alarm();
        this.mAlarm = alarm;
        alarm.setOnAlarmListener(this);
    }

    public void cancel() {
        this.mAlarm.cancelAlarm();
    }

    public void setAlarm(CellLayout cl) {
        this.mAlarm.cancelAlarm();
        this.mAlarm.setAlarm(cl == null ? 950L : 500L);
        this.mScreen = cl;
    }

    @Override // com.android.launcher3.OnAlarmListener
    public void onAlarm(Alarm alarm) {
        if (this.mScreen != null) {
            Workspace workspace = this.mLauncher.getWorkspace();
            int iIndexOfChild = workspace.indexOfChild(this.mScreen);
            if (iIndexOfChild != workspace.getCurrentPage()) {
                workspace.snapToPage(iIndexOfChild);
                return;
            }
            return;
        }
        this.mLauncher.getDragController().cancelDrag();
    }
}

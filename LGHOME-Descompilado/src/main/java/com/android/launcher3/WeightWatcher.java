package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.MemoryTracker;

/* JADX INFO: loaded from: classes.dex */
public class WeightWatcher extends LinearLayout {
    private static final int BACKGROUND_COLOR = -1073741824;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_UPDATE = 3;
    private static final int RAM_GRAPH_PSS_COLOR = -6697984;
    private static final int RAM_GRAPH_RSS_COLOR = -6750208;
    private static final int TEXT_COLOR = -1;
    private static final int UPDATE_RATE = 5000;
    Handler mHandler;
    MemoryTracker mMemoryService;

    static int indexOf(int[] a, int x) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == x) {
                return i;
            }
        }
        return -1;
    }

    public WeightWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = new Handler() { // from class: com.android.launcher3.WeightWatcher.1
            @Override // android.os.Handler
            public void handleMessage(Message m) {
                int i = m.what;
                if (i == 1) {
                    WeightWatcher.this.mHandler.sendEmptyMessage(3);
                    return;
                }
                if (i == 2) {
                    WeightWatcher.this.mHandler.removeMessages(3);
                    return;
                }
                if (i != 3) {
                    return;
                }
                int[] trackedProcesses = WeightWatcher.this.mMemoryService.getTrackedProcesses();
                int childCount = WeightWatcher.this.getChildCount();
                if (trackedProcesses.length == childCount) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= childCount) {
                            break;
                        }
                        ProcessWatcher processWatcher = (ProcessWatcher) WeightWatcher.this.getChildAt(i2);
                        if (WeightWatcher.indexOf(trackedProcesses, processWatcher.getPid()) < 0) {
                            WeightWatcher.this.initViews();
                            break;
                        } else {
                            processWatcher.update();
                            i2++;
                        }
                    }
                } else {
                    WeightWatcher.this.initViews();
                }
                WeightWatcher.this.mHandler.sendEmptyMessageDelayed(3, 5000L);
            }
        };
        context.bindService(new Intent(context, (Class<?>) MemoryTracker.class), new ServiceConnection() { // from class: com.android.launcher3.WeightWatcher.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName className, IBinder service) {
                WeightWatcher.this.mMemoryService = ((MemoryTracker.MemoryTrackerInterface) service).getService();
                WeightWatcher.this.initViews();
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName className) {
                WeightWatcher.this.mMemoryService = null;
            }
        }, 1);
        setOrientation(1);
        setBackgroundColor(BACKGROUND_COLOR);
    }

    public void initViews() {
        removeAllViews();
        for (int i : this.mMemoryService.getTrackedProcesses()) {
            ProcessWatcher processWatcher = new ProcessWatcher(this, getContext());
            processWatcher.setPid(i);
            addView(processWatcher);
        }
    }

    public WeightWatcher(Context context) {
        this(context, null);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.sendEmptyMessage(2);
    }

    public class ProcessWatcher extends LinearLayout {
        MemoryTracker.ProcessMemInfo mMemInfo;
        int mPid;
        GraphView mRamGraph;
        TextView mText;

        public ProcessWatcher(final WeightWatcher this$0, Context context) {
            this(context, null);
        }

        public ProcessWatcher(Context context, AttributeSet attrs) {
            super(context, attrs);
            float f = getResources().getDisplayMetrics().density;
            TextView textView = new TextView(getContext());
            this.mText = textView;
            textView.setTextColor(-1);
            this.mText.setTextSize(0, 10.0f * f);
            this.mText.setGravity(19);
            int i = (int) (2.0f * f);
            setPadding(i, 0, i, 0);
            this.mRamGraph = new GraphView(this, getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, (int) (14.0f * f), 1.0f);
            addView(this.mText, layoutParams);
            layoutParams.leftMargin = (int) (4.0f * f);
            layoutParams.weight = 0.0f;
            layoutParams.width = (int) (f * 200.0f);
            addView(this.mRamGraph, layoutParams);
        }

        public void setPid(int pid) {
            this.mPid = pid;
            MemoryTracker.ProcessMemInfo memInfo = WeightWatcher.this.mMemoryService.getMemInfo(this.mPid);
            this.mMemInfo = memInfo;
            if (memInfo == null) {
                Log.v("WeightWatcher", "Missing info for pid " + this.mPid + ", removing view: " + this);
                WeightWatcher.this.initViews();
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public String getUptimeString() {
            long uptime = this.mMemInfo.getUptime() / 1000;
            StringBuilder sb = new StringBuilder();
            long j = uptime / 86400;
            if (j > 0) {
                uptime -= 86400 * j;
                sb.append(j);
                sb.append("d");
            }
            long j2 = uptime / 3600;
            if (j2 > 0) {
                uptime -= 3600 * j2;
                sb.append(j2);
                sb.append("h");
            }
            long j3 = uptime / 60;
            if (j3 > 0) {
                uptime -= 60 * j3;
                sb.append(j3);
                sb.append("m");
            }
            sb.append(uptime);
            sb.append("s");
            return sb.toString();
        }

        public void update() {
            TextView textView = this.mText;
            int i = this.mPid;
            String str = i == Process.myPid() ? "/A" : "/S";
            textView.setText("(" + i + str + ") up " + getUptimeString() + " P=" + this.mMemInfo.currentPss + " U=" + this.mMemInfo.currentUss);
            this.mRamGraph.invalidate();
        }

        public class GraphView extends View {
            Paint headPaint;
            Paint pssPaint;
            Paint ussPaint;

            public GraphView(Context context, AttributeSet attrs) {
                super(context, attrs);
                Paint paint = new Paint();
                this.pssPaint = paint;
                paint.setColor(WeightWatcher.RAM_GRAPH_PSS_COLOR);
                Paint paint2 = new Paint();
                this.ussPaint = paint2;
                paint2.setColor(WeightWatcher.RAM_GRAPH_RSS_COLOR);
                Paint paint3 = new Paint();
                this.headPaint = paint3;
                paint3.setColor(com.lge.launcher3.util.Utilities.sWhite);
            }

            public GraphView(final ProcessWatcher this$1, Context context) {
                this(context, null);
            }

            @Override // android.view.View
            public void onDraw(Canvas c) {
                int width = c.getWidth();
                int height = c.getHeight();
                if (ProcessWatcher.this.mMemInfo == null) {
                    return;
                }
                int length = ProcessWatcher.this.mMemInfo.pss.length;
                float f = width / length;
                float fMax = Math.max(1.0f, f);
                float f2 = height;
                float f3 = f2 / ProcessWatcher.this.mMemInfo.max;
                for (int i = 0; i < length; i++) {
                    float f4 = i * f;
                    float f5 = f4 + fMax;
                    c.drawRect(f4, f2 - (ProcessWatcher.this.mMemInfo.pss[i] * f3), f5, f2, this.pssPaint);
                    c.drawRect(f4, f2 - (ProcessWatcher.this.mMemInfo.uss[i] * f3), f5, f2, this.ussPaint);
                }
                float f6 = ProcessWatcher.this.mMemInfo.head * f;
                c.drawRect(f6, 0.0f, f6 + fMax, f2, this.headPaint);
            }
        }
    }
}

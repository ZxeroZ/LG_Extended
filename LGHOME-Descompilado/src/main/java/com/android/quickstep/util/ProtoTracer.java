package com.android.quickstep.util;

import android.content.Context;
import android.os.SystemClock;
import com.android.launcher3.tracing.nano.LauncherTraceEntryProto;
import com.android.launcher3.tracing.nano.LauncherTraceFileProto;
import com.android.launcher3.tracing.nano.LauncherTraceProto;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.shared.tracing.FrameProtoTracer;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.google.protobuf.nano.MessageNano;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class ProtoTracer implements FrameProtoTracer.ProtoTraceParams<MessageNano, LauncherTraceFileProto, LauncherTraceEntryProto, LauncherTraceProto> {
    public static final MainThreadInitializedObject<ProtoTracer> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.quickstep.util.-$$Lambda$iEF0GNPkeiSdz2FKTr8j3kzJhfE
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new ProtoTracer(context);
        }
    });
    private static final long MAGIC_NUMBER_VALUE = 4851032461007867468L;
    private static final String TAG = "ProtoTracer";
    private final Context mContext;
    private final FrameProtoTracer<MessageNano, LauncherTraceFileProto, LauncherTraceEntryProto, LauncherTraceProto> mProtoTracer = new FrameProtoTracer<>(this);

    public ProtoTracer(Context context) {
        this.mContext = context;
    }

    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public File getTraceFile() {
        return new File(this.mContext.getFilesDir(), "launcher_trace.pb");
    }

    /* JADX DEBUG: Method merged with bridge method: getEncapsulatingTraceProto()Ljava/lang/Object; */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public LauncherTraceFileProto getEncapsulatingTraceProto() {
        return new LauncherTraceFileProto();
    }

    /* JADX DEBUG: Method merged with bridge method: updateBufferProto(Ljava/lang/Object;Ljava/util/ArrayList;)Ljava/lang/Object; */
    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public LauncherTraceEntryProto updateBufferProto(LauncherTraceEntryProto reuseObj, ArrayList<ProtoTraceable<LauncherTraceProto>> traceables) {
        if (reuseObj == null) {
            reuseObj = new LauncherTraceEntryProto();
        }
        reuseObj.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        reuseObj.launcher = reuseObj.launcher != null ? reuseObj.launcher : new LauncherTraceProto();
        Iterator<ProtoTraceable<LauncherTraceProto>> it = traceables.iterator();
        while (it.hasNext()) {
            it.next().writeToProto(reuseObj.launcher);
        }
        return reuseObj;
    }

    /* JADX DEBUG: Method merged with bridge method: serializeEncapsulatingProto(Ljava/lang/Object;Ljava/util/Queue;)[B */
    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public byte[] serializeEncapsulatingProto(LauncherTraceFileProto encapsulatingProto, Queue<LauncherTraceEntryProto> buffer) {
        encapsulatingProto.magicNumber = MAGIC_NUMBER_VALUE;
        encapsulatingProto.entry = (LauncherTraceEntryProto[]) buffer.toArray(new LauncherTraceEntryProto[0]);
        return MessageNano.toByteArray(encapsulatingProto);
    }

    /* JADX DEBUG: Method merged with bridge method: getProtoBytes(Ljava/lang/Object;)[B */
    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public byte[] getProtoBytes(MessageNano proto) {
        return MessageNano.toByteArray(proto);
    }

    /* JADX DEBUG: Method merged with bridge method: getProtoSize(Ljava/lang/Object;)I */
    @Override // com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams
    public int getProtoSize(MessageNano proto) {
        return proto.getCachedSize();
    }

    public void start() {
        this.mProtoTracer.start();
    }

    public void stop() {
        this.mProtoTracer.stop();
    }

    public void add(ProtoTraceable<LauncherTraceProto> traceable) {
        this.mProtoTracer.add(traceable);
    }

    public void remove(ProtoTraceable<LauncherTraceProto> traceable) {
        this.mProtoTracer.remove(traceable);
    }

    public void scheduleFrameUpdate() {
        this.mProtoTracer.scheduleFrameUpdate();
    }

    public void update() {
        this.mProtoTracer.update();
    }
}

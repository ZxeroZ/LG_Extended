package com.android.systemui.flags;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.android.systemui.flags.FlagListenable;
import com.android.systemui.flags.FlagManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: FlagManager.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u001e\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 52\u00020\u0001:\u0003567B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\tJ\u001c\u0010\u001a\u001a\u00020\u001b2\n\u0010\u001c\u001a\u0006\u0012\u0002\b\u00030\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\fH\u0002J\u001e\u0010#\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\f2\u000e\u0010$\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u000bJ\u000e\u0010%\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\fJ\u0016\u0010&\u001a\u0012\u0012\u000e\u0012\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u001d0(0'J\u000e\u0010)\u001a\u00020*2\u0006\u0010\"\u001a\u00020\fJ\u0015\u0010+\u001a\u0004\u0018\u00010\u00152\u0006\u0010\"\u001a\u00020\f¢\u0006\u0002\u0010,J)\u0010-\u001a\u0004\u0018\u0001H.\"\u0004\b\u0000\u0010.2\u0006\u0010\"\u001a\u00020\f2\f\u0010/\u001a\b\u0012\u0004\u0012\u0002H.00¢\u0006\u0002\u00101J\u0010\u00102\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0016\u00103\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\f2\u0006\u00104\u001a\u00020\u0015R\"\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\"\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000e\"\u0004\b\u0017\u0010\u0010R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00068"}, d2 = {"Lcom/android/systemui/flags/FlagManager;", "Lcom/android/systemui/flags/FlagListenable;", "context", "Landroid/content/Context;", "handler", "Landroid/os/Handler;", "(Landroid/content/Context;Landroid/os/Handler;)V", "settings", "Lcom/android/systemui/flags/FlagSettingsHelper;", "(Landroid/content/Context;Lcom/android/systemui/flags/FlagSettingsHelper;Landroid/os/Handler;)V", "clearCacheAction", "Ljava/util/function/Consumer;", "", "getClearCacheAction", "()Ljava/util/function/Consumer;", "setClearCacheAction", "(Ljava/util/function/Consumer;)V", "listeners", "", "Lcom/android/systemui/flags/FlagManager$PerFlagListener;", "onSettingsChangedAction", "", "getOnSettingsChangedAction", "setOnSettingsChangedAction", "settingsObserver", "Landroid/database/ContentObserver;", "addListener", "", "flag", "Lcom/android/systemui/flags/Flag;", "listener", "Lcom/android/systemui/flags/FlagListenable$Listener;", "createIntent", "Landroid/content/Intent;", "id", "dispatchListenersAndMaybeRestart", "restartAction", "eraseFlag", "getFlagsFuture", "Lcom/google/common/util/concurrent/ListenableFuture;", "", "idToSettingsKey", "", SBContract.SmartBulletin.IS_ENABLED, "(I)Ljava/lang/Boolean;", "readFlagValue", "T", "serializer", "Lcom/android/systemui/flags/FlagSerializer;", "(ILcom/android/systemui/flags/FlagSerializer;)Ljava/lang/Object;", "removeListener", "setFlagValue", "enabled", "Companion", "PerFlagListener", "SettingsObserver", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class FlagManager implements FlagListenable {
    public static final String ACTION_GET_FLAGS = "com.android.systemui.action.GET_FLAGS";
    public static final String ACTION_SET_FLAG = "com.android.systemui.action.SET_FLAG";
    public static final String EXTRA_FLAGS = "flags";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_VALUE = "value";
    public static final String FLAGS_PERMISSION = "com.android.systemui.permission.FLAGS";
    public static final String RECEIVING_PACKAGE = "com.android.systemui";
    private static final String SETTINGS_PREFIX = "systemui/flags";
    private Consumer<Integer> clearCacheAction;
    private final Context context;
    private final Handler handler;
    private final Set<PerFlagListener> listeners;
    private Consumer<Boolean> onSettingsChangedAction;
    private final FlagSettingsHelper settings;
    private final ContentObserver settingsObserver;

    public FlagManager(Context context, FlagSettingsHelper settings, Handler handler) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(settings, "settings");
        Intrinsics.checkNotNullParameter(handler, "handler");
        this.context = context;
        this.settings = settings;
        this.handler = handler;
        this.listeners = new LinkedHashSet();
        this.settingsObserver = new SettingsObserver(this);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public FlagManager(Context context, Handler handler) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(handler, "handler");
        ContentResolver contentResolver = context.getContentResolver();
        Intrinsics.checkNotNullExpressionValue(contentResolver, "context.contentResolver");
        this(context, new FlagSettingsHelper(contentResolver), handler);
    }

    public final Consumer<Boolean> getOnSettingsChangedAction() {
        return this.onSettingsChangedAction;
    }

    public final void setOnSettingsChangedAction(Consumer<Boolean> consumer) {
        this.onSettingsChangedAction = consumer;
    }

    public final Consumer<Integer> getClearCacheAction() {
        return this.clearCacheAction;
    }

    public final void setClearCacheAction(Consumer<Integer> consumer) {
        this.clearCacheAction = consumer;
    }

    public final ListenableFuture<Collection<Flag<?>>> getFlagsFuture() {
        final Intent intent = new Intent(ACTION_GET_FLAGS);
        intent.setPackage(RECEIVING_PACKAGE);
        ListenableFuture<Collection<Flag<?>>> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver() { // from class: com.android.systemui.flags.FlagManager.getFlagsFuture.1
            public final Object attachCompleter(final CallbackToFutureAdapter.Completer<Collection<Flag<?>>> completer) {
                Intrinsics.checkNotNullParameter(completer, "completer");
                FlagManager.this.context.sendOrderedBroadcast(intent, null, new BroadcastReceiver() { // from class: com.android.systemui.flags.FlagManager.getFlagsFuture.1.1
                    @Override // android.content.BroadcastReceiver
                    public void onReceive(Context context, Intent intent2) {
                        Intrinsics.checkNotNullParameter(context, "context");
                        Intrinsics.checkNotNullParameter(intent2, "intent");
                        Bundle resultExtras = getResultExtras(false);
                        ArrayList parcelableArrayList = resultExtras == null ? null : resultExtras.getParcelableArrayList(FlagManager.EXTRA_FLAGS);
                        if (parcelableArrayList != null) {
                            completer.set(parcelableArrayList);
                        } else {
                            completer.setException(new NoFlagResultsException());
                        }
                    }
                }, null, -1, "extra data", null);
                return "QueryingFlags";
            }
        });
        Intrinsics.checkNotNullExpressionValue(future, "fun getFlagsFuture(): Li…ingFlags\"\n        }\n    }");
        return future;
    }

    public final Boolean isEnabled(int id) {
        return (Boolean) readFlagValue(id, BooleanFlagSerializer.INSTANCE);
    }

    public final void setFlagValue(int id, boolean enabled) {
        Intent intentCreateIntent = createIntent(id);
        intentCreateIntent.putExtra("value", enabled);
        this.context.sendBroadcast(intentCreateIntent);
    }

    public final void eraseFlag(int id) {
        this.context.sendBroadcast(createIntent(id));
    }

    public final <T> T readFlagValue(int id, FlagSerializer<T> serializer) {
        Intrinsics.checkNotNullParameter(serializer, "serializer");
        return serializer.fromSettingsData(this.settings.getString(idToSettingsKey(id)));
    }

    @Override // com.android.systemui.flags.FlagListenable
    public void addListener(Flag<?> flag, FlagListenable.Listener listener) {
        Intrinsics.checkNotNullParameter(flag, "flag");
        Intrinsics.checkNotNullParameter(listener, "listener");
        synchronized (this.listeners) {
            boolean zIsEmpty = this.listeners.isEmpty();
            this.listeners.add(new PerFlagListener(flag.getId(), listener));
            if (zIsEmpty) {
                this.settings.registerContentObserver(SETTINGS_PREFIX, true, this.settingsObserver);
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    @Override // com.android.systemui.flags.FlagListenable
    public void removeListener(final FlagListenable.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        synchronized (this.listeners) {
            if (this.listeners.isEmpty()) {
                return;
            }
            this.listeners.removeIf(new Predicate() { // from class: com.android.systemui.flags.FlagManager$removeListener$1$1
                /* JADX DEBUG: Method merged with bridge method: test(Ljava/lang/Object;)Z */
                @Override // java.util.function.Predicate
                public final boolean test(FlagManager.PerFlagListener it) {
                    Intrinsics.checkNotNullParameter(it, "it");
                    return Intrinsics.areEqual(it.getListener(), listener);
                }
            });
            if (this.listeners.isEmpty()) {
                this.settings.unregisterContentObserver(this.settingsObserver);
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    private final Intent createIntent(int id) {
        Intent intent = new Intent(ACTION_SET_FLAG);
        intent.setPackage(RECEIVING_PACKAGE);
        intent.putExtra("id", id);
        return intent;
    }

    public final String idToSettingsKey(int id) {
        return Intrinsics.stringPlus("systemui/flags/", Integer.valueOf(id));
    }

    /* JADX INFO: compiled from: FlagManager.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/flags/FlagManager$SettingsObserver;", "Landroid/database/ContentObserver;", "(Lcom/android/systemui/flags/FlagManager;)V", "onChange", "", "selfChange", "", "uri", "Landroid/net/Uri;", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public final class SettingsObserver extends ContentObserver {
        final /* synthetic */ FlagManager this$0;

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SettingsObserver(FlagManager this$0) {
            super(this$0.handler);
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (uri == null) {
                return;
            }
            String idStr = uri.getPathSegments().get(r2.size() - 1);
            try {
                Intrinsics.checkNotNullExpressionValue(idStr, "idStr");
                int i = Integer.parseInt(idStr);
                Consumer<Integer> clearCacheAction = this.this$0.getClearCacheAction();
                if (clearCacheAction != null) {
                    clearCacheAction.accept(Integer.valueOf(i));
                }
                FlagManager flagManager = this.this$0;
                flagManager.dispatchListenersAndMaybeRestart(i, flagManager.getOnSettingsChangedAction());
            } catch (NumberFormatException unused) {
            }
        }
    }

    public final void dispatchListenersAndMaybeRestart(final int id, Consumer<Boolean> restartAction) {
        ArrayList arrayList;
        synchronized (this.listeners) {
            Set<PerFlagListener> set = this.listeners;
            ArrayList arrayList2 = new ArrayList();
            for (PerFlagListener perFlagListener : set) {
                FlagListenable.Listener listener = perFlagListener.getId() == id ? perFlagListener.getListener() : null;
                if (listener != null) {
                    arrayList2.add(listener);
                }
            }
            arrayList = arrayList2;
        }
        boolean z = false;
        if (arrayList.isEmpty()) {
            if (restartAction == null) {
                return;
            }
            restartAction.accept(false);
            return;
        }
        ArrayList<FlagListenable.Listener> arrayList3 = arrayList;
        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList3, 10));
        for (FlagListenable.Listener listener2 : arrayList3) {
            final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
            listener2.onFlagChanged(new FlagListenable.FlagEvent(id, booleanRef) { // from class: com.android.systemui.flags.FlagManager$dispatchListenersAndMaybeRestart$suppressRestartList$1$event$1
                final /* synthetic */ Ref.BooleanRef $didRequestNoRestart;
                final /* synthetic */ int $id;
                private final int flagId;

                {
                    this.$id = id;
                    this.$didRequestNoRestart = booleanRef;
                    this.flagId = id;
                }

                @Override // com.android.systemui.flags.FlagListenable.FlagEvent
                public int getFlagId() {
                    return this.flagId;
                }

                @Override // com.android.systemui.flags.FlagListenable.FlagEvent
                public void requestNoRestart() {
                    this.$didRequestNoRestart.element = true;
                }
            });
            arrayList4.add(Boolean.valueOf(booleanRef.element));
        }
        ArrayList arrayList5 = arrayList4;
        if ((arrayList5 instanceof Collection) && arrayList5.isEmpty()) {
            z = true;
        } else {
            Iterator it = arrayList5.iterator();
            while (it.hasNext()) {
                if (!((Boolean) it.next()).booleanValue()) {
                    break;
                }
            }
            z = true;
        }
        if (restartAction == null) {
            return;
        }
        restartAction.accept(Boolean.valueOf(z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: FlagManager.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/flags/FlagManager$PerFlagListener;", "", "id", "", "listener", "Lcom/android/systemui/flags/FlagListenable$Listener;", "(ILcom/android/systemui/flags/FlagListenable$Listener;)V", "getId", "()I", "getListener", "()Lcom/android/systemui/flags/FlagListenable$Listener;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    static final /* data */ class PerFlagListener {
        private final int id;
        private final FlagListenable.Listener listener;

        public static /* synthetic */ PerFlagListener copy$default(PerFlagListener perFlagListener, int i, FlagListenable.Listener listener, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = perFlagListener.id;
            }
            if ((i2 & 2) != 0) {
                listener = perFlagListener.listener;
            }
            return perFlagListener.copy(i, listener);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final int getId() {
            return this.id;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final FlagListenable.Listener getListener() {
            return this.listener;
        }

        public final PerFlagListener copy(int id, FlagListenable.Listener listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            return new PerFlagListener(id, listener);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PerFlagListener)) {
                return false;
            }
            PerFlagListener perFlagListener = (PerFlagListener) other;
            return this.id == perFlagListener.id && Intrinsics.areEqual(this.listener, perFlagListener.listener);
        }

        public int hashCode() {
            return (Integer.hashCode(this.id) * 31) + this.listener.hashCode();
        }

        public String toString() {
            return "PerFlagListener(id=" + this.id + ", listener=" + this.listener + ')';
        }

        public PerFlagListener(int i, FlagListenable.Listener listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            this.id = i;
            this.listener = listener;
        }

        public final int getId() {
            return this.id;
        }

        public final FlagListenable.Listener getListener() {
            return this.listener;
        }
    }
}

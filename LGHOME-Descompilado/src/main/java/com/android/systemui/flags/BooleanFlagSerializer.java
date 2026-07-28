package com.android.systemui.flags;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.AdaptedFunctionReference;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: FlagSerializer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/flags/BooleanFlagSerializer;", "Lcom/android/systemui/flags/FlagSerializer;", "", "()V", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class BooleanFlagSerializer extends FlagSerializer<Boolean> {
    public static final BooleanFlagSerializer INSTANCE = new BooleanFlagSerializer();

    /* JADX INFO: renamed from: com.android.systemui.flags.BooleanFlagSerializer$1, reason: invalid class name */
    /* JADX INFO: compiled from: FlagSerializer.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* synthetic */ class AnonymousClass1 extends AdaptedFunctionReference implements Function3<JSONObject, String, Boolean, Unit> {
        public static final AnonymousClass1 INSTANCE = new AnonymousClass1();

        AnonymousClass1() {
            super(3, JSONObject.class, "put", "put(Ljava/lang/String;Z)Lorg/json/JSONObject;", 8);
        }

        /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object, java.lang.Object, java.lang.Object] */
        /* JADX DEBUG: Return type fixed from 'java.lang.Object' to match base method */
        @Override // kotlin.jvm.functions.Function3
        public /* bridge */ /* synthetic */ Unit invoke(JSONObject jSONObject, String str, Boolean bool) throws JSONException {
            invoke(jSONObject, str, bool.booleanValue());
            return Unit.INSTANCE;
        }

        public final void invoke(JSONObject p0, String str, boolean z) throws JSONException {
            Intrinsics.checkNotNullParameter(p0, "p0");
            BooleanFlagSerializer._init_$put(p0, str, z);
        }
    }

    /* JADX INFO: renamed from: com.android.systemui.flags.BooleanFlagSerializer$2, reason: invalid class name */
    /* JADX INFO: compiled from: FlagSerializer.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* synthetic */ class AnonymousClass2 extends FunctionReferenceImpl implements Function2<JSONObject, String, Boolean> {
        public static final AnonymousClass2 INSTANCE = new AnonymousClass2();

        AnonymousClass2() {
            super(2, JSONObject.class, "getBoolean", "getBoolean(Ljava/lang/String;)Z", 0);
        }

        /* JADX DEBUG: Method merged with bridge method: invoke(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // kotlin.jvm.functions.Function2
        public final Boolean invoke(JSONObject p0, String str) {
            Intrinsics.checkNotNullParameter(p0, "p0");
            return Boolean.valueOf(p0.getBoolean(str));
        }
    }

    private BooleanFlagSerializer() {
        super("boolean", AnonymousClass1.INSTANCE, AnonymousClass2.INSTANCE);
    }

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.systemui.flags.BooleanFlagSerializer.1.invoke(org.json.JSONObject, java.lang.String, boolean):void] */
    /* JADX INFO: Access modifiers changed from: private */
    public static final /* synthetic */ void _init_$put(JSONObject jSONObject, String str, boolean z) throws JSONException {
        jSONObject.put(str, z);
    }
}

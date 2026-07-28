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
@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/flags/StringFlagSerializer;", "Lcom/android/systemui/flags/FlagSerializer;", "", "()V", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class StringFlagSerializer extends FlagSerializer<String> {
    public static final StringFlagSerializer INSTANCE = new StringFlagSerializer();

    /* JADX INFO: renamed from: com.android.systemui.flags.StringFlagSerializer$1, reason: invalid class name */
    /* JADX INFO: compiled from: FlagSerializer.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* synthetic */ class AnonymousClass1 extends AdaptedFunctionReference implements Function3<JSONObject, String, Object, Unit> {
        public static final AnonymousClass1 INSTANCE = new AnonymousClass1();

        AnonymousClass1() {
            super(3, JSONObject.class, "put", "put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;", 8);
        }

        /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object, java.lang.Object, java.lang.Object] */
        /* JADX DEBUG: Return type fixed from 'java.lang.Object' to match base method */
        @Override // kotlin.jvm.functions.Function3
        public /* bridge */ /* synthetic */ Unit invoke(JSONObject jSONObject, String str, Object obj) throws JSONException {
            invoke2(jSONObject, str, obj);
            return Unit.INSTANCE;
        }

        /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
        public final void invoke2(JSONObject p0, String str, Object obj) throws JSONException {
            Intrinsics.checkNotNullParameter(p0, "p0");
            StringFlagSerializer._init_$put(p0, str, obj);
        }
    }

    /* JADX INFO: renamed from: com.android.systemui.flags.StringFlagSerializer$2, reason: invalid class name */
    /* JADX INFO: compiled from: FlagSerializer.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* synthetic */ class AnonymousClass2 extends FunctionReferenceImpl implements Function2<JSONObject, String, String> {
        public static final AnonymousClass2 INSTANCE = new AnonymousClass2();

        AnonymousClass2() {
            super(2, JSONObject.class, "getString", "getString(Ljava/lang/String;)Ljava/lang/String;", 0);
        }

        /* JADX DEBUG: Method merged with bridge method: invoke(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // kotlin.jvm.functions.Function2
        public final String invoke(JSONObject p0, String str) {
            Intrinsics.checkNotNullParameter(p0, "p0");
            return p0.getString(str);
        }
    }

    private StringFlagSerializer() {
        super("string", AnonymousClass1.INSTANCE, AnonymousClass2.INSTANCE);
    }

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.systemui.flags.StringFlagSerializer.1.invoke(org.json.JSONObject, java.lang.String, java.lang.Object):void] */
    /* JADX INFO: Access modifiers changed from: private */
    public static final /* synthetic */ void _init_$put(JSONObject jSONObject, String str, Object obj) throws JSONException {
        jSONObject.put(str, obj);
    }
}

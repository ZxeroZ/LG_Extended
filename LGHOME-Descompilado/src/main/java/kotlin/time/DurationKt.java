package kotlin.time;

import kotlin.Deprecated;
import kotlin.DeprecatedSinceKotlin;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: Duration.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000>\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b*\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\f\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a \u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u0005H\u0002ø\u0001\u0000¢\u0006\u0002\u0010&\u001a\u0018\u0010'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010+\u001a\u00020\u00072\u0006\u0010,\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010-\u001a\u00020\u00072\u0006\u0010.\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0010\u0010/\u001a\u00020\u00012\u0006\u0010*\u001a\u00020\u0001H\u0002\u001a\u0010\u00100\u001a\u00020\u00012\u0006\u0010.\u001a\u00020\u0001H\u0002\u001a \u00101\u001a\u00020\u00072\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u000205H\u0002ø\u0001\u0000¢\u0006\u0002\u00106\u001a\u0010\u00107\u001a\u00020\u00012\u0006\u00102\u001a\u000203H\u0002\u001a)\u00108\u001a\u00020\u0005*\u0002032\u0006\u00109\u001a\u00020\u00052\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020<\u0012\u0004\u0012\u0002050;H\u0082\b\u001a)\u0010=\u001a\u000203*\u0002032\u0006\u00109\u001a\u00020\u00052\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020<\u0012\u0004\u0012\u0002050;H\u0082\b\u001a\u001f\u0010>\u001a\u00020\u0007*\u00020\b2\u0006\u0010?\u001a\u00020\u0007H\u0087\nø\u0001\u0000¢\u0006\u0004\b@\u0010A\u001a\u001f\u0010>\u001a\u00020\u0007*\u00020\u00052\u0006\u0010?\u001a\u00020\u0007H\u0087\nø\u0001\u0000¢\u0006\u0004\bB\u0010C\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\b2\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010G\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\u00052\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010H\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\u00012\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010I\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"!\u0010\u0006\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\n\u001a\u0004\b\u000b\u0010\f\"!\u0010\u0006\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\r\u001a\u0004\b\u000b\u0010\u000e\"!\u0010\u0006\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\u000f\u001a\u0004\b\u000b\u0010\u0010\"!\u0010\u0011\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\n\u001a\u0004\b\u0013\u0010\f\"!\u0010\u0011\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\r\u001a\u0004\b\u0013\u0010\u000e\"!\u0010\u0011\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\u000f\u001a\u0004\b\u0013\u0010\u0010\"!\u0010\u0014\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\n\u001a\u0004\b\u0016\u0010\f\"!\u0010\u0014\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\r\u001a\u0004\b\u0016\u0010\u000e\"!\u0010\u0014\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\u000f\u001a\u0004\b\u0016\u0010\u0010\"!\u0010\u0017\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\n\u001a\u0004\b\u0019\u0010\f\"!\u0010\u0017\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\r\u001a\u0004\b\u0019\u0010\u000e\"!\u0010\u0017\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\u000f\u001a\u0004\b\u0019\u0010\u0010\"!\u0010\u001a\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\n\u001a\u0004\b\u001c\u0010\f\"!\u0010\u001a\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\r\u001a\u0004\b\u001c\u0010\u000e\"!\u0010\u001a\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\u000f\u001a\u0004\b\u001c\u0010\u0010\"!\u0010\u001d\u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\n\u001a\u0004\b\u001f\u0010\f\"!\u0010\u001d\u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\r\u001a\u0004\b\u001f\u0010\u000e\"!\u0010\u001d\u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\u000f\u001a\u0004\b\u001f\u0010\u0010\"!\u0010 \u001a\u00020\u0007*\u00020\b8FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\n\u001a\u0004\b\"\u0010\f\"!\u0010 \u001a\u00020\u0007*\u00020\u00058FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\r\u001a\u0004\b\"\u0010\u000e\"!\u0010 \u001a\u00020\u0007*\u00020\u00018FX\u0087\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\u000f\u001a\u0004\b\"\u0010\u0010\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006J"}, d2 = {"MAX_MILLIS", "", "MAX_NANOS", "MAX_NANOS_IN_MILLIS", "NANOS_IN_MILLIS", "", "days", "Lkotlin/time/Duration;", "", "getDays$annotations", "(D)V", "getDays", "(D)J", "(I)V", "(I)J", "(J)V", "(J)J", "hours", "getHours$annotations", "getHours", "microseconds", "getMicroseconds$annotations", "getMicroseconds", "milliseconds", "getMilliseconds$annotations", "getMilliseconds", "minutes", "getMinutes$annotations", "getMinutes", "nanoseconds", "getNanoseconds$annotations", "getNanoseconds", "seconds", "getSeconds$annotations", "getSeconds", "durationOf", "normalValue", "unitDiscriminator", "(JI)J", "durationOfMillis", "normalMillis", "durationOfMillisNormalized", "millis", "durationOfNanos", "normalNanos", "durationOfNanosNormalized", "nanos", "millisToNanos", "nanosToMillis", "parseDuration", "value", "", "strictIso", "", "(Ljava/lang/String;Z)J", "parseOverLongIsoComponent", "skipWhile", "startIndex", "predicate", "Lkotlin/Function1;", "", "substringWhile", "times", "duration", "times-kIfJnKk", "(DJ)J", "times-mvk6XK0", "(IJ)J", "toDuration", "unit", "Lkotlin/time/DurationUnit;", "(DLkotlin/time/DurationUnit;)J", "(ILkotlin/time/DurationUnit;)J", "(JLkotlin/time/DurationUnit;)J", "kotlin-stdlib"}, k = 2, mv = {1, 6, 0}, xi = 48)
public final class DurationKt {
    public static final long MAX_MILLIS = 4611686018427387903L;
    public static final long MAX_NANOS = 4611686018426999999L;
    private static final long MAX_NANOS_IN_MILLIS = 4611686018426L;
    public static final int NANOS_IN_MILLIS = 1000000;

    @Deprecated(message = "Use 'Double.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(long j) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long millisToNanos(long j) {
        return j * ((long) NANOS_IN_MILLIS);
    }

    public static final long toDuration(int i, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        if (unit.compareTo(DurationUnit.SECONDS) <= 0) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(i, unit, DurationUnit.NANOSECONDS));
        }
        return toDuration(i, unit);
    }

    public static final long toDuration(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        long jConvertDurationUnitOverflow = DurationUnitKt.convertDurationUnitOverflow(MAX_NANOS, DurationUnit.NANOSECONDS, unit);
        boolean z = false;
        if ((-jConvertDurationUnitOverflow) <= j && j <= jConvertDurationUnitOverflow) {
            z = true;
        }
        if (z) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(j, unit, DurationUnit.NANOSECONDS));
        }
        return durationOfMillis(RangesKt.coerceIn(DurationUnitKt.convertDurationUnit(j, unit, DurationUnit.MILLISECONDS), -4611686018427387903L, MAX_MILLIS));
    }

    public static final long toDuration(double d, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        double dConvertDurationUnit = DurationUnitKt.convertDurationUnit(d, unit, DurationUnit.NANOSECONDS);
        if (!(!Double.isNaN(dConvertDurationUnit))) {
            throw new IllegalArgumentException("Duration value cannot be NaN.".toString());
        }
        long jRoundToLong = MathKt.roundToLong(dConvertDurationUnit);
        if (-4611686018426999999L <= jRoundToLong && jRoundToLong < 4611686018427000000L) {
            return durationOfNanos(jRoundToLong);
        }
        return durationOfMillisNormalized(MathKt.roundToLong(DurationUnitKt.convertDurationUnit(d, unit, DurationUnit.MILLISECONDS)));
    }

    public static final long getNanoseconds(int i) {
        return toDuration(i, DurationUnit.NANOSECONDS);
    }

    public static final long getNanoseconds(long j) {
        return toDuration(j, DurationUnit.NANOSECONDS);
    }

    public static final long getNanoseconds(double d) {
        return toDuration(d, DurationUnit.NANOSECONDS);
    }

    public static final long getMicroseconds(int i) {
        return toDuration(i, DurationUnit.MICROSECONDS);
    }

    public static final long getMicroseconds(long j) {
        return toDuration(j, DurationUnit.MICROSECONDS);
    }

    public static final long getMicroseconds(double d) {
        return toDuration(d, DurationUnit.MICROSECONDS);
    }

    public static final long getMilliseconds(int i) {
        return toDuration(i, DurationUnit.MILLISECONDS);
    }

    public static final long getMilliseconds(long j) {
        return toDuration(j, DurationUnit.MILLISECONDS);
    }

    public static final long getMilliseconds(double d) {
        return toDuration(d, DurationUnit.MILLISECONDS);
    }

    public static final long getSeconds(int i) {
        return toDuration(i, DurationUnit.SECONDS);
    }

    public static final long getSeconds(long j) {
        return toDuration(j, DurationUnit.SECONDS);
    }

    public static final long getSeconds(double d) {
        return toDuration(d, DurationUnit.SECONDS);
    }

    public static final long getMinutes(int i) {
        return toDuration(i, DurationUnit.MINUTES);
    }

    public static final long getMinutes(long j) {
        return toDuration(j, DurationUnit.MINUTES);
    }

    public static final long getMinutes(double d) {
        return toDuration(d, DurationUnit.MINUTES);
    }

    public static final long getHours(int i) {
        return toDuration(i, DurationUnit.HOURS);
    }

    public static final long getHours(long j) {
        return toDuration(j, DurationUnit.HOURS);
    }

    public static final long getHours(double d) {
        return toDuration(d, DurationUnit.HOURS);
    }

    public static final long getDays(int i) {
        return toDuration(i, DurationUnit.DAYS);
    }

    public static final long getDays(long j) {
        return toDuration(j, DurationUnit.DAYS);
    }

    public static final long getDays(double d) {
        return toDuration(d, DurationUnit.DAYS);
    }

    /* JADX INFO: renamed from: times-mvk6XK0, reason: not valid java name */
    private static final long m2538timesmvk6XK0(int i, long j) {
        return Duration.m2450timesUwyO8pc(j, i);
    }

    /* JADX INFO: renamed from: times-kIfJnKk, reason: not valid java name */
    private static final long m2537timeskIfJnKk(double d, long j) {
        return Duration.m2449timesUwyO8pc(j, d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:183:0x00a6 A[EDGE_INSN: B:183:0x00a6->B:53:0x00a6 BREAK  A[LOOP:1: B:36:0x006c->B:51:0x009a], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x009a A[LOOP:1: B:36:0x006c->B:51:0x009a, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final long parseDuration(java.lang.String r27, boolean r28) {
        /*
            r6 = r27
            int r7 = r27.length()
            if (r7 == 0) goto L2ba
            kotlin.time.Duration$Companion r0 = kotlin.time.Duration.INSTANCE
            long r8 = r0.m2514getZEROUwyO8pc()
            r10 = 0
            char r0 = r6.charAt(r10)
            r1 = 43
            r2 = 45
            r11 = 1
            if (r0 != r1) goto L1c
        L1a:
            r12 = r11
            goto L20
        L1c:
            if (r0 != r2) goto L1f
            goto L1a
        L1f:
            r12 = r10
        L20:
            if (r12 <= 0) goto L24
            r13 = r11
            goto L25
        L24:
            r13 = r10
        L25:
            r0 = 2
            r14 = 0
            if (r13 == 0) goto L34
            r1 = r6
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            boolean r1 = kotlin.text.StringsKt.startsWith$default(r1, r2, r10, r0, r14)
            if (r1 == 0) goto L34
            r15 = r11
            goto L35
        L34:
            r15 = r10
        L35:
            java.lang.String r5 = "No components"
            if (r7 <= r12) goto L2b3
            char r1 = r6.charAt(r12)
            r2 = 80
            java.lang.String r4 = "this as java.lang.String).substring(startIndex)"
            java.lang.String r3 = "Unexpected order of duration components"
            r16 = r5
            r5 = 58
            r0 = 48
            java.lang.String r10 = "this as java.lang.String…ing(startIndex, endIndex)"
            if (r1 != r2) goto L157
            int r12 = r12 + r11
            if (r12 == r7) goto L151
            r2 = r14
            r1 = 0
        L52:
            if (r12 >= r7) goto L14d
            char r13 = r6.charAt(r12)
            r11 = 84
            if (r13 != r11) goto L6b
            if (r1 != 0) goto L65
            int r12 = r12 + 1
            if (r12 == r7) goto L65
            r1 = 1
            r11 = 1
            goto L52
        L65:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L6b:
            r11 = r12
        L6c:
            int r13 = r27.length()
            if (r11 >= r13) goto La3
            char r13 = r6.charAt(r11)
            if (r0 > r13) goto L7d
            if (r13 >= r5) goto L7d
            r16 = 1
            goto L7f
        L7d:
            r16 = 0
        L7f:
            if (r16 != 0) goto L94
            java.lang.String r16 = "+-."
            r0 = r16
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r17 = r15
            r5 = 2
            r15 = 0
            boolean r0 = kotlin.text.StringsKt.contains$default(r0, r13, r15, r5, r14)
            if (r0 == 0) goto L92
            goto L97
        L92:
            r0 = 0
            goto L98
        L94:
            r17 = r15
            r5 = 2
        L97:
            r0 = 1
        L98:
            if (r0 == 0) goto La6
            int r11 = r11 + 1
            r15 = r17
            r0 = 48
            r5 = 58
            goto L6c
        La3:
            r17 = r15
            r5 = 2
        La6:
            java.lang.String r0 = r6.substring(r12, r11)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r10)
            r20 = r0
            java.lang.CharSequence r20 = (java.lang.CharSequence) r20
            int r11 = r20.length()
            if (r11 != 0) goto Lb9
            r11 = 1
            goto Lba
        Lb9:
            r11 = 0
        Lba:
            if (r11 != 0) goto L147
            int r11 = r0.length()
            int r12 = r12 + r11
            r11 = r6
            java.lang.CharSequence r11 = (java.lang.CharSequence) r11
            if (r12 < 0) goto L13b
            int r13 = kotlin.text.StringsKt.getLastIndex(r11)
            if (r12 > r13) goto L13b
            char r11 = r11.charAt(r12)
            int r12 = r12 + 1
            kotlin.time.DurationUnit r11 = kotlin.time.DurationUnitKt.durationUnitByIsoChar(r11, r1)
            if (r2 == 0) goto Le8
            r13 = r11
            java.lang.Enum r13 = (java.lang.Enum) r13
            int r2 = r2.compareTo(r13)
            if (r2 <= 0) goto Le2
            goto Le8
        Le2:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r3)
            throw r0
        Le8:
            r21 = 46
            r22 = 0
            r23 = 0
            r24 = 6
            r25 = 0
            int r2 = kotlin.text.StringsKt.indexOf$default(r20, r21, r22, r23, r24, r25)
            kotlin.time.DurationUnit r13 = kotlin.time.DurationUnit.SECONDS
            if (r11 != r13) goto L124
            if (r2 <= 0) goto L124
            r13 = 0
            java.lang.String r15 = r0.substring(r13, r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r15, r10)
            long r14 = parseOverLongIsoComponent(r15)
            long r13 = toDuration(r14, r11)
            long r8 = kotlin.time.Duration.m2448plusLRDsOJo(r8, r13)
            java.lang.String r0 = r0.substring(r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r4)
            double r13 = java.lang.Double.parseDouble(r0)
            long r13 = toDuration(r13, r11)
            long r8 = kotlin.time.Duration.m2448plusLRDsOJo(r8, r13)
            goto L130
        L124:
            long r13 = parseOverLongIsoComponent(r0)
            long r13 = toDuration(r13, r11)
            long r8 = kotlin.time.Duration.m2448plusLRDsOJo(r8, r13)
        L130:
            r2 = r11
            r15 = r17
            r0 = 48
            r5 = 58
            r11 = 1
            r14 = 0
            goto L52
        L13b:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Missing unit for value "
            java.lang.String r0 = kotlin.jvm.internal.Intrinsics.stringPlus(r2, r0)
            r1.<init>(r0)
            throw r1
        L147:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L14d:
            r17 = r15
            goto L2a6
        L151:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L157:
            r17 = r15
            if (r28 != 0) goto L2ad
            r5 = 0
            int r0 = r7 - r12
            r1 = 8
            int r11 = java.lang.Math.max(r0, r1)
            r14 = 1
            java.lang.String r2 = "Infinity"
            r15 = 48
            r0 = r27
            r1 = r12
            r26 = r3
            r3 = r5
            r5 = r4
            r4 = r11
            r11 = r5
            r15 = r16
            r5 = r14
            boolean r0 = kotlin.text.StringsKt.regionMatches(r0, r1, r2, r3, r4, r5)
            if (r0 == 0) goto L183
            kotlin.time.Duration$Companion r0 = kotlin.time.Duration.INSTANCE
            long r8 = r0.m2512getINFINITEUwyO8pc()
            goto L2a6
        L183:
            r0 = r13 ^ 1
            if (r13 == 0) goto L1a8
            char r1 = r6.charAt(r12)
            r2 = 40
            if (r1 != r2) goto L1a8
            r1 = r6
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            char r1 = kotlin.text.StringsKt.last(r1)
            r2 = 41
            if (r1 != r2) goto L1a8
            int r12 = r12 + 1
            int r7 = r7 + (-1)
            if (r12 == r7) goto L1a2
            r0 = 1
            goto L1a8
        L1a2:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r15)
            throw r0
        L1a8:
            r14 = 0
            r15 = 0
        L1aa:
            if (r12 >= r7) goto L2a6
            if (r15 == 0) goto L1c6
            if (r0 == 0) goto L1c6
        L1b0:
            int r1 = r27.length()
            if (r12 >= r1) goto L1c6
            char r1 = r6.charAt(r12)
            r2 = 32
            if (r1 != r2) goto L1c0
            r15 = 1
            goto L1c1
        L1c0:
            r15 = 0
        L1c1:
            if (r15 == 0) goto L1c6
            int r12 = r12 + 1
            goto L1b0
        L1c6:
            r1 = r12
        L1c7:
            int r2 = r27.length()
            if (r1 >= r2) goto L1eb
            char r2 = r6.charAt(r1)
            r3 = 48
            r4 = 58
            if (r3 > r2) goto L1db
            if (r2 >= r4) goto L1db
            r15 = 1
            goto L1dc
        L1db:
            r15 = 0
        L1dc:
            if (r15 != 0) goto L1e5
            r5 = 46
            if (r2 != r5) goto L1e3
            goto L1e5
        L1e3:
            r15 = 0
            goto L1e6
        L1e5:
            r15 = 1
        L1e6:
            if (r15 == 0) goto L1ef
            int r1 = r1 + 1
            goto L1c7
        L1eb:
            r3 = 48
            r4 = 58
        L1ef:
            java.lang.String r1 = r6.substring(r12, r1)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r10)
            r18 = r1
            java.lang.CharSequence r18 = (java.lang.CharSequence) r18
            int r2 = r18.length()
            if (r2 != 0) goto L202
            r15 = 1
            goto L203
        L202:
            r15 = 0
        L203:
            if (r15 != 0) goto L2a0
            int r2 = r1.length()
            int r12 = r12 + r2
            r2 = r12
        L20b:
            int r5 = r27.length()
            if (r2 >= r5) goto L225
            char r5 = r6.charAt(r2)
            r13 = 97
            if (r13 > r5) goto L21f
            r13 = 123(0x7b, float:1.72E-43)
            if (r5 >= r13) goto L21f
            r15 = 1
            goto L220
        L21f:
            r15 = 0
        L220:
            if (r15 == 0) goto L225
            int r2 = r2 + 1
            goto L20b
        L225:
            java.lang.String r2 = r6.substring(r12, r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r10)
            int r5 = r2.length()
            int r12 = r12 + r5
            kotlin.time.DurationUnit r2 = kotlin.time.DurationUnitKt.durationUnitByShortName(r2)
            if (r14 == 0) goto L249
            r5 = r2
            java.lang.Enum r5 = (java.lang.Enum) r5
            int r5 = r14.compareTo(r5)
            if (r5 <= 0) goto L241
            goto L249
        L241:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r5 = r26
            r0.<init>(r5)
            throw r0
        L249:
            r5 = r26
            r19 = 46
            r20 = 0
            r21 = 0
            r22 = 6
            r23 = 0
            int r13 = kotlin.text.StringsKt.indexOf$default(r18, r19, r20, r21, r22, r23)
            if (r13 <= 0) goto L28d
            r14 = 0
            java.lang.String r15 = r1.substring(r14, r13)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r15, r10)
            long r3 = java.lang.Long.parseLong(r15)
            long r3 = toDuration(r3, r2)
            long r3 = kotlin.time.Duration.m2448plusLRDsOJo(r8, r3)
            java.lang.String r1 = r1.substring(r13)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r11)
            double r8 = java.lang.Double.parseDouble(r1)
            long r8 = toDuration(r8, r2)
            long r8 = kotlin.time.Duration.m2448plusLRDsOJo(r3, r8)
            if (r12 < r7) goto L285
            goto L29a
        L285:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Fractional component must be last"
            r0.<init>(r1)
            throw r0
        L28d:
            r14 = 0
            long r3 = java.lang.Long.parseLong(r1)
            long r3 = toDuration(r3, r2)
            long r8 = kotlin.time.Duration.m2448plusLRDsOJo(r8, r3)
        L29a:
            r14 = r2
            r26 = r5
            r15 = 1
            goto L1aa
        L2a0:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L2a6:
            if (r17 == 0) goto L2ac
            long r8 = kotlin.time.Duration.m2464unaryMinusUwyO8pc(r8)
        L2ac:
            return r8
        L2ad:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L2b3:
            r15 = r5
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r15)
            throw r0
        L2ba:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "The string is empty"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.time.DurationKt.parseDuration(java.lang.String, boolean):long");
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0062  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static final long parseOverLongIsoComponent(java.lang.String r7) {
        /*
            int r0 = r7.length()
            r1 = 0
            r2 = 2
            r3 = 1
            r4 = 0
            if (r0 <= 0) goto L1a
            java.lang.String r5 = "+-"
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            char r6 = r7.charAt(r4)
            boolean r5 = kotlin.text.StringsKt.contains$default(r5, r6, r4, r2, r1)
            if (r5 == 0) goto L1a
            r5 = r3
            goto L1b
        L1a:
            r5 = r4
        L1b:
            int r0 = r0 - r5
            r6 = 16
            if (r0 <= r6) goto L73
            kotlin.ranges.IntRange r0 = new kotlin.ranges.IntRange
            r6 = r7
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            int r6 = kotlin.text.StringsKt.getLastIndex(r6)
            r0.<init>(r5, r6)
            java.lang.Iterable r0 = (java.lang.Iterable) r0
            boolean r5 = r0 instanceof java.util.Collection
            if (r5 == 0) goto L3d
            r5 = r0
            java.util.Collection r5 = (java.util.Collection) r5
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L3d
        L3b:
            r0 = r3
            goto L60
        L3d:
            java.util.Iterator r0 = r0.iterator()
        L41:
            boolean r5 = r0.hasNext()
            if (r5 == 0) goto L3b
            r5 = r0
            kotlin.collections.IntIterator r5 = (kotlin.collections.IntIterator) r5
            int r5 = r5.nextInt()
            char r5 = r7.charAt(r5)
            r6 = 48
            if (r6 > r5) goto L5c
            r6 = 58
            if (r5 >= r6) goto L5c
            r5 = r3
            goto L5d
        L5c:
            r5 = r4
        L5d:
            if (r5 != 0) goto L41
            r0 = r4
        L60:
            if (r0 == 0) goto L73
            char r7 = r7.charAt(r4)
            r0 = 45
            if (r7 != r0) goto L6d
            r0 = -9223372036854775808
            goto L72
        L6d:
            r0 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
        L72:
            return r0
        L73:
            java.lang.String r0 = "+"
            boolean r0 = kotlin.text.StringsKt.startsWith$default(r7, r0, r4, r2, r1)
            if (r0 == 0) goto L7f
            java.lang.String r7 = kotlin.text.StringsKt.drop(r7, r3)
        L7f:
            long r0 = java.lang.Long.parseLong(r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.time.DurationKt.parseOverLongIsoComponent(java.lang.String):long");
    }

    private static final int skipWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        while (i < str.length() && function1.invoke(Character.valueOf(str.charAt(i))).booleanValue()) {
            i++;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long nanosToMillis(long j) {
        return j / ((long) NANOS_IN_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfNanos(long j) {
        return Duration.m2412constructorimpl(j << 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfMillis(long j) {
        return Duration.m2412constructorimpl((j << 1) + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOf(long j, int i) {
        return Duration.m2412constructorimpl((j << 1) + ((long) i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfNanosNormalized(long j) {
        boolean z = false;
        if (-4611686018426999999L <= j && j < 4611686018427000000L) {
            z = true;
        }
        if (z) {
            return durationOfNanos(j);
        }
        return durationOfMillis(nanosToMillis(j));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfMillisNormalized(long j) {
        boolean z = false;
        if (-4611686018426L <= j && j < 4611686018427L) {
            z = true;
        }
        if (z) {
            return durationOfNanos(millisToNanos(j));
        }
        return durationOfMillis(RangesKt.coerceIn(j, -4611686018427387903L, MAX_MILLIS));
    }

    private static final String substringWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        int i2 = i;
        while (i2 < str.length() && function1.invoke(Character.valueOf(str.charAt(i2))).booleanValue()) {
            i2++;
        }
        String strSubstring = str.substring(i, i2);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
        return strSubstring;
    }
}

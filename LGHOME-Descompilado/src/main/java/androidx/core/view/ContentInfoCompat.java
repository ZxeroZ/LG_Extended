package androidx.core.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.core.util.Predicate;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class ContentInfoCompat {
    public static final int FLAG_CONVERT_TO_PLAIN_TEXT = 1;
    public static final int SOURCE_APP = 0;
    public static final int SOURCE_CLIPBOARD = 1;
    public static final int SOURCE_DRAG_AND_DROP = 3;
    public static final int SOURCE_INPUT_METHOD = 2;
    final ClipData mClip;
    final Bundle mExtras;
    final int mFlags;
    final Uri mLinkUri;
    final int mSource;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Source {
    }

    static String sourceToString(int source) {
        return source != 0 ? source != 1 ? source != 2 ? source != 3 ? String.valueOf(source) : "SOURCE_DRAG_AND_DROP" : "SOURCE_INPUT_METHOD" : "SOURCE_CLIPBOARD" : "SOURCE_APP";
    }

    static String flagsToString(int flags) {
        return (flags & 1) != 0 ? "FLAG_CONVERT_TO_PLAIN_TEXT" : String.valueOf(flags);
    }

    ContentInfoCompat(Builder b) {
        this.mClip = (ClipData) Preconditions.checkNotNull(b.mClip);
        this.mSource = Preconditions.checkArgumentInRange(b.mSource, 0, 3, "source");
        this.mFlags = Preconditions.checkFlagsArgument(b.mFlags, 1);
        this.mLinkUri = b.mLinkUri;
        this.mExtras = b.mExtras;
    }

    public String toString() {
        return "ContentInfoCompat{clip=" + this.mClip.getDescription() + ", source=" + sourceToString(this.mSource) + ", flags=" + flagsToString(this.mFlags) + (this.mLinkUri == null ? "" : ", hasLinkUri(" + this.mLinkUri.toString().length() + ")") + (this.mExtras != null ? ", hasExtras" : "") + "}";
    }

    public ClipData getClip() {
        return this.mClip;
    }

    public int getSource() {
        return this.mSource;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public Uri getLinkUri() {
        return this.mLinkUri;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Pair<ContentInfoCompat, ContentInfoCompat> partition(Predicate<ClipData.Item> itemPredicate) {
        if (this.mClip.getItemCount() == 1) {
            boolean zTest = itemPredicate.test(this.mClip.getItemAt(0));
            return Pair.create(zTest ? this : null, zTest ? null : this);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < this.mClip.getItemCount(); i++) {
            ClipData.Item itemAt = this.mClip.getItemAt(i);
            if (itemPredicate.test(itemAt)) {
                arrayList.add(itemAt);
            } else {
                arrayList2.add(itemAt);
            }
        }
        if (arrayList.isEmpty()) {
            return Pair.create(null, this);
        }
        if (arrayList2.isEmpty()) {
            return Pair.create(this, null);
        }
        return Pair.create(new Builder(this).setClip(buildClipData(this.mClip.getDescription(), arrayList)).build(), new Builder(this).setClip(buildClipData(this.mClip.getDescription(), arrayList2)).build());
    }

    private static ClipData buildClipData(ClipDescription description, List<ClipData.Item> items) {
        ClipData clipData = new ClipData(new ClipDescription(description), items.get(0));
        for (int i = 1; i < items.size(); i++) {
            clipData.addItem(items.get(i));
        }
        return clipData;
    }

    public static final class Builder {
        ClipData mClip;
        Bundle mExtras;
        int mFlags;
        Uri mLinkUri;
        int mSource;

        public Builder(ContentInfoCompat other) {
            this.mClip = other.mClip;
            this.mSource = other.mSource;
            this.mFlags = other.mFlags;
            this.mLinkUri = other.mLinkUri;
            this.mExtras = other.mExtras;
        }

        public Builder(ClipData clip, int source) {
            this.mClip = clip;
            this.mSource = source;
        }

        public Builder setClip(ClipData clip) {
            this.mClip = clip;
            return this;
        }

        public Builder setSource(int source) {
            this.mSource = source;
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public Builder setLinkUri(Uri linkUri) {
            this.mLinkUri = linkUri;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public ContentInfoCompat build() {
            return new ContentInfoCompat(this);
        }
    }
}

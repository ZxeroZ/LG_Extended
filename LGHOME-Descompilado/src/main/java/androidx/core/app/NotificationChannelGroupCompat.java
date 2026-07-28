package androidx.core.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.os.Build;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NotificationChannelGroupCompat {
    private boolean mBlocked;
    private List<NotificationChannelCompat> mChannels;
    String mDescription;
    final String mId;
    CharSequence mName;

    public static class Builder {
        final NotificationChannelGroupCompat mGroup;

        public Builder(String id) {
            this.mGroup = new NotificationChannelGroupCompat(id);
        }

        public Builder setName(CharSequence name) {
            this.mGroup.mName = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.mGroup.mDescription = description;
            return this;
        }

        public NotificationChannelGroupCompat build() {
            return this.mGroup;
        }
    }

    NotificationChannelGroupCompat(String id) {
        this.mChannels = Collections.emptyList();
        this.mId = (String) Preconditions.checkNotNull(id);
    }

    NotificationChannelGroupCompat(NotificationChannelGroup group) {
        this(group, Collections.emptyList());
    }

    NotificationChannelGroupCompat(NotificationChannelGroup group, List<NotificationChannel> allChannels) {
        this(group.getId());
        this.mName = group.getName();
        if (Build.VERSION.SDK_INT >= 28) {
            this.mDescription = group.getDescription();
        }
        if (Build.VERSION.SDK_INT >= 28) {
            this.mBlocked = group.isBlocked();
            this.mChannels = getChannelsCompat(group.getChannels());
        } else {
            this.mChannels = getChannelsCompat(allChannels);
        }
    }

    private List<NotificationChannelCompat> getChannelsCompat(List<NotificationChannel> channels) {
        ArrayList arrayList = new ArrayList();
        for (NotificationChannel notificationChannel : channels) {
            if (this.mId.equals(notificationChannel.getGroup())) {
                arrayList.add(new NotificationChannelCompat(notificationChannel));
            }
        }
        return arrayList;
    }

    NotificationChannelGroup getNotificationChannelGroup() {
        if (Build.VERSION.SDK_INT < 26) {
            return null;
        }
        NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(this.mId, this.mName);
        if (Build.VERSION.SDK_INT >= 28) {
            notificationChannelGroup.setDescription(this.mDescription);
        }
        return notificationChannelGroup;
    }

    public Builder toBuilder() {
        return new Builder(this.mId).setName(this.mName).setDescription(this.mDescription);
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public boolean isBlocked() {
        return this.mBlocked;
    }

    public List<NotificationChannelCompat> getChannels() {
        return this.mChannels;
    }
}

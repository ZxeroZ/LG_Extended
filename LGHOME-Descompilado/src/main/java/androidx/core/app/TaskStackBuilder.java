package androidx.core.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class TaskStackBuilder implements Iterable<Intent> {
    private static final String TAG = "TaskStackBuilder";
    private final ArrayList<Intent> mIntents = new ArrayList<>();
    private final Context mSourceContext;

    public interface SupportParentable {
        Intent getSupportParentActivityIntent();
    }

    private TaskStackBuilder(Context a) {
        this.mSourceContext = a;
    }

    public static TaskStackBuilder create(Context context) {
        return new TaskStackBuilder(context);
    }

    @Deprecated
    public static TaskStackBuilder from(Context context) {
        return create(context);
    }

    public TaskStackBuilder addNextIntent(Intent nextIntent) {
        this.mIntents.add(nextIntent);
        return this;
    }

    public TaskStackBuilder addNextIntentWithParentStack(Intent nextIntent) {
        ComponentName component = nextIntent.getComponent();
        if (component == null) {
            component = nextIntent.resolveActivity(this.mSourceContext.getPackageManager());
        }
        if (component != null) {
            addParentStack(component);
        }
        addNextIntent(nextIntent);
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.app.Activity */
    /* JADX WARN: Multi-variable type inference failed */
    public TaskStackBuilder addParentStack(Activity sourceActivity) {
        Intent supportParentActivityIntent = sourceActivity instanceof SupportParentable ? ((SupportParentable) sourceActivity).getSupportParentActivityIntent() : null;
        if (supportParentActivityIntent == null) {
            supportParentActivityIntent = NavUtils.getParentActivityIntent(sourceActivity);
        }
        if (supportParentActivityIntent != null) {
            ComponentName component = supportParentActivityIntent.getComponent();
            if (component == null) {
                component = supportParentActivityIntent.resolveActivity(this.mSourceContext.getPackageManager());
            }
            addParentStack(component);
            addNextIntent(supportParentActivityIntent);
        }
        return this;
    }

    public TaskStackBuilder addParentStack(Class<?> sourceActivityClass) {
        return addParentStack(new ComponentName(this.mSourceContext, sourceActivityClass));
    }

    public TaskStackBuilder addParentStack(ComponentName sourceActivityName) {
        int size = this.mIntents.size();
        try {
            Intent parentActivityIntent = NavUtils.getParentActivityIntent(this.mSourceContext, sourceActivityName);
            while (parentActivityIntent != null) {
                this.mIntents.add(size, parentActivityIntent);
                parentActivityIntent = NavUtils.getParentActivityIntent(this.mSourceContext, parentActivityIntent.getComponent());
            }
            return this;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Bad ComponentName while traversing activity parent metadata");
            throw new IllegalArgumentException(e);
        }
    }

    public int getIntentCount() {
        return this.mIntents.size();
    }

    @Deprecated
    public Intent getIntent(int index) {
        return editIntentAt(index);
    }

    public Intent editIntentAt(int index) {
        return this.mIntents.get(index);
    }

    @Override // java.lang.Iterable
    @Deprecated
    public Iterator<Intent> iterator() {
        return this.mIntents.iterator();
    }

    public void startActivities() {
        startActivities(null);
    }

    public void startActivities(Bundle options) {
        if (this.mIntents.isEmpty()) {
            throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
        }
        ArrayList<Intent> arrayList = this.mIntents;
        Intent[] intentArr = (Intent[]) arrayList.toArray(new Intent[arrayList.size()]);
        intentArr[0] = new Intent(intentArr[0]).addFlags(268484608);
        if (ContextCompat.startActivities(this.mSourceContext, intentArr, options)) {
            return;
        }
        Intent intent = new Intent(intentArr[intentArr.length - 1]);
        intent.addFlags(268435456);
        this.mSourceContext.startActivity(intent);
    }

    public PendingIntent getPendingIntent(int requestCode, int flags) {
        return getPendingIntent(requestCode, flags, null);
    }

    public PendingIntent getPendingIntent(int requestCode, int flags, Bundle options) {
        if (this.mIntents.isEmpty()) {
            throw new IllegalStateException("No intents added to TaskStackBuilder; cannot getPendingIntent");
        }
        ArrayList<Intent> arrayList = this.mIntents;
        Intent[] intentArr = (Intent[]) arrayList.toArray(new Intent[arrayList.size()]);
        intentArr[0] = new Intent(intentArr[0]).addFlags(268484608);
        if (Build.VERSION.SDK_INT >= 16) {
            return PendingIntent.getActivities(this.mSourceContext, requestCode, intentArr, flags, options);
        }
        return PendingIntent.getActivities(this.mSourceContext, requestCode, intentArr, flags);
    }

    public Intent[] getIntents() {
        int size = this.mIntents.size();
        Intent[] intentArr = new Intent[size];
        if (size == 0) {
            return intentArr;
        }
        intentArr[0] = new Intent(this.mIntents.get(0)).addFlags(268484608);
        for (int i = 1; i < size; i++) {
            intentArr[i] = new Intent(this.mIntents.get(i));
        }
        return intentArr;
    }
}

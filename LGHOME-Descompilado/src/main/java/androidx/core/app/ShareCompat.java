package androidx.core.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import androidx.core.content.IntentCompat;
import androidx.core.util.Preconditions;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class ShareCompat {
    public static final String EXTRA_CALLING_ACTIVITY = "androidx.core.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_ACTIVITY_INTEROP = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_PACKAGE = "androidx.core.app.EXTRA_CALLING_PACKAGE";
    public static final String EXTRA_CALLING_PACKAGE_INTEROP = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
    private static final String HISTORY_FILENAME_PREFIX = ".sharecompat_";

    private ShareCompat() {
    }

    public static String getCallingPackage(Activity calledActivity) {
        Intent intent = calledActivity.getIntent();
        String callingPackage = calledActivity.getCallingPackage();
        return (callingPackage != null || intent == null) ? callingPackage : getCallingPackage(intent);
    }

    static String getCallingPackage(Intent intent) {
        String stringExtra = intent.getStringExtra(EXTRA_CALLING_PACKAGE);
        return stringExtra == null ? intent.getStringExtra(EXTRA_CALLING_PACKAGE_INTEROP) : stringExtra;
    }

    public static ComponentName getCallingActivity(Activity calledActivity) {
        Intent intent = calledActivity.getIntent();
        ComponentName callingActivity = calledActivity.getCallingActivity();
        return callingActivity == null ? getCallingActivity(intent) : callingActivity;
    }

    static ComponentName getCallingActivity(Intent intent) {
        ComponentName componentName = (ComponentName) intent.getParcelableExtra(EXTRA_CALLING_ACTIVITY);
        return componentName == null ? (ComponentName) intent.getParcelableExtra(EXTRA_CALLING_ACTIVITY_INTEROP) : componentName;
    }

    @Deprecated
    public static void configureMenuItem(MenuItem item, IntentBuilder shareIntent) {
        ShareActionProvider shareActionProvider;
        ActionProvider actionProvider = item.getActionProvider();
        if (!(actionProvider instanceof ShareActionProvider)) {
            shareActionProvider = new ShareActionProvider(shareIntent.getContext());
        } else {
            shareActionProvider = (ShareActionProvider) actionProvider;
        }
        shareActionProvider.setShareHistoryFileName(HISTORY_FILENAME_PREFIX + shareIntent.getContext().getClass().getName());
        shareActionProvider.setShareIntent(shareIntent.getIntent());
        item.setActionProvider(shareActionProvider);
        if (Build.VERSION.SDK_INT >= 16 || item.hasSubMenu()) {
            return;
        }
        item.setIntent(shareIntent.createChooserIntent());
    }

    @Deprecated
    public static void configureMenuItem(Menu menu, int menuItemId, IntentBuilder shareIntent) {
        MenuItem menuItemFindItem = menu.findItem(menuItemId);
        if (menuItemFindItem == null) {
            throw new IllegalArgumentException("Could not find menu item with id " + menuItemId + " in the supplied menu");
        }
        configureMenuItem(menuItemFindItem, shareIntent);
    }

    public static class IntentBuilder {
        private ArrayList<String> mBccAddresses;
        private ArrayList<String> mCcAddresses;
        private CharSequence mChooserTitle;
        private final Context mContext;
        private final Intent mIntent;
        private ArrayList<Uri> mStreams;
        private ArrayList<String> mToAddresses;

        @Deprecated
        public static IntentBuilder from(Activity launchingActivity) {
            return new IntentBuilder(launchingActivity);
        }

        public IntentBuilder(Context launchingContext) {
            Activity activity;
            this.mContext = (Context) Preconditions.checkNotNull(launchingContext);
            Intent action = new Intent().setAction("android.intent.action.SEND");
            this.mIntent = action;
            action.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, launchingContext.getPackageName());
            action.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE_INTEROP, launchingContext.getPackageName());
            action.addFlags(524288);
            while (true) {
                if (!(launchingContext instanceof ContextWrapper)) {
                    activity = null;
                    break;
                } else {
                    if (launchingContext instanceof Activity) {
                        activity = (Activity) launchingContext;
                        break;
                    }
                    launchingContext = ((ContextWrapper) launchingContext).getBaseContext();
                }
            }
            if (activity != null) {
                ComponentName componentName = activity.getComponentName();
                this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName);
                this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY_INTEROP, componentName);
            }
        }

        public Intent getIntent() {
            ArrayList<String> arrayList = this.mToAddresses;
            if (arrayList != null) {
                combineArrayExtra("android.intent.extra.EMAIL", arrayList);
                this.mToAddresses = null;
            }
            ArrayList<String> arrayList2 = this.mCcAddresses;
            if (arrayList2 != null) {
                combineArrayExtra("android.intent.extra.CC", arrayList2);
                this.mCcAddresses = null;
            }
            ArrayList<String> arrayList3 = this.mBccAddresses;
            if (arrayList3 != null) {
                combineArrayExtra("android.intent.extra.BCC", arrayList3);
                this.mBccAddresses = null;
            }
            ArrayList<Uri> arrayList4 = this.mStreams;
            if (!(arrayList4 != null && arrayList4.size() > 1)) {
                this.mIntent.setAction("android.intent.action.SEND");
                ArrayList<Uri> arrayList5 = this.mStreams;
                if (arrayList5 != null && !arrayList5.isEmpty()) {
                    this.mIntent.putExtra("android.intent.extra.STREAM", this.mStreams.get(0));
                    if (Build.VERSION.SDK_INT >= 16) {
                        Api16Impl.migrateExtraStreamToClipData(this.mIntent, this.mStreams);
                    }
                } else {
                    this.mIntent.removeExtra("android.intent.extra.STREAM");
                    if (Build.VERSION.SDK_INT >= 16) {
                        Api16Impl.removeClipData(this.mIntent);
                    }
                }
            } else {
                this.mIntent.setAction("android.intent.action.SEND_MULTIPLE");
                this.mIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mStreams);
                if (Build.VERSION.SDK_INT >= 16) {
                    Api16Impl.migrateExtraStreamToClipData(this.mIntent, this.mStreams);
                }
            }
            return this.mIntent;
        }

        Context getContext() {
            return this.mContext;
        }

        private void combineArrayExtra(String extra, ArrayList<String> add) {
            String[] stringArrayExtra = this.mIntent.getStringArrayExtra(extra);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr = new String[add.size() + length];
            add.toArray(strArr);
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr, add.size(), length);
            }
            this.mIntent.putExtra(extra, strArr);
        }

        private void combineArrayExtra(String extra, String[] add) {
            Intent intent = getIntent();
            String[] stringArrayExtra = intent.getStringArrayExtra(extra);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr = new String[add.length + length];
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr, 0, length);
            }
            System.arraycopy(add, 0, strArr, length, add.length);
            intent.putExtra(extra, strArr);
        }

        public Intent createChooserIntent() {
            return Intent.createChooser(getIntent(), this.mChooserTitle);
        }

        public void startChooser() {
            this.mContext.startActivity(createChooserIntent());
        }

        public IntentBuilder setChooserTitle(CharSequence title) {
            this.mChooserTitle = title;
            return this;
        }

        public IntentBuilder setChooserTitle(int resId) {
            return setChooserTitle(this.mContext.getText(resId));
        }

        public IntentBuilder setType(String mimeType) {
            this.mIntent.setType(mimeType);
            return this;
        }

        public IntentBuilder setText(CharSequence text) {
            this.mIntent.putExtra("android.intent.extra.TEXT", text);
            return this;
        }

        public IntentBuilder setHtmlText(String htmlText) {
            this.mIntent.putExtra(IntentCompat.EXTRA_HTML_TEXT, htmlText);
            if (!this.mIntent.hasExtra("android.intent.extra.TEXT")) {
                setText(Html.fromHtml(htmlText));
            }
            return this;
        }

        public IntentBuilder setStream(Uri streamUri) {
            this.mStreams = null;
            if (streamUri != null) {
                addStream(streamUri);
            }
            return this;
        }

        public IntentBuilder addStream(Uri streamUri) {
            if (this.mStreams == null) {
                this.mStreams = new ArrayList<>();
            }
            this.mStreams.add(streamUri);
            return this;
        }

        public IntentBuilder setEmailTo(String[] addresses) {
            if (this.mToAddresses != null) {
                this.mToAddresses = null;
            }
            this.mIntent.putExtra("android.intent.extra.EMAIL", addresses);
            return this;
        }

        public IntentBuilder addEmailTo(String address) {
            if (this.mToAddresses == null) {
                this.mToAddresses = new ArrayList<>();
            }
            this.mToAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailTo(String[] addresses) {
            combineArrayExtra("android.intent.extra.EMAIL", addresses);
            return this;
        }

        public IntentBuilder setEmailCc(String[] addresses) {
            this.mIntent.putExtra("android.intent.extra.CC", addresses);
            return this;
        }

        public IntentBuilder addEmailCc(String address) {
            if (this.mCcAddresses == null) {
                this.mCcAddresses = new ArrayList<>();
            }
            this.mCcAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailCc(String[] addresses) {
            combineArrayExtra("android.intent.extra.CC", addresses);
            return this;
        }

        public IntentBuilder setEmailBcc(String[] addresses) {
            this.mIntent.putExtra("android.intent.extra.BCC", addresses);
            return this;
        }

        public IntentBuilder addEmailBcc(String address) {
            if (this.mBccAddresses == null) {
                this.mBccAddresses = new ArrayList<>();
            }
            this.mBccAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailBcc(String[] addresses) {
            combineArrayExtra("android.intent.extra.BCC", addresses);
            return this;
        }

        public IntentBuilder setSubject(String subject) {
            this.mIntent.putExtra("android.intent.extra.SUBJECT", subject);
            return this;
        }
    }

    public static class IntentReader {
        private static final String TAG = "IntentReader";
        private final ComponentName mCallingActivity;
        private final String mCallingPackage;
        private final Context mContext;
        private final Intent mIntent;
        private ArrayList<Uri> mStreams;

        @Deprecated
        public static IntentReader from(Activity activity) {
            return new IntentReader(activity);
        }

        public IntentReader(Activity activity) {
            this((Context) Preconditions.checkNotNull(activity), activity.getIntent());
        }

        public IntentReader(Context context, Intent intent) {
            this.mContext = (Context) Preconditions.checkNotNull(context);
            this.mIntent = (Intent) Preconditions.checkNotNull(intent);
            this.mCallingPackage = ShareCompat.getCallingPackage(intent);
            this.mCallingActivity = ShareCompat.getCallingActivity(intent);
        }

        public boolean isShareIntent() {
            String action = this.mIntent.getAction();
            return "android.intent.action.SEND".equals(action) || "android.intent.action.SEND_MULTIPLE".equals(action);
        }

        public boolean isSingleShare() {
            return "android.intent.action.SEND".equals(this.mIntent.getAction());
        }

        public boolean isMultipleShare() {
            return "android.intent.action.SEND_MULTIPLE".equals(this.mIntent.getAction());
        }

        public String getType() {
            return this.mIntent.getType();
        }

        public CharSequence getText() {
            return this.mIntent.getCharSequenceExtra("android.intent.extra.TEXT");
        }

        public String getHtmlText() {
            String stringExtra = this.mIntent.getStringExtra(IntentCompat.EXTRA_HTML_TEXT);
            if (stringExtra != null) {
                return stringExtra;
            }
            CharSequence text = getText();
            if (text instanceof Spanned) {
                return Html.toHtml((Spanned) text);
            }
            if (text == null) {
                return stringExtra;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                return Html.escapeHtml(text);
            }
            StringBuilder sb = new StringBuilder();
            withinStyle(sb, text, 0, text.length());
            return sb.toString();
        }

        private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
            while (start < end) {
                char cCharAt = text.charAt(start);
                if (cCharAt == '<') {
                    out.append("&lt;");
                } else if (cCharAt == '>') {
                    out.append("&gt;");
                } else if (cCharAt == '&') {
                    out.append("&amp;");
                } else if (cCharAt > '~' || cCharAt < ' ') {
                    out.append("&#").append((int) cCharAt).append(";");
                } else if (cCharAt == ' ') {
                    while (true) {
                        int i = start + 1;
                        if (i >= end || text.charAt(i) != ' ') {
                            break;
                        }
                        out.append("&nbsp;");
                        start = i;
                    }
                    out.append(' ');
                } else {
                    out.append(cCharAt);
                }
                start++;
            }
        }

        public Uri getStream() {
            return (Uri) this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
        }

        public Uri getStream(int index) {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            }
            ArrayList<Uri> arrayList = this.mStreams;
            if (arrayList != null) {
                return arrayList.get(index);
            }
            if (index == 0) {
                return (Uri) this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
            }
            throw new IndexOutOfBoundsException("Stream items available: " + getStreamCount() + " index requested: " + index);
        }

        public int getStreamCount() {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            }
            ArrayList<Uri> arrayList = this.mStreams;
            if (arrayList != null) {
                return arrayList.size();
            }
            return this.mIntent.hasExtra("android.intent.extra.STREAM") ? 1 : 0;
        }

        public String[] getEmailTo() {
            return this.mIntent.getStringArrayExtra("android.intent.extra.EMAIL");
        }

        public String[] getEmailCc() {
            return this.mIntent.getStringArrayExtra("android.intent.extra.CC");
        }

        public String[] getEmailBcc() {
            return this.mIntent.getStringArrayExtra("android.intent.extra.BCC");
        }

        public String getSubject() {
            return this.mIntent.getStringExtra("android.intent.extra.SUBJECT");
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public ComponentName getCallingActivity() {
            return this.mCallingActivity;
        }

        public Drawable getCallingActivityIcon() {
            if (this.mCallingActivity == null) {
                return null;
            }
            try {
                return this.mContext.getPackageManager().getActivityIcon(this.mCallingActivity);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling activity", e);
                return null;
            }
        }

        public Drawable getCallingApplicationIcon() {
            if (this.mCallingPackage == null) {
                return null;
            }
            try {
                return this.mContext.getPackageManager().getApplicationIcon(this.mCallingPackage);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling application", e);
                return null;
            }
        }

        public CharSequence getCallingApplicationLabel() {
            if (this.mCallingPackage == null) {
                return null;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                return packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mCallingPackage, 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve label for calling application", e);
                return null;
            }
        }
    }

    private static class Api16Impl {
        private Api16Impl() {
        }

        static void migrateExtraStreamToClipData(Intent intent, ArrayList<Uri> streams) {
            ClipData clipData = new ClipData(null, new String[]{intent.getType()}, new ClipData.Item(intent.getCharSequenceExtra("android.intent.extra.TEXT"), intent.getStringExtra(IntentCompat.EXTRA_HTML_TEXT), null, streams.get(0)));
            int size = streams.size();
            for (int i = 1; i < size; i++) {
                clipData.addItem(new ClipData.Item(streams.get(i)));
            }
            intent.setClipData(clipData);
            intent.addFlags(1);
        }

        static void removeClipData(Intent intent) {
            intent.setClipData(null);
            intent.setFlags(intent.getFlags() & (-2));
        }
    }
}

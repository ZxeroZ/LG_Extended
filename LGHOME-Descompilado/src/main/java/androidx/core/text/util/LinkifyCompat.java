package androidx.core.text.util;

import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.core.net.MailTo;
import androidx.core.util.PatternsCompat;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class LinkifyCompat {
    private static final String[] EMPTY_STRING = new String[0];
    private static final Comparator<LinkSpec> COMPARATOR = new Comparator<LinkSpec>() { // from class: androidx.core.text.util.LinkifyCompat.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(LinkSpec a, LinkSpec b) {
            if (a.start < b.start) {
                return -1;
            }
            if (a.start <= b.start && a.end >= b.end) {
                return a.end > b.end ? -1 : 0;
            }
            return 1;
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkifyMask {
    }

    public static boolean addLinks(Spannable text, int mask) {
        if (shouldAddLinksFallbackToFramework()) {
            return Linkify.addLinks(text, mask);
        }
        if (mask == 0) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
        for (int length = uRLSpanArr.length - 1; length >= 0; length--) {
            text.removeSpan(uRLSpanArr[length]);
        }
        if ((mask & 4) != 0) {
            Linkify.addLinks(text, 4);
        }
        ArrayList<LinkSpec> arrayList = new ArrayList();
        if ((mask & 1) != 0) {
            gatherLinks(arrayList, text, PatternsCompat.AUTOLINK_WEB_URL, new String[]{"http://", "https://", "rtsp://"}, Linkify.sUrlMatchFilter, null);
        }
        if ((mask & 2) != 0) {
            gatherLinks(arrayList, text, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[]{MailTo.MAILTO_SCHEME}, null, null);
        }
        if ((mask & 8) != 0) {
            gatherMapLinks(arrayList, text);
        }
        pruneOverlaps(arrayList, text);
        if (arrayList.size() == 0) {
            return false;
        }
        for (LinkSpec linkSpec : arrayList) {
            if (linkSpec.frameworkAddedSpan == null) {
                applyLink(linkSpec.url, linkSpec.start, linkSpec.end, text);
            }
        }
        return true;
    }

    public static boolean addLinks(TextView text, int mask) {
        if (shouldAddLinksFallbackToFramework()) {
            return Linkify.addLinks(text, mask);
        }
        if (mask == 0) {
            return false;
        }
        CharSequence text2 = text.getText();
        if (text2 instanceof Spannable) {
            if (!addLinks((Spannable) text2, mask)) {
                return false;
            }
            addLinkMovementMethod(text);
            return true;
        }
        SpannableString spannableStringValueOf = SpannableString.valueOf(text2);
        if (!addLinks(spannableStringValueOf, mask)) {
            return false;
        }
        addLinkMovementMethod(text);
        text.setText(spannableStringValueOf);
        return true;
    }

    public static void addLinks(TextView text, Pattern pattern, String scheme) {
        if (shouldAddLinksFallbackToFramework()) {
            Linkify.addLinks(text, pattern, scheme);
        } else {
            addLinks(text, pattern, scheme, (String[]) null, (Linkify.MatchFilter) null, (Linkify.TransformFilter) null);
        }
    }

    public static void addLinks(TextView text, Pattern pattern, String scheme, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        if (shouldAddLinksFallbackToFramework()) {
            Linkify.addLinks(text, pattern, scheme, matchFilter, transformFilter);
        } else {
            addLinks(text, pattern, scheme, (String[]) null, matchFilter, transformFilter);
        }
    }

    public static void addLinks(TextView text, Pattern pattern, String defaultScheme, String[] schemes, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        if (shouldAddLinksFallbackToFramework()) {
            Linkify.addLinks(text, pattern, defaultScheme, schemes, matchFilter, transformFilter);
            return;
        }
        SpannableString spannableStringValueOf = SpannableString.valueOf(text.getText());
        if (addLinks(spannableStringValueOf, pattern, defaultScheme, schemes, matchFilter, transformFilter)) {
            text.setText(spannableStringValueOf);
            addLinkMovementMethod(text);
        }
    }

    public static boolean addLinks(Spannable text, Pattern pattern, String scheme) {
        if (shouldAddLinksFallbackToFramework()) {
            return Linkify.addLinks(text, pattern, scheme);
        }
        return addLinks(text, pattern, scheme, (String[]) null, (Linkify.MatchFilter) null, (Linkify.TransformFilter) null);
    }

    public static boolean addLinks(Spannable spannable, Pattern pattern, String scheme, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        if (shouldAddLinksFallbackToFramework()) {
            return Linkify.addLinks(spannable, pattern, scheme, matchFilter, transformFilter);
        }
        return addLinks(spannable, pattern, scheme, (String[]) null, matchFilter, transformFilter);
    }

    public static boolean addLinks(Spannable spannable, Pattern pattern, String defaultScheme, String[] schemes, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        if (shouldAddLinksFallbackToFramework()) {
            return Linkify.addLinks(spannable, pattern, defaultScheme, schemes, matchFilter, transformFilter);
        }
        if (defaultScheme == null) {
            defaultScheme = "";
        }
        if (schemes == null || schemes.length < 1) {
            schemes = EMPTY_STRING;
        }
        String[] strArr = new String[schemes.length + 1];
        strArr[0] = defaultScheme.toLowerCase(Locale.ROOT);
        int i = 0;
        while (i < schemes.length) {
            String str = schemes[i];
            i++;
            strArr[i] = str == null ? "" : str.toLowerCase(Locale.ROOT);
        }
        Matcher matcher = pattern.matcher(spannable);
        boolean z = false;
        while (matcher.find()) {
            int iStart = matcher.start();
            int iEnd = matcher.end();
            if (matchFilter != null ? matchFilter.acceptMatch(spannable, iStart, iEnd) : true) {
                applyLink(makeUrl(matcher.group(0), strArr, matcher, transformFilter), iStart, iEnd, spannable);
                z = true;
            }
        }
        return z;
    }

    private static boolean shouldAddLinksFallbackToFramework() {
        return Build.VERSION.SDK_INT >= 28;
    }

    private static void addLinkMovementMethod(TextView t) {
        if ((t.getMovementMethod() instanceof LinkMovementMethod) || !t.getLinksClickable()) {
            return;
        }
        t.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static String makeUrl(String url, String[] prefixes, Matcher matcher, Linkify.TransformFilter filter) {
        boolean z;
        if (filter != null) {
            url = filter.transformUrl(matcher, url);
        }
        int i = 0;
        while (true) {
            z = true;
            if (i >= prefixes.length) {
                z = false;
                break;
            }
            if (url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
                if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }
            } else {
                i++;
            }
        }
        return (z || prefixes.length <= 0) ? url : prefixes[0] + url;
    }

    private static void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            int iStart = matcher.start();
            int iEnd = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, iStart, iEnd)) {
                LinkSpec linkSpec = new LinkSpec();
                linkSpec.url = makeUrl(matcher.group(0), schemes, matcher, transformFilter);
                linkSpec.start = iStart;
                linkSpec.end = iEnd;
                links.add(linkSpec);
            }
        }
    }

    private static void applyLink(String url, int start, int end, Spannable text) {
        text.setSpan(new URLSpan(url), start, end, 33);
    }

    private static void gatherMapLinks(ArrayList<LinkSpec> links, Spannable s) {
        int iIndexOf;
        String string = s.toString();
        int i = 0;
        while (true) {
            try {
                String strFindAddress = findAddress(string);
                if (strFindAddress != null && (iIndexOf = string.indexOf(strFindAddress)) >= 0) {
                    LinkSpec linkSpec = new LinkSpec();
                    int length = strFindAddress.length() + iIndexOf;
                    linkSpec.start = iIndexOf + i;
                    i += length;
                    linkSpec.end = i;
                    string = string.substring(length);
                    try {
                        linkSpec.url = "geo:0,0?q=" + URLEncoder.encode(strFindAddress, "UTF-8");
                        links.add(linkSpec);
                    } catch (UnsupportedEncodingException unused) {
                    }
                }
                return;
            } catch (UnsupportedOperationException unused2) {
                return;
            }
        }
    }

    private static String findAddress(String addr) {
        if (Build.VERSION.SDK_INT >= 28) {
            return WebView.findAddress(addr);
        }
        return FindAddress.findAddress(addr);
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> links, Spannable text) {
        int i;
        int i2 = 0;
        URLSpan[] uRLSpanArr = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
        for (int i3 = 0; i3 < uRLSpanArr.length; i3++) {
            LinkSpec linkSpec = new LinkSpec();
            linkSpec.frameworkAddedSpan = uRLSpanArr[i3];
            linkSpec.start = text.getSpanStart(uRLSpanArr[i3]);
            linkSpec.end = text.getSpanEnd(uRLSpanArr[i3]);
            links.add(linkSpec);
        }
        Collections.sort(links, COMPARATOR);
        int size = links.size();
        while (i2 < size - 1) {
            LinkSpec linkSpec2 = links.get(i2);
            int i4 = i2 + 1;
            LinkSpec linkSpec3 = links.get(i4);
            if (linkSpec2.start <= linkSpec3.start && linkSpec2.end > linkSpec3.start) {
                if (linkSpec3.end > linkSpec2.end && linkSpec2.end - linkSpec2.start <= linkSpec3.end - linkSpec3.start) {
                    i = linkSpec2.end - linkSpec2.start < linkSpec3.end - linkSpec3.start ? i2 : -1;
                } else {
                    i = i4;
                }
                if (i != -1) {
                    Object obj = links.get(i).frameworkAddedSpan;
                    if (obj != null) {
                        text.removeSpan(obj);
                    }
                    links.remove(i);
                    size--;
                }
            }
            i2 = i4;
        }
    }

    private LinkifyCompat() {
    }

    private static class LinkSpec {
        int end;
        URLSpan frameworkAddedSpan;
        int start;
        String url;

        LinkSpec() {
        }
    }
}

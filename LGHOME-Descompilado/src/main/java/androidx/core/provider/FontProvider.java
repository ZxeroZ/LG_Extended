package androidx.core.provider;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.FontsContractCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class FontProvider {
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() { // from class: androidx.core.provider.FontProvider.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(byte[] l, byte[] r) {
            int length;
            int length2;
            if (l.length != r.length) {
                length = l.length;
                length2 = r.length;
            } else {
                for (int i = 0; i < l.length; i++) {
                    if (l[i] != r[i]) {
                        length = l[i];
                        length2 = r[i];
                    }
                }
                return 0;
            }
            return length - length2;
        }
    };

    private FontProvider() {
    }

    static FontsContractCompat.FontFamilyResult getFontFamilyResult(Context context, FontRequest request, CancellationSignal cancellationSignal) throws PackageManager.NameNotFoundException {
        ProviderInfo provider = getProvider(context.getPackageManager(), request, context.getResources());
        if (provider == null) {
            return FontsContractCompat.FontFamilyResult.create(1, null);
        }
        return FontsContractCompat.FontFamilyResult.create(0, query(context, request, provider.authority, cancellationSignal));
    }

    static ProviderInfo getProvider(PackageManager packageManager, FontRequest request, Resources resources) throws PackageManager.NameNotFoundException {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo providerInfoResolveContentProvider = packageManager.resolveContentProvider(providerAuthority, 0);
        if (providerInfoResolveContentProvider == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
        }
        if (!providerInfoResolveContentProvider.packageName.equals(request.getProviderPackage())) {
            throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
        }
        List<byte[]> listConvertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(providerInfoResolveContentProvider.packageName, 64).signatures);
        Collections.sort(listConvertToByteArrayList, sByteArrayComparator);
        List<List<byte[]>> certificates = getCertificates(request, resources);
        for (int i = 0; i < certificates.size(); i++) {
            ArrayList arrayList = new ArrayList(certificates.get(i));
            Collections.sort(arrayList, sByteArrayComparator);
            if (equalsByteArrayList(listConvertToByteArrayList, arrayList)) {
                return providerInfoResolveContentProvider;
            }
        }
        return null;
    }

    static FontsContractCompat.FontInfo[] query(Context context, FontRequest request, String authority, CancellationSignal cancellationSignal) {
        int i;
        Uri uriWithAppendedId;
        boolean z;
        int i2;
        ArrayList arrayList = new ArrayList();
        Uri uriBuild = new Uri.Builder().scheme("content").authority(authority).build();
        Uri uriBuild2 = new Uri.Builder().scheme("content").authority(authority).appendPath("file").build();
        Cursor cursorQuery = null;
        try {
            String[] strArr = {"_id", FontsContractCompat.Columns.FILE_ID, FontsContractCompat.Columns.TTC_INDEX, FontsContractCompat.Columns.VARIATION_SETTINGS, FontsContractCompat.Columns.WEIGHT, FontsContractCompat.Columns.ITALIC, FontsContractCompat.Columns.RESULT_CODE};
            int i3 = 0;
            if (Build.VERSION.SDK_INT > 16) {
                cursorQuery = context.getContentResolver().query(uriBuild, strArr, "query = ?", new String[]{request.getQuery()}, null, cancellationSignal);
            } else {
                cursorQuery = context.getContentResolver().query(uriBuild, strArr, "query = ?", new String[]{request.getQuery()}, null);
            }
            if (cursorQuery != null && cursorQuery.getCount() > 0) {
                int columnIndex = cursorQuery.getColumnIndex(FontsContractCompat.Columns.RESULT_CODE);
                ArrayList arrayList2 = new ArrayList();
                int columnIndex2 = cursorQuery.getColumnIndex("_id");
                int columnIndex3 = cursorQuery.getColumnIndex(FontsContractCompat.Columns.FILE_ID);
                int columnIndex4 = cursorQuery.getColumnIndex(FontsContractCompat.Columns.TTC_INDEX);
                int columnIndex5 = cursorQuery.getColumnIndex(FontsContractCompat.Columns.WEIGHT);
                int columnIndex6 = cursorQuery.getColumnIndex(FontsContractCompat.Columns.ITALIC);
                while (cursorQuery.moveToNext()) {
                    int i4 = columnIndex != -1 ? cursorQuery.getInt(columnIndex) : i3;
                    int i5 = columnIndex4 != -1 ? cursorQuery.getInt(columnIndex4) : i3;
                    if (columnIndex3 == -1) {
                        i = i4;
                        uriWithAppendedId = ContentUris.withAppendedId(uriBuild, cursorQuery.getLong(columnIndex2));
                    } else {
                        i = i4;
                        uriWithAppendedId = ContentUris.withAppendedId(uriBuild2, cursorQuery.getLong(columnIndex3));
                    }
                    int i6 = columnIndex5 != -1 ? cursorQuery.getInt(columnIndex5) : 400;
                    if (columnIndex6 == -1 || cursorQuery.getInt(columnIndex6) != 1) {
                        z = false;
                        i2 = i;
                    } else {
                        i2 = i;
                        z = true;
                    }
                    arrayList2.add(FontsContractCompat.FontInfo.create(uriWithAppendedId, i5, i6, z, i2));
                    i3 = 0;
                }
                arrayList = arrayList2;
            }
            return (FontsContractCompat.FontInfo[]) arrayList.toArray(new FontsContractCompat.FontInfo[0]);
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, request.getCertificatesArrayResId());
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        ArrayList arrayList = new ArrayList();
        for (Signature signature : signatures) {
            arrayList.add(signature.toByteArray());
        }
        return arrayList;
    }
}

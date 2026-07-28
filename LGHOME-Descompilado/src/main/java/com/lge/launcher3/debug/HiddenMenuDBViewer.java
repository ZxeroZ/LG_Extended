package com.lge.launcher3.debug;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;
import com.android.launcher3.LauncherProvider;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class HiddenMenuDBViewer extends Fragment implements AdapterView.OnItemClickListener {
    public static final int DISPALY_TABLE_COUNT = 20;
    public static final String TAG = "HiddenMenuDBViewer";
    MyDatabaseHelper mDbm;
    HorizontalScrollView mHsv;
    LinearLayout mMainLayout;
    ScrollView mMainscrollview;
    Button mNext;
    Button mPrevious;
    Spinner mSelectTable;
    TableLayout mTableLayout;
    TableRow.LayoutParams mTableRowParams;
    TextView mTextView;
    Cursor mCursor = null;
    IndexInfo mInfo = new IndexInfo();
    AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: com.lge.launcher3.debug.HiddenMenuDBViewer.2
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> arg0) {
        }

        /* JADX DEBUG: TODO: convert one arg to string using `String.valueOf()`, args: r5v22 */
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            LinearLayout linearLayout = (LinearLayout) HiddenMenuDBViewer.this.mMainscrollview.findViewById(R.id.secondrow);
            LinearLayout linearLayout2 = (LinearLayout) HiddenMenuDBViewer.this.mMainscrollview.findViewById(R.id.thirdrow);
            if (pos == 0 && !HiddenMenuDBViewer.this.mInfo.isCustomQuery) {
                linearLayout.setVisibility(8);
                HiddenMenuDBViewer.this.mHsv.setVisibility(8);
                linearLayout2.setVisibility(8);
            }
            if (pos != 0) {
                linearLayout.setVisibility(0);
                HiddenMenuDBViewer.this.mHsv.setVisibility(0);
                linearLayout2.setVisibility(0);
                int i = pos - 1;
                HiddenMenuDBViewer.this.mCursor.moveToPosition(i);
                HiddenMenuDBViewer.this.mInfo.cursorpostion = i;
                HiddenMenuDBViewer.this.mInfo.table_name = HiddenMenuDBViewer.this.mCursor.getString(0);
                HiddenMenuDBViewer.this.mTableLayout.removeAllViews();
                Cursor cursor = HiddenMenuDBViewer.this.mDbm.getData("select * from " + HiddenMenuDBViewer.this.mCursor.getString(0)).get(0);
                HiddenMenuDBViewer.this.mInfo.maincursor = cursor;
                if (cursor != null) {
                    int count = cursor.getCount();
                    HiddenMenuDBViewer.this.mInfo.isEmpty = false;
                    TextView textView = HiddenMenuDBViewer.this.mTextView;
                    StringBuilder sb = new StringBuilder();
                    sb.append(count);
                    textView.setText(sb.toString());
                    TableRow tableRow = new TableRow(HiddenMenuDBViewer.this.getContext());
                    tableRow.setBackgroundColor(Utilities.sBlack);
                    tableRow.setPadding(0, 2, 0, 2);
                    for (int i2 = 0; i2 < cursor.getColumnCount(); i2++) {
                        LinearLayout linearLayout3 = new LinearLayout(HiddenMenuDBViewer.this.getContext());
                        linearLayout3.setBackgroundColor(Utilities.sWhite);
                        linearLayout3.setLayoutParams(HiddenMenuDBViewer.this.mTableRowParams);
                        TextView textView2 = new TextView(HiddenMenuDBViewer.this.getContext());
                        textView2.setPadding(0, 0, 4, 3);
                        textView2.setText(cursor.getColumnName(i2));
                        textView2.setTextColor(Utilities.sBlack);
                        linearLayout3.addView(textView2);
                        tableRow.addView(linearLayout3);
                    }
                    HiddenMenuDBViewer.this.mTableLayout.addView(tableRow);
                    cursor.moveToFirst();
                    HiddenMenuDBViewer.this.paginatetable(cursor.getCount());
                    return;
                }
                HiddenMenuDBViewer.this.mTableLayout.removeAllViews();
                HiddenMenuDBViewer.this.getcolumnnames();
                TableRow tableRow2 = new TableRow(HiddenMenuDBViewer.this.getContext());
                tableRow2.setBackgroundColor(Utilities.sBlack);
                LinearLayout linearLayout4 = new LinearLayout(HiddenMenuDBViewer.this.getContext());
                linearLayout4.setBackgroundColor(Utilities.sWhite);
                linearLayout4.setLayoutParams(HiddenMenuDBViewer.this.mTableRowParams);
                TextView textView3 = new TextView(HiddenMenuDBViewer.this.getContext());
                textView3.setText("   Table   Is   Empty   ");
                textView3.setTextSize(30.0f);
                textView3.setTextColor(SupportMenu.CATEGORY_MASK);
                linearLayout4.addView(textView3);
                tableRow2.addView(linearLayout4);
                HiddenMenuDBViewer.this.mTableLayout.addView(tableRow2);
                HiddenMenuDBViewer.this.mTextView.setText("0");
            }
        }
    };

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

    static class IndexInfo {
        public ArrayList<String> emptytablecolumnnames;
        public boolean isCustomQuery;
        public boolean isEmpty;
        public Cursor maincursor;
        public ArrayList<String> tableheadernames;
        public ArrayList<String> value_string;
        public int index = 10;
        public int numberofpages = 0;
        public int currentpage = 0;
        public String table_name = "";
        public int cursorpostion = 0;

        IndexInfo() {
        }
    }

    class MyDatabaseHelper extends LauncherProvider.DatabaseHelper {
        MyDatabaseHelper(Context context, String launcherDBFileName) {
            super(context, launcherDBFileName);
        }

        public ArrayList<Cursor> getData(String Query) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ArrayList<Cursor> arrayList = new ArrayList<>(2);
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"mesage"});
            arrayList.add(null);
            arrayList.add(null);
            try {
                Cursor cursorRawQuery = writableDatabase.rawQuery(Query, null);
                matrixCursor.addRow(new Object[]{"Success"});
                arrayList.set(1, matrixCursor);
                if (cursorRawQuery != null && cursorRawQuery.getCount() > 0) {
                    arrayList.set(0, cursorRawQuery);
                    cursorRawQuery.moveToFirst();
                    cursorRawQuery.close();
                    return arrayList;
                }
                cursorRawQuery.close();
                return arrayList;
            } catch (SQLException e) {
                Log.d("printing exception", e.getMessage());
                matrixCursor.addRow(new Object[]{e.getMessage()});
                arrayList.set(1, matrixCursor);
                return arrayList;
            } catch (Exception e2) {
                Log.d("printing exception", e2.getMessage());
                matrixCursor.addRow(new Object[]{e2.getMessage()});
                arrayList.set(1, matrixCursor);
                return arrayList;
            }
        }
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String launcherDBFileName = LauncherProvider.getLauncherDBFileName(getContext());
        LGLog.i(TAG, "launcherDBFileName = " + launcherDBFileName);
        this.mDbm = new MyDatabaseHelper(getContext(), launcherDBFileName);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.dbviewer, container, false);
        ScrollView scrollView = (ScrollView) viewInflate.findViewById(R.id.mainscrollview);
        this.mMainscrollview = scrollView;
        LinearLayout linearLayout = (LinearLayout) scrollView.findViewById(R.id.mainview);
        this.mMainLayout = linearLayout;
        linearLayout.setScrollContainer(true);
        this.mSelectTable = (Spinner) this.mMainscrollview.findViewById(R.id.select_table);
        this.mTextView = (TextView) this.mMainscrollview.findViewById(R.id.counttextview);
        this.mHsv = (HorizontalScrollView) this.mMainscrollview.findViewById(R.id.hsv);
        TableLayout tableLayout = (TableLayout) this.mMainscrollview.findViewById(R.id.tableLayout);
        this.mTableLayout = tableLayout;
        tableLayout.setHorizontalScrollBarEnabled(true);
        this.mHsv.setScrollbarFadingEnabled(false);
        this.mHsv.setScrollBarStyle(50331648);
        this.mPrevious = (Button) this.mMainscrollview.findViewById(R.id.previous);
        this.mNext = (Button) this.mMainscrollview.findViewById(R.id.next);
        this.mTableRowParams = new TableRow.LayoutParams(-2, -2);
        ArrayList<Cursor> data = this.mDbm.getData("SELECT name _id FROM sqlite_master WHERE type ='table'");
        this.mCursor = data.get(0);
        Cursor cursor = data.get(1);
        cursor.moveToLast();
        Log.d("Message from sql = ", cursor.getString(0));
        ArrayList arrayList = new ArrayList();
        Cursor cursor2 = this.mCursor;
        if (cursor2 != null) {
            cursor2.moveToFirst();
            arrayList.add("click here");
            do {
                arrayList.add(this.mCursor.getString(0));
            } while (this.mCursor.moveToNext());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayList) { // from class: com.lge.launcher3.debug.HiddenMenuDBViewer.1
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return super.getDropDownView(position, convertView, parent);
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSelectTable.setAdapter((SpinnerAdapter) arrayAdapter);
        this.mSelectTable.setOnItemSelectedListener(this.mItemSelectedListener);
        return viewInflate;
    }

    public void getcolumnnames() {
        Cursor cursor = this.mDbm.getData("PRAGMA table_info(" + this.mInfo.table_name + ")").get(0);
        this.mInfo.isEmpty = true;
        if (cursor != null) {
            this.mInfo.isEmpty = true;
            ArrayList<String> arrayList = new ArrayList<>();
            cursor.moveToFirst();
            do {
                arrayList.add(cursor.getString(1));
            } while (cursor.moveToNext());
            this.mInfo.emptytablecolumnnames = arrayList;
        }
    }

    /* JADX DEBUG: TODO: convert one arg to string using `String.valueOf()`, args: r4v6 */
    public void refreshTable(int d) {
        Cursor cursor;
        this.mTableLayout.removeAllViews();
        if (d == 0) {
            cursor = this.mDbm.getData("select * from " + this.mInfo.table_name).get(0);
            this.mInfo.maincursor = cursor;
        } else {
            cursor = null;
        }
        if (d == 1) {
            cursor = this.mInfo.maincursor;
        }
        if (cursor != null) {
            int count = cursor.getCount();
            TextView textView = this.mTextView;
            StringBuilder sb = new StringBuilder();
            sb.append(count);
            textView.setText(sb.toString());
            TableRow tableRow = new TableRow(getContext());
            tableRow.setBackgroundColor(Utilities.sBlack);
            tableRow.setPadding(0, 2, 0, 2);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setBackgroundColor(Utilities.sWhite);
                linearLayout.setLayoutParams(this.mTableRowParams);
                TextView textView2 = new TextView(getContext());
                textView2.setPadding(0, 0, 4, 3);
                textView2.setText(cursor.getColumnName(i));
                textView2.setTextColor(Utilities.sBlack);
                linearLayout.addView(textView2);
                tableRow.addView(linearLayout);
            }
            this.mTableLayout.addView(tableRow);
            cursor.moveToFirst();
            paginatetable(cursor.getCount());
            return;
        }
        TableRow tableRow2 = new TableRow(getContext());
        tableRow2.setBackgroundColor(Utilities.sBlack);
        tableRow2.setPadding(0, 2, 0, 2);
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        linearLayout2.setBackgroundColor(Utilities.sWhite);
        linearLayout2.setLayoutParams(this.mTableRowParams);
        TextView textView3 = new TextView(getContext());
        textView3.setPadding(0, 0, 4, 3);
        textView3.setText("   Table   Is   Empty   ");
        textView3.setTextSize(30.0f);
        textView3.setTextColor(SupportMenu.CATEGORY_MASK);
        linearLayout2.addView(textView3);
        tableRow2.addView(linearLayout2);
        this.mTableLayout.addView(tableRow2);
        this.mTextView.setText("0");
    }

    public void paginatetable(final int number) {
        String string;
        Cursor cursor = this.mInfo.maincursor;
        this.mInfo.numberofpages = (cursor.getCount() / 20) + 1;
        this.mInfo.currentpage = 1;
        cursor.moveToFirst();
        int i = 0;
        do {
            TableRow tableRow = new TableRow(getContext());
            tableRow.setBackgroundColor(Utilities.sBlack);
            tableRow.setPadding(0, 2, 0, 2);
            for (int i2 = 0; i2 < cursor.getColumnCount(); i2++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setBackgroundColor(Utilities.sWhite);
                linearLayout.setLayoutParams(this.mTableRowParams);
                TextView textView = new TextView(getContext());
                try {
                    string = cursor.getString(i2);
                } catch (Exception unused) {
                    string = "";
                }
                textView.setText(string);
                textView.setTextColor(Utilities.sBlack);
                textView.setPadding(0, 0, 4, 3);
                linearLayout.addView(textView);
                tableRow.addView(linearLayout);
            }
            tableRow.setVisibility(0);
            i++;
            this.mTableLayout.addView(tableRow);
            if (!cursor.moveToNext()) {
                break;
            }
        } while (i < 20);
        this.mInfo.index = i;
        setListener(cursor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tableChange(final Cursor c3) {
        boolean z = true;
        for (int i = 1; i < this.mTableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) this.mTableLayout.getChildAt(i);
            if (z) {
                tableRow.setVisibility(0);
                for (int i2 = 0; i2 < tableRow.getChildCount(); i2++) {
                    TextView textView = (TextView) ((LinearLayout) tableRow.getChildAt(i2)).getChildAt(0);
                    try {
                        textView.setText(c3.getString(i2));
                    } catch (Exception unused) {
                        textView.setText("unKnown");
                    }
                }
                z = !c3.isLast();
                if (!c3.isLast()) {
                    c3.moveToNext();
                }
            } else {
                tableRow.setVisibility(8);
            }
        }
    }

    private void setListener(final Cursor c3) {
        this.mPrevious.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.debug.HiddenMenuDBViewer.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int i = (HiddenMenuDBViewer.this.mInfo.currentpage - 2) * 20;
                if (HiddenMenuDBViewer.this.mInfo.currentpage == 1) {
                    Toast.makeText(HiddenMenuDBViewer.this.getContext(), "This is the first page", 1).show();
                    return;
                }
                HiddenMenuDBViewer.this.mInfo.currentpage--;
                c3.moveToPosition(i);
                HiddenMenuDBViewer.this.tableChange(c3);
                HiddenMenuDBViewer.this.mInfo.index = i;
            }
        });
        this.mNext.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.debug.HiddenMenuDBViewer.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (HiddenMenuDBViewer.this.mInfo.currentpage >= HiddenMenuDBViewer.this.mInfo.numberofpages) {
                    Toast.makeText(HiddenMenuDBViewer.this.getContext(), "This is the last page", 1).show();
                    return;
                }
                HiddenMenuDBViewer.this.mInfo.currentpage++;
                HiddenMenuDBViewer.this.tableChange(c3);
            }
        });
    }
}

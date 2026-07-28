package com.lge.launcher3.debug;

import android.R;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.debug.LGHiddenMenuUtil;
import com.lge.launcher3.util.LGHomeFeature;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class LGHiddenMenuFeatureList extends ListFragment implements SearchView.OnQueryTextListener {
    static boolean sIschangeRestart;
    private ListView lv;
    private final LGHiddenMenuUtil.functionDataManager fManger = new LGHiddenMenuUtil.functionDataManager();
    private final ArrayList<Integer> mSearchResultList = new ArrayList<>();

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.fManger.getArray().clear();
        loadFeatureList();
        setHasOptionsMenu(true);
        setListAdapter(new ArrayAdapter(getActivity(), R.layout.simple_list_item_checked, this.fManger.getNameList()));
        ListView listView = getListView();
        this.lv = listView;
        listView.setChoiceMode(2);
        for (int i = 0; i < this.fManger.size(); i++) {
            this.lv.setItemChecked(i, this.fManger.getData(i).mValume);
        }
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.debug.LGHiddenMenuFeatureList.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LGHiddenMenuFeatureList.this.mSearchResultList.size() == 0) {
                    CheckedTextView checkedTextView = (CheckedTextView) view;
                    Toast.makeText(LGHiddenMenuFeatureList.this.getActivity(), LGHiddenMenuFeatureList.this.fManger.getArray().get(position).featureFields + " " + checkedTextView.isChecked(), 0).show();
                    LGHiddenMenuFeatureList.this.fManger.getArray().get(position).mValume = checkedTextView.isChecked();
                } else {
                    CheckedTextView checkedTextView2 = (CheckedTextView) view;
                    Toast.makeText(LGHiddenMenuFeatureList.this.getActivity(), LGHiddenMenuFeatureList.this.fManger.getArray().get(((Integer) LGHiddenMenuFeatureList.this.mSearchResultList.get(position)).intValue()).featureFields + " " + checkedTextView2.isChecked(), 0).show();
                    LGHiddenMenuFeatureList.this.fManger.getArray().get(((Integer) LGHiddenMenuFeatureList.this.mSearchResultList.get(position)).intValue()).mValume = checkedTextView2.isChecked();
                }
                LGHiddenMenuFeatureList.sIschangeRestart = true;
            }
        });
    }

    private void loadFeatureList() {
        String name = LGFeatureConfig.class.getName();
        String name2 = LGHomeFeature.Config.class.getName();
        int i = 0;
        for (Class<?> cls : LGHiddenMenuUtil.sFeatureClassList) {
            String name3 = cls.getName();
            if (name3.equals(name)) {
                setBooleanData(i, cls.getFields());
            } else if (name3.equals(name2)) {
                setConfigClassData(i, cls.getFields());
            } else {
                setBooleanData(i, cls.getDeclaredFields());
            }
            i++;
        }
    }

    private void setBooleanData(int classNumber, Field[] featureFields) {
        for (int i = 0; i < featureFields.length; i++) {
            if ("boolean".equals(featureFields[i].getType().getName())) {
                LGHiddenMenuUtil.FunctionData functionData = new LGHiddenMenuUtil.FunctionData();
                functionData.featureFields = featureFields[i].getName();
                functionData.classNumber = classNumber;
                functionData.mValume = LGHiddenMenuUtil.getItemValue(classNumber, featureFields[i], getActivity());
                this.fManger.addtData(functionData);
            }
        }
    }

    private void setConfigClassData(int classNumber, Field[] featureFields) {
        for (int i = 0; i < featureFields.length; i++) {
            if (LGHiddenMenuUtil.sFeatureClassList[classNumber].getName().equals(featureFields[i].getType().getName())) {
                LGHiddenMenuUtil.FunctionData functionData = new LGHiddenMenuUtil.FunctionData();
                functionData.featureFields = featureFields[i].getName();
                functionData.classNumber = classNumber;
                featureFields[i].setAccessible(true);
                functionData.mValume = LGHomeFeature.Config.valueOf(functionData.featureFields).getValue();
                this.fManger.addtData(functionData);
            }
        }
    }

    @Override // android.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItemAdd = menu.add("Search");
        menuItemAdd.setIcon(R.drawable.ic_menu_search);
        menuItemAdd.setShowAsAction(9);
        SearchView searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(this);
        menuItemAdd.setActionView(searchView);
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String newText) {
        int i = 0;
        if (TextUtils.isEmpty(newText)) {
            this.mSearchResultList.clear();
            this.lv.setAdapter((ListAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_checked, this.fManger.getNameList()));
            while (i < this.fManger.size()) {
                this.lv.setItemChecked(i, this.fManger.getData(i).mValume);
                i++;
            }
            return true;
        }
        String upperCase = newText.toUpperCase(Locale.getDefault());
        this.mSearchResultList.clear();
        for (int i2 = 0; i2 < this.fManger.size(); i2++) {
            if (this.fManger.getArray().get(i2).featureFields.indexOf(upperCase) != -1) {
                this.mSearchResultList.add(Integer.valueOf(i2));
            }
        }
        this.lv.setAdapter((ListAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_checked, this.fManger.getNameList(this.mSearchResultList)));
        while (i < this.mSearchResultList.size()) {
            this.lv.setItemChecked(i, this.fManger.getArray().get(this.mSearchResultList.get(i).intValue()).mValume);
            i++;
        }
        return true;
    }

    @Override // android.app.Fragment
    public void onStop() throws Throwable {
        FileOutputStream fileOutputStream;
        Throwable th;
        super.onStop();
        if (sIschangeRestart) {
            FileOutputStream fileOutputStream2 = null;
            try {
                try {
                    File file = new File(getActivity().getFilesDir(), LGHiddenMenuUtil.HIDDENMENU_FILENAME);
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                } catch (Exception unused) {
                } catch (Throwable th2) {
                    fileOutputStream = null;
                    th = th2;
                }
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(this.fManger);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (Exception unused2) {
                    fileOutputStream2 = fileOutputStream;
                    if (fileOutputStream2 == null) {
                    } else {
                        fileOutputStream2.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}

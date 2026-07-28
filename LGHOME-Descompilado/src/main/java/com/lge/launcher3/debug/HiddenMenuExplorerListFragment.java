package com.lge.launcher3.debug;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.launcher3.LauncherFiles;
import com.lge.launcher3.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* JADX INFO: loaded from: classes.dex */
public class HiddenMenuExplorerListFragment extends ListFragment {
    private TextView mFileContent;
    private TextView myPath;
    private Scanner scanner;
    private List<String> item = null;
    private List<String> path = null;
    private String root = "./data/data/com.lge.launcher3/";

    @Override // android.app.ListFragment, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.filemain, container, false);
        this.myPath = (TextView) viewInflate.findViewById(R.id.path);
        this.mFileContent = (TextView) viewInflate.findViewById(R.id.file_content);
        String str = getActivity().getApplicationInfo().dataDir;
        this.root = str;
        getDir(str);
        return viewInflate;
    }

    private void getDir(String dirPath) {
        this.myPath.setText("Location: " + dirPath);
        this.item = new ArrayList();
        this.path = new ArrayList();
        File file = new File(dirPath);
        File[] fileArrListFiles = file.listFiles();
        if (!dirPath.equals(this.root)) {
            this.item.add(this.root);
            this.path.add(this.root);
            this.item.add("../");
            this.path.add(file.getParent());
        }
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                this.path.add(file2.getPath());
                if (file2.isDirectory()) {
                    this.item.add(file2.getName() + "/");
                } else {
                    this.item.add(file2.getName());
                }
            }
        }
        setListAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, this.item));
    }

    @Override // android.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(this.path.get(position));
        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(this.path.get(position));
                return;
            }
            new AlertDialog.Builder(getActivity()).setIcon(R.drawable.grid_button).setTitle("[" + file.getName() + "] folder can't be read!\n").setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.debug.HiddenMenuExplorerListFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            return;
        }
        if (file.getName().contains(LauncherFiles.LAUNCHER_DB)) {
            getActivity().getActionBar().getTabAt(3).select();
            return;
        }
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            this.scanner = new Scanner(fileInputStream);
            while (this.scanner.hasNext()) {
                sb.append(this.scanner.nextLine()).append("\n");
            }
            fileInputStream.close();
            this.scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        getListView().setVisibility(8);
        this.mFileContent.setVisibility(0);
        this.mFileContent.setText(sb);
    }
}

package com.lge.launcher3.debug;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class LGHiddenMenuResCheck extends Fragment {
    private static final int GET_CODE = 0;
    private static boolean sCheckStartFlag;
    private static StringBuilder sResultString = new StringBuilder();
    private TextView mResults;

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sCheckStartFlag) {
            if (getActivity().getRequestedOrientation() == 0) {
                sResultString.append("LANDSCAPE check \n");
            }
            if (getActivity().getRequestedOrientation() == 1) {
                sResultString.append("PORTRAIT check \n");
            }
            sResultString.append(LGResouceCheckTool.checkAllRes(getActivity().getApplicationContext()));
            if (getActivity().getRequestedOrientation() == 0) {
                getActivity().setRequestedOrientation(1);
            }
        }
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.lg_hiddenmenu_savehome, container, false);
        this.mResults = (TextView) viewInflate.findViewById(R.id.results);
        Button button = (Button) viewInflate.findViewById(R.id.savemenu);
        button.setText("Check Res");
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.debug.LGHiddenMenuResCheck.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LGHiddenMenuResCheck.sCheckStartFlag = true;
                LGHiddenMenuResCheck.sResultString.delete(0, LGHiddenMenuResCheck.sResultString.length());
                LGHiddenMenuResCheck.this.getActivity().setRequestedOrientation(0);
            }
        });
        ((Button) viewInflate.findViewById(R.id.saveworkspace)).setVisibility(8);
        if (sCheckStartFlag && getActivity().getRequestedOrientation() == 1) {
            viewInflate.postDelayed(new Runnable() { // from class: com.lge.launcher3.debug.LGHiddenMenuResCheck.2
                @Override // java.lang.Runnable
                public void run() {
                    LGHiddenMenuResCheck.this.mResults.setText(LGHiddenMenuResCheck.sResultString.toString());
                    LGHiddenMenuResCheck.sCheckStartFlag = false;
                }
            }, 200L);
        }
        return viewInflate;
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("result", sResultString.toString());
    }

    @Override // android.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Editable editable = (Editable) this.mResults.getText();
            if (resultCode == 0) {
                editable.append((CharSequence) "(cancelled)");
            } else {
                editable.append((CharSequence) "(okay ");
                editable.append((CharSequence) Integer.toString(resultCode));
                editable.append((CharSequence) ") ");
                if (data != null) {
                    editable.append((CharSequence) data.getAction());
                }
            }
            editable.append((CharSequence) "\n");
        }
    }
}

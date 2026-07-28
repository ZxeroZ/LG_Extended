package com.android.quickstep;

import java.util.LinkedList;
import java.util.List;

/* JADX INFO: compiled from: TouchInteractionService.java */
/* JADX INFO: loaded from: classes.dex */
class ArgList extends LinkedList<String> {
    public ArgList(List<String> l) {
        super(l);
    }

    public String peekArg() {
        return peekFirst();
    }

    public String nextArg() {
        return pollFirst().toLowerCase();
    }
}

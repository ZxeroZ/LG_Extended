package com.google.android.libraries.a;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import android.view.WindowManager;

/* JADX INFO: compiled from: ILauncherOverlay.java */
/* JADX INFO: loaded from: classes.dex */
public interface c extends IInterface {
    void a(float f) throws RemoteException;

    void a(int i) throws RemoteException;

    void a(Bundle bundle) throws RemoteException;

    void a(Bundle bundle, e eVar) throws RemoteException;

    void a(WindowManager.LayoutParams layoutParams, e eVar, int i) throws RemoteException;

    void a(boolean z) throws RemoteException;

    void b() throws RemoteException;

    void b(int i) throws RemoteException;

    void b(Bundle bundle) throws RemoteException;

    void b(boolean z) throws RemoteException;

    void c() throws RemoteException;

    void c(int i) throws RemoteException;

    void c(Bundle bundle) throws RemoteException;

    void d() throws RemoteException;

    void e() throws RemoteException;

    boolean f() throws RemoteException;
}

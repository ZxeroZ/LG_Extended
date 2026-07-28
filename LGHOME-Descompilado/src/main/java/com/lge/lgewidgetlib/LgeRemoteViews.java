package com.lge.lgewidgetlib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.UserHandle;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class LgeRemoteViews extends RemoteViews {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LgeRemoteView {
    }

    public LgeRemoteViews(Parcel parcel) {
        super(parcel);
    }

    public static LgeRemoteViews getLgeRemoteViewsFromRemoteViews(RemoteViews remoteView) {
        Parcel parcelObtain = Parcel.obtain();
        remoteView.writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        return new LgeRemoteViews(parcelObtain);
    }

    @Override // android.widget.RemoteViews
    public View apply(Context context, ViewGroup parent) {
        return applyForCustomView(context, parent, null);
    }

    @Override // android.widget.RemoteViews
    public void reapply(Context context, View v) {
        reapplyForCustomView(context, v, null);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler) {
        return applyForCustomView(context, parent, handler);
    }

    public void reapply(Context context, View v, RemoteViews.InteractionHandler handler) {
        reapplyForCustomView(context, v, handler);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler, SizeF size) {
        return applyForCustomView(context, parent, handler);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler, SizeF size, RemoteViews.ColorResources colorResources) {
        return applyForCustomView(context, parent, handler);
    }

    public void reapply(Context context, View v, RemoteViews.InteractionHandler handler, SizeF size, RemoteViews.ColorResources colorResources) {
        reapplyForCustomView(context, v, handler);
    }

    private Context prepareContextForCustomView(Context context) {
        try {
            return new LgeWidgetContext(context.createApplicationContext((ApplicationInfo) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mApplication"), 3));
        } catch (PackageManager.NameNotFoundException unused) {
            return context;
        } catch (RuntimeException unused2) {
            String str = (String) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mPackage");
            if (str != null) {
                try {
                    return new LgeWidgetContext(context.createPackageContextAsUser(str, 3, (UserHandle) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mUser")));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    return context;
                }
            }
            return context;
        }
    }

    private RemoteViews getLgeRemoteViewsToApply(Context context) {
        RemoteViews remoteViews = (RemoteViews) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mLandscape");
        RemoteViews remoteViews2 = (RemoteViews) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mPortrait");
        return (remoteViews == null || remoteViews2 == null) ? this : context.getResources().getConfiguration().orientation == 2 ? remoteViews : remoteViews2;
    }

    private void performApplyForLgeRemoteViews(RemoteViews rvToApply, View result, ViewGroup parent, RemoteViews.InteractionHandler handler) {
        try {
            Method declaredMethod = getClass().getSuperclass().getDeclaredMethod("performApply", View.class, ViewGroup.class, RemoteViews.InteractionHandler.class, RemoteViews.ColorResources.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(rvToApply, result, parent, handler, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public View applyForCustomView(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler) {
        RemoteViews lgeRemoteViewsToApply = getLgeRemoteViewsToApply(context);
        Context contextPrepareContextForCustomView = prepareContextForCustomView(context);
        LayoutInflater layoutInflaterCloneInContext = ((LayoutInflater) contextPrepareContextForCustomView.getSystemService("layout_inflater")).cloneInContext(contextPrepareContextForCustomView);
        layoutInflaterCloneInContext.setFilter(this);
        layoutInflaterCloneInContext.setFactory(new CustomLayoutInflaterFactory());
        View viewInflate = layoutInflaterCloneInContext.inflate(lgeRemoteViewsToApply.getLayoutId(), parent, false);
        performApplyForLgeRemoteViews(lgeRemoteViewsToApply, viewInflate, parent, handler);
        return viewInflate;
    }

    public void reapplyForCustomView(Context context, View v, RemoteViews.InteractionHandler handler) {
        RemoteViews lgeRemoteViewsToApply = getLgeRemoteViewsToApply(context);
        RemoteViews remoteViews = (RemoteViews) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mLandscape");
        RemoteViews remoteViews2 = (RemoteViews) LgeReflectionUtil.getPrivateField(RemoteViews.class, this, "mPortrait");
        if (remoteViews != null && remoteViews2 != null && v.getId() != lgeRemoteViewsToApply.getLayoutId()) {
            throw new RuntimeException("Attempting to re-apply RemoteViews to a view that that does not share the same root layout id.");
        }
        prepareContextForCustomView(context);
        performApplyForLgeRemoteViews(lgeRemoteViewsToApply, v, (ViewGroup) v.getParent(), handler);
    }

    public static boolean checkAnnotationForCustomView(Class clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        String name = LgeRemoteView.class.getName();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.RemoteViews, android.view.LayoutInflater.Filter
    public boolean onLoadClass(Class clazz) {
        if (super.onLoadClass(clazz)) {
            return true;
        }
        return checkAnnotationForCustomView(clazz);
    }
}

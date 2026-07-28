package com.lge.lgewidgetlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
class CustomLayoutInflaterFactory implements LayoutInflater.Factory2 {
    private static final String TAG = "CustomLayoutInflaterFactory";
    final Object[] mConstructorArgs = new Object[2];
    private static LayoutInflater.Filter mFilter = new LayoutInflater.Filter() { // from class: com.lge.lgewidgetlib.CustomLayoutInflaterFactory.1
        @Override // android.view.LayoutInflater.Filter
        public boolean onLoadClass(Class clazz) {
            return LgeRemoteViews.checkAnnotationForCustomView(clazz);
        }
    };
    static Class<?>[] mConstructorSignature = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();

    @Override // android.view.LayoutInflater.Factory
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        try {
            return createCustomView(name, null, context, attrs);
        } catch (InflateException | ClassNotFoundException unused) {
            return null;
        }
    }

    @Override // android.view.LayoutInflater.Factory2
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        try {
            return createCustomView(name, null, context, attrs);
        } catch (InflateException | ClassNotFoundException unused) {
            return null;
        }
    }

    public static void clearConstructorMap() {
        sConstructorMap.clear();
    }

    private View createCustomView(String name, String prefix, Context context, AttributeSet attrs) throws InflateException, ClassNotFoundException {
        String str;
        HashMap<String, Constructor<? extends View>> map = sConstructorMap;
        Constructor<? extends View> constructor = map.get(name);
        try {
            if (constructor == null) {
                ClassLoader classLoader = context.getClassLoader();
                if (prefix != null) {
                    str = prefix + name;
                } else {
                    str = name;
                }
                Class clsAsSubclass = classLoader.loadClass(str).asSubclass(View.class);
                LayoutInflater.Filter filter = mFilter;
                if (filter != null && clsAsSubclass != null && !filter.onLoadClass(clsAsSubclass)) {
                    failNotAllowed(name, prefix, attrs);
                }
                constructor = clsAsSubclass.getConstructor(mConstructorSignature);
                map.put(name, constructor);
                WLog.d(TAG, "new Constructor added to map");
            } else {
                WLog.d(TAG, "Constructor founded from map");
            }
            Object[] objArr = this.mConstructorArgs;
            objArr[0] = context;
            objArr[1] = attrs;
            return constructor.newInstance(objArr);
        } catch (ClassCastException | ClassNotFoundException | NoSuchMethodException | Exception unused) {
            return null;
        }
    }

    private void failNotAllowed(String name, String prefix, AttributeSet attrs) {
        String positionDescription = attrs.getPositionDescription();
        if (prefix != null) {
            name = prefix + name;
        }
        throw new InflateException(positionDescription + ": Class not allowed to be inflated " + name);
    }
}

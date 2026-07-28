package androidx.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import androidx.collection.ArrayMap;
import androidx.core.content.res.TypedArrayUtils;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class TransitionInflater {
    private final Context mContext;
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final ArrayMap<String, Constructor> CONSTRUCTORS = new ArrayMap<>();

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    public Transition inflateTransition(int i) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                return createTransitionFromXml(xml, Xml.asAttributeSet(xml), null);
            } catch (IOException e) {
                throw new InflateException(xml.getPositionDescription() + ": " + e.getMessage(), e);
            } catch (XmlPullParserException e2) {
                throw new InflateException(e2.getMessage(), e2);
            }
        } finally {
            xml.close();
        }
    }

    public TransitionManager inflateTransitionManager(int i, ViewGroup viewGroup) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                return createTransitionManagerFromXml(xml, Xml.asAttributeSet(xml), viewGroup);
            } catch (IOException e) {
                InflateException inflateException = new InflateException(xml.getPositionDescription() + ": " + e.getMessage());
                inflateException.initCause(e);
                throw inflateException;
            } catch (XmlPullParserException e2) {
                InflateException inflateException2 = new InflateException(e2.getMessage());
                inflateException2.initCause(e2);
                throw inflateException2;
            }
        } finally {
            xml.close();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:82:0x017f, code lost:
    
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private androidx.transition.Transition createTransitionFromXml(org.xmlpull.v1.XmlPullParser r8, android.util.AttributeSet r9, androidx.transition.Transition r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r7 = this;
            int r0 = r8.getDepth()
            boolean r1 = r10 instanceof androidx.transition.TransitionSet
            r2 = 0
            if (r1 == 0) goto Ld
            r1 = r10
            androidx.transition.TransitionSet r1 = (androidx.transition.TransitionSet) r1
            goto Le
        Ld:
            r1 = r2
        Le:
            r3 = r2
        Lf:
            int r4 = r8.next()
            r5 = 3
            if (r4 != r5) goto L1c
            int r5 = r8.getDepth()
            if (r5 <= r0) goto L17f
        L1c:
            r5 = 1
            if (r4 == r5) goto L17f
            r5 = 2
            if (r4 == r5) goto L23
            goto Lf
        L23:
            java.lang.String r4 = r8.getName()
            java.lang.String r5 = "fade"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L38
            androidx.transition.Fade r3 = new androidx.transition.Fade
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L38:
            java.lang.String r5 = "changeBounds"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L49
            androidx.transition.ChangeBounds r3 = new androidx.transition.ChangeBounds
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L49:
            java.lang.String r5 = "slide"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L5a
            androidx.transition.Slide r3 = new androidx.transition.Slide
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L5a:
            java.lang.String r5 = "explode"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L6b
            androidx.transition.Explode r3 = new androidx.transition.Explode
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L6b:
            java.lang.String r5 = "changeImageTransform"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L7c
            androidx.transition.ChangeImageTransform r3 = new androidx.transition.ChangeImageTransform
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L7c:
            java.lang.String r5 = "changeTransform"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L8d
            androidx.transition.ChangeTransform r3 = new androidx.transition.ChangeTransform
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L8d:
            java.lang.String r5 = "changeClipBounds"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L9e
            androidx.transition.ChangeClipBounds r3 = new androidx.transition.ChangeClipBounds
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        L9e:
            java.lang.String r5 = "autoTransition"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto Laf
            androidx.transition.AutoTransition r3 = new androidx.transition.AutoTransition
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        Laf:
            java.lang.String r5 = "changeScroll"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto Lc0
            androidx.transition.ChangeScroll r3 = new androidx.transition.ChangeScroll
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        Lc0:
            java.lang.String r5 = "transitionSet"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto Ld0
            androidx.transition.TransitionSet r3 = new androidx.transition.TransitionSet
            android.content.Context r4 = r7.mContext
            r3.<init>(r4, r9)
            goto L13c
        Ld0:
            java.lang.String r5 = "transition"
            boolean r6 = r5.equals(r4)
            if (r6 == 0) goto Le1
            java.lang.Class<androidx.transition.Transition> r3 = androidx.transition.Transition.class
            java.lang.Object r3 = r7.createCustom(r9, r3, r5)
            androidx.transition.Transition r3 = (androidx.transition.Transition) r3
            goto L13c
        Le1:
            java.lang.String r5 = "targets"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto Led
            r7.getTargetIds(r8, r9, r10)
            goto L13c
        Led:
            java.lang.String r5 = "arcMotion"
            boolean r5 = r5.equals(r4)
            if (r5 == 0) goto L10a
            if (r10 == 0) goto L102
            androidx.transition.ArcMotion r4 = new androidx.transition.ArcMotion
            android.content.Context r5 = r7.mContext
            r4.<init>(r5, r9)
            r10.setPathMotion(r4)
            goto L13c
        L102:
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.String r9 = "Invalid use of arcMotion element"
            r8.<init>(r9)
            throw r8
        L10a:
            java.lang.String r5 = "pathMotion"
            boolean r6 = r5.equals(r4)
            if (r6 == 0) goto L128
            if (r10 == 0) goto L120
            java.lang.Class<androidx.transition.PathMotion> r4 = androidx.transition.PathMotion.class
            java.lang.Object r4 = r7.createCustom(r9, r4, r5)
            androidx.transition.PathMotion r4 = (androidx.transition.PathMotion) r4
            r10.setPathMotion(r4)
            goto L13c
        L120:
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.String r9 = "Invalid use of pathMotion element"
            r8.<init>(r9)
            throw r8
        L128:
            java.lang.String r5 = "patternPathMotion"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L162
            if (r10 == 0) goto L15a
            androidx.transition.PatternPathMotion r4 = new androidx.transition.PatternPathMotion
            android.content.Context r5 = r7.mContext
            r4.<init>(r5, r9)
            r10.setPathMotion(r4)
        L13c:
            if (r3 == 0) goto Lf
            boolean r4 = r8.isEmptyElementTag()
            if (r4 != 0) goto L147
            r7.createTransitionFromXml(r8, r9, r3)
        L147:
            if (r1 == 0) goto L14e
            r1.addTransition(r3)
            goto Le
        L14e:
            if (r10 != 0) goto L152
            goto Lf
        L152:
            android.view.InflateException r8 = new android.view.InflateException
            java.lang.String r9 = "Could not add transition to another transition."
            r8.<init>(r9)
            throw r8
        L15a:
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.String r9 = "Invalid use of patternPathMotion element"
            r8.<init>(r9)
            throw r8
        L162:
            java.lang.RuntimeException r9 = new java.lang.RuntimeException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "Unknown scene name: "
            java.lang.StringBuilder r10 = r10.append(r0)
            java.lang.String r8 = r8.getName()
            java.lang.StringBuilder r8 = r10.append(r8)
            java.lang.String r8 = r8.toString()
            r9.<init>(r8)
            throw r9
        L17f:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.transition.TransitionInflater.createTransitionFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, androidx.transition.Transition):androidx.transition.Transition");
    }

    private Object createCustom(AttributeSet attributeSet, Class cls, String str) {
        Object objNewInstance;
        Class<? extends U> clsAsSubclass;
        String attributeValue = attributeSet.getAttributeValue(null, "class");
        if (attributeValue == null) {
            throw new InflateException(str + " tag must have a 'class' attribute");
        }
        try {
            ArrayMap<String, Constructor> arrayMap = CONSTRUCTORS;
            synchronized (arrayMap) {
                Constructor constructor = arrayMap.get(attributeValue);
                if (constructor == null && (clsAsSubclass = this.mContext.getClassLoader().loadClass(attributeValue).asSubclass(cls)) != 0) {
                    constructor = clsAsSubclass.getConstructor(CONSTRUCTOR_SIGNATURE);
                    constructor.setAccessible(true);
                    arrayMap.put(attributeValue, constructor);
                }
                objNewInstance = constructor.newInstance(this.mContext, attributeSet);
            }
            return objNewInstance;
        } catch (Exception e) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e);
        }
    }

    private void getTargetIds(XmlPullParser xmlPullParser, AttributeSet attributeSet, Transition transition) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2) {
                if (xmlPullParser.getName().equals("target")) {
                    TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_TARGET);
                    int namedResourceId = TypedArrayUtils.getNamedResourceId(typedArrayObtainStyledAttributes, xmlPullParser, "targetId", 1, 0);
                    if (namedResourceId != 0) {
                        transition.addTarget(namedResourceId);
                    } else {
                        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(typedArrayObtainStyledAttributes, xmlPullParser, "excludeId", 2, 0);
                        if (namedResourceId2 != 0) {
                            transition.excludeTarget(namedResourceId2, true);
                        } else {
                            String namedString = TypedArrayUtils.getNamedString(typedArrayObtainStyledAttributes, xmlPullParser, "targetName", 4);
                            if (namedString != null) {
                                transition.addTarget(namedString);
                            } else {
                                String namedString2 = TypedArrayUtils.getNamedString(typedArrayObtainStyledAttributes, xmlPullParser, "excludeName", 5);
                                if (namedString2 != null) {
                                    transition.excludeTarget(namedString2, true);
                                } else {
                                    String namedString3 = TypedArrayUtils.getNamedString(typedArrayObtainStyledAttributes, xmlPullParser, "excludeClass", 3);
                                    if (namedString3 != null) {
                                        try {
                                            transition.excludeTarget((Class) Class.forName(namedString3), true);
                                        } catch (ClassNotFoundException e) {
                                            typedArrayObtainStyledAttributes.recycle();
                                            throw new RuntimeException("Could not create " + namedString3, e);
                                        }
                                    } else {
                                        String namedString4 = TypedArrayUtils.getNamedString(typedArrayObtainStyledAttributes, xmlPullParser, "targetClass", 0);
                                        if (namedString4 != null) {
                                            transition.addTarget(Class.forName(namedString4));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    typedArrayObtainStyledAttributes.recycle();
                } else {
                    throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0056, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private androidx.transition.TransitionManager createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser r5, android.util.AttributeSet r6, android.view.ViewGroup r7) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r4 = this;
            int r0 = r5.getDepth()
            r1 = 0
        L5:
            int r2 = r5.next()
            r3 = 3
            if (r2 != r3) goto L12
            int r3 = r5.getDepth()
            if (r3 <= r0) goto L56
        L12:
            r3 = 1
            if (r2 == r3) goto L56
            r3 = 2
            if (r2 == r3) goto L19
            goto L5
        L19:
            java.lang.String r2 = r5.getName()
            java.lang.String r3 = "transitionManager"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L2b
            androidx.transition.TransitionManager r1 = new androidx.transition.TransitionManager
            r1.<init>()
            goto L5
        L2b:
            java.lang.String r3 = "transition"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L39
            if (r1 == 0) goto L39
            r4.loadTransition(r6, r5, r7, r1)
            goto L5
        L39:
            java.lang.RuntimeException r6 = new java.lang.RuntimeException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "Unknown scene name: "
            java.lang.StringBuilder r7 = r7.append(r0)
            java.lang.String r5 = r5.getName()
            java.lang.StringBuilder r5 = r7.append(r5)
            java.lang.String r5 = r5.toString()
            r6.<init>(r5)
            throw r6
        L56:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.transition.TransitionInflater.createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.ViewGroup):androidx.transition.TransitionManager");
    }

    private void loadTransition(AttributeSet attributeSet, XmlPullParser xmlPullParser, ViewGroup viewGroup, TransitionManager transitionManager) throws Resources.NotFoundException {
        Transition transitionInflateTransition;
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_MANAGER);
        int namedResourceId = TypedArrayUtils.getNamedResourceId(typedArrayObtainStyledAttributes, xmlPullParser, "transition", 2, -1);
        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(typedArrayObtainStyledAttributes, xmlPullParser, "fromScene", 0, -1);
        Scene sceneForLayout = namedResourceId2 < 0 ? null : Scene.getSceneForLayout(viewGroup, namedResourceId2, this.mContext);
        int namedResourceId3 = TypedArrayUtils.getNamedResourceId(typedArrayObtainStyledAttributes, xmlPullParser, "toScene", 1, -1);
        Scene sceneForLayout2 = namedResourceId3 >= 0 ? Scene.getSceneForLayout(viewGroup, namedResourceId3, this.mContext) : null;
        if (namedResourceId >= 0 && (transitionInflateTransition = inflateTransition(namedResourceId)) != null) {
            if (sceneForLayout2 == null) {
                throw new RuntimeException("No toScene for transition ID " + namedResourceId);
            }
            if (sceneForLayout == null) {
                transitionManager.setTransition(sceneForLayout2, transitionInflateTransition);
            } else {
                transitionManager.setTransition(sceneForLayout, sceneForLayout2, transitionInflateTransition);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }
}

package androidx.core.view;

/* JADX INFO: loaded from: classes.dex */
public interface WindowInsetsAnimationControlListenerCompat {
    void onCancelled(WindowInsetsAnimationControllerCompat controller);

    void onFinished(WindowInsetsAnimationControllerCompat controller);

    void onReady(WindowInsetsAnimationControllerCompat controller, int types);
}

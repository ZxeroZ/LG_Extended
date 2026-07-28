package com.lge.launcher3.wing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelContentsView extends RelativeLayout {
    Context context;
    int currentPosition;
    ImageView imageView;
    VideoView videoView;

    public void initVideoContent() {
    }

    public SwivelContentsView(Context context) {
        super(context);
        this.currentPosition = 0;
        this.context = context;
        initView();
    }

    public SwivelContentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentPosition = 0;
        this.context = context;
        initView();
    }

    public SwivelContentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.currentPosition = 0;
        this.context = context;
        initView();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initView() {
        addView(((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.swivel_contents_view, (ViewGroup) this, false));
        this.videoView = (VideoView) findViewById(R.id.videoView);
    }

    public void release() {
        this.videoView.stopPlayback();
        setVisibility(8);
        this.videoView.setOnPreparedListener(null);
        this.videoView.setVisibility(8);
    }

    public VideoView getVideoView() {
        return this.videoView;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }
}

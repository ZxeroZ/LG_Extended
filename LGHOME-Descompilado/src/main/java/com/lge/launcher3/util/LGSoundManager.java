package com.lge.launcher3.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class LGSoundManager {
    private static final int MAX_SIMULTANIOUS_STREAM_NUM = 5;
    private static final int STREAM_TYPE = 1;
    private static final String SYSTEM_AUDIO_PATH = "/system/media/audio/ui/";
    private static final String TAG = "Util.LGSoundManager";
    private static LGSoundManager sInstance;
    private AudioManager mAudioManager;
    private Context mContext;
    private HashMap<SoundType, Integer> mHashMap;
    private SoundPool mSoundPool;

    public enum SoundType {
        SOUND_INDEX_UNINSTALL,
        SOUND_INDEX_REMOVE
    }

    private LGSoundManager(Context context) {
        this.mContext = null;
        this.mAudioManager = null;
        this.mSoundPool = null;
        this.mHashMap = null;
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mSoundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(1).build()).build();
        this.mHashMap = new HashMap<>();
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { // from class: com.lge.launcher3.util.LGSoundManager.1
            @Override // android.media.SoundPool.OnLoadCompleteListener
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                LGSoundManager.this.play(sampleId);
            }
        });
    }

    public static final LGSoundManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LGSoundManager(context);
        }
        return sInstance;
    }

    public int add(SoundType type, int resId) {
        if (this.mHashMap.containsKey(type)) {
            Integer num = this.mHashMap.get(type);
            if (num != null) {
                return num.intValue();
            }
        } else {
            int iLoad = this.mSoundPool.load(this.mContext, resId, 1);
            if (iLoad != 0) {
                this.mHashMap.put(type, Integer.valueOf(iLoad));
                LGLog.d(TAG, String.format("Load a res sound %s(%d) from resources", type, Integer.valueOf(iLoad)));
                return iLoad;
            }
        }
        return 0;
    }

    public int add(SoundType type, String path) {
        if (this.mHashMap.containsKey(type)) {
            Integer num = this.mHashMap.get(type);
            if (num != null) {
                return num.intValue();
            }
        } else {
            int iLoad = this.mSoundPool.load(path, 1);
            if (iLoad != 0) {
                this.mHashMap.put(type, Integer.valueOf(iLoad));
                LGLog.d(TAG, String.format("Load a sound %s(%d) frome system media path", type, Integer.valueOf(iLoad)));
                return iLoad;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play(int soundId) {
        int streamVolume = this.mAudioManager.getStreamVolume(1);
        int streamMaxVolume = this.mAudioManager.getStreamMaxVolume(1);
        float f = streamVolume / streamMaxVolume;
        this.mSoundPool.play(soundId, f, f, 1, 0, 1.0f);
        LGLog.d(TAG, String.format("Play a sound %d with %d(%d) volume", Integer.valueOf(soundId), Integer.valueOf(streamVolume), Integer.valueOf(streamMaxVolume)));
    }

    public void play(final SoundType type) {
        if (!this.mHashMap.containsKey(type)) {
            loadSoundResource(type);
            return;
        }
        Integer num = this.mHashMap.get(type);
        if (num == null) {
            return;
        }
        play(num.intValue());
    }

    /* JADX INFO: renamed from: com.lge.launcher3.util.LGSoundManager$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$util$LGSoundManager$SoundType;

        static {
            int[] iArr = new int[SoundType.values().length];
            $SwitchMap$com$lge$launcher3$util$LGSoundManager$SoundType = iArr;
            try {
                iArr[SoundType.SOUND_INDEX_UNINSTALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$util$LGSoundManager$SoundType[SoundType.SOUND_INDEX_REMOVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public boolean loadSoundResource(SoundType type) {
        int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$util$LGSoundManager$SoundType[type.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return false;
            }
            if (add(type, "/system/media/audio/ui/Homescreen_Remove.ogg") == 0) {
                add(type, R.raw.lg_sound_remove);
            }
        } else if (add(type, "/system/media/audio/ui/Homescreen_Uninstall.ogg") == 0) {
            add(type, R.raw.lg_sound_uninstall);
        }
        return true;
    }

    public void stop(SoundType type) {
        Integer num = this.mHashMap.get(type);
        if (num == null) {
            return;
        }
        this.mSoundPool.stop(num.intValue());
    }

    public void pause(SoundType type) {
        Integer num = this.mHashMap.get(type);
        if (num == null) {
            return;
        }
        this.mSoundPool.pause(num.intValue());
    }
}

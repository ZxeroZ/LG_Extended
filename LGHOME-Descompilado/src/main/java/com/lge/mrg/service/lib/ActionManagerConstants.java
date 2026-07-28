package com.lge.mrg.service.lib;

import android.os.Bundle;

/* JADX INFO: loaded from: classes2.dex */
public class ActionManagerConstants {
    public static final long ACTION_ADDRESSBOOK_ADD = 513;
    public static final long ACTION_ADDRESSBOOK_MODIFY = 514;
    public static final long ACTION_ADDRESSBOOK_REMOVE = 515;
    public static final long ACTION_ALARM_ADD = 16385;
    public static final long ACTION_ALARM_DELETE = 16387;
    public static final long ACTION_ALARM_MODIFY = 16386;
    public static final long ACTION_ALARM_OFF = 16389;
    public static final long ACTION_ALARM_RING = 16388;
    public static final long ACTION_BOARD_DISABLED = 16777218;
    public static final long ACTION_BOARD_ENABLED = 16777217;
    public static final long ACTION_BOARD_PAUSE = 16777220;
    public static final long ACTION_BOARD_RESUME = 16777219;
    public static final long ACTION_CALLLOG_BLOCK_PHONE_NUMBER = 2052;
    public static final long ACTION_CALLLOG_DELETE = 2049;
    public static final long ACTION_CALLLOG_DELETE_ALL = 2051;
    public static final long ACTION_CALLLOG_DELETE_FOR_PHONE_NUMBER = 2050;
    public static final long ACTION_CALLLOG_REGISTER_SPAM = 2053;
    public static final long ACTION_CALLLOG_UNBLOCK_PHONE_NUMBER = 2054;
    public static final long ACTION_CALLLOG_UNREGISTER_SPAM = 2055;
    public static final long ACTION_CATEGORY_ADDRESSBOOK = 512;
    public static final long ACTION_CATEGORY_ALARM = 16384;
    public static final long ACTION_CATEGORY_BOARD = 16777216;
    public static final long ACTION_CATEGORY_CALLLOG = 2048;
    public static final long ACTION_CATEGORY_CONCIERGE = 262144;
    public static final long ACTION_CATEGORY_FEATURE = 33554432;
    public static final long ACTION_CATEGORY_MASK = -256;
    public static final long ACTION_CATEGORY_MEMO = 65536;
    public static final long ACTION_CATEGORY_MMS = 8192;
    public static final long ACTION_CATEGORY_MUSIC_PLAYER = 4194304;
    public static final long ACTION_CATEGORY_SCHEDULE = 256;
    public static final long ACTION_CATEGORY_SMS = 4096;
    public static final long ACTION_CATEGORY_STARTUP_WIZARD = 67108864;
    public static final long ACTION_CATEGORY_TELEPHONE = 1024;
    public static final long ACTION_CATEGORY_TODO = 32768;
    public static final long ACTION_CATEGORY_VIDEO_PLAYER = 8388608;
    public static final long ACTION_CATEGORY_WEB_BROWSER = 131072;
    public static final long ACTION_CONCIERGE_ACCEPT = 262145;
    public static final long ACTION_CONCIERGE_CARD_UPDATE = 262147;
    public static final long ACTION_CONCIERGE_DECLINE = 262148;
    public static final long ACTION_CONCIERGE_DELETE = 262146;
    public static final long ACTION_FEATURE_SETTING_CHANGED = 33554434;
    public static final long ACTION_FEATURE_USAGE = 33554433;
    public static final long ACTION_MEMO_ADD = 65537;
    public static final long ACTION_MEMO_CHANGE_NAME = 65540;
    public static final long ACTION_MEMO_DELETE = 65539;
    public static final long ACTION_MEMO_MODIFY = 65538;
    public static final long ACTION_MMS_DELETE_MSG = 8195;
    public static final long ACTION_MMS_INCOMING_MSG = 8194;
    public static final long ACTION_MMS_OUTGOING_MSG = 8193;
    public static final long ACTION_MUSIC_PLAYER_PLAY = 4194305;
    public static final long ACTION_SCHEDULE_ADD = 257;
    public static final long ACTION_SCHEDULE_MODIFY = 258;
    public static final long ACTION_SCHEDULE_REMOVE = 259;
    public static final long ACTION_SCHEDULE_SETTING = 260;
    public static final long ACTION_SMS_DELETE_MSG = 4099;
    public static final long ACTION_SMS_INCOMING_MSG = 4098;
    public static final long ACTION_SMS_OUTGOING_MSG = 4097;
    public static final long ACTION_STARTUP_WIZARD_COMPLETE = 67108865;
    public static final long ACTION_TELEPHONE_INCOMING_CALL = 1026;
    public static final long ACTION_TELEPHONE_OUTGOING_CALL = 1025;
    public static final long ACTION_TELEPHONE_REJECT_CALL = 1027;
    public static final long ACTION_TELEPHONE_REJECT_MSG_SENDING = 1029;
    public static final long ACTION_TELEPHONE_SUSPEND_CALL = 1028;
    public static final long ACTION_TODO_ADD = 32769;
    public static final long ACTION_TODO_CHECK_DONE = 32772;
    public static final long ACTION_TODO_DELETE = 32771;
    public static final long ACTION_TODO_MODIFY = 32770;
    public static final long ACTION_TODO_MODIFY_LIST = 32773;
    public static final long ACTION_VIDEO_PLAYER_PLAY = 8388609;
    public static final long ACTION_WEB_BROWSER_BOOKMARK_ADD = 131074;
    public static final long ACTION_WEB_BROWSER_BOOKMARK_DELETE = 131076;
    public static final long ACTION_WEB_BROWSER_BOOKMARK_MODIFY = 131075;
    public static final long ACTION_WEB_BROWSER_BROWSING_PAGE = 131077;
    public static final long ACTION_WEB_BROWSER_SEARCH = 131073;
    public static final String CATEGORY_CONCIERGE = "com.lge.intent.category.CONCIERGE";
    public static final String CATEGORY_DYK_ALL = "com.lge.intent.category.doyouknow.*";
    public static final String CATEGORY_DYK_GUIDE = "com.lge.intent.category.doyouknow.GUIDE";
    public static final String CATEGORY_DYK_PROMOTION = "com.lge.intent.category.doyouknow.PROMOTION";
    public static final String CATEGORY_DYK_SMARTWORLD = "com.lge.intent.category.doyouknow.SMARTWORLD";
    public static final String CATEGORY_GBOARD_ALL = "com.lge.intent.category.gboard.*";
    public static final String CATEGORY_GBOARD_DOYOKNOW = "com.lge.intent.category.gboard.DOYOUKNOW";
    public static final String CATEGORY_GBOARD_LIFESQUARE = "com.lge.intent.category.gboard.LIFESQUARE";
    public static final String CATEGORY_GBOARD_MYWELLNESS = "com.lge.intent.category.gboard.MYWELLNESS";
    public static final String KEY_ACTION_ID = "action";
    public static final String KEY_ROW_ID = "id";

    public static long getActionCategory(long j) {
        return j & (-256);
    }

    public static Bundle makeBasicUserLog(long j) {
        return makeBasicUserLog(j, -1L);
    }

    public static Bundle makeBasicUserLog(long j, long j2) {
        if (j <= 0) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ACTION_ID, j);
        if (j2 != -1) {
            bundle.putLong("id", j2);
        }
        return bundle;
    }
}

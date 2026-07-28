package com.android.launcher3;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import com.android.launcher3.config.ProviderConfig;

/* JADX INFO: loaded from: classes.dex */
public class LauncherSettings {

    public static final class AppWidgets {
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/appwidgets");
        public static final String EXTRA_APPWIDGET_IDS = "appwidget_ids";
        public static final String METHOD_DELETE_APPWIDGET_IDS = "delete_appwidget_ids";
    }

    public interface BaseLauncherColumns extends ChangeLogColumns {
        public static final String ICON = "icon";
        public static final String ICON_ID = "iconId";
        public static final String ICON_PACKAGE = "iconPackage";
        public static final String ICON_RESOURCE = "iconResource";
        public static final String ICON_TYPE = "iconType";
        public static final int ICON_TYPE_BITMAP = 1;
        public static final int ICON_TYPE_RESOURCE = 0;
        public static final String INTENT = "intent";
        public static final String ITEM_TYPE = "itemType";
        public static final int ITEM_TYPE_APPLICATION = 0;
        public static final int ITEM_TYPE_SHORTCUT = 1;
        public static final String SWIVEL_POSITION = "swivelPosition";
        public static final String TITLE = "title";
        public static final String USER_CUSTOMIZED_ICON = "userCustomizedIcon";
    }

    public interface ChangeLogColumns extends BaseColumns {
        public static final String MODIFIED = "modified";
    }

    public static final class HideApps implements ChangeLogColumns {
        public static final String COMPONENT = "componentName";
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/hideapps");
        public static final String TABLE_NAME = "hideapps";
        public static final String USER = "profileId";
    }

    public static final class HomePreferences implements ChangeLogColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/homePreferences");
        public static final String KEY = "key";
        public static final String TABLE_NAME = "homePreferences";
        public static final String VALUE = "value";
    }

    public static final class SharingContents implements ChangeLogColumns {
        public static final String ADAPTIVE_TEXT_COLOR = "adaptiveTextColor";
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/sharingContents");
        public static final String TABLE_NAME = "sharingContents";
        public static final String WALLPAPER_COMMON_COLOR = "wallpaperCommonColor";
    }

    public static final class WorkspaceDetail implements ChangeLogColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/workspaceDetail");
        public static final String DEFAULT_SCREEN = "defaultScreen";
        public static final String TABLE_NAME = "workspaceDetail";
    }

    public static final class WorkspaceScreens implements ChangeLogColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/workspaceScreens");
        public static final String SCREEN_RANK = "screenRank";
        public static final String TABLE_NAME = "workspaceScreens";
    }

    public static final class Favorites implements BaseLauncherColumns {
        public static final String APPWIDGET_ID = "appWidgetId";
        public static final String APPWIDGET_PROVIDER = "appWidgetProvider";
        public static final String CELLX = "cellX";
        public static final String CELLY = "cellY";
        public static final String CONTAINER = "container";
        public static final int CONTAINER_ALLAPPS = -110;
        public static final int CONTAINER_ALL_APPS = -104;
        public static final int CONTAINER_DESKTOP = -100;
        public static final int CONTAINER_HOTSEAT = -101;
        public static final int CONTAINER_HOTSEAT_PREDICTION = -103;
        public static final int CONTAINER_PREDICTION = -102;
        public static final int CONTAINER_SEARCH_RESULTS = -106;
        public static final int CONTAINER_SETTINGS = -108;
        public static final int CONTAINER_SHORTCUTS = -107;
        public static final int CONTAINER_TASKSWITCHER = -109;
        public static final int CONTAINER_WIDGETS_TRAY = -105;

        @Deprecated
        static final String DISPLAY_MODE = "displayMode";
        public static final int ITEM_TYPE_APPWIDGET = 4;
        public static final int ITEM_TYPE_CUSTOM_APPWIDGET = 5;
        public static final int ITEM_TYPE_DEEP_SHORTCUT = 6;
        public static final int ITEM_TYPE_FOLDER = 2;
        public static final int ITEM_TYPE_FULLSCREEN_ITEM = 8;
        public static final int ITEM_TYPE_TASK = 7;
        public static final String OPTIONS = "options";
        public static final String PROFILE_ID = "profileId";
        public static final String RANK = "rank";
        public static final String RESTORED = "restored";
        public static final String SCREEN = "screen";
        public static final String SPANX = "spanX";
        public static final String SPANY = "spanY";
        public static final String SWIVEL_POSITION = "swivelPosition";
        public static final String TABLE_NAME = "favorites";

        @Deprecated
        static final String URI = "uri";
        private static final String URI_PARAM_IS_EXTERNAL_ADD = "isExternalAdd";
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/favorites");
        public static final Uri CONTENT_URI_SWIVEL = Uri.parse("content://" + ProviderConfig.AUTHORITY_SWIVEL + "/favorites");
        public static final Uri CONTENT_URI_EXTERNAL_ADD = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/favorites?isExternalAdd=true");

        public static Uri getContentUri(long id) {
            return Uri.parse("content://" + ProviderConfig.AUTHORITY + "/favorites/" + id);
        }

        public static Uri getContentUriSwivel(long id) {
            return Uri.parse("content://" + ProviderConfig.AUTHORITY_SWIVEL + "/favorites/" + id);
        }

        static final String containerToString(int container) {
            return container != -101 ? container != -100 ? String.valueOf(container) : "desktop" : "hotseat";
        }

        public static void addTableToDb(SQLiteDatabase db, long myProfileId, boolean optional) {
            db.execSQL("CREATE TABLE " + (optional ? " IF NOT EXISTS " : "") + "favorites (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,screen INTEGER,cellX INTEGER,cellY INTEGER,spanX INTEGER,spanY INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,isShortcut INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB,uri TEXT,displayMode INTEGER,appWidgetProvider TEXT,modified INTEGER NOT NULL DEFAULT 0,restored INTEGER NOT NULL DEFAULT 0,profileId INTEGER DEFAULT " + myProfileId + ",rank INTEGER NOT NULL DEFAULT 0,options INTEGER NOT NULL DEFAULT 0,iconId TEXT,userCustomizedIcon BLOB);");
        }

        public static void addTableToDbSwivel(SQLiteDatabase db, long myProfileId, boolean optional) {
            db.execSQL("CREATE TABLE " + (optional ? " IF NOT EXISTS " : "") + "favorites (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,swivelPosition INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,isShortcut INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB,uri TEXT,displayMode INTEGER,appWidgetProvider TEXT,modified INTEGER NOT NULL DEFAULT 0,restored INTEGER NOT NULL DEFAULT 0,profileId INTEGER DEFAULT " + myProfileId + ",rank INTEGER NOT NULL DEFAULT 0,options INTEGER NOT NULL DEFAULT 0,iconId TEXT,userCustomizedIcon BLOB);");
        }
    }

    public static final class Settings {
        public static final Uri CONTENT_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/settings");
        public static final Uri CONTENT_URI_SWIVEL = Uri.parse("content://" + ProviderConfig.AUTHORITY_SWIVEL + "/settings");
        public static final String EXTRA_DEFAULT_VALUE = "default_value";
        public static final String EXTRA_EXTRACTED_COLORS = "extra_extractedColors";
        public static final String EXTRA_VALUE = "value";
        public static final String EXTRA_WALLPAPER_ID = "extra_wallpaperId";
        public static final String GRID_COMLUMN_ROW = "GridColumnRow";
        public static final String METHOD_CLEAR_EMPTY_DB_FLAG = "clear_empty_db_flag";
        public static final String METHOD_CLOSE_DB = "close_db";
        public static final String METHOD_CONVERT_SHORTCUTS_TO_LAUNCHER_ACTIVITIES = "convert_shortcuts_to_launcher_activities";
        public static final String METHOD_CREATE_EMPTY_DB = "create_empty_db";
        public static final String METHOD_DELETE_DATABASE = "delete_database";
        public static final String METHOD_DELETE_EMPTY_FOLDERS = "delete_empty_folders";
        public static final String METHOD_END_RESTORE_DB = "end_restore_db";
        public static final String METHOD_GET_BOOLEAN = "get_boolean_setting";
        public static final String METHOD_GET_DB_NAME = "get_db_name";
        public static final String METHOD_GET_FEATURE_VALUE = "get_feature_value";
        public static final String METHOD_GET_GRID_INFO = "get_grid_info";
        public static final String METHOD_IS_RESTORE_DB = "is_restore_db";
        public static final String METHOD_LOAD_DEFAULT_FAVORITES = "load_default_favorites";
        public static final String METHOD_LOAD_DEFAULT_SWIVEL_FAVORITES = "load_default_swivel_favorites";
        public static final String METHOD_MIGRATE_LAUNCHER2_SHORTCUTS = "migrate_launcher2_shortcuts";
        public static final String METHOD_NEW_ITEM_ID = "generate_new_item_id";
        public static final String METHOD_NEW_SCREEN_ID = "generate_new_screen_id";
        public static final String METHOD_REMOVE_GHOST_WIDGETS = "remove_ghost_widgets";
        public static final String METHOD_RESET_DATABASE_HELPER = "reset_database_helper";
        public static final String METHOD_SET_BOOLEAN = "set_boolean_setting";
        public static final String METHOD_SET_EXTRACTED_COLORS_AND_WALLPAPER_ID = "set_extracted_colors_and_wallpaper_id_setting";
        public static final String METHOD_START_RESTORE_DB = "start_restore_db";
        public static final String METHOD_UPDATE_FOLDER_ITEMS_RANK = "update_folder_items_rank";
        public static final String METHOD_UPDATE_MAX_SCREEN_ID = "update_max_screen_id";
        public static final String METHOD_WAS_EMPTY_DB_CREATED = "get_empty_db_flag";
        public static final String METHOD_WAS_NEW_DB_CREATED = "was_new_db_created";

        public static Bundle call(ContentResolver cr, String method) {
            return cr.call(CONTENT_URI, method, (String) null, (Bundle) null);
        }

        public static Bundle callSwivel(ContentResolver cr, String method) {
            return cr.call(CONTENT_URI_SWIVEL, method, (String) null, (Bundle) null);
        }

        public static Bundle call(ContentResolver cr, String method, String arg) {
            return cr.call(CONTENT_URI, method, arg, (Bundle) null);
        }
    }
}

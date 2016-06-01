package in.nisargjhaveri.counter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private static final String DATABASE_NAME = "CounterData";
    private static final int DATABASE_VERSION = 1;

    protected static final String TABLE_NAME_META = "meta";
    protected static final String META_COLUMN_NAME = "name";
    protected static final String META_COLUMN_VALUE = "value";

    protected static final String TABLE_NAME_COUNTERS = "counters";

    private static SQLiteDatabase db = null;
    private static DataStore outInstance = new DataStore();

    public TableMeta meta;

    public static void init(Context context) {
        DatabaseOpenHelper dbOpener = new DatabaseOpenHelper(context);
        db = dbOpener.getWritableDatabase();
    }

    public static DataStore getInstance() {
        return outInstance;
    }

    private DataStore() {
        meta = new TableMeta();
    }

    public static class TableMeta {
        public String getString(String name, String defaultValue) {
            Cursor c = db.query(
                    TABLE_NAME_META,
                    new String[]{META_COLUMN_VALUE},
                    META_COLUMN_NAME + " = ?",
                    new String[]{name},
                    null,
                    null,
                    null
            );

            if (c.moveToFirst()) {
                String value = c.getString(c.getColumnIndex(META_COLUMN_VALUE));
                c.close();
                return value;
            }

            return defaultValue;
        }

        public int getInt(String name, int defaultValue) {
            return Integer.parseInt(getString(name, String.valueOf(defaultValue)));
        }

        public void set(String name, String value) {
            ContentValues values = new ContentValues();
            values.put(META_COLUMN_NAME, name);
            values.put(META_COLUMN_VALUE, value);

            db.insertWithOnConflict(TABLE_NAME_META, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public void set(String name, int value) {
            set(name, String.valueOf(value));
        }

        public void remove(String name) {
            int c = db.delete(
                    TABLE_NAME_META,
                    META_COLUMN_NAME + " = ?",
                    new String[]{name}
            );
        }
    }

    public static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_TABLE_META =
                "CREATE TABLE " + TABLE_NAME_META + " (" +
                        META_COLUMN_NAME + " TEXT PRIMARY KEY," +
                        META_COLUMN_VALUE + " TEXT" +
                        ");";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_META);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
        }
    }
}

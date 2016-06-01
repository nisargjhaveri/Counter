package in.nisargjhaveri.counter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataStore {
    private static final String DATABASE_NAME = "CounterData";
    private static final int DATABASE_VERSION = 1;

    protected static final String TABLE_NAME_META = "meta";
    protected static final String META_COLUMN_NAME = "name";
    protected static final String META_COLUMN_VALUE = "value";

    protected static final String TABLE_NAME_COUNTERS = "counters";
    protected static final String COUNTERS_COLUMN_ID = "id";
    protected static final String COUNTERS_COLUMN_BGCOLOR = "bgcolor";
    protected static final String COUNTERS_COLUMN_LABEL = "label";
    protected static final String COUNTERS_COLUMN_COUNT = "count";

    private static SQLiteDatabase db = null;
    private static DataStore outInstance = new DataStore();

    public Meta meta;
    public Counters counters;

    public static void init(Context context) {
        DatabaseOpenHelper dbOpener = new DatabaseOpenHelper(context);
        db = dbOpener.getWritableDatabase();
    }

    public static DataStore getInstance() {
        return outInstance;
    }

    private DataStore() {
        meta = new Meta();
        counters = new Counters();
    }

    public static class Meta {
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

    public static class Counter {
        long ID;
        String label;
        int count;
        int bgcolor;

        public Counter(long i, String l, int c, int bg) {
            ID = i;
            label = l;
            count = c;
            bgcolor = bg;
        }

        private void set(String column, String value) {
            ContentValues values = new ContentValues();
            values.put(column, value);

            db.update(TABLE_NAME_COUNTERS, values, COUNTERS_COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        }

        private void set(String column, int value) {
            ContentValues values = new ContentValues();
            values.put(column, value);

            db.update(TABLE_NAME_COUNTERS, values, COUNTERS_COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        }

        public long getId() {
            return ID;
        }

        public String getLabel(String defaultLabel) {
            if (label == null) {
                return defaultLabel;
            }
            return label;
        }

        public void setLabel(String l) {
            label = l;
            set(COUNTERS_COLUMN_LABEL, label);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int c) {
            count = c;
            set(COUNTERS_COLUMN_COUNT, c);
        }

        public int getBgColor() {
            return bgcolor;
        }

        public void setBgColor(int bg) {
            bgcolor = bg;
            set(COUNTERS_COLUMN_BGCOLOR, bg);
        }
    }

    public static class Counters {
        public Counter getCounterAt(int position) {
            Cursor c = db.query(
                    false,
                    TABLE_NAME_COUNTERS,
                    new String[]{COUNTERS_COLUMN_ID, COUNTERS_COLUMN_LABEL, COUNTERS_COLUMN_COUNT, COUNTERS_COLUMN_BGCOLOR},
                    null,
                    null,
                    null,
                    null,
                    COUNTERS_COLUMN_ID + " ASC",
                    null
            );

            if (c.moveToPosition(position)) {
                return new Counter(
                        c.getLong(c.getColumnIndex(COUNTERS_COLUMN_ID)),
                        c.getString(c.getColumnIndex(COUNTERS_COLUMN_LABEL)),
                        c.getInt(c.getColumnIndex(COUNTERS_COLUMN_COUNT)),
                        c.getInt(c.getColumnIndex(COUNTERS_COLUMN_BGCOLOR))
                );
            } // else Something is wrong

            return null;
        }

        private Counter getCounter(long id) {
            Cursor c = db.query(
                    TABLE_NAME_COUNTERS,
                    new String[]{COUNTERS_COLUMN_ID, COUNTERS_COLUMN_LABEL, COUNTERS_COLUMN_COUNT, COUNTERS_COLUMN_BGCOLOR},
                    COUNTERS_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            if (c.moveToFirst()) {
                return new Counter(
                        c.getLong(c.getColumnIndex(COUNTERS_COLUMN_ID)),
                        c.getString(c.getColumnIndex(COUNTERS_COLUMN_LABEL)),
                        c.getInt(c.getColumnIndex(COUNTERS_COLUMN_COUNT)),
                        c.getInt(c.getColumnIndex(COUNTERS_COLUMN_BGCOLOR))
                );
            }

            return null;
        }

        public Counter createCounter(String label, int count, int bgcolor) {
            ContentValues values = new ContentValues();
            values.put(COUNTERS_COLUMN_LABEL, label);
            values.put(COUNTERS_COLUMN_COUNT, count);
            values.put(COUNTERS_COLUMN_BGCOLOR, bgcolor);

            long id = db.insert(TABLE_NAME_COUNTERS, null, values);
            return getCounter(id);
        }

        public void removeCounter(long id) {
            db.delete(TABLE_NAME_COUNTERS, COUNTERS_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_TABLE_META =
                "CREATE TABLE " + TABLE_NAME_META + " (" +
                        META_COLUMN_NAME + " TEXT PRIMARY KEY," +
                        META_COLUMN_VALUE + " TEXT" +
                        ");";

        private static final String CREATE_TABLE_COUNTERS =
                "CREATE TABLE " + TABLE_NAME_COUNTERS + " (" +
                        COUNTERS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        COUNTERS_COLUMN_LABEL + " TEXT," +
                        COUNTERS_COLUMN_COUNT + " INTEGER," +
                        COUNTERS_COLUMN_BGCOLOR + " INTEGER" +
                        ");";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_META);
            db.execSQL(CREATE_TABLE_COUNTERS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
        }
    }
}

package fall2018.csc2017.slidingtiles.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import fall2018.csc2017.slidingtiles.ScoreBoard;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "User.db";
    static final String TABLE_NAME = "user_table";
    static final String KEY_NAME = "USERNAME";
    static final String KEY_PASSWORD = "PASSWORD";
    static final String KEY_AGE = "AGE";
    static final String KEY_EMAIL = "EMAIL";
    static final String KEY_ST3X3 = "ST3X3";
    static final String KEY_ST4X4 = "ST4X4";
    static final String KEY_ST5X5 = "ST5X5";
    static final String KEY_2048 = "T2048";
    static final String KEY_MS = "MS";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (USERNAME TEXT PRIMARY KEY, " +
                "PASSWORD TEXT, AGE INTEGER, EMAIL TEXT, ST3X3 OBJECT, ST4X4 OBJECT, ST5X5 OBJECT, "
                + "T2048 OBJECT, MS OBJECT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    private String convertToJson(Object o) {
    Gson gson = new Gson();
        return gson.toJson(o);
    }

    public boolean insertUserData(String username, String password, int age, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, username);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_AGE, age); //change default value of age = 0 and email = "" in regist activity
        contentValues.put(KEY_EMAIL, email);
        contentValues.put(KEY_ST3X3, convertToJson(new ScoreBoard("SlidingTiles")));
        contentValues.put(KEY_ST4X4, convertToJson(new ScoreBoard("SlidingTiles")));
        contentValues.put(KEY_ST5X5, convertToJson(new ScoreBoard("SlidingTiles")));
        contentValues.put(KEY_2048, convertToJson(new ScoreBoard("2048")));
        contentValues.put(KEY_MS, convertToJson(new ScoreBoard("MineSweeper")));
        long result = db.insert(TABLE_NAME,null ,contentValues);
        db.close();

        return result != -1;
    }

    public boolean updateUserData(String username, String password, int age, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, username);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_AGE, age);
        contentValues.put(KEY_EMAIL, email);
        db.update(TABLE_NAME, contentValues, "USERNAME = ?",new String[] { username });
        return true;
    }

    public boolean insertScoreboard(){return true;}

    public Integer deleteData (String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "USERNAME = ?",new String[] {username});
    }


}

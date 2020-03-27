package com.oroz.anton.expenseapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.oroz.anton.expenseapp.ListModel;
import com.oroz.anton.expenseapp.R;

import java.util.ArrayList;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "my_expenses_app.db";

    private static final String CATEGORIES_TABLE_NAME = "categories";
    private static final String EXPENSES_TABLE_NAME = "expenses";

    private static final String COL_1 = "catID";
    private static final String COL_2 = "CATEGORY";

    private static final String COL_expId = "expenses_ID";
    private static final String COL_expCat = "expenseCategory";
    public static final String COL_expAmount = "expenseAmount";
    public static final String COL_total = "expenseTotal";
    public static final String COL_date = "date";

    private Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + CATEGORIES_TABLE_NAME +
                //" ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " ( " + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT NOT NULL)";
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORIES);


        String CREATE_TABLE_EXPENSES = "CREATE TABLE " + EXPENSES_TABLE_NAME +
                " ( " + COL_expId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_expCat + " TEXT NOT NULL, " +
                COL_total + " INTEGER NOT NULL, " +
                COL_date + " TEXT NOT NULL, " +
                COL_expAmount + " INTEGER NOT NULL)";
        sqLiteDatabase.execSQL(CREATE_TABLE_EXPENSES);


        ContentValues contentValues = new ContentValues();
        Resources resources = context.getResources();
        String[] mArray = resources.getStringArray(R.array.initial_categories);
        for (String cats : mArray) {
            contentValues.put(COL_2, cats);
            sqLiteDatabase.insert(CATEGORIES_TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public boolean addExp(String category, int amount, int total, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_expCat, category);
        contentValues.put(COL_expAmount, amount);
        contentValues.put(COL_total, total);
        contentValues.put(COL_date, date);
        long result = db.insert(EXPENSES_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else return true;
    }

    public ArrayList<ListModel> getExpensesList() {
        ArrayList<ListModel> listOfExpenses = new ArrayList();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "The database was empty", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String categoryName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expCat));
                String amount = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expAmount));
                String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_date));
                ListModel listModel = new ListModel(categoryName, amount, date);
                listOfExpenses.add(listModel);
            }
        }
        return listOfExpenses;
    }

    public int total() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT " + COL_expAmount + " FROM " + EXPENSES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(COL_expAmount));
        }
        return total;
    }

    public int totalForToday() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = date('now', 'localtime')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(COL_expAmount));
        }
        return total;
    }

    public int totalForWeek() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date
                + " BETWEEN date('now','-6 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(COL_expAmount));
        }
        return total;
    }

    public int totalForMonth() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " BETWEEN date('now','-29 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(COL_expAmount));
        }
        return total;
    }

    public int totalForCertainDate(String dateC) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = '" + dateC + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(COL_expAmount));
        }
        return total;
    }

    public boolean addCat(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, item);
        long result = db.insert(CATEGORIES_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else return true;
    }

    public ArrayList<String> getCategoriesList() {
        ArrayList<String> listOfCategories = new ArrayList();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + CATEGORIES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "The database was empty", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                listOfCategories.add(cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_2)));
            }

        }
        return listOfCategories;
    }

    public ArrayList<ListModel> getTodayData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = date('now', 'localtime')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expCat));
            String amount = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expAmount));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_date));
            ListModel listModel = new ListModel(categoryName, amount, date);
            listOfExpenses.add(listModel);

        }

        return listOfExpenses;
    }

    public ArrayList<ListModel> getLasTWeekData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date
                + " BETWEEN date('now','-6 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expCat));
            String amount = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expAmount));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_date));
            ListModel listModel = new ListModel(categoryName, amount, date);
            listOfExpenses.add(listModel);
        }

        return listOfExpenses;
    }

    public ArrayList<ListModel> getLastMonthData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE "
                + COL_date + " BETWEEN date('now','-29 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expCat));
            String amount = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expAmount));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_date));
            ListModel listModel = new ListModel(categoryName, amount, date);
            listOfExpenses.add(listModel);
        }

        return listOfExpenses;
    }

    public ArrayList<ListModel> getCertainDateData(String dateC) {
        ArrayList<ListModel> listOfExpenses = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = '" + dateC + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expCat));
            String amount = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_expAmount));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_date));
            ListModel listModel = new ListModel(categoryName, amount, date);
            listOfExpenses.add(listModel);
        }

        return listOfExpenses;
    }

    public void deleteCatRow(String name) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query3 = "DELETE FROM " + CATEGORIES_TABLE_NAME + " WHERE " + COL_2 + " = '" + name + "'";
        sqLiteDatabase.execSQL(query3);

    }


}




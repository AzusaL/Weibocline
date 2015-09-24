package com.AzusaL.database;

import com.AzusaL.bean.UserBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteOpenhelper extends SQLiteOpenHelper {

	public SqliteOpenhelper(Context context) {
		super(context, "weibo.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"create table if not exists weibo( _id integer primary key autoincrement ,name varchar(50) , password varchar(50),introduction varchar(100),head_img_path varchar(100)) ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void insertusermessage(UserBean bean) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", bean.getName());
		contentValues.put("password", bean.getPassword());
		contentValues.put("introduction", bean.getIntroduction());
		contentValues.put("head_img_path", bean.getHead_img_path());
		SQLiteDatabase db = getWritableDatabase();
		db.insert("weibo", null, contentValues);
	}

	public void updateusermessage(UserBean bean) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", bean.getName());
		contentValues.put("password", bean.getPassword());
		contentValues.put("introduction", bean.getIntroduction());
		contentValues.put("head_img_path", bean.getHead_img_path());
		SQLiteDatabase db = getWritableDatabase();
		db.update("weibo", contentValues, "name=?", new String[] { bean.getName() });
	}

	public void updateuserimgpath(String name, String path) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("head_img_path", path);
		SQLiteDatabase db = getWritableDatabase();
		db.update("weibo", contentValues, "name=?", new String[] { name });
	}

	public void updateuserintroduction(String name, String introduction) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("introduction", introduction);
		SQLiteDatabase db = getWritableDatabase();
		db.update("weibo", contentValues, "name=?", new String[] { name });
	}

	public UserBean queryusermessage(String name) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("weibo", null, "name like?", new String[] { name }, null, null, null);
		if (cursor.moveToNext()) {
			String password = cursor.getString(cursor.getColumnIndex("password"));
			String introduction = cursor.getString(cursor.getColumnIndex("introduction"));
			String head_img_path = cursor.getString(cursor.getColumnIndex("head_img_path"));
			return new UserBean(name, password, introduction, head_img_path);
		}
		return null;
	}

	public Boolean hasname(String name) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("weibo", null, "name=?", new String[] { name }, null, null, null);
		if (cursor.moveToNext()) {
			return true;
		}
		return false;
	}
}

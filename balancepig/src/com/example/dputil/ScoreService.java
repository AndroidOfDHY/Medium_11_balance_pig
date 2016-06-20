package com.example.dputil;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScoreService {

	private DBOpenHelper dbOpenHelper;

	public ScoreService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	/**
	 * 从数据库中读取数据
	 * 
	 * @return 返回一组数据
	 */
	public List<Score> readFromDB() {
		List<Score> list = new ArrayList<Score>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String sql = "select * from score";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int score = cursor.getInt(cursor.getColumnIndex("score"));
			String date = cursor.getString(cursor.getColumnIndex("date"));
			Score sc = new Score(_id, score, date);
			list.add(sc);
		}
		db.close();
		return list;
	}

	/**
	 * 向数据库中添加数据
	 * 
	 * @param score
	 *            Score对象
	 */
	public void insertToDB(Score score) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into score (score,date) values(?,?)", new Object[] {
				score.getScore(), score.getDate() });
		db.close();
	}

	/**
	 * 删除数据库中的数据
	 * 
	 * @param _id
	 *            记录的编号
	 */
	public void deleteFromDB(Integer _id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from score where _id=?",
				new Object[] { _id.toString() });
		db.close();

	}

	public void updateDB(Integer _id, String score, String date) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update score set score=?,date=?  where _id=?",
				new Object[] { score, date, _id.toString() });
		db.close();
	}

}

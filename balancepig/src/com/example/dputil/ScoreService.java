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
	 * �����ݿ��ж�ȡ����
	 * 
	 * @return ����һ������
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
	 * �����ݿ����������
	 * 
	 * @param score
	 *            Score����
	 */
	public void insertToDB(Score score) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into score (score,date) values(?,?)", new Object[] {
				score.getScore(), score.getDate() });
		db.close();
	}

	/**
	 * ɾ�����ݿ��е�����
	 * 
	 * @param _id
	 *            ��¼�ı��
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

package com.example.dputil;

import java.io.Serializable;

public class Score implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id;
	private int score;
	private String date;

	public Score(int _id, int score, String date) {
		this._id = _id;
		this.score = score;
		this.date = date;
	}

	public Score(int score, String date) {
		this.score = score;
		this.date = date;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}

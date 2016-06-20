package com.example.dputil;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {

	private int itemT;
	private int itemG;
	private int itemS;
	private int itemB;
	private int gold_count;
	private boolean[] bg = new boolean[10];
	private boolean red_gold;
	private boolean blue_gold;

	public void setConfig(SharedPreferences spf) {
		Editor editor = spf.edit();
		editor.putInt("itemT", itemT);
		editor.putInt("itemG", itemG);
		editor.putInt("itemS", itemS);
		editor.putInt("itemB", itemB);
		editor.putInt("gold_count", gold_count);

		for (int i = 0; i < bg.length; i++) {
			editor.putBoolean("bg" + i, bg[i]);
		}
		editor.putBoolean("red_gold", red_gold);
		editor.putBoolean("blue_gold", blue_gold);
		editor.commit();
	}

	public void getConfig(SharedPreferences spf) {
		// 读取道具参数
		itemT = spf.getInt("itemT", 0);
		itemG = spf.getInt("itemG", 0);
		itemS = spf.getInt("itemS", 0);
		itemB = spf.getInt("itemB", 0);
		// 读取金币参数
		gold_count = spf.getInt("gold_count", 0);
		// 读取背景参数
		for (int i = 0; i < bg.length; i++) {
			bg[i] = spf.getBoolean("bg" + i, false);
		}
		// 读取金币类型参数
		red_gold = spf.getBoolean("red_gold", false);
		blue_gold = spf.getBoolean("blue_gold", false);
	}

	public void addItemG() {
		itemG++;
	}
	
	public void cutItemG() {
		itemG--;
	}
	
	public void addItemT() {
		itemT++;
	}
	
	public void cutItemT() {
		itemT--;
	}
	
	public int getItemT() {
		return itemT;
	}
	
	public void addItemS() {
		itemS++;
	}
	
	public void cutItemS() {
		itemS--;
	}
	
	public void addItemB() {
		itemB++;
	}
	
	public void cutItemB() {
		itemB--;
	}

	public void setItemT(int itemT) {
		this.itemT = itemT;
	}

	public int getItemG() {
		return itemG;
	}

	public void setItemG(int itemG) {
		this.itemG = itemG;
	}

	public int getItemS() {
		return itemS;
	}

	public void setItemS(int itemS) {
		this.itemS = itemS;
	}

	public int getItemB() {
		return itemB;
	}

	public void setItemB(int itemB) {
		this.itemB = itemB;
	}

	public int getGold_count() {
		return gold_count;
	}

	public void setGold_count(int gold_count) {
		this.gold_count = gold_count;
	}

	public boolean[] getBg() {
		return bg;
	}

	public void setBg(boolean[] bg) {
		this.bg = bg;
	}

	public boolean isRed_gold() {
		return red_gold;
	}

	public void setRed_gold(boolean red_gold) {
		this.red_gold = red_gold;
	}

	public boolean isBlue_gold() {
		return blue_gold;
	}

	public void setBlue_gold(boolean blue_gold) {
		this.blue_gold = blue_gold;
	}
}

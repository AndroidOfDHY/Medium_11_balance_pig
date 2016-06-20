package com.pig.imageviews;

import com.example.pig.BalanceActivity;
import com.example.pig.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Wing extends ImageView {

	private BalanceActivity main;
	
	private int vWidth;
	private int vHeight;
	public static int leftM;
	public static int topM;
	private float curX, curY;
	private float v, addY;
	
	public Wing(Context context) {
		super(context);
		
		this.setAdjustViewBounds(true);
		main = (BalanceActivity) context;
		this.setImageResource(R.drawable.small_wing);
		this.setAdjustViewBounds(true);
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = bd.getBitmap();
		vHeight = bitmap.getHeight();
		vWidth = bitmap.getWidth();
		
		leftM = main.getBalanceLM() + main.getBalanceV().getvWidth() / 2
				- vWidth / 2;
		topM = main.getBalanceTM() - vHeight;
		
		curX = 0;
		curY = 0;
	}
	
	public void translateAnimX() {
		curX = main.getPigV().getCurX();
		curY = main.getPigV().getCurY() + 5;
		v = main.getPigV().getV();
		TranslateAnimation animX = new TranslateAnimation(curX, curX += v
				* main.rate, curY, curY);
		animX.setDuration(200);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}
	
	public void translateAnimY() {
		curX = main.getPigV().getCurX();
		curY = main.getPigV().getCurY() + 5;
		addY = (float) (curX * Math.tan(main.getBalanceV().getCurAngle()
				* Math.PI / 180));
		TranslateAnimation animY = new TranslateAnimation(curX, curX, curY,
				addY);
		animY.setDuration(100);
		animY.setFillAfter(true);
		this.startAnimation(animY);
	}
	
	public void dropDown() {
		curX = main.getPigV().getCurX();
		curY = main.getPigV().getCurY() + 5;
		TranslateAnimation animX = new TranslateAnimation(curX, curX + 6, curY,
				curY + 400);
		animX.setDuration(2000);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}
	
	public int getvWidth() {
		return vWidth;
	}

	public void setvWidth(int vWidth) {
		this.vWidth = vWidth;
	}

	public int getvHeight() {
		return vHeight;
	}

	public void setvHeight(int vHeight) {
		this.vHeight = vHeight;
	}

	public float getCurX() {
		return curX;
	}

	public void setCurX(float curX) {
		this.curX = curX;
	}

	public float getCurY() {
		return curY;
	}

	public void setCurY(float curY) {
		this.curY = curY;
	}



}

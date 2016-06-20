package com.pig.imageviews;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.pig.BalanceActivity;
import com.example.pig.R;

public class GoldView extends ImageView {

	public static int leftM;
	public static int topM;

	private BalanceActivity main;

	private Random random;
	private float curX, curY, addY;

	private int vHeight;
	private int vWidth;

	private boolean live;
	public int kind;

	public GoldView(Context context) {
		super(context);
		main = (BalanceActivity) context;

		random = new Random();
		int usertime = main.getPigV().getUsetime() / 1000;
		if (usertime >= 30 && usertime < 80 && main.config.isRed_gold()) {

			kind = random.nextInt(2);
			if (kind == 0)
				this.setImageResource(R.drawable.gold);
			else
				this.setImageResource(R.drawable.goldx5);
		} else if (usertime >= 80) {

			if (main.config.isBlue_gold()) {
				kind = random.nextInt(3);
				if (kind == 0)
					this.setImageResource(R.drawable.gold);
				else if (kind == 1)
					this.setImageResource(R.drawable.goldx5);
				else {
					this.setImageResource(R.drawable.goldx10);
				}
			} else if (main.config.isRed_gold()) {
				kind = random.nextInt(2);
				if (kind == 0)
					this.setImageResource(R.drawable.gold);
				else
					this.setImageResource(R.drawable.goldx5);
			} else {
				this.setImageResource(R.drawable.gold);
			}

		} else {
			this.setImageResource(R.drawable.gold);
			kind = 0;
		}

		if (usertime >= 30 && usertime < 80) {
			addY = 1.2f;
		} else if (usertime >= 80) {
			addY = 1.6f;
		} else {
			addY = 0.8f;
		}

		this.setAdjustViewBounds(true);
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = bd.getBitmap();
		vHeight = bitmap.getHeight();
		vWidth = bitmap.getWidth();
		leftM = getRandom();
		topM = 0;

		live = true;
	}

	public int getRandom() {
		return main.getBalanceLM() + 10
				+ random.nextInt(main.getBalanceV().getvWidth() - vWidth - 10);
	}

	public void DropAnim() {
		TranslateAnimation anim = new TranslateAnimation(curX, curX, curY,
				curY += addY);
		anim.setDuration(10);
		anim.setFillAfter(true);
		this.startAnimation(anim);
		if (curY > 1000) {
			live = false;
		} else if (isEate()) {
			live = false;
			Message msg = main.getHandler().obtainMessage(0x129, this);
			main.getHandler().sendMessage(msg);
		}
	}

	public float getRelativeDisX() {
		main.getPigV();
		return leftM - PigView.leftM;
	}

	public float getRelativeDisY() {
		main.getPigV();
		return curY + topM - PigView.topM;
	}

	public boolean isEate() {
		if (getRelativeDisX() + vWidth / 2 - 10 > main.getPigV().getCurX()
				&& getRelativeDisX() + 10 < main.getPigV().getCurX()
						+ main.getPigV().getvWidth()
				&& getRelativeDisY() + vHeight / 2 - 10 > main.getPigV()
						.getCurY()
				&& getRelativeDisY() + 10 < main.getPigV().getCurY()
						+ main.getPigV().getvHeight()) {
			return true;
		}
		return false;
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

	public int getvHeight() {
		return vHeight;
	}

	public int getvWidth() {
		return vWidth;
	}

	public float getAddY() {
		return addY;
	}

	public void setAddY(float addY) {
		this.addY = addY;
	}

	public boolean isLive() {
		return live;
	}

}

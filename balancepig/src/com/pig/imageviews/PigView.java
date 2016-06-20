package com.pig.imageviews;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.pig.BalanceActivity;
import com.example.pig.R;

/***
 * ƽ�������ͼ
 * 
 * @author ���� ��˫ϲ
 * 
 */
public class PigView extends ImageView {

	public static int MAXS = 200; // ����ٶ�
	public static int leftM; // ��View��marginLeft
	public static int topM;// ��iew��marginTop

	private BalanceActivity main; // �ܿ���Activity
	private int weight; // ƽ���������
	private int vHeight; // ƽ����ĸ߶�
	private int vWidth;// ƽ����Ŀ��
	private int power;// ƽ���������
	private float a, v, tempv;// ƽ����ļ��ٶȡ��ٶ�
	private float curX, curY, addY;// ƽ�����ʵʱλ��
	private float friction;// ƽ�����ƽ����Ħ����
	private int score; // ƽ����ĵ÷�
	private int scores;
	private int usetime;

	private boolean live; // ƽ���������
	private int time;// ƽ�����ʱ��

	/**
	 * timer��ʵʱ������ļ��ٶȺ��ٶȺ�λ�� timer1����ʱ��Ŀ��ƣ�ÿ�����һ
	 */
	public Timer timer = null, timer1 = null, timer2 = null;
	private TimerTask task = null, task1 = null, task2 = null;
	private boolean isRunning = false, isRunning1 = false, isRunning2;

	/***
	 * ���캯��
	 * 
	 * @param context
	 *            �����Ķ���
	 */
	public PigView(Context context) {
		super(context);
		this.setAdjustViewBounds(true);

		main = (BalanceActivity) context;
		this.setImageResource(R.drawable.pigr);
		this.setAdjustViewBounds(true);
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = bd.getBitmap();
		vHeight = bitmap.getHeight();
		vWidth = bitmap.getWidth();

		leftM = main.getBalanceLM() + main.getBalanceV().getvWidth() / 2
				- vWidth / 2;
		topM = main.getBalanceTM() - vHeight;
		weight = 100;
		a = 0;
		v = 0;
		time = 90;
		score = 0;
		scores = 0;
		usetime = 0;
		power = 50;
		friction = 1000;
		live = true;

	}

	/***
	 * timer ��ʼ��ʱ
	 */
	public void timer_start() {
		if (task == null) {
			task = new TimerTask() {

				@Override
				public void run() {
					if (live = judgeLive()) {
						setV(calcV());
						main.getHandler().sendEmptyMessage(0x124);
					} else {
						main.getHandler().sendEmptyMessage(0x128);
						pause();
					}
				}
			};
		}
		if (timer == null) {
			timer = new Timer();
			timer.schedule(task, 100, (long) (main.rate * 1000));
		}
		isRunning = true;
	}

	/***
	 * timer ֹͣ��ʱ
	 */
	public void timer_pause() {
		if (isRunning) {
			task.cancel();
			task = null;
			timer.cancel();
			timer = null;
			isRunning = false;
		}
	}

	/***
	 * timer1 ��ʼ��ʱ
	 */
	public void timer1_start() {
		if (task1 == null) {
			task1 = new TimerTask() {

				@Override
				public void run() {
					if (time == 0) {
						live = false;
						main.getHandler().sendEmptyMessage(0x128);
						pause();
					} else {
						time--;
					}
				}
			};
		}
		if (timer1 == null) {
			timer1 = new Timer();
			timer1.schedule(task1, 1000, 1000);
		}
		isRunning1 = true;
	}

	/***
	 * timer1 ֹͣ��ʱ
	 */
	public void timer1_pause() {
		if (isRunning1) {
			task1.cancel();
			task1 = null;
			timer1.cancel();
			timer1 = null;
			isRunning1 = false;
		}
	}

	public void timer2_start() {
		if (task2 == null) {
			task2 = new TimerTask() {

				@Override
				public void run() {
					usetime += 100;
					main.getHandler().sendEmptyMessage(0x132);
				}
			};
		}
		if (timer2 == null) {
			timer2 = new Timer();
			timer2.schedule(task2, 1000, 100);
		}
		isRunning2 = true;
	}

	public void timer2_pause() {
		if (isRunning2) {
			task2.cancel();
			task2 = null;
			timer2.cancel();
			timer2 = null;
			isRunning2 = false;
		}
	}

	public void reLive() {
		live = true;
		v = 0;
		a = 0;
		curX = 0;
		curY = 0;
		weight = 100;
	}

	/***
	 * ���¿�ʼ��Ϸ�����ú���
	 */
	public void reset() {
		v = 0;
		a = 0;
		time = 60;
		score = 0;
		curX = 0;
		curY = 0;
		weight = 100;
		timer1_start();
		timer_start();
		timer2_start();

	}

	/***
	 * ���ظ���ͼʱ�ĳ�ʼ��
	 */
	public void resume() {
		weight = 100;
		v = tempv;
		timer_start();
		timer1_start();
		timer2_start();
	}

	/***
	 * ��Ϸ��ͣʱ����ͣ����
	 */
	public void pause() {
		weight = 0;
		tempv = v;
		v = 0;
		a = 0;
		power = 0;
		timer_pause();
		timer1_pause();
		timer2_pause();
	}

	/***
	 * ����С��ķ���
	 */
	public void updateDir() {
		if (v > 0) {
			this.setImageResource(R.drawable.pigr);
		} else {
			this.setImageResource(R.drawable.pigl);
		}
	}

	/***
	 * ����С��ļ��ٶ�
	 * 
	 * @return
	 */
	public float getAc() {
		float a1 = 8f * (float) (power + weight
				* Math.sin(main.getBalanceV().getCurAngle() * Math.PI / 180));
		if (a1 * v < 0) {
			if (v > 0)
				return -friction;
			else if (v < 0)
				return friction;
			else
				return 0;
		}
		return a1;
	}

	/**
	 * ����С����ٶ�
	 * 
	 * @return С����ٶ�
	 */
	public float calcV() {
		a = getAc();
		if (v > MAXS || v < -MAXS) {
			if (v > MAXS)
				return MAXS;
			else
				return -MAXS;
		} else {
			return v += a * main.rate;
		}
	}

	/**
	 * �ж�С�������
	 * 
	 * @return true���� false����
	 */
	public boolean judgeLive() {
		if (Math.abs(curX) - vWidth / 2 > (main.getBalanceV().getvWidth() / 2)
				* Math.cos(main.getBalanceV().getCurAngle() * Math.PI / 180)) {
			return false;
		}
		return true;
	}

	/**
	 * ƽ��������䶯��
	 */
	public void dropDown() {
		TranslateAnimation animX = new TranslateAnimation(curX, curX + 6, curY,
				curY + 400);
		animX.setDuration(2000);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}

	/**
	 * ƽ������ƶ�����
	 */
	public void translateAnimX() {
		TranslateAnimation animX = new TranslateAnimation(curX, curX += v
				* main.rate, curY, curY);
		animX.setDuration(200);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}

	/**
	 * ƽ������ƶ�����
	 */
	public void translateAnimY() {
		addY = (float) (curX * Math.tan(main.getBalanceV().getCurAngle()
				* Math.PI / 180));
		TranslateAnimation animY = new TranslateAnimation(curX, curX, curY,
				addY);
		curY = addY;
		animY.setDuration(100);
		animY.setFillAfter(true);
		this.startAnimation(animY);
	}

	public int getTime() {
		return time;
	}

	public float getV() {
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public float getA() {
		return a;
	}

	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
	}

	public int getUsetime() {
		return usetime;
	}

	public void setUsetime(int usetime) {
		this.usetime = usetime;
	}

	public void setA(float a) {
		this.a = a;
	}

	public int getvHeight() {
		return vHeight;
	}

	public int getvWidth() {
		return vWidth;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
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

	public float getAddY() {
		return addY;
	}

	public void setAddY(float addY) {
		this.addY = addY;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isLive() {
		return live;
	}

	public void setTime(int time) {
		this.time = time;
	}
}

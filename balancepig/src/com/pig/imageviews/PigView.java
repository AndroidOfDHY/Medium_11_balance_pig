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
 * 平衡猪的视图
 * 
 * @author 郭招 陈双喜
 * 
 */
public class PigView extends ImageView {

	public static int MAXS = 200; // 最大速度
	public static int leftM; // 该View的marginLeft
	public static int topM;// 该iew的marginTop

	private BalanceActivity main; // 总控制Activity
	private int weight; // 平衡猪的重量
	private int vHeight; // 平衡猪的高度
	private int vWidth;// 平衡猪的宽度
	private int power;// 平衡猪的力量
	private float a, v, tempv;// 平衡猪的加速度、速度
	private float curX, curY, addY;// 平衡猪的实时位置
	private float friction;// 平衡猪和平衡板的摩擦力
	private int score; // 平衡猪的得分
	private int scores;
	private int usetime;

	private boolean live; // 平衡猪的生命
	private int time;// 平衡猪的时间

	/**
	 * timer：实时计算猪的加速度和速度和位置 timer1：对时间的控制，每秒减少一
	 */
	public Timer timer = null, timer1 = null, timer2 = null;
	private TimerTask task = null, task1 = null, task2 = null;
	private boolean isRunning = false, isRunning1 = false, isRunning2;

	/***
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
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
	 * timer 开始计时
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
	 * timer 停止计时
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
	 * timer1 开始计时
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
	 * timer1 停止计时
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
	 * 重新开始游戏的重置函数
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
	 * 加载该视图时的初始化
	 */
	public void resume() {
		weight = 100;
		v = tempv;
		timer_start();
		timer1_start();
		timer2_start();
	}

	/***
	 * 游戏暂停时的暂停函数
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
	 * 更新小猪的方向
	 */
	public void updateDir() {
		if (v > 0) {
			this.setImageResource(R.drawable.pigr);
		} else {
			this.setImageResource(R.drawable.pigl);
		}
	}

	/***
	 * 计算小猪的加速度
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
	 * 计算小猪的速度
	 * 
	 * @return 小猪的速度
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
	 * 判断小猪的生命
	 * 
	 * @return true：活 false：死
	 */
	public boolean judgeLive() {
		if (Math.abs(curX) - vWidth / 2 > (main.getBalanceV().getvWidth() / 2)
				* Math.cos(main.getBalanceV().getCurAngle() * Math.PI / 180)) {
			return false;
		}
		return true;
	}

	/**
	 * 平衡猪的下落动画
	 */
	public void dropDown() {
		TranslateAnimation animX = new TranslateAnimation(curX, curX + 6, curY,
				curY + 400);
		animX.setDuration(2000);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}

	/**
	 * 平衡猪的移动动画
	 */
	public void translateAnimX() {
		TranslateAnimation animX = new TranslateAnimation(curX, curX += v
				* main.rate, curY, curY);
		animX.setDuration(200);
		animX.setFillAfter(true);
		this.startAnimation(animX);
	}

	/**
	 * 平衡猪的移动动画
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

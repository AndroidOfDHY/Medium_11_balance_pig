package com.pig.imageviews;

import java.util.Timer;
import java.util.TimerTask;
import com.example.pig.BalanceActivity;
import com.example.pig.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/***
 * 平衡棒视图
 * @author 郭招 	陈双喜
 *
 */
public class BalanceView extends ImageView {

	public static int leftM; //该imageView的marginLeft值
	public static int topM; //该imageView的marginTop值

	private BalanceActivity main; //总控制Activity
	private int J;  //求角加速度的阻力
	private int vHeight; //平衡棒的高度
	private int vWidth;//平衡棒的宽度
	private float curAngle;//平衡棒的倾斜角度
	private float addAngle;//增加角度的速度

	public Timer timer;//定时器-定时计算角加速度
	private TimerTask task;
	private boolean isRunning;

	/***
	 * 构造函数
	 * @param context 上下文对象
	 */
	public BalanceView(Context context) {
		super(context);
		main = (BalanceActivity) context;
		this.setImageResource(R.drawable.balance);
		this.setAdjustViewBounds(true);
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = bd.getBitmap();
		vHeight = bitmap.getHeight();
		vWidth = bitmap.getWidth();
		leftM = main.getBalanceLM();
		topM = main.getBalanceTM();
		J = 1000;
	}

	/***
	 * timer 定时计算角的倾斜度并实时更新
	 */
	public void timer_start() {
		if(task == null) {
			task = new TimerTask() {
				
				@Override
				public void run() {
					if (main.getPigV().isLive()) {
						calcCurAngle();
						main.getHandler().sendEmptyMessage(0x125);
					}
				}
			};
		}
		if(timer == null) {
			timer = new Timer();
			timer.schedule(task, 100, (long) (main.rate * 1000));
		}
		isRunning = true;
	}
	
	/***
	 * timer 的取消
	 */
	public void timer_pause() {
		if(isRunning) {
			task.cancel();
			task = null;
			timer.cancel();
			timer = null;
			isRunning = false;
		}
	}
	
	/***
	 * 重新开始该View重新置位的函数
	 */
	public void reset() {
		 curAngle = 0;
		 addAngle = 0;
		 timer_start();
	}
	
	/***
	 * 旋转函数
	 */
	public void rotateAnim() {

		RotateAnimation bAnim = new RotateAnimation(curAngle,
				curAngle += addAngle, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		bAnim.setDuration(100);
		bAnim.setFillAfter(true);
		this.startAnimation(bAnim);
	}

	/***
	 * 计算当前角倾斜度的函数
	 */
	public void calcCurAngle() {
		float angleA = 0;
		angleA = (float) (main.getPigV().getCurX()
				* main.getPigV().getWeight()
				* Math.cos(getCurAngle() * Math.PI / 180) / getJ()
				- Math.sin(getCurAngle() * Math.PI / 180));
		
		if (getCurAngle() > 40 || getCurAngle() < -40) {
			setAddAngle(0);
			if (getCurAngle() > 40)
				setCurAngle(40);
			else
				setCurAngle(-40);
		} else {

			setAddAngle(angleA * main.rate);
		}
	}

	public int getJ() {
		return J;
	}

	public int getvHeight() {
		return vHeight;
	}

	public int getvWidth() {
		return vWidth;
	}

	public float getCurAngle() {
		return curAngle;
	}

	public void setCurAngle(float curAngle) {
		this.curAngle = curAngle;
	}

	public float getAddAngle() {
		return addAngle;
	}

	public void setAddAngle(float addAngle) {
		this.addAngle = addAngle;
	}

}

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
 * ƽ�����ͼ
 * @author ���� 	��˫ϲ
 *
 */
public class BalanceView extends ImageView {

	public static int leftM; //��imageView��marginLeftֵ
	public static int topM; //��imageView��marginTopֵ

	private BalanceActivity main; //�ܿ���Activity
	private int J;  //��Ǽ��ٶȵ�����
	private int vHeight; //ƽ����ĸ߶�
	private int vWidth;//ƽ����Ŀ��
	private float curAngle;//ƽ�������б�Ƕ�
	private float addAngle;//���ӽǶȵ��ٶ�

	public Timer timer;//��ʱ��-��ʱ����Ǽ��ٶ�
	private TimerTask task;
	private boolean isRunning;

	/***
	 * ���캯��
	 * @param context �����Ķ���
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
	 * timer ��ʱ����ǵ���б�Ȳ�ʵʱ����
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
	 * timer ��ȡ��
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
	 * ���¿�ʼ��View������λ�ĺ���
	 */
	public void reset() {
		 curAngle = 0;
		 addAngle = 0;
		 timer_start();
	}
	
	/***
	 * ��ת����
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
	 * ���㵱ǰ����б�ȵĺ���
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

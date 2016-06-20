package com.pig.imageviews;

import com.example.pig.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

/***
 * 支点视图
 * @author 郭招	陈双喜
 *
 */
public class DotV extends ImageView {

	private int vHeight;
	private int vWidth;

	/**
	 * 构造函数
	 * @param context 上下文对象
	 */
	public DotV(Context context) {
		super(context);
		this.setImageResource(R.drawable.zhidian);
		this.setAdjustViewBounds(true);
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = bd.getBitmap();

		vHeight = bitmap.getHeight();
		vWidth = bitmap.getWidth();
		Log.i("Balance", "vHeight" + vHeight + " vWidth" + vWidth);
	}

	public int getvHeight() {
		return vHeight;
	}

	public int getvWidth() {
		return vWidth;
	}

}

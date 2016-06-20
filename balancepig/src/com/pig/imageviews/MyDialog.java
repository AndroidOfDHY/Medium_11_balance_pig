package com.pig.imageviews;

import com.example.pig.BalanceActivity;
import com.example.pig.MainActivity;
import com.example.pig.R;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MyDialog extends Dialog implements OnClickListener {

	private Context context;

	public MyDialog(Context context, int layout) {
		super(context, layout);
		this.context = context;
		setContentView(layout);
		ImageButton btncontinue = (ImageButton) findViewById(R.id.dialog_continue);
		ImageButton btnrestart = (ImageButton) findViewById(R.id.dialog_restart);
		Button btnexit = (Button) findViewById(R.id.dialog_exit);
		btnrestart.setOnClickListener(this);
		btncontinue.setOnClickListener(this);
		btnexit.setOnClickListener(this);

		Window window = getWindow();
		WindowManager.LayoutParams lParams = window.getAttributes();
		lParams.alpha = 0.5f;
		window.setAttributes(lParams);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.playChooseSound();
			((BalanceActivity) context).onResume();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_continue:
			MainActivity.playChooseSound();
			((BalanceActivity) context).onResume();
			this.cancel();
			break;
		case R.id.dialog_restart:
			MainActivity.playChooseSound();
			((BalanceActivity) context).reset();
			this.cancel();
			break;
		case R.id.dialog_exit:
			MainActivity.playChooseSound();
			((BalanceActivity) context).finish();
			break;

		}

	}
}

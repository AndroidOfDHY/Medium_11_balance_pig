package com.example.pig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.gif.GifView;
import com.android.gif.GifView.GifImageType;
import com.example.sound.SoundPlay;

/**
 * 
 * @author 陈双喜
 * @time 2012/10/30
 */
public class MainActivity extends Activity implements OnClickListener {

	/** 选择按钮音效 */
	private static final int ID_SOUND_CHOOSE = 1;
	/** 开关按钮音效 */
	private static final int ID_SOUND_TOGGLE = 2;
	private static final int ID_SOUND_PIG = 3;
	private static final int ID_SOUND_WALLPAPER = 4;
	private static final int ID_SOUND_MONEY = 5;

	private Button start;// 新游戏
	private Button exit;// 退出
	private Button charts;// 排行榜
	private Button select;// 选项
	private Button item;// 道具
	private static RelativeLayout layout;
	public static MediaPlayer bg_player;// 背景音乐
	public static SoundPlay soundPlay;// 背景音效
	// private static int backId = R.drawable.background1;
	/** gif动画 */
	private GifView gifView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layout = (RelativeLayout) findViewById(R.id.main);
		updateBackground(SelectActivity.backId);
		// 播放gif动画
		gifView = (GifView) findViewById(R.id.gif_view);
		gifView.setGifImageType(GifImageType.WAIT_FINISH);
		gifView.setShowDimension(300, 300);
		gifView.setGifImage(R.drawable.zhuzhuxia);

		bg_player = MediaPlayer.create(this, R.raw.bg);// 加载音乐资源
		initSound(this);// 加载音效资源
		start = (Button) findViewById(R.id.start);
		select = (Button) findViewById(R.id.select);
		charts = (Button) findViewById(R.id.charts);
		exit = (Button) findViewById(R.id.exit);
		item = (Button) findViewById(R.id.item);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_rotate);
		start.setAnimation(animation);
		select.setAnimation(animation);
		charts.setAnimation(animation);
		exit.setAnimation(animation);
		item.setAnimation(animation);
		animation.start();

		start.setOnClickListener(this);
		select.setOnClickListener(this);
		charts.setOnClickListener(this);
		exit.setOnClickListener(this);
		item.setOnClickListener(this);

	}

	/**
	 * 初始化音效
	 * 
	 * @param context
	 */
	public static void initSound(Context context) {
		soundPlay = new SoundPlay();
		soundPlay.initSounds(context);
		soundPlay.loadSfx(context, R.raw.buttonclick1, ID_SOUND_TOGGLE);
		soundPlay.loadSfx(context, R.raw.buttonclick2, ID_SOUND_CHOOSE);
		soundPlay.loadSfx(context, R.raw.pig_damage, ID_SOUND_PIG);
		soundPlay.loadSfx(context, R.raw.money_break, ID_SOUND_MONEY);
		soundPlay.loadSfx(context, R.raw.new_wallpaper, ID_SOUND_WALLPAPER);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			playChooseSound();
			exit_game(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			selectBtn(BalanceActivity.class);
			break;
		case R.id.exit:
			playChooseSound();
			exit_game(this);
			break;
		case R.id.select:
			selectBtn(SelectActivity.class);
			break;
		case R.id.charts:
			selectBtn(ChartsActivity.class);
			break;
		case R.id.item:
			selectBtn(ItemActivity.class);
			break;
		}
	}

	/** 开始一个新的Activity */
	public <T> void selectBtn(Class<T> cls) {
		playChooseSound();
		Intent intent = new Intent(MainActivity.this, cls);
		startActivity(intent);
	}

	/** 播放选择按钮音效 */
	public static void playChooseSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_CHOOSE, 0);
		}
	}

	/** 播放开关按钮音效 */
	public static void playToggleSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_TOGGLE, 0);
		}
	}

	/** 播放游戏结束音效 */
	public static void playPigSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_PIG, 0);
		}
	}

	/** 播放更换背景音效 */
	public static void playWallpaperSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_WALLPAPER, 0);
		}
	}

	/** 播放金币音效 */
	public static void playMoneySound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_MONEY, 0);
		}
	}

	public static void playBGMusic(boolean flag) {
		if (flag) {
			bg_player.setLooping(true);// 设置循环播放
			bg_player.start();
		}
	}

	/** 暂停背景音乐 */
	public static void pauseBGMusic() {
		if (bg_player.isPlaying()) {
			bg_player.pause();
		}
	}

	/**
	 * 退出游戏对话框
	 * 
	 * @param context
	 *            上下文
	 * 
	 */
	public void exit_game(Context context) {
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle(R.string.quit)
				.setMessage(R.string.sure_quit)
				.setPositiveButton(R.string.quit,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								MainActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel, null).create();
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		playBGMusic(SelectActivity.btnOn);

	}

	@Override
	protected void onPause() {
		super.onPause();
		pauseBGMusic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bg_player.stop();
		bg_player.release();
	}

	/** 更新背景 */
	public static void updateBackground(int id) {
		layout.setBackgroundResource(id);
	}

}

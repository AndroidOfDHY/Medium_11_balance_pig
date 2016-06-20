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
 * @author ��˫ϲ
 * @time 2012/10/30
 */
public class MainActivity extends Activity implements OnClickListener {

	/** ѡ��ť��Ч */
	private static final int ID_SOUND_CHOOSE = 1;
	/** ���ذ�ť��Ч */
	private static final int ID_SOUND_TOGGLE = 2;
	private static final int ID_SOUND_PIG = 3;
	private static final int ID_SOUND_WALLPAPER = 4;
	private static final int ID_SOUND_MONEY = 5;

	private Button start;// ����Ϸ
	private Button exit;// �˳�
	private Button charts;// ���а�
	private Button select;// ѡ��
	private Button item;// ����
	private static RelativeLayout layout;
	public static MediaPlayer bg_player;// ��������
	public static SoundPlay soundPlay;// ������Ч
	// private static int backId = R.drawable.background1;
	/** gif���� */
	private GifView gifView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layout = (RelativeLayout) findViewById(R.id.main);
		updateBackground(SelectActivity.backId);
		// ����gif����
		gifView = (GifView) findViewById(R.id.gif_view);
		gifView.setGifImageType(GifImageType.WAIT_FINISH);
		gifView.setShowDimension(300, 300);
		gifView.setGifImage(R.drawable.zhuzhuxia);

		bg_player = MediaPlayer.create(this, R.raw.bg);// ����������Դ
		initSound(this);// ������Ч��Դ
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
	 * ��ʼ����Ч
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

	/** ��ʼһ���µ�Activity */
	public <T> void selectBtn(Class<T> cls) {
		playChooseSound();
		Intent intent = new Intent(MainActivity.this, cls);
		startActivity(intent);
	}

	/** ����ѡ��ť��Ч */
	public static void playChooseSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_CHOOSE, 0);
		}
	}

	/** ���ſ��ذ�ť��Ч */
	public static void playToggleSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_TOGGLE, 0);
		}
	}

	/** ������Ϸ������Ч */
	public static void playPigSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_PIG, 0);
		}
	}

	/** ���Ÿ���������Ч */
	public static void playWallpaperSound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_WALLPAPER, 0);
		}
	}

	/** ���Ž����Ч */
	public static void playMoneySound() {
		if (SelectActivity.btnOn2) {
			soundPlay.play(ID_SOUND_MONEY, 0);
		}
	}

	public static void playBGMusic(boolean flag) {
		if (flag) {
			bg_player.setLooping(true);// ����ѭ������
			bg_player.start();
		}
	}

	/** ��ͣ�������� */
	public static void pauseBGMusic() {
		if (bg_player.isPlaying()) {
			bg_player.pause();
		}
	}

	/**
	 * �˳���Ϸ�Ի���
	 * 
	 * @param context
	 *            ������
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

	/** ���±��� */
	public static void updateBackground(int id) {
		layout.setBackgroundResource(id);
	}

}

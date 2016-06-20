package com.example.pig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dputil.Config;
import com.example.dputil.Score;
import com.pig.imageviews.BalanceView;
import com.pig.imageviews.DotV;
import com.pig.imageviews.GoldView;
import com.pig.imageviews.MyDialog;
import com.pig.imageviews.PigView;
import com.pig.imageviews.Wing;

/***
 * 平衡猪的总控制界面
 * 
 * @author 郭招 陈双喜
 * 
 */
@SuppressLint("HandlerLeak")
public class BalanceActivity extends Activity implements SensorEventListener {

	private MediaPlayer back_player;// 背景音乐
	private RelativeLayout layout; // 布局对象
	private BalanceView balanceV; // 平衡棒对象
	private PigView pigV; // 平衡猪对象
	private Wing wing;
	private DotV dotV; // 平衡棒支点
	private TextView scoreT; // 分数文本View
	private TextView scoresT;
	private TextView timeT; // 时间文本View
	private List<GoldView> goldsV; // 金币
	private int balanceLM; // 平衡棒的margeleft
	private int balanceTM; // 平衡棒的margeright

	private SensorManager sensorManager; // 传感器管理机制

	private SharedPreferences spf;
	public Config config;
	private TextView txt_prop_g;
	private TextView txt_prop_time;
	private TextView txt_prop_live;
	private TextView txt_prop_lives;

	private ImageButton ib_prop_g;
	private ImageButton ib_prop_time;
	private ImageButton ib_prop_live;
	private ImageButton ib_prop_lives;

	private ProgressBar progress_g;
	private ProgressBar progress_time;
	private ProgressBar progress_live;
	private ProgressBar progress_lives;

	private boolean cooling_g;
	private boolean cooling_time;
	private boolean cooling_live;
	private boolean cooling_lives;

	public boolean hasWing;
	public boolean gravity;

	private ImageView img_count_back;

	/**
	 * timer:定时产生金币， timer1：金币定时下落
	 */
	private Timer timer, timer1, timer2;
	private TimerTask task, task1, task2;
	private boolean isRunning, isRunning1;

	public float rate = 0.01f; // 更新速率

	private DisplayMetrics dm; // 设备信息管理

	/**
	 * Handler对象，更新UI界面
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0x123:
				pigV.translateAnimX();
				if (hasWing)
					wing.translateAnimX();
				break;
			case 0x124:
				pigV.translateAnimY();
				if (hasWing)
					wing.translateAnimY();
				pigV.updateDir();
				break;
			case 0x125:
				balanceV.rotateAnim();
				break;
			case 0x126:
				for (int i = 0; i < goldsV.size(); i++) {
					goldsV.get(i).setAddY(goldsV.get(i).getAddY() + 0.01f);
					goldsV.get(i).DropAnim();
					if (!goldsV.get(i).isLive()) {
						layout.removeView(goldsV.get(i));
						goldsV.remove(i);
					}
				}
				scoreT.setText(pigV.getScore() + "");
				timeT.setText(pigV.getTime() + "");
				break;
			case 0x127:
				GoldView goldv = createGoldV();
				goldsV.add(goldv);
				break;
			case 0x128:
				pigV.dropDown();
				wing.dropDown();
				if (hasWing) {
					onPause();
					layout.addView(img_count_back);
					final AnimationDrawable anim = (AnimationDrawable) img_count_back
							.getBackground();
					anim.start();
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							balanceV.reset();
							pigV.reLive();
							pigV.setTime(pigV.getTime() + 5);
							hasWing = false;
							timer2_pause();
							anim.stop();
							handler.sendEmptyMessage(0x133);
							handler.sendEmptyMessage(0x138);
							onResume();
						}
					}, 3000);

				} else {
					layout.removeAllViews();
					RelativeLayout.LayoutParams params = new LayoutParams(400,
							400);
					params.topMargin = 200;
					params.leftMargin = 300;

					TextView overT = new TextView(BalanceActivity.this);
					overT.setText("游戏结束");
					overT.setTextSize(50);
					overT.setTextColor(Color.RED);
					layout.addView(overT, params);

					MainActivity.playPigSound();
					Animation animation = AnimationUtils.loadAnimation(
							BalanceActivity.this, R.anim.anim_scale);
					overT.setAnimation(animation);
					animation.start();
					final Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy/MM/dd");
							String date = sdf.format(new Date());
							Score s = new Score(pigV.getScores(), date);
							Bundle data = new Bundle();
							data.putSerializable("score", s);
							Intent intent = new Intent(BalanceActivity.this,
									EndActivity.class);
							intent.putExtras(data);
							config.setGold_count(config.getGold_count()
									+ pigV.getScore());
							config.setConfig(spf);
							startActivity(intent);
							BalanceActivity.this.finish();
						}
					}, 1000);
				}
				break;
			case 0x129:
				MainActivity.playMoneySound();
				GoldView gold = (GoldView) msg.obj;
				if (gold.kind == 0) {
					pigV.setScore(pigV.getScore() + 1);
				} else if (gold.kind == 1) {
					pigV.setScore(pigV.getScore() + 5);
				} else {
					pigV.setScore(pigV.getScore() + 10);
				}
				break;
			case 0x130:
				progress_g.setVisibility(ProgressBar.VISIBLE);
				config.cutItemG();
				txt_prop_g.setText(config.getItemG() + "");
				pigV.setWeight(0);

				(new Timer()).schedule(new TimerTask() {
					public void run() {
						pigV.setWeight(100);
						gravity = false;
					}
				}, 5000);

				(new Timer()).schedule(new TimerTask() {
					public void run() {
						handler.sendEmptyMessage(0x134);
					}
				}, 15000);
				break;
			case 0x131:

				progress_time.setVisibility(ProgressBar.VISIBLE);
				config.cutItemT();
				txt_prop_time.setText(config.getItemT() + "");
				pigV.setTime(pigV.getTime() + 10);
				new Timer().schedule(new TimerTask() {
					public void run() {
						handler.sendEmptyMessage(0x135);
					}
				}, 15000);

				break;
			case 0x132:
				int scores = pigV.getUsetime() / 100 + pigV.getScore() * 10;
				pigV.setScores(scores);
				scoresT.setText(scores + "");
				break;
			case 0x133:
				layout.removeView(img_count_back);
				layout.removeView(wing);
				break;
			case 0x134:
				cooling_g = false;
				progress_g.setVisibility(ProgressBar.GONE);
				break;
			case 0x135:
				cooling_time = false;
				progress_time.setVisibility(ProgressBar.GONE);
				break;
			case 0x136:
				cooling_live = false;
				progress_live.setVisibility(ProgressBar.GONE);
				break;
			case 0x137:
				cooling_lives = false;
				progress_lives.setVisibility(ProgressBar.GONE);
				break;
			case 0x138:
				hasWing = false;
				layout.removeView(wing);
				break;
			}
		}
	};

	/**
	 * Activity 的入口函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.i("Balance", dm.widthPixels + "X" + dm.heightPixels);

		img_count_back = (ImageView) this.findViewById(R.id.img_count);

		scoresT = (TextView) this.findViewById(R.id.scores);

		spf = getSharedPreferences("config", BalanceActivity.MODE_PRIVATE);
		config = new Config();
		config.getConfig(spf);

		txt_prop_g = (TextView) this.findViewById(R.id.num_g);
		txt_prop_time = (TextView) this.findViewById(R.id.num_t);
		txt_prop_live = (TextView) this.findViewById(R.id.num_live);
		txt_prop_lives = (TextView) this.findViewById(R.id.num_lives);
		txt_prop_g.setText(config.getItemG() + "");
		txt_prop_time.setText(config.getItemT() + "");
		txt_prop_live.setText(config.getItemS() + "");
		txt_prop_lives.setText(config.getItemB() + "");

		progress_g = (ProgressBar) this.findViewById(R.id.progress_g);
		progress_time = (ProgressBar) this.findViewById(R.id.progress_time);
		progress_live = (ProgressBar) this.findViewById(R.id.progress_live);
		progress_lives = (ProgressBar) this.findViewById(R.id.progress_lives);

		ib_prop_g = (ImageButton) this.findViewById(R.id.prop_g);
		ib_prop_g.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!cooling_g && config.getItemG() > 0) {
					cooling_g = true;
					gravity = true;
					handler.sendEmptyMessage(0x130);
				} else if (cooling_g) {
					Toast.makeText(BalanceActivity.this, "该道具在冷却中",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BalanceActivity.this, "该道具已用完",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		ib_prop_time = (ImageButton) this.findViewById(R.id.prop_time);
		ib_prop_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!cooling_time && config.getItemT() > 0) {
					cooling_time = true;
					handler.sendEmptyMessage(0x131);
				} else if (cooling_time) {
					Toast.makeText(BalanceActivity.this, "该道具在冷却中",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BalanceActivity.this, "该道具已用完",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		ib_prop_live = (ImageButton) this.findViewById(R.id.prop_live);
		ib_prop_live.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (config.getItemS() > 0 && !cooling_live) {
					if (hasWing == false) {
						cooling_live = true;
						hasWing = true;
						wing.setImageResource(R.drawable.small_wing);
						layout.addView(wing);
						config.cutItemS();
						txt_prop_live.setText(config.getItemS() + "");
						progress_live.setVisibility(ProgressBar.VISIBLE);
						timer2_start();
					} else {
						Toast.makeText(BalanceActivity.this, "小猪已有复活道具",
								Toast.LENGTH_SHORT).show();
					}
				} else if (cooling_live) {
					Toast.makeText(BalanceActivity.this, "该道具在冷却中",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BalanceActivity.this, "该道具已用完",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		ib_prop_lives = (ImageButton) this.findViewById(R.id.prop_lives);
		ib_prop_lives.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (config.getItemB() > 0 && !cooling_lives) {
					if (!hasWing) {
						cooling_lives = true;
						hasWing = true;
						wing.setImageResource(R.drawable.big_wing);
						layout.addView(wing);
						config.cutItemB();
						txt_prop_lives.setText(config.getItemB() + "");
						progress_lives.setVisibility(ProgressBar.VISIBLE);
						new Timer().schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(0x137);
							}
						}, 60000);

						timer2_start();
					} else {
						Toast.makeText(BalanceActivity.this, "小猪已有复活道具",
								Toast.LENGTH_SHORT).show();
					}
				} else if (cooling_lives) {
					Toast.makeText(BalanceActivity.this, "该道具在冷却中",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BalanceActivity.this, "该道具已用完",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		balanceV = new BalanceView(this);
		balanceLM = dm.widthPixels / 2 - balanceV.getvWidth() / 2;
		balanceTM = dm.heightPixels * 2 / 3;

		back_player = MediaPlayer.create(this, R.raw.backgame);// 加载音乐资源
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		scoreT = (TextView) this.findViewById(R.id.score);
		timeT = (TextView) this.findViewById(R.id.time);
		goldsV = new ArrayList<GoldView>();

		layout = (RelativeLayout) this.findViewById(R.id.layout);
		layout.removeView(img_count_back);

		layout.setBackgroundResource(SelectActivity.backId);

		RelativeLayout.LayoutParams paramsB = new LayoutParams(
				balanceV.getvWidth(), balanceV.getvHeight());
		paramsB.leftMargin = BalanceView.leftM = balanceLM;
		paramsB.topMargin = BalanceView.topM = balanceTM;
		balanceV.setCurAngle(0);
		layout.addView(balanceV, paramsB);

		dotV = new DotV(this);
		RelativeLayout.LayoutParams paramsZ = new LayoutParams(
				dotV.getvWidth() / 2, dotV.getvHeight() / 2);
		paramsZ.leftMargin = balanceLM + balanceV.getvWidth() / 2
				- dotV.getvWidth() / 4;
		paramsZ.topMargin = balanceTM + balanceV.getvHeight();
		layout.addView(dotV, paramsZ);

		pigV = new PigView(this);
		RelativeLayout.LayoutParams paramsP = new LayoutParams(
				pigV.getvWidth(), pigV.getvHeight());
		paramsP.leftMargin = PigView.leftM;
		paramsP.topMargin = PigView.topM;
		layout.addView(pigV, paramsP);
		int[] location = new int[2];
		pigV.getLocationOnScreen(location);
		pigV.setCurX(location[0]);
		pigV.setCurY(location[1]);

		wing = new Wing(this);
		RelativeLayout.LayoutParams paramsW = new LayoutParams(
				pigV.getvWidth(), pigV.getvHeight());
		paramsW.leftMargin = PigView.leftM;
		paramsW.topMargin = PigView.topM - pigV.getvHeight();
		wing.setLayoutParams(paramsW);
		;

		Button btn_pause = (Button) findViewById(R.id.btn_pause);
		btn_pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BalanceActivity.this.onPause();
				showDialog();
			}
		});

	}

	/***
	 * 创建金币函数
	 * 
	 * @return 金币对象
	 */

	public GoldView createGoldV() {
		int[] location = new int[2];
		GoldView goldV = new GoldView(this);
		RelativeLayout.LayoutParams paramsBall = new LayoutParams(
				goldV.getvWidth() / 2, goldV.getvHeight() / 2);
		paramsBall.leftMargin = GoldView.leftM;
		paramsBall.topMargin = GoldView.topM;
		layout.addView(goldV, paramsBall);
		goldV.getLocationOnScreen(location);
		goldV.setCurX(location[0]);
		goldV.setCurY(location[1]);
		return goldV;
	}

	/***
	 * timer定时器的开始
	 */
	public void timer_start() {
		if (task == null) {
			task = new TimerTask() {

				@Override
				public void run() {
					if (pigV.isLive()) {
						handler.sendEmptyMessage(0x127);
					}
				}
			};
		}
		if (timer == null) {
			timer = new Timer();
			timer.schedule(task, 2000, 2500);
		}
		isRunning = true;
	}

	/***
	 * timer 定时器结束
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
	 * timer1 定时器开始
	 */
	public void timer1_start() {
		if (task1 == null) {
			task1 = new TimerTask() {

				@Override
				public void run() {
					if (pigV.isLive()) {
						handler.sendEmptyMessage(0x126);
					}
				}
			};
		}
		if (timer1 == null) {
			timer1 = new Timer();
			timer1.schedule(task1, 200, 9);
		}
		isRunning1 = true;
	}

	/**
	 * timer1 定时器结束
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
					handler.sendEmptyMessage(0x138);
				}
			};
		}
		if (timer2 == null) {
			timer2 = new Timer();
			timer2.schedule(task2, 30000);
		}
	}

	public void timer2_pause() {

		if (task2 != null) {
			task2.cancel();
			task2 = null;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SelectActivity.btnOn) {
			back_player.setLooping(true);// 设置循环播放
			back_player.start();
		}
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_GAME);

		timer_start();
		timer1_start();
		balanceV.timer_start();
		pigV.resume();
	}

	/***
	 * 重新开始复位函数
	 */
	public void reset() {
		pigV.reset();
		balanceV.reset();
		for (int i = 0; i < goldsV.size(); i++) {
			layout.removeView(goldsV.get(i));
		}
		goldsV.clear();
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_GAME);
		timer1_start();
		timer_start();
		if (SelectActivity.btnOn) {
			back_player.setLooping(true);// 设置循环播放
			back_player.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (back_player.isPlaying()) {
			back_player.pause();
		}
		sensorManager.unregisterListener(this);
		handler.removeMessages(0x127);
		timer_pause();
		timer1_pause();
		balanceV.timer_pause();
		MainActivity.pauseBGMusic();
		pigV.pause();
	}

	/***
	 * 开启暂停界面
	 */
	public void showDialog() {
		MyDialog myDialog = new MyDialog(this, R.layout.my_dialog);
		myDialog.setCanceledOnTouchOutside(false);
		config.setConfig(spf);
		myDialog.show();
	}

	/***
	 * 按键时间处理函数
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.playChooseSound();
			BalanceActivity.this.onPause();
			showDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer_pause();
		timer1_pause();
		balanceV.timer_pause();
		pigV.timer_pause();
		pigV.timer1_pause();
		// config.setGold_count(config.getGold_count() + pigV.getScore());
		// config.setConfig(spf);
		back_player.stop();
		back_player.release();

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	/***
	 * 传感器检测回调函数
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		float[] values = event.values;
		float y = values[1];
		if (pigV.isLive()) {
			if (y > 0.7) {
				pigV.setPower(50);
				handler.sendEmptyMessage(0x123);
			} else if (y < -0.7) {
				pigV.setPower(-50);
				handler.sendEmptyMessage(0x123);
			} else {
				pigV.setPower(0);
				handler.sendEmptyMessage(0x123);
			}
		}
	}

	public int getBalanceLM() {
		return balanceLM;
	}

	public int getBalanceTM() {
		return balanceTM;
	}

	public BalanceView getBalanceV() {
		return balanceV;
	}

	public PigView getPigV() {
		return pigV;
	}

	public TextView getScoreT() {
		return scoreT;
	}

	public Handler getHandler() {
		return handler;
	}

}

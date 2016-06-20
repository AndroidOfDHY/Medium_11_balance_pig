package com.example.pig;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gif.GifView;
import com.android.gif.GifView.GifImageType;
import com.example.dputil.ComparatorScore;
import com.example.dputil.Score;
import com.example.dputil.ScoreService;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.TencentOpenAPI2;
import com.tencent.tauth.TencentOpenHost;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;

public class EndActivity extends Activity implements OnClickListener {

	private GifView gifView;
	private RelativeLayout layout;
	private Button qq_share;
	public String mAppid = "100341979";// 申请时分配的appid
	private AuthReceiver receiver;
	public static final int PROGRESS = 0;
	public String mAccessToken, mOpenId;
	private int s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);
		layout = (RelativeLayout) findViewById(R.id.end_layout);
		layout.setBackgroundResource(SelectActivity.backId);
		WebView.enablePlatformNotifications();
		registerIntentReceivers();
		// 播放gif动画
		gifView = (GifView) findViewById(R.id.gif_view);
		gifView.setGifImageType(GifImageType.WAIT_FINISH);
		gifView.setShowDimension(300, 300);
		gifView.setGifImage(R.drawable.zhuzhuxia);

		TextView txt_refresh = (TextView) findViewById(R.id.txt_refresh);
		Button again_end = (Button) findViewById(R.id.again_end);
		Button exit_end = (Button) findViewById(R.id.exit_end);
		Button charts_end = (Button) findViewById(R.id.charts_end);
		qq_share = (Button) findViewById(R.id.qq_share);
		TextView txt_score = (TextView) findViewById(R.id.txt_score);

		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_rotate);
		exit_end.setAnimation(animation);
		again_end.setAnimation(animation);
		charts_end.setAnimation(animation);
		animation.start();

		again_end.setOnClickListener(this);
		exit_end.setOnClickListener(this);
		charts_end.setOnClickListener(this);
		qq_share.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		Score score = (Score) data.getSerializable("score");
		s = score.getScore();
		txt_score.setText(s + "");// 设置得分
		ScoreService service = new ScoreService(this);

		List<Score> list = service.readFromDB();
		if (null != list && !list.isEmpty()) {
			if (score.getScore() > list.get(0).getScore()) {
				txt_refresh.setVisibility(View.VISIBLE);
			}
		}
		int len = list.size();
		for (int i = 0; i < len; i++) {
			service.deleteFromDB(list.get(i).get_id());// 删除数据库中的记录
		}
		list.add(score);
		ComparatorScore comparator = new ComparatorScore();
		Collections.sort(list, comparator);// 排序
		if (len < 10) {
			for (int i = 0; i < len + 1; i++) {
				service.insertToDB(list.get(i));// 添加数据库中的记录
			}
		}
		if (len == 10) {
			for (int i = 0; i < len; i++) {
				service.insertToDB(list.get(i));
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.again_end:
			selectBtn(BalanceActivity.class);
			finish();
			break;
		case R.id.exit_end:
			MainActivity.playChooseSound();
			finish();
			break;
		case R.id.charts_end:
			selectBtn(ChartsActivity.class);
			break;
		case R.id.qq_share:
			Bundle bundle = null;
			bundle = new Bundle();
			bundle.putString("title", "平衡猪游戏");// 必须。feeds的标题，最长36个中文字，超出部分会被截断。
			bundle.putString(
					"url",
					"http://apps.ztems.com/newdetails.html?appCode=402825aa3abf32c1013ada64bd7c0720&curnavistop=ITNAV2_P8a885ffc2b46d1f6012b46dbb3880007"
							+ "#" + System.currentTimeMillis());// 必须。分享所在网页资源的链接，点击后跳转至第三方网页，
			// 请以http://开头。
			bundle.putString("comment",
					("我在玩一款平衡猪游戏，秀一下我的得分：  " + s + "分   求超越！"));// 用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
			bundle.putString(
					"summary",
					"平衡猪：一款很有意思的益智游戏，Q版很卡哇伊的游戏画面，玩家需要通过重力感应来控制猪的移动，并且在移动中会导致平衡板的上下摆动，玩家可以通过移动小猪来接取金币，注意不要为了金币掉下平衡板哦~");// 所分享的网页资源的摘要内容，或者是网页的概要描述。
			// 最长80个中文字，超出部分会被截断。
			bundle.putString(
					"images",
					"http://b121.photo.store.qq.com/psb?/626444962/4LXUO85usI9dbHhB3Q2MnKwtYmIkE1fCuwnIv.PnWMg!/b/dCfbJkhnHwAA&bo=YABgAAAAAAADACU!");// 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
			bundle.putString("type", "4");// 分享内容的类型。
			bundle.putString(
					"playurl",
					"http://apps.ztems.com/newdetails.html?appCode=402825aa3abf32c1013ada64bd7c0720&curnavistop=ITNAV2_P8a885ffc2b46d1f6012b46dbb3880007");// 长度限制为256字节。仅在type=5的时候有效。
			TencentOpenAPI2.sendStore(EndActivity.this, mAccessToken, mAppid,
					mOpenId, "_self", bundle, new Callback() {

						@Override
						public void onSuccess(final Object obj) {
							EndActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									EndActivity.this.dismissDialog(PROGRESS);
									// mActivity.showMessage("分享到QQ空间",
									// "share_id: " + obj.toString());
									Toast toast = Toast.makeText(
											EndActivity.this, "成功分享到QQ空间！",
											Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							});
						}

						@Override
						public void onFail(final int ret, final String msg) {
							EndActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// 如果要加进度条，
									// addshareview只好关闭的时候广播下，看下需要不需要
									// mActivity.dismissDialog(TAuthDemoActivity.PROGRESS);
									// TDebug.msg(ret + ": " + msg,
									// mActivity);
									Toast toast = Toast.makeText(
											EndActivity.this, "分享失败，错误信息:"
													+ ret + ", " + msg,
											Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							});
						}

						@Override
						public void onCancel(int flag) {
							// TODO Auto-generated method stub

						}
					}, null);

			break;

		}

	}

	/** 开始一个新的Activity */
	public <T> void selectBtn(Class<T> cls) {
		MainActivity.playChooseSound();
		Intent intent = new Intent(EndActivity.this, cls);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.playBGMusic(SelectActivity.btnOn);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MainActivity.pauseBGMusic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterIntentReceivers();
		}
	}

	private void registerIntentReceivers() {
		receiver = new AuthReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TencentOpenHost.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}

	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}

	/**
	 * 广播的侦听，授权完成后的回调是以广播的形式将结果返回
	 * 
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	public class AuthReceiver extends BroadcastReceiver {

		private static final String TAG = "AuthReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle exts = intent.getExtras();
			String raw = exts.getString("raw");
			String access_token = exts.getString(TencentOpenHost.ACCESS_TOKEN);
			String expires_in = exts.getString(TencentOpenHost.EXPIRES_IN);
			Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s",
					raw, access_token, expires_in));

			if (access_token != null) {
				mAccessToken = access_token;

				// TDebug.msg("正在获取OpenID...", getApplicationContext());
				if (!isFinishing()) {
					showDialog(PROGRESS);
				}

				// 用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {

					public void onCancel(int flag) {

					}

					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissDialog(PROGRESS);
								// setOpenIdText(((OpenId) obj).getOpenId());
							}
						});
					}

					@Override
					public void onFail(int ret, final String msg) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissDialog(PROGRESS);
								TDebug.msg(msg, getApplicationContext());
							}
						});
					}
				});
			}

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setMessage("请求中,请稍等...");
			break;
		}

		return dialog;
	}

}

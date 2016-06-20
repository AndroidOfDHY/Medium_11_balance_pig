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
	public String mAppid = "100341979";// ����ʱ�����appid
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
		// ����gif����
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
		txt_score.setText(s + "");// ���õ÷�
		ScoreService service = new ScoreService(this);

		List<Score> list = service.readFromDB();
		if (null != list && !list.isEmpty()) {
			if (score.getScore() > list.get(0).getScore()) {
				txt_refresh.setVisibility(View.VISIBLE);
			}
		}
		int len = list.size();
		for (int i = 0; i < len; i++) {
			service.deleteFromDB(list.get(i).get_id());// ɾ�����ݿ��еļ�¼
		}
		list.add(score);
		ComparatorScore comparator = new ComparatorScore();
		Collections.sort(list, comparator);// ����
		if (len < 10) {
			for (int i = 0; i < len + 1; i++) {
				service.insertToDB(list.get(i));// ������ݿ��еļ�¼
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
			bundle.putString("title", "ƽ������Ϸ");// ���롣feeds�ı��⣬�36�������֣��������ֻᱻ�ضϡ�
			bundle.putString(
					"url",
					"http://apps.ztems.com/newdetails.html?appCode=402825aa3abf32c1013ada64bd7c0720&curnavistop=ITNAV2_P8a885ffc2b46d1f6012b46dbb3880007"
							+ "#" + System.currentTimeMillis());// ���롣����������ҳ��Դ�����ӣ��������ת����������ҳ��
			// ����http://��ͷ��
			bundle.putString("comment",
					("������һ��ƽ������Ϸ����һ���ҵĵ÷֣�  " + s + "��   ��Խ��"));// �û��������ݣ�Ҳ�з������ʱ�ķ������ɡ���ֹʹ��ϵͳ�����������д��档�40�������֣��������ֻᱻ�ضϡ�
			bundle.putString(
					"summary",
					"ƽ����һ�������˼��������Ϸ��Q��ܿ���������Ϸ���棬�����Ҫͨ��������Ӧ����������ƶ����������ƶ��лᵼ��ƽ�������°ڶ�����ҿ���ͨ���ƶ�С������ȡ��ң�ע�ⲻҪΪ�˽�ҵ���ƽ���Ŷ~");// ���������ҳ��Դ��ժҪ���ݣ���������ҳ�ĸ�Ҫ������
			// �80�������֣��������ֻᱻ�ضϡ�
			bundle.putString(
					"images",
					"http://b121.photo.store.qq.com/psb?/626444962/4LXUO85usI9dbHhB3Q2MnKwtYmIkE1fCuwnIv.PnWMg!/b/dCfbJkhnHwAA&bo=YABgAAAAAAADACU!");// ���������ҳ��Դ�Ĵ�����ͼƬ����"������http://��ͷ����������255�ַ�������ͼƬ�����ߣ�|���ָ���Ŀǰֻ�е�һ��ͼƬ��Ч��ͼƬ���100*100Ϊ�ѡ�
			bundle.putString("type", "4");// �������ݵ����͡�
			bundle.putString(
					"playurl",
					"http://apps.ztems.com/newdetails.html?appCode=402825aa3abf32c1013ada64bd7c0720&curnavistop=ITNAV2_P8a885ffc2b46d1f6012b46dbb3880007");// ��������Ϊ256�ֽڡ�����type=5��ʱ����Ч��
			TencentOpenAPI2.sendStore(EndActivity.this, mAccessToken, mAppid,
					mOpenId, "_self", bundle, new Callback() {

						@Override
						public void onSuccess(final Object obj) {
							EndActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									EndActivity.this.dismissDialog(PROGRESS);
									// mActivity.showMessage("����QQ�ռ�",
									// "share_id: " + obj.toString());
									Toast toast = Toast.makeText(
											EndActivity.this, "�ɹ�����QQ�ռ䣡",
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
									// ���Ҫ�ӽ�������
									// addshareviewֻ�ùرյ�ʱ��㲥�£�������Ҫ����Ҫ
									// mActivity.dismissDialog(TAuthDemoActivity.PROGRESS);
									// TDebug.msg(ret + ": " + msg,
									// mActivity);
									Toast toast = Toast.makeText(
											EndActivity.this, "����ʧ�ܣ�������Ϣ:"
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

	/** ��ʼһ���µ�Activity */
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
	 * �㲥����������Ȩ��ɺ�Ļص����Թ㲥����ʽ���������
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

				// TDebug.msg("���ڻ�ȡOpenID...", getApplicationContext());
				if (!isFinishing()) {
					showDialog(PROGRESS);
				}

				// ��access token ����ȡopen id
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
			((ProgressDialog) dialog).setMessage("������,���Ե�...");
			break;
		}

		return dialog;
	}

}

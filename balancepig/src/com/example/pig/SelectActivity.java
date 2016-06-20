package com.example.pig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dputil.Config;

public class SelectActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	private int[] backIds;
	private String[] names;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> listitems;
	private ListView listview;
	private int selectId;

	public static boolean btnOn = true;
	public static boolean btnOn2 = true;
	public static int backId = R.drawable.background1;
	private ToggleButton btntoggle;
	private ToggleButton btntoggle2;
	private Button exit_select;
	private Button btn_select;
	private Config config;
	private SharedPreferences spf;
	private boolean[] bg;

	private Animation anim_scale;
	private Animation anim_rotate;

	public static RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);

		layout = (RelativeLayout) this.findViewById(R.id.select_layout);
		layout.setBackgroundResource(backId);

		btn_select = (Button) this.findViewById(R.id.select);
		exit_select = (Button) findViewById(R.id.exit_select);
		btntoggle = (ToggleButton) findViewById(R.id.btntoggle);
		btntoggle2 = (ToggleButton) findViewById(R.id.btntoggle2);
		btntoggle.setChecked(!btnOn);
		btntoggle2.setChecked(!btnOn2);

		btntoggle.setOnCheckedChangeListener(this);
		btntoggle2.setOnCheckedChangeListener(this);

		anim_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
		anim_rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
		btn_select.setAnimation(anim_scale);
		exit_select.setAnimation(anim_rotate);
		anim_scale.start();

		anim_rotate.start();
		btn_select.setOnClickListener(this);
		exit_select.setOnClickListener(this);

		spf = getSharedPreferences("config", Context.MODE_PRIVATE);// 使用SharedPreferences来保存参数文件
		config = new Config();
		config.getConfig(spf);
		bg = config.getBg();

		int[] ids = new int[] { R.drawable.background1, R.drawable.back0,
				R.drawable.back1, R.drawable.back2, R.drawable.back3,
				R.drawable.back4, R.drawable.back5, R.drawable.back6,
				R.drawable.back7, R.drawable.back8, R.drawable.back9 };
		String[] contents = getResources().getStringArray(R.array.contents);
		backIds = new int[11];
		names = new String[11];
		backIds[0] = ids[0];
		names[0] = "阳光海滩";
		for (int i = 1; i <= 10; i++) {
			if (bg[i - 1]) {
				backIds[i] = ids[i];
				names[i] = contents[i + 5];
			} else {
				backIds[i] = R.drawable.locked;
				names[i] = "未解锁";
			}

		}

		listitems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < names.length; i++) {
			Map<String, Object> listitem = new HashMap<String, Object>();
			listitem.put("img", backIds[i]);
			listitem.put("name", names[i]);
			listitems.add(listitem);
		}

		adapter = new SimpleAdapter(this, listitems, R.layout.back_list,
				new String[] { "img", "name" }, new int[] { R.id.back,
						R.id.name });

		listview = (ListView) this.findViewById(R.id.mylist);
		listview.setAdapter(adapter);

		listview.setBackgroundColor(Color.DKGRAY);
		listview.setSelector(R.drawable.blue);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				btn_select.setAnimation(anim_scale);
				anim_scale.start();
				selectId = position;
			}

		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select:
			if (selectId != 0) {
				if (bg[selectId - 1]) {
					MainActivity.playWallpaperSound();
					backId = backIds[selectId];
					MainActivity.updateBackground(backId);
					layout.setBackgroundResource(backId);
				} else {
					Toast.makeText(SelectActivity.this, "未解锁背景不能使用！！",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				MainActivity.playWallpaperSound();
				backId = backIds[selectId];
				MainActivity.updateBackground(backId);
				layout.setBackgroundResource(backId);
			}

			break;

		case R.id.exit_select:
			MainActivity.playChooseSound();
			SelectActivity.this.finish();
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MainActivity.playToggleSound();
		switch (buttonView.getId()) {
		case R.id.btntoggle:
			if (isChecked) {
				MainActivity.pauseBGMusic();
				btnOn = false;
			} else {
				MainActivity.playBGMusic(true);
				btnOn = true;
			}
			break;
		case R.id.btntoggle2:
			if (isChecked) {
				btnOn2 = false;
			} else {
				btnOn2 = true;
			}
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.playBGMusic(btnOn);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MainActivity.pauseBGMusic();
	}

}

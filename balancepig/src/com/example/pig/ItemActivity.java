package com.example.pig;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dputil.Config;

public class ItemActivity extends Activity {

	private TextView text_gold;
	private ListView list_item;
	private Button btn_exit;
	private RelativeLayout layout;
	private SharedPreferences spf;
	private Config config;
	private int[] imgs;
	private String[] contents;
	private String[] counts;
	private String[] moneys;
	private ArrayList<Map<String, Object>> listitems;
	private SimpleAdapter adapter;

	private int itemT;
	private int itemG;
	private int itemS;
	private int itemB;
	private int gold_count;
	private boolean[] bg;
	private boolean red_gold;
	private boolean blue_gold;
	private int m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		layout = (RelativeLayout) findViewById(R.id.item_layout);
		layout.setBackgroundResource(SelectActivity.backId);

		text_gold = (TextView) findViewById(R.id.text_gold);
		btn_exit = (Button) findViewById(R.id.exit_item);

		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_rotate);
		btn_exit.setAnimation(animation);
		animation.start();

		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.playChooseSound();
				ItemActivity.this.finish();
			}
		});

		spf = getSharedPreferences("config", Context.MODE_PRIVATE);// 使用SharedPreferences来保存参数文件
		config = new Config();
		config.getConfig(spf);
		itemT = config.getItemT();
		itemG = config.getItemG();
		itemS = config.getItemS();
		itemB = config.getItemB();
		gold_count = config.getGold_count();
		bg = config.getBg();
		red_gold = config.isRed_gold();
		blue_gold = config.isBlue_gold();

		text_gold.setText(gold_count + "");

		imgs = new int[] { R.drawable.add_time, R.drawable.stop,
				R.drawable.small_wing, R.drawable.big_wing, R.drawable.goldx5,
				R.drawable.goldx10, R.drawable.back0, R.drawable.back1,
				R.drawable.back2, R.drawable.back3, R.drawable.back4,
				R.drawable.back5, R.drawable.back6, R.drawable.back7,
				R.drawable.back8, R.drawable.back9 };

		contents = getResources().getStringArray(R.array.contents);
		counts = new String[16];
		counts[0] = "" + itemT;
		counts[1] = "" + itemG;
		counts[2] = "" + itemS;
		counts[3] = "" + itemB;
		if (red_gold) {
			counts[4] = "已解锁";
		} else {
			counts[4] = "未解锁";
		}
		if (blue_gold) {
			counts[5] = "已解锁";
		} else {
			counts[5] = "未解锁";
		}
		for (int i = 0; i < 10; i++) {
			if (bg[i]) {
				counts[i + 6] = "已解锁";
			} else {
				counts[i + 6] = "未解锁";
			}
		}

		moneys = getResources().getStringArray(R.array.moneys);

		listitems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 16; i++) {
			Map<String, Object> listitem = new HashMap<String, Object>();
			listitem.put("item_img", imgs[i]);
			listitem.put("item_content", contents[i]);
			listitem.put("item_count", counts[i]);
			listitem.put("item_money", moneys[i]);

			listitems.add(i, listitem);
		}

		adapter = new SimpleAdapter(this, listitems, R.layout.item_list,
				new String[] { "item_img", "item_content", "item_count",
						"item_money" }, new int[] { R.id.item_img,
						R.id.item_content, R.id.item_count, R.id.item_money });

		list_item = (ListView) this.findViewById(R.id.list_item);
		list_item.setAdapter(adapter);

		list_item.setBackgroundColor(Color.DKGRAY);
		list_item.setSelector(R.drawable.blue);
		list_item.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				m = Integer.parseInt(moneys[position]);
				if (gold_count >= m) {
					if (position > 5) {
						if (bg[position - 6]) {
							Toast.makeText(ItemActivity.this,
									R.string.unlocked, Toast.LENGTH_SHORT)
									.show();
						} else {
							bg[position - 6] = true;
							gold_count -= m;
							counts[position] = "已解锁";
						}
					} else {
						changeCount(position, view);
					}

					text_gold.setText(gold_count + "");

				} else {
					Toast.makeText(ItemActivity.this, R.string.no_gold,
							Toast.LENGTH_SHORT).show();
				}

				listitems.remove(position);
				Map<String, Object> listitem = new HashMap<String, Object>();
				listitem.put("item_img", imgs[position]);
				listitem.put("item_content", contents[position]);
				listitem.put("item_count", counts[position]);
				listitem.put("item_money", moneys[position]);
				listitems.add(position, listitem);
				adapter.notifyDataSetChanged();

			}

		});
	}

	public void changeCount(int position, View view) {
		switch (position) {
		case 0:
			itemT++;
			gold_count -= m;
			counts[position] = "" + itemT;
			break;
		case 1:
			itemG++;
			gold_count -= m;
			counts[position] = "" + itemG;
			break;
		case 2:
			itemS++;
			gold_count -= m;
			counts[position] = "" + itemS;
			break;
		case 3:
			itemB++;
			gold_count -= m;
			counts[position] = "" + itemB;
			break;
		case 4:
			if (red_gold) {
				Toast.makeText(ItemActivity.this, R.string.unlocked,
						Toast.LENGTH_SHORT).show();
			} else {
				red_gold = true;
				gold_count -= m;
				counts[position] = "已解锁";
			}
			break;
		case 5:
			if (blue_gold) {
				Toast.makeText(ItemActivity.this, R.string.unlocked,
						Toast.LENGTH_SHORT).show();
			} else {
				blue_gold = true;
				gold_count -= m;
				counts[position] = "已解锁";
			}
			break;
		}
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
		config.setItemT(itemT);
		config.setItemG(itemG);
		config.setItemS(itemS);
		config.setItemB(itemB);
		config.setGold_count(gold_count);
		config.setBg(bg);
		config.setRed_gold(red_gold);
		config.setBlue_gold(blue_gold);
		config.setConfig(spf);

	}

}

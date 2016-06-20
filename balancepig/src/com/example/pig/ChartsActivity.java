package com.example.pig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.dputil.Score;
import com.example.dputil.ScoreService;

/**
 * 排行榜
 * 
 */
public class ChartsActivity extends Activity {

	private Button exit_charts;
	private ListView listView;
	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charts);
		layout = (RelativeLayout) findViewById(R.id.charts_layout);
		layout.setBackgroundResource(SelectActivity.backId);

		// 从数据库中读取记录，并添加到map里
		ScoreService service = new ScoreService(this);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Score> list_score = service.readFromDB();
		int len = list_score.size();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_id", i + 1);
			if (len != 0) {
				map.put("item_score", list_score.get(i).getScore());
				map.put("item_date", list_score.get(i).getDate());
				len--;
			} else {
				map.put("item_score", "");
				map.put("item_date", "");
			}
			list.add(map);
		}
		// 将map里的数据在listView中显示出来
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.list_view, new String[] { "item_id", "item_score",
						"item_date" }, new int[] { R.id.item_id,
						R.id.item_score, R.id.item_date });

		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(simpleAdapter);

		exit_charts = (Button) findViewById(R.id.exit_charts);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_rotate);
		exit_charts.setAnimation(animation);
		animation.start();
		exit_charts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.playChooseSound();
				ChartsActivity.this.finish();
			}
		});
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
	}

}

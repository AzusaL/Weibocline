package com.AzusaL.weibo;

import java.util.HashMap;
import java.util.Map;

import com.AzusaL.fragment.MainFragment;
import com.AzusaL.fragment.MeFragment;
import com.AzusaL.fragment.NewWeiboActivity;
import com.AzusaL.fragment.NewsFragment;
import com.example.weibo.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private Button main_btn, news_btn, search_btn, me_btn, add_btn;
	private TextView title, main_tv, news_tv, search_tv, me_tv;
	private Map<String, Fragment> map; // 存当前显示页面的fragment对象
	private Fragment fg1, fg2, fg3, fg4;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		initview();
		setlistener();
		initdata();
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == 200) {
//			if (requestCode == 100) {
//				fg1.onResume();
//			}
//		}
//	}

	private void initdata() {
		name = getIntent().getStringExtra("name");
		title.setText(name);
		fg1 = new MainFragment();
		fg2 = new NewsFragment();
		fg3 = new SearchFragment();
		fg4 = new MeFragment();

		Bundle bundle1 = new Bundle();
		bundle1.putString("name", name);
		fg1.setArguments(bundle1);
		map = new HashMap<String, Fragment>();

		// 将首页fragment设为默认打开页面
		getFragmentManager().beginTransaction().add(R.id.main_fragment, fg1).commit();
		setclickcolor(main_btn, main_tv, R.drawable.home_click);
		map.put("curentfragment", fg1);
	}

	private void setlistener() {
		findViewById(R.id.bottom_layout1).setOnClickListener(this);
		findViewById(R.id.bottom_layout2).setOnClickListener(this);
		findViewById(R.id.bottom_layout3).setOnClickListener(this);
		findViewById(R.id.bottom_layout4).setOnClickListener(this);

		add_btn.setOnClickListener(this);
	}

	private void initview() {
		main_btn = (Button) findViewById(R.id.bottom_bt1);
		news_btn = (Button) findViewById(R.id.bottom_bt2);
		search_btn = (Button) findViewById(R.id.bottom_bt3);
		me_btn = (Button) findViewById(R.id.bottom_bt4);
		add_btn = (Button) findViewById(R.id.bottom_add);

		title = (TextView) findViewById(R.id.main_title_tv);
		main_tv = (TextView) findViewById(R.id.bottom_tv1);
		news_tv = (TextView) findViewById(R.id.bottom_tv2);
		search_tv = (TextView) findViewById(R.id.bottom_tv3);
		me_tv = (TextView) findViewById(R.id.bottom_tv4);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bottom_layout1:
			setclickcolor(main_btn, main_tv, R.drawable.home_click);
			switchFragment(map.get("curentfragment"), fg1);
			map.put("curentfragment", fg1);
			break;
		case R.id.bottom_layout2:
			setclickcolor(news_btn, news_tv, R.drawable.news_click);
			switchFragment(map.get("curentfragment"), fg2);
			map.put("curentfragment", fg2);
			break;
		case R.id.bottom_layout3:
			setclickcolor(search_btn, search_tv, R.drawable.search_click);
			switchFragment(map.get("curentfragment"), fg3);
			map.put("curentfragment", fg3);
			break;
		case R.id.bottom_layout4:
			setclickcolor(me_btn, me_tv, R.drawable.me_click);
			switchFragment(map.get("curentfragment"), fg4);
			map.put("curentfragment", fg4);
			break;
		case R.id.bottom_add:
			Intent intent = new Intent(MainActivity.this, NewWeiboActivity.class);
			intent.putExtra("name", name);
			startActivityForResult(intent, 100);
			break;
		}
	}

	public void switchFragment(Fragment from, Fragment to) {
		if (from == null || to == null) {
			return;
		}
		if (from == to) {
			return;
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!to.isAdded()) {
			// 隐藏当前的fragment，add下一个到Activity中
			transaction.hide(from).add(R.id.main_fragment, to).commit();
		} else {
			// 隐藏当前的fragment，显示下一个
			transaction.hide(from).show(to).commit();
		}
	}

	public void setdefaultcolor() {
		main_btn.setBackgroundResource(R.drawable.home);
		news_btn.setBackgroundResource(R.drawable.news);
		search_btn.setBackgroundResource(R.drawable.search);
		me_btn.setBackgroundResource(R.drawable.me);

		main_tv.setTextColor(Color.rgb(162, 160, 160));
		news_tv.setTextColor(Color.rgb(162, 160, 160));
		search_tv.setTextColor(Color.rgb(162, 160, 160));
		me_tv.setTextColor(Color.rgb(162, 160, 160));
	}

	public void setclickcolor(Button btn, TextView tv, int color) {
		setdefaultcolor();
		btn.setBackgroundResource(color);
		tv.setTextColor(Color.rgb(152, 245, 152));
	}
}

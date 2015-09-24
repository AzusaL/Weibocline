package com.AzusaL.weibo;

import java.util.Map;

import com.AzusaL.sharedpreference.Sharedpreference;
import com.example.weibo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class GuidepageActivity extends Activity {

	private Map<String, Object> map;
	private ImageView img;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				img.clearAnimation();
			}
			if (msg.what == 2) {
				map = Sharedpreference.readFromSP(GuidepageActivity.this);
				if ((Boolean) map.get("cbischeck")) {
					Intent intent = new Intent(GuidepageActivity.this, MainActivity.class);
					intent.putExtra("name", map.get("name").toString());
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(GuidepageActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guidepage);
		getActionBar().hide();
		
		// 设置引导页面动画
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.myanim);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		img = (ImageView) findViewById(R.id.guid_img);

		img.startAnimation(animation);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1200);
					handler.sendEmptyMessage(1);
					Thread.sleep(400);
					handler.sendEmptyMessage(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}

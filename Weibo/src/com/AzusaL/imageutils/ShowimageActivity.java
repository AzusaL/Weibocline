package com.AzusaL.imageutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.weibo.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowimageActivity extends Activity {

	private GridView gridView;
	private Button sure_btn, cancel_btn;
	private List<String> list;
	private List<String> list_data;
	private ProgressDialog progressDialog;
	private Myadapter adapter = new Myadapter();
	private Map<Integer, Boolean> selectmap = new HashMap<Integer, Boolean>();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				progressDialog.dismiss();
				gridView.setAdapter(adapter);
				gridView.setOnScrollListener(adapter);
				gridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CheckBox checkBox = (CheckBox) gridView.findViewWithTag(position);
						checkBox.setChecked(checkBox.isChecked() ? false : true);
						if (checkBox.isChecked()) {
							addAnimation(checkBox);
						}
						selectmap.put(position, checkBox.isChecked());
						if (getSelectItems().size() > 0) {
							sure_btn.setBackgroundResource(R.drawable.sign_up_select);
							sure_btn.setClickable(true);
						} else {
							sure_btn.setBackgroundResource(R.drawable.unclickable_shap);
							sure_btn.setClickable(false);
						}
					}
				});
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showimage);
		getActionBar().hide();
		progressDialog = ProgressDialog.show(ShowimageActivity.this, null, "正在加载...");
		initview();
		getimagurl();
		setlistener();
	}

	private void setlistener() {
		sure_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String where = getIntent().getStringExtra("where");
				int count = getIntent().getIntExtra("count", -1);
				List<Integer> img_position_list = getSelectItems();
				if (where.equals("head")) {
					if (1 == img_position_list.size()) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("url", list.get(img_position_list.get(0)));
						intent.putExtras(bundle);
						setResult(727, intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "只能选取" + 1 + "张图片", 1000).show();
					}

				}
				if (where.equals("photo")) {
					if (img_position_list.size() <= count) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						ArrayList<String> imguri = new ArrayList<String>();
						for (int i = 0; i < img_position_list.size(); i++) {
							imguri.add(list.get(img_position_list.get(i)));
						}
						bundle.putStringArrayList("url", imguri);
						intent.putExtras(bundle);
						setResult(727, intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "最多只能选取" + count + "张图片", 1000).show();
					}
				}
			}
		});
		
		sure_btn.setClickable(false);

		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initview() {
		gridView = (GridView) findViewById(R.id.gridview);
		sure_btn = (Button) findViewById(R.id.sure_btn);
		cancel_btn = (Button) findViewById(R.id.cancle_btn);

		sure_btn.setBackgroundResource(R.drawable.unclickable_shap);
		list = new ArrayList<String>();
		list_data = new ArrayList<String>();
	}

	private void getimagurl() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ContentResolver contentResolver = getContentResolver();
				String[] projection = { Images.Media._ID, Images.Media.DATA, Images.Media.TITLE };
				Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
						null, null);
				cursor.moveToLast();
				while (cursor.moveToPrevious()) {
					list.add(cursor.getString(cursor.getColumnIndex(projection[1])));
					list_data.add(cursor.getString(cursor.getColumnIndex(projection[2])));
				}
				cursor.close();
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	public List<Integer> getSelectItems() {
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = selectmap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}
		return list;
	}

	public class Myadapter extends BaseAdapter implements OnScrollListener {

		private int mFirstVisibleItem; // GridView中可见的第一张图片的下标
		private int mVisibleItemCount;// GridView中可见的图片的数量
		private boolean isFirstEnterThisActivity = true;// 记录是否是第一次进入该界面

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.girlview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imgview = (ImageView) convertView.findViewById(R.id.itemImage);
				viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.item_checkbox);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imgview.setTag(list.get(position));
			viewHolder.checkBox.setTag(position);
			if (Setimg.cache.get(list.get(position)) == null) {
				viewHolder.imgview.setImageResource(R.drawable.img_back);
			} else {
				viewHolder.imgview.setImageBitmap(Setimg.cache.get(list.get(position)));
			}

			viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (!selectmap.containsKey(position) || !selectmap.get(position)) {
						addAnimation(viewHolder.checkBox);
					}
					selectmap.put(position, isChecked);
					if (getSelectItems().size() > 0) {
						sure_btn.setBackgroundResource(R.drawable.sign_up_select);
						sure_btn.setClickable(true);
					} else {
						sure_btn.setBackgroundResource(R.drawable.unclickable_shap);
						sure_btn.setClickable(false);
					}
				}
			});
			viewHolder.checkBox.setChecked(selectmap.containsKey(position) ? selectmap.get(position) : false);
			return convertView;
		}

		class ViewHolder {
			ImageView imgview;
			CheckBox checkBox;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == SCROLL_STATE_IDLE) {
				Setimg.stop = true;
				loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
			} else {
				Setimg.stop = false;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
			if (isFirstEnterThisActivity && visibleItemCount > 0) {
				Setimg.stop = true;
				loadBitmaps(firstVisibleItem, visibleItemCount);
				isFirstEnterThisActivity = false;
				Setimg.stop = false;
			}
		}

		private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
			for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
				new Setimg().setimage(list.get(i), (ImageView) gridView.findViewWithTag(list.get(i)));
			}
		}
	}

	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
	 * 
	 * @param view
	 */
	private void addAnimation(View view) {
		float[] vaules = new float[] { 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f,
				1.0f };
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}
}

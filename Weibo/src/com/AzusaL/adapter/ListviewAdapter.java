package com.AzusaL.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.AzusaL.bean.WeiboBean;
import com.example.weibo.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListviewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<WeiboBean> listview_data;

	public ListviewAdapter(Context context, ArrayList<WeiboBean> listview_data) {
		super();
		this.context = context;
		this.listview_data = listview_data;
	}

	@Override
	public int getCount() {
		return listview_data.size();
	}

	@Override
	public Object getItem(int position) {
		return listview_data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.home_listview_item, null);

			holder.lv_item_head_img = (ImageView) convertView.findViewById(R.id.lv_item_headimg);
			holder.lv_item_name_tv = (TextView) convertView.findViewById(R.id.lv_item_name);
			holder.lv_item_time_tv = (TextView) convertView.findViewById(R.id.lv_item_timetv);
			holder.down_btn = (Button) convertView.findViewById(R.id.lv_item_downbtn);

			holder.weibo_content_tv = (TextView) convertView.findViewById(R.id.weibo_content);
			holder.inlude_listview_item_imge = (FrameLayout) convertView.findViewById(R.id.include_listview_item_image);
			holder.gridView = (WrapHeightGridView) holder.inlude_listview_item_imge.findViewById(R.id.lv_gv_images);

			holder.ll_share_bottom = (LinearLayout) convertView.findViewById(R.id.ll_share_bottom);
			holder.ll_comment_bottom = (LinearLayout) convertView.findViewById(R.id.ll_comment_bottom);
			holder.ll_like_bottom = (LinearLayout) convertView.findViewById(R.id.ll_like_bottom);
			holder.frameLayout = (FrameLayout) convertView.findViewById(R.id.include_listview_item_image);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WeiboBean bean = listview_data.get(position);
		holder.lv_item_head_img.setImageBitmap(
				BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Weibo_app_data/AzusaL.jpg"));
		holder.lv_item_name_tv.setText(bean.getName());
		holder.lv_item_time_tv.setText(settime(bean.getTime()));
		holder.weibo_content_tv.setText(bean.getContent());
		if (bean.getPhotourls().size() > 0) {
			holder.frameLayout.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new LvchildGridviewAdapter(context, bean.getPhotourls()));
		} else {
			holder.frameLayout.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView lv_item_head_img;
		public TextView lv_item_name_tv, lv_item_time_tv;
		public Button down_btn;

		public TextView weibo_content_tv;
		public FrameLayout inlude_listview_item_imge;
		public WrapHeightGridView gridView;

		public LinearLayout ll_share_bottom, ll_comment_bottom, ll_like_bottom;
		public FrameLayout frameLayout;
	}

	public String settime(String longtime) {
		if (longtime == null || longtime.equals("")) {
			return "";
		}
		long currenttime = System.currentTimeMillis();
		long servertime = Long.valueOf(longtime);
		long time = currenttime / 1000 - servertime / 1000;
		if (time < 60) {
			return time + "秒前";
		}
		if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		}
		if (time >= 3600 && time < 7200) {
			return time / 3600 + "小时前";
		}
		if (time >= 7200 && time < 86400) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			Date date = new Date(servertime);
			return "今天" + formatter.format(date);
		}
		if (time >= 86400 && time < 31536000) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			Date date = new Date(servertime);
			return formatter.format(date);
		}
		if (time > 31536000) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(servertime);
			return formatter.format(date);
		}
		return "";
	}
}

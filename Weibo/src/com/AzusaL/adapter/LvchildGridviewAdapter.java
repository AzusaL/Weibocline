package com.AzusaL.adapter;

import java.util.ArrayList;

import com.AzusaL.imageutils.Setweiboimg;
import com.example.weibo.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class LvchildGridviewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> photourls;

	public LvchildGridviewAdapter(Context context, ArrayList<String> photourls) {
		super();
		this.context = context;
		this.photourls = photourls;
	}

	@Override
	public int getCount() {
		return photourls.size();
	}

	@Override
	public Object getItem(int position) {
		return photourls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.lv_child_girlview_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.lv_girlview_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GridView gv = (GridView) parent;
		int horizontalSpacing = gv.getHorizontalSpacing();
		int numColumns = gv.getNumColumns();
		int itemWidth = (gv.getWidth() - (numColumns - 1) * horizontalSpacing - gv.getPaddingLeft()
				- gv.getPaddingRight()) / numColumns;

		LayoutParams params = new LayoutParams(itemWidth, itemWidth);
		holder.imageView.setLayoutParams(params);
		if(photourls.size()>0){
			if (Setweiboimg.cache.get(photourls.get(position)) == null) {
				holder.imageView.setImageResource(R.drawable.img_back);
				new Setweiboimg(photourls.get(position), holder.imageView).setimg();
			} else {
				holder.imageView.setImageBitmap(Setweiboimg.cache.get(photourls.get(position)));
			}
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView imageView;
	}

}

package com.AzusaL.adapter;

import java.util.ArrayList;

import com.AzusaL.imageutils.Setimg;
import com.example.weibo.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class NewWeiboGridviewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> photourls;

	public NewWeiboGridviewAdapter(Context context, ArrayList<String> photourls) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.newweibo_girlview_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.newweibo_itemImage);
			holder.btn = (Button) convertView.findViewById(R.id.newweibo_delete);
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
		holder.imageView.setTag(photourls.get(position));
		if (position == 0) {
			holder.btn.setVisibility(View.GONE);
			holder.imageView.setImageResource(R.drawable.add_photo);
		} else {
			holder.btn.setVisibility(View.VISIBLE);
			Setimg.stop = true;
			new Setimg().setimage(photourls.get(position), holder.imageView);
		}
		holder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				photourls.remove(position);
				NewWeiboGridviewAdapter.this.notifyDataSetChanged();
			}
		});

		return convertView;
	}

	class ViewHolder {
		public ImageView imageView;
		public Button btn;
	}

}

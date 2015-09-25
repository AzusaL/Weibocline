package com.AzusaL.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.AzusaL.adapter.ListviewAdapter;
import com.AzusaL.bean.WeiboBean;
import com.AzusaL.database.SqliteOpenhelper;
import com.AzusaL.httputils.GetAllWeibo;
import com.AzusaL.httputils.HttpUtilsforimg;
import com.AzusaL.imageutils.ShowimageActivity;
import com.AzusaL.sharedpreference.Sharedpreference;
import com.example.weibo.R;

import android.support.v4.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	protected static final int GET_LIST_OK = 1;
	private SqliteOpenhelper sql;
	private View view;
	private String name;
	private TextView tv_name, tv_introduction;
	private ImageView head_img;
	private View view_dialog;
	private AlertDialog dialog;
	private static String IMAGE_FILE_LOCATION, CAMERA_IMAGE_FILE_LOCATION;
	private Uri imageUri, cameraimageUri;
	private File imgforSD, cameraimgforSD;
	private ListView lv;
	private ListviewAdapter listviewAdapter;
	public SwipeRefreshLayout mSwipeLayout;
	private ArrayList<WeiboBean> list;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == GET_LIST_OK) {
				mSwipeLayout.setRefreshing(false);
				if (list != null) {
					Sharedpreference.savelisttosp(getActivity(), list);
					listviewAdapter = new ListviewAdapter(getActivity(), list);
					lv.setAdapter(listviewAdapter);
				}
			}
		}
	};;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.main_fragment1, null);
		initview();
		initdata();
		setlistener();
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		dialog.dismiss();
		if (resultCode == 727) {
			if (requestCode == 823) {
				if (data == null) {
					return;
				}
				Bundle bundle = data.getExtras();
				String url = bundle.getString("url");
				cutimg(Uri.fromFile(new File(url)));
			}
		}

		if (requestCode == 23) {
			if (data == null) {
				return;
			}
			if (imageUri != null) {
				Bitmap bitmap = decodeUriAsBitmap(imageUri);
				head_img.setImageBitmap(bitmap);
				sql = new SqliteOpenhelper(getActivity());
				sql.updateuserimgpath(name, imgforSD.getAbsolutePath());
				new HttpUtilsforimg(imgforSD.getAbsolutePath(), name, getActivity()).uploadheadHttpClient();
			}
			if (cameraimgforSD.exists()) {
				cameraimgforSD.delete();
			}
		}

		if (requestCode == 7) {
			if (cameraimageUri != null) {
				cutimg(cameraimageUri);
			} else {
				if (cameraimgforSD.exists()) {
					cameraimgforSD.delete();
				}
			}
		}
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private void cutimg(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, 23);
	}

	private void setlistener() {
		head_img.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				view_dialog = View.inflate(getActivity(), R.layout.dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				dialog = builder.create();
				dialog.show();
				WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
				WindowManager windowManager = getActivity().getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				params.width = (int) (display.getWidth() * 0.7);
				params.height = (int) (display.getHeight() * 0.3);
				dialog.getWindow().setAttributes(params);
				dialog.getWindow().setContentView(view_dialog);
				setdialogbtn();
			}

			private void setdialogbtn() {
				view_dialog.findViewById(R.id.dialog_btn1).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra("return-data", false);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraimageUri);
						startActivityForResult(intent, 7);
					}
				});

				view_dialog.findViewById(R.id.dialog_btn2).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), ShowimageActivity.class);
						intent.putExtra("where", "head");
						startActivityForResult(intent, 823);
					}
				});
			}
		});

	}

	private void initdata() {
		name = getArguments().getString("name", "...");
		tv_name.setText(name);

		File temDir = new File(Environment.getExternalStorageDirectory() + "/Weibo_app_data");
		if (!temDir.exists()) {
			temDir.mkdir();
		}
		IMAGE_FILE_LOCATION = "file:///" + temDir.getAbsolutePath() + "/" + name + ".jpg";
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);

		CAMERA_IMAGE_FILE_LOCATION = "file:///" + temDir.getAbsolutePath() + "/camera" + name + ".jpg";
		cameraimageUri = Uri.parse(CAMERA_IMAGE_FILE_LOCATION);

		imgforSD = new File(temDir.getAbsolutePath() + "/" + name + ".jpg");
		if (imgforSD.exists()) {
			head_img.setImageBitmap(BitmapFactory.decodeFile(temDir.getAbsolutePath() + "/" + name + ".jpg"));
			sql = new SqliteOpenhelper(getActivity());
			if (sql.queryusermessage(name).getHead_img_path() == null) {
				sql.updateuserimgpath(name, imgforSD.getAbsolutePath());
			}
		}
		cameraimgforSD = new File(temDir.getAbsolutePath() + "/camera" + name + ".jpg");

		ArrayList<WeiboBean> splist = Sharedpreference.getlistfromsp(getActivity());
		if(splist!=null){
			listviewAdapter = new ListviewAdapter(getActivity(), splist);
			lv.setAdapter(listviewAdapter);
		}else{
			getlistforserver();
		}
	}

	private void initview() {
		tv_name = (TextView) view.findViewById(R.id.home_username_tv);
		tv_introduction = (TextView) view.findViewById(R.id.home_introduction_tv);
		head_img = (ImageView) view.findViewById(R.id.user_head_img);
		lv = (ListView) view.findViewById(R.id.home_listview);
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

	}

	private void getlistforserver() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				list = GetAllWeibo.getusersweibo(name);
				handler.sendEmptyMessage(GET_LIST_OK);
			}
		}).start();
	}

	@Override
	public void onRefresh() {
		getlistforserver();
	}
}

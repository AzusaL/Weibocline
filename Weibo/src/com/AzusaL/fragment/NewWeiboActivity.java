package com.AzusaL.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.AzusaL.adapter.NewWeiboGridviewAdapter;
import com.AzusaL.bean.WeiboBean;
import com.AzusaL.httputils.AddWeiboToServer;
import com.AzusaL.imageutils.ShowimageActivity;
import com.example.weibo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class NewWeiboActivity extends Activity {

	private String name;
	private TextView name_tv, textcount;
	private EditText content_et;
	private Button cancel_btn, sent_btn;
	private GridView mgridview;
	private NewWeiboGridviewAdapter madapter;
	private ArrayList<String> photouri = new ArrayList<String>();
	private View view_dialog;
	private AlertDialog dialog;
	private Uri cameraimageUri;
	private int text_count;
	private File temDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_weibo);
		getActionBar().hide();

		initView();
		initdata();
		setlistener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dialog.dismiss();
		if (resultCode == 727) {
			if (requestCode == 823) {
				if (data == null) {
					return;
				}
				Bundle bundle = data.getExtras();
				ArrayList<String> imguri = bundle.getStringArrayList("url");
				for (int i = 0; i < imguri.size(); i++) {
					photouri.add(imguri.get(i));
				}
				madapter.notifyDataSetChanged();
			}
		}

		if (requestCode == 7) {
			if (cameraimageUri != null) {
				Bitmap bitmap = decodeUriAsBitmap(cameraimageUri);
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(cameraimageUri.getEncodedPath());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
				photouri.add(cameraimageUri.getEncodedPath());
				madapter.notifyDataSetChanged();
			}
		}
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private void setlistener() {
		mgridview.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					if (photouri.size() == 10) {
						Toast.makeText(getApplicationContext(), "最多只能选取9张图片", 1000).show();
						return;
					}
					view_dialog = View.inflate(NewWeiboActivity.this, R.layout.dialog, null);
					AlertDialog.Builder builder = new AlertDialog.Builder(NewWeiboActivity.this);
					dialog = builder.create();
					dialog.show();
					WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
					WindowManager windowManager = NewWeiboActivity.this.getWindowManager();
					Display display = windowManager.getDefaultDisplay();
					params.width = (int) (display.getWidth() * 0.7);
					params.height = (int) (display.getHeight() * 0.3);
					dialog.getWindow().setAttributes(params);
					dialog.getWindow().setContentView(view_dialog);
					setdialogbtn();
				}
			}

			private void setdialogbtn() {
				// 调用相机
				view_dialog.findViewById(R.id.dialog_btn1).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						cameraimageUri = Uri.parse("file:///" + temDir.getAbsolutePath() + "/camera"
								+ String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpg");

						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra("return-data", false);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraimageUri);
						startActivityForResult(intent, 7);
					}
				});

				// 调用相册
				view_dialog.findViewById(R.id.dialog_btn2).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(NewWeiboActivity.this, ShowimageActivity.class);
						intent.putExtra("count", 10 - photouri.size());
						intent.putExtra("where", "photo");
						startActivityForResult(intent, 823);
					}
				});
			}
		});

		// 监听文本输入
		content_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = content_et.getText().toString();
				text_count = content.length();
				if (text_count > 300) {
					textcount.setVisibility(View.VISIBLE);
					textcount.setText("" + (300 - text_count));
				} else {
					textcount.setVisibility(View.GONE);
				}

				if (!content.equals("")) {
					sent_btn.setBackgroundResource(R.drawable.sentweibo_btn_select);
					sent_btn.setClickable(true);
				} else {
					sent_btn.setBackgroundColor(Color.rgb(218, 251, 218));
					sent_btn.setClickable(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 取消按钮
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 发送按钮
		sent_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (text_count > 300) {
					Toast.makeText(getApplicationContext(), "超过字数限制", 1000).show();
					return;
				}
				String content = content_et.getText().toString();
				WeiboBean bean = new WeiboBean(name, String.valueOf(System.currentTimeMillis()), content, photouri);

				new AddWeiboToServer(bean, getApplicationContext()).addWeibo();
				setResult(200);
				finish();
			}
		});

		sent_btn.setClickable(false);
	}

	private void initdata() {
		temDir = new File(Environment.getExternalStorageDirectory() + "/Weibo_app_data/weibocacsh");
		if (!temDir.exists()) {
			temDir.mkdir();
		}

		photouri.add("");
		madapter = new NewWeiboGridviewAdapter(getApplicationContext(), photouri);
		mgridview.setAdapter(madapter);
	}

	private void initView() {
		name_tv = (TextView) findViewById(R.id.new_name_tv);
		content_et = (EditText) findViewById(R.id.newweibo_tv);
		cancel_btn = (Button) findViewById(R.id.new_back_btn);
		sent_btn = (Button) findViewById(R.id.new_weibo_sent_btn);
		mgridview = (GridView) findViewById(R.id.new_gridview);
		textcount = (TextView) findViewById(R.id.textcount);

		name = getIntent().getStringExtra("name");
		name_tv.setText(name);
		sent_btn.setBackgroundColor(Color.rgb(218, 251, 218));
		textcount.setVisibility(View.GONE);
	}
}

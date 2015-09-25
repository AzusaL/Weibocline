package com.AzusaL.weibo;

import java.io.File;
import java.util.Map;

import com.AzusaL.bean.UserBean;
import com.AzusaL.database.SqliteOpenhelper;
import com.AzusaL.httputils.HttpUtils;
import com.AzusaL.httputils.JsonUtils;
import com.AzusaL.sharedpreference.Sharedpreference;
import com.example.weibo.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	private static final String login_url = "http://azusal.tunnel.mobi/Weibo/servlet/UsersLogin";
	private static final int REGISTER_ATIVITY_RESULTCOE = 8;
	private static final int REQUESTCODE = 123;
	private SqliteOpenhelper sql = new SqliteOpenhelper(this);
	private EditText et1_name, et2_psw;
	private Button sign_btn, register_btn;
	private ImageView head_img;
	private CheckBox mcheckbox;
	private ProgressBar pb;
	private Map<String, Object> map;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			pb.setVisibility(View.GONE);
			if (msg.obj.equals("login success")) {
				String name = msg.getData().getString("name");
				String password = msg.getData().getString("password");
				if (mcheckbox.isChecked()) {
					Sharedpreference.savelogintoSp(getApplicationContext(), name, password, true);
					if (!sql.hasname(name)) {
						sql.insertusermessage(new UserBean(name, password));
					}
				} else {
					Sharedpreference.savelogintoSp(getApplicationContext(), name, "", false);
					if (!sql.hasname(name)) {
						sql.insertusermessage(new UserBean(name, ""));
					}
				}

				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra("name", msg.getData().getString("name"));
				startActivity(intent);
				finish();
			} else if (msg.obj.equals("user no be found")) {
				Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
			} else if (msg.obj.equals("password error")) {
				Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
			} else if (msg.obj.equals("server error")) {
				Toast.makeText(LoginActivity.this, "网络连接出错", Toast.LENGTH_SHORT).show();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().hide();

		initview();
		setlisener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE) {
			if (resultCode == REGISTER_ATIVITY_RESULTCOE) {
				Bundle bundle = data.getExtras();
				et1_name.setText(bundle.getString("name"));
				et2_psw.setText("");
				mcheckbox.setChecked(false);
				head_img.setImageResource(R.drawable.default_head_);
			}
		}
	}

	private void setlisener() {
		sign_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = et1_name.getText().toString();
				String password = et2_psw.getText().toString();
				if (!TextUtils.isEmpty(name.replace(" ", "")) && !TextUtils.isEmpty(password.replace(" ", ""))) {
					jdugebyserver(name, password);
					pb.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});

		register_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(intent, REQUESTCODE);
			}
		});

		et1_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = et1_name.getText().toString();
				if (sql.hasname(name)) {
					String path = sql.queryusermessage(name).getHead_img_path();
					if (path != null) {
						File file = new File(path);
						if (file.exists()) {
							head_img.setImageBitmap(BitmapFactory.decodeFile(path));
						} else {
							head_img.setImageResource(R.drawable.default_head_);
						}
					} else {
						head_img.setImageResource(R.drawable.default_head_);
					}
				} else {
					head_img.setImageResource(R.drawable.default_head_);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void jdugebyserver(final String name, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = HttpUtils.getresult(JsonUtils.getjsonobject(name, password), login_url);
				Message message = Message.obtain();
				message.obj = result;
				Bundle bundle = new Bundle();
				bundle.putString("name", name);
				bundle.putString("password", password);
				message.setData(bundle);
				handler.sendMessage(message);
			}
		}).start();
	}

	private void initview() {
		et1_name = (EditText) findViewById(R.id.login_et1);
		et2_psw = (EditText) findViewById(R.id.login_et2);
		sign_btn = (Button) findViewById(R.id.login_btn);
		register_btn = (Button) findViewById(R.id.register_btn);
		mcheckbox = (CheckBox) findViewById(R.id.checkBox1);
		head_img = (ImageView) findViewById(R.id.login_head_img);

		pb = (ProgressBar) findViewById(R.id.login_pb);
		pb.setVisibility(View.GONE);

		map = Sharedpreference.readFromSP(LoginActivity.this);
		et1_name.setText(map.get("name").toString());
		et2_psw.setText(map.get("password").toString());
		mcheckbox.setChecked((Boolean) map.get("cbischeck"));

		String defaultname = et1_name.getText().toString();
		if (sql.hasname(defaultname)) {
			String path = sql.queryusermessage(defaultname).getHead_img_path();
			if (path != null) {
				File file = new File(path);
				if (file.exists()) {
					head_img.setImageBitmap(BitmapFactory.decodeFile(path));
				} else {
					head_img.setImageResource(R.drawable.default_head_);
				}
			} else {
				head_img.setImageResource(R.drawable.default_head_);
			}
		} else {
			head_img.setImageResource(R.drawable.default_head_);
		}
	}
}

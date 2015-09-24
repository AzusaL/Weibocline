package com.AzusaL.weibo;

import com.AzusaL.bean.UserBean;
import com.AzusaL.database.SqliteOpenhelper;
import com.AzusaL.httputils.HttpUtils;
import com.AzusaL.httputils.JsonUtils;
import com.example.weibo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private static final String register_url = "http://azusal.tunnel.mobi/Weibo/servlet/Register";
	protected static final int RESULT_CODE = 8;
	private EditText et1_name, et2_psw;
	private Button sign_up_btn, back_btn;
	private SqliteOpenhelper sql = new SqliteOpenhelper(this);;
	private ProgressBar pb;
	private String name, password;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			pb.setVisibility(View.GONE);
			if (msg.obj.equals("success")) {
				Toast.makeText(RegisterActivity.this, "注册成功！", 1000).show();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("name", name);
				intent.putExtras(bundle);
				setResult(RESULT_CODE,intent);
				sql.insertusermessage(new UserBean(name, ""));
				finish();
			} else if (msg.obj.equals("this name has bean registered")) {
				Toast.makeText(RegisterActivity.this, "该用户名已存在", 1000).show();
			} else if (msg.obj.equals("server error")) {
				Toast.makeText(RegisterActivity.this, "网络连接出错", 1000).show();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().hide();

		initview();
		setlistener();
	}

	private void jdugebyserver(final String name, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = HttpUtils.getresult(JsonUtils.getjsonobject(name, password), register_url);
				Message message = Message.obtain();
				message.obj = result;
				handler.sendMessage(message);
			}
		}).start();
	}

	private void setlistener() {
		sign_up_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name = et1_name.getText().toString();
				password = et2_psw.getText().toString();
				if (!TextUtils.isEmpty(name.replace(" ", "")) && !TextUtils.isEmpty(password.replace(" ", ""))) {
					jdugebyserver(name, password);
					pb.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				}
			}

		});

		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void initview() {
		et1_name = (EditText) findViewById(R.id.register_et1);
		et2_psw = (EditText) findViewById(R.id.register_et2);
		sign_up_btn = (Button) findViewById(R.id.register_btn);
		back_btn = (Button) findViewById(R.id.title_back_btn);
		pb = (ProgressBar) findViewById(R.id.register_pb);

		pb.setVisibility(View.GONE);
	}
}

package com.AzusaL.httputils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HttpUtilsforimg {

	protected static final int SUCCESS = 1;
	protected static final int ERROR = 0;
	private String path;
	private String username;
	private String upimgurl = "http://azusal.tunnel.mobi/Weibo/servlet/UsersHeadimg";
	private Context context;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCCESS) {
				Toast.makeText(context, "头像上传成功！", 1000).show();
			} else if (msg.what == ERROR) {
				Toast.makeText(context, "头像上传失败〒▽〒", 1000).show();
			}
		};
	};

	public HttpUtilsforimg(String path, String name, Context context) {
		this.path = path;
		this.username = name;
		this.context = context;
	}

	// 上传头像
	public void uploadheadHttpClient() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(upimgurl);
				MultipartEntity entity = new MultipartEntity();
				File file = new File(path);

				FileBody body = new FileBody(file);
				try {
					entity.addPart("username", new StringBody(username, Charset.forName("UTF-8")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				entity.addPart("img", body);
				post.setEntity(entity);

				try {
					HttpResponse response = client.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						handler.sendEmptyMessage(SUCCESS);
					} else {
						handler.sendEmptyMessage(ERROR);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}

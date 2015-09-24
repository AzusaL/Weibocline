package com.AzusaL.httputils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.AzusaL.bean.WeiboBean;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class AddWeiboToServer {
	private WeiboBean bean;
	private Context context;
	private String addweibourl = "http://azusal.tunnel.mobi/Weibo/servlet/Addweibo";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.obj.equals("success")) {
				Toast.makeText(context, "微博发送成功！", 1000).show();
			} else if (msg.obj.equals("server error")) {
				Toast.makeText(context, "服务器出错，微博发送失败", 1000).show();
			}
		};
	};

	public AddWeiboToServer(WeiboBean weiboBean, Context context) {
		this.bean = weiboBean;
		this.context = context;
	}

	// 上传头像
	public void addWeibo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = Message.obtain();
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(addweibourl);
				MultipartEntity entity = new MultipartEntity();
				ArrayList<String> path = bean.getPhotourls();
				try {
					entity.addPart("username", new StringBody(bean.getName(), Charset.forName("UTF-8")));
					entity.addPart("time", new StringBody(bean.getTime(), Charset.forName("UTF-8")));
					entity.addPart("weibocontent", new StringBody(bean.getContent(), Charset.forName("UTF-8")));
					entity.addPart("imgsize", new StringBody(String.valueOf(path.size()), Charset.forName("UTF-8")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				for (int i = 1; i < path.size(); i++) {
					File file = new File(path.get(i));
					FileBody body = new FileBody(file);
					entity.addPart("weiboimg" + bean.getName() + i, body);
				}
				post.setEntity(entity);
				try {
					HttpResponse response = client.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						String responseresult = EntityUtils.toString(response.getEntity(), "UTF-8");
						message.obj = responseresult;
						handler.sendMessage(message);
					} else {
						message.obj = "server error";
						handler.sendMessage(message);
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

package com.AzusaL.imageutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class Setweiboimg {
	private String urlpath;
	private ImageView img;
	public static LruCache<String, Bitmap> cache;
	public File imgforsd;

	static {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cachesize = maxMemory / 4;
		cache = new LruCache<String, Bitmap>(cachesize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}

		};
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			cache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return cache.get(key);
	}

	public Setweiboimg(String url, ImageView img) {
		super();
		this.urlpath = url;
		this.img = img;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				img.setImageBitmap(bitmap);
			}
		};
	};

	public void setimg() {
		File file = new File(Environment.getExternalStorageDirectory() + "/Weibo_app_data");
		if (!file.exists()) {
			file.mkdir();
		}
		imgforsd = new File(file.getAbsolutePath() + "/" + urlpath.replace("/", ""));
		if (imgforsd.exists()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = cutbitmap();
					addBitmapToMemoryCache(urlpath, bitmap);
					Message message = Message.obtain();
					message.what = 1;
					message.obj = bitmap;
					handler.sendMessage(message);
				}
			}).start();
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(urlpath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(3000);
					if (conn.getResponseCode() == 200) {
						FileOutputStream out = new FileOutputStream(imgforsd.getAbsolutePath());
						InputStream in = conn.getInputStream();
						int i = 0;
						byte[] b = new byte[1024];
						while ((i = in.read(b)) != -1) {
							out.write(b, 0, i);
							out.flush();
						}
						out.close();
						in.close();

						Bitmap bitmap = cutbitmap();
						addBitmapToMemoryCache(urlpath, bitmap);
						Message message = Message.obtain();
						message.what = 1;
						message.obj = bitmap;
						handler.sendMessage(message);
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public Bitmap cutbitmap() {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgforsd.getAbsolutePath(), option);
		option.inSampleSize = Setimg.calculateInSampleSize(option, 200, 200);
		option.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgforsd.getAbsolutePath(), option);
		return bitmap;
	}
}

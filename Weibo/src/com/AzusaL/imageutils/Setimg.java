package com.AzusaL.imageutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class Setimg {

	private ImageView mImageView;
	private Bitmap bitmap;
	private String img_uri;
	static LruCache<String, Bitmap> cache;
	public static Boolean stop = false;

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

	public Bitmap getbitmapfromcache(String url) {
		return cache.get(url);
	}

	public void addbitmaptochche(String url, Bitmap bitmaps) {
		if (getbitmapfromcache(url) == null) {
			cache.put(url, bitmaps);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (mImageView.getTag().equals(img_uri)) {
				mImageView.setImageBitmap((Bitmap) msg.obj);
				addbitmaptochche(img_uri, (Bitmap) msg.obj);
			}
		};
	};

	public void setimage(String uri, ImageView imageView) {
		mImageView = imageView;
		img_uri = uri;
		if (stop) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message message = Message.obtain();
					if (getbitmapfromcache(img_uri) == null) {
						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inJustDecodeBounds = true; // inJustDecodeBounds��������Ϊtrue�Ϳ����ý���������ֹΪbitmap�����ڴ棬
															// ����ֵҲ������һ��Bitmap���󣬶���null����ȻBitmap��null�ˣ�
															// ����BitmapFactory.Options��outWidth��outHeight��outMimeType���Զ��ᱻ��ֵ
						BitmapFactory.decodeFile(img_uri, option);
						option.inSampleSize = calculateInSampleSize(option, 200, 200); // inSampleSize����Ϊ����ͼƬ��ѹ������
						option.inJustDecodeBounds = false;
						bitmap = BitmapFactory.decodeFile(img_uri, option);
					} else {
						bitmap = getbitmapfromcache(img_uri);
					}
					message.obj = bitmap;
					handler.sendMessage(message);
				}
			}).start();
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// ԴͼƬ�ĸ߶ȺͿ��
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// �����ʵ�ʿ�ߺ�Ŀ���ߵı���
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// ѡ���͸�����С�ı�����ΪinSampleSize��ֵ���������Ա�֤����ͼƬ�Ŀ�͸�
			// һ��������ڵ���Ŀ��Ŀ�͸ߡ�
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}

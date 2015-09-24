package com.AzusaL.httputils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;

@SuppressWarnings("deprecation")
public class HttpUtils {
	//将用户填写的登陆信息或注册信息发送给服务器，并接收返回登陆的结果
	public static String getresult(JsonObject params,String url) {
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("charset", "UTF-8");
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost hp = new HttpPost(url);
		try {
			hp.setEntity(new StringEntity(params.toString(), "UTF-8"));
			HttpResponse hr = httpclient.execute(hp);
			if (hr.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(hr.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
		return "server error";
	}
}

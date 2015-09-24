package com.AzusaL.httputils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.AzusaL.bean.WeiboBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.util.Log;

public class GetAllWeibo {
	private static String urlpath = "http://azusal.tunnel.mobi/Weibo/servlet/GetWeibo";

	public static ArrayList<WeiboBean> getusersweibo(String name) {
		ArrayList<WeiboBean> list = new ArrayList<WeiboBean>();
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlpath);
			if (url != null) {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				ObjectMapper mappersent = new ObjectMapper();
				mappersent.writeValue(new OutputStreamWriter(conn.getOutputStream(), "utf-8"), name);

				if (conn.getResponseCode() != 200) {
					return null;
				}

				ObjectMapper mObjectMapper = new ObjectMapper();
				list = mObjectMapper.readValue(new InputStreamReader(conn.getInputStream(), "utf-8"),
						new TypeReference<ArrayList<WeiboBean>>() {
						});
				return list;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

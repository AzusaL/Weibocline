package com.AzusaL.sharedpreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.AzusaL.bean.WeiboBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Sharedpreference {

	public static void savelogintoSp(Context context, String name, String password, Boolean cbischeck) {
		SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("name", name);
		editor.putString("password", password);
		editor.putBoolean("cbischeck", cbischeck);
		editor.commit();
		editor.clear();
	}

	public static Map<String, Object> readFromSP(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
		String name = sp.getString("name", "");
		String password = sp.getString("password", "");
		Boolean cbischeck = sp.getBoolean("cbischeck", false);
		map.put("name", name);
		map.put("password", password);
		map.put("cbischeck", cbischeck);
		return map;
	}

	public static void savelisttosp(Context context, ArrayList<WeiboBean> SceneList) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
		Editor edit = mySharedPreferences.edit();
		ObjectMapper mapper = new ObjectMapper();
		String liststr = null;
		try {
			liststr = mapper.writeValueAsString(SceneList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		edit.putString("list", liststr);
		edit.commit();
		edit.clear();
	}

	public static ArrayList<WeiboBean> getlistfromsp(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
		String liststr = sharedPreferences.getString("list", "");
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<WeiboBean> list = new ArrayList<WeiboBean>();
		try {
			list = mapper.readValue(liststr, new TypeReference<ArrayList<WeiboBean>>() {
			});
			Log.i("main", list.get(0).getContent());
			return list;
		} catch (JsonParseException e) {
			e.printStackTrace();
			return null;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

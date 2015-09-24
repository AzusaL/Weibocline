package com.AzusaL.httputils;

import com.google.gson.JsonObject;

public class JsonUtils {
	//将发送给服务器的用户登陆名和密码封装成json数据格式
	public static JsonObject getjsonobject(String name, String password) {
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		object.addProperty("password", password);
		return object;
	}
}

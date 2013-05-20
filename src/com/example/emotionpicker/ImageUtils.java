package com.example.emotionpicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ImageUtils {

	public static Map<String,String> faceMap = new HashMap<String,String>();
	
	private static void prepareFaceMap(String arg) {
		try {
			JSONObject root = new JSONObject(arg);
			JSONArray array = root.getJSONArray("smileyList");
			if(array == null || array.length() == 0)
				return;
			for(int i=0; i < array.length(); i ++) {
				JSONObject pair = array.getJSONObject(i);
				faceMap.put(pair.optString("smileyString"), pair.optString("fileName"));
			}
			
		} catch (JSONException e) {
			Log.e("error", e.getMessage());
		}
		
		
	}

	public static void initEmotion(Context context) {
		InputStream is;
		try {
			is = context.getAssets().open("smiley_des.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sbr = new StringBuilder();
			String str;
			while((str = br.readLine())!= null) {
				sbr.append(str);
			}
			prepareFaceMap(sbr.toString());
		} catch (IOException e) {
			Log.e("error", e.getMessage());
		}
	}
}

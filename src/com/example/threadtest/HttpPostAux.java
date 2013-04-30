package com.example.threadtest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.util.Log;

public class HttpPostAux {
	
	InputStream is = null;
	String result = "";

	public JSONArray getserverdata(ArrayList<NameValuePair> parameters, String urlwebserver) {
		
		httpconnect(parameters, urlwebserver);
			
		if(is != null) {
			getresponse();
				
			return getjsonarray();
		} else {
			return null;
		}
		
	}
	
	public void httpconnect(ArrayList<NameValuePair> parameters, String urlwebserver) {
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlwebserver);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			
			is = httpEntity.getContent();
			
		} catch(Exception e) {
			Log.e("log_tag", "Error en httpconnect: " + e.toString());
		}
		
	}
	
	public void getresponse() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			while((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			is.close();
			
			result = sb.toString();
			
			Log.e("getresponse", "result = " + result);
		} catch(Exception e) {
			Log.e("log_tag", "Error en getresponse: " + e.toString());
		}
	
	}
	
	public JSONArray getjsonarray() {
	
		try {
			JSONArray jarray = new JSONArray(result);
			
			return jarray;
		} catch(Exception e) {
			Log.e("log_tag", "Error en getjsonarray: " + e.toString());
			
			return null;
		}
	
	}
	
}

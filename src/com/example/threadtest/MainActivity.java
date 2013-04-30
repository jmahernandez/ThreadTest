package com.example.threadtest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText _usuario;
	EditText _password;
	Button _login;
	
	HttpPostAux post;
	
	String URI = "http://jma-hernandez.com/droid/access.php";
	
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_usuario = (EditText) findViewById(R.id.editText1);
		_password = (EditText) findViewById(R.id.editText2);
		_login = (Button) findViewById(R.id.button1);
		
		_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String usuario = _usuario.getText().toString();
				String password = _password.getText().toString();
			
				new asyncLogin().execute(usuario, password);
			}
		});
	}
	
	public void onStart() {
		super.onStart();
		
		boolean hayConexion = false;
		
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo wifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if(wifi.isConnected() || mobile.isConnected()) {
        	hayConexion = true;
        }
        
        if(!hayConexion) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	 
            builder.setTitle("Información");
            builder.setMessage("La aplicación requiere una conexión a Internet");
            builder.setCancelable(false);
            
            builder.setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	finish();
                }
            });
         
            AlertDialog alert = builder.create();
            
            alert.show();
    	}
	}
	
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean checkLoginStatus(String user, String pass) {
		
		int status = -1;
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		
		postParameters.add(new BasicNameValuePair("usuario", user));
		postParameters.add(new BasicNameValuePair("password", pass));
		
		post = new HttpPostAux();
		
		// JSONArray data = post.getserverdata(postParameters, URI);
		JSONArray data = post.getserverdata(postParameters, URI);
		
		if(data != null && data.length() > 0) {
			JSONObject json_data;
			
			try {
				json_data = data.getJSONObject(0);
				
				status = json_data.getInt("status");
				
				Log.e("status", "status = " + status);
				
			} catch(JSONException e) {
				e.printStackTrace();
			}
			
			if(status == 0) {
				Log.e("status", "invalido");
				
				return false;
			} else {
				Log.e("status", "valido");
				
				return true;
			}
		} else {
			Log.e("json", "json error");
			
			return false;
		}
		
	}
	
	public void err_login() {
		Toast.makeText(MainActivity.this, "Usuario o Password Incorrectos", Toast.LENGTH_SHORT).show();
	}
	
	class asyncLogin extends AsyncTask<String, String, String> {
		String user;
		String pass;
		
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MainActivity.this);
			
			pDialog.setMessage("Authenticando ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			
			pDialog.show();
		}
		
		protected String doInBackground(String... params) {
			
			user = params[0];
			pass = params[1];
			
			if(checkLoginStatus(user,pass) == true) {
				return "ok";
			} else {
				return "err";
			}
			
		}
		
		protected void onPostExecute(String result) {
			pDialog.dismiss();
			
			Log.e("onPostExecute", "result " + result);
			
			if(result.equals("ok")) {
				Intent intent = new Intent(MainActivity.this, MenuActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
				
			} else {
				err_login();
			}
		}	
	}
}

package com.fang.wangyi.ui.more;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.android.volley.toolbox.StringRequest;
import com.fang.wangyi.BaseActivity;
import com.fang.wangyi.R;
import com.fang.wangyi.utils.DES;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

	public static final String TAG = "LoginActivity";

	private EditText et_username, et_password;
	private Button btn_login;

	private DES des = new DES();

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		et_username = (EditText) findViewById(R.id.username);
		et_password = (EditText) findViewById(R.id.password);

		btn_login = (Button) findViewById(R.id.button);

		if (spn_user.getBoolean(SPN_USER_ISLOGIN, false)) {
			btn_login.setText("注销");
			et_username.setText(spn_user.getString(SPN_USER_NAME, ""));
			et_password.setText(spn_user.getString(SPN_USER_PASSWORD, ""));
		} else {
			btn_login.setText("登录");
			et_username.setText("");
			et_password.setText("");
		}

		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (spn_user.getBoolean(SPN_USER_ISLOGIN, false)) {
					logout();
				} else {
					login();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void login() {
		if (et_username.getText().toString().trim().equals("")) {
			Toast.makeText(LoginActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT)
					.show();
		} else if (et_password.getText().toString().trim().equals("")) {
			Toast.makeText(LoginActivity.this, "密码不能为空!", Toast.LENGTH_SHORT)
					.show();
		} else {
			httpLogin();
		}
	}

	private void logout() {
		btn_login.setText("登录");
		et_username.setText("");
		et_password.setText("");
		editor_user.putString(SPN_USER_NAME, "");
		editor_user.putString(SPN_USER_PASSWORD, "");
		editor_user.putBoolean(SPN_USER_ISLOGIN, false);
		editor_user.commit();
	}

	private void httpLogin() {
		StringRequest request = new StringRequest(Request.Method.POST,
				HTTP_SERVER + HTTP_LOGIN, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "response = " + response);
						String str = response.substring(0, 1);
						int start = response.indexOf("|") + 1;
						String subStr = response.substring(start,
								response.length());
						if (str.equals("0")) {
							Toast.makeText(LoginActivity.this, "登录成功",
									Toast.LENGTH_SHORT).show();
							editor_user.putBoolean(SPN_USER_ISLOGIN, true);
							editor_user.putString(SPN_USER_NAME, et_username
									.getText().toString().trim());
							editor_user.putString(
									SPN_USER_PASSWORD,
									des.encrypt(et_password.getText()
											.toString().trim()));
							editor_user.commit();
							finish();
						} else {
							Toast.makeText(LoginActivity.this, subStr,
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("username", et_username.getText().toString().trim());
				params.put("password",
						des.encrypt(et_password.getText().toString().trim()));
				return params;
			}
		};
		queue.add(request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_register) {
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

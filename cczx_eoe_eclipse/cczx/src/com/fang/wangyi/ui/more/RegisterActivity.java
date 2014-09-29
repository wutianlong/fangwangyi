package com.fang.wangyi.ui.more;

import android.app.Activity;
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
import com.android.volley.toolbox.StringPostRequest;
import com.android.volley.toolbox.StringRequest;
import com.fang.wangyi.BaseActivity;
import com.fang.wangyi.R;
import com.fang.wangyi.utils.DES;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    public static final String TAG = "RegisterActivity";

    private EditText et_username, et_password, et_confirm_password, et_email;
    private Button button;

    private DES des = new DES();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.password);
        et_confirm_password = (EditText) findViewById(R.id.confirm_password);
        et_email = (EditText) findViewById(R.id.email);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        if (et_username.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入用户名!", Toast.LENGTH_SHORT).show();
        } else if (et_password.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入密码!", Toast.LENGTH_SHORT).show();
        } else if (et_confirm_password.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入确认密码!", Toast.LENGTH_SHORT).show();
        } else if (et_email.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入邮箱!", Toast.LENGTH_SHORT).show();
        } else if (!et_password.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致!", Toast.LENGTH_SHORT).show();
        } else {
            httpRegister();
        }
    }

    private void httpRegister() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("username", et_username.getText().toString().trim());
//        params.put("password", des.encrypt(et_password.getText().toString().trim()));
//        params.put("email", et_email.getText().toString().trim());
        StringRequest request = new StringRequest(Request.Method.POST, HTTP_SERVER + HTTP_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response === " + response);
                String str = response.substring(0, 1);
                int start = response.indexOf("|") + 1;
                String error = response.substring(
                        start, response.length());
                if (str.equals("0")) {
                    Toast.makeText(
                            RegisterActivity.this,
                            "注册成功", Toast.LENGTH_SHORT)
                            .show();

                    finish();
                } else {
                    Toast.makeText(
                            RegisterActivity.this,
                            error, Toast.LENGTH_SHORT)
                            .show();
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
                params.put("password", des.encrypt(et_password.getText().toString().trim()));
                params.put("email", et_email.getText().toString().trim());
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        } else
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

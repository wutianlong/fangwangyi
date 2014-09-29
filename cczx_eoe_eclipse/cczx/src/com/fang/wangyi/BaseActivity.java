package com.fang.wangyi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import net.tsz.afinal.FinalDb;

/**
 * Created by changliang on 14-6-27.
 */
public class BaseActivity extends FragmentActivity implements Constant {

    protected RequestQueue queue;

    public FinalDb db;

    public SharedPreferences spn_user;
    public SharedPreferences.Editor editor_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        db = FinalDb.create(this, "cczx.db", true);

        spn_user = getSharedPreferences(SPN_USER, MODE_PRIVATE);
        editor_user = spn_user.edit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return false;
            }
        });
    }
}

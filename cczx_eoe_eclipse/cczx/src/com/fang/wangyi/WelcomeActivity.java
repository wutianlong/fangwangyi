package com.fang.wangyi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import c.i.a.m;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.fang.wangyi.push.Utils;

public class WelcomeActivity extends BaseActivity {

	public static final String TAG = "WelcomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		// 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
		// if (!Utils.hasBind(getApplicationContext())) {
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY,
		// Utils.getMetaValue(WelcomeActivity.this, "api_key"));
		// } else {
		// Log.d(TAG , "PushManager.isPushEnabled(WelcomeActivity.this) === " +
		// PushManager.isPushEnabled(WelcomeActivity.this));
		// if (!PushManager.isPushEnabled(WelcomeActivity.this)) {
		// PushManager.resumeWork(WelcomeActivity.this);
		// }
		// }

		// iadpush id
		m.initSDK(this, "505-4-3329");

		AlphaAnimation mAlphaAnimation = 
				new AlphaAnimation(0.3f, 1.0f);
		mAlphaAnimation.setDuration(3 * 1000);
		mAlphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		findViewById(R.id.welcome).setAnimation(mAlphaAnimation);
	}

}

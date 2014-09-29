package com.fang.wangyi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.kyview.AdViewInterface;
import com.kyview.AdViewLayout;
import com.kyview.AdViewTargeting;
import com.kyview.AdViewTargeting.RunMode;
import com.kyview.AdViewTargeting.SwitcherMode;
import com.kyview.AdViewTargeting.UpdateMode;
import com.umeng.analytics.MobclickAgent;

public class WebActivity extends BaseActivity implements Constant,
		AdViewInterface {

	public static final String TAG = "WebActivity";

	private WebView webView;

	private String web_url, title;

	private FrameLayout loading;

	private AdViewLayout adViewLayout;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		Intent intent = getIntent();
		web_url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(title);

		// 以查询资源的方式查询AdView并加载请求。
		// AdView adView = (AdView) this.findViewById(R.id.adView);
		// AdRequest adRequest = new AdRequest.Builder().build();
		// adView.loadAd(adRequest);

		loading = (FrameLayout) findViewById(R.id.loading);

		webView = (WebView) findViewById(R.id.webView);

		AdViewTargeting.setSwitcherMode(SwitcherMode.DEFAULT); // 广告可被关闭，如不需要可修改为SwitcherMode.DEFAULT

		/* 下面两行只用于测试,完成后一定要去掉,参考文挡说明 */
		// AdViewTargeting.setUpdateMode(UpdateMode.EVERYTIME); // 每次都从服务器取配置
		// AdViewTargeting.setRunMode(RunMode.TEST); // 保证所有选中的广告公司都为测试状态

		adViewLayout = (AdViewLayout) findViewById(R.id.adview_ayout);
		adViewLayout.setAdViewInterface(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateDisplay();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@SuppressLint("NewApi")
	private void updateDisplay() {
		if (getIntent().hasExtra("notification")) {
			webView.loadUrl(web_url);
		} else {
			webView.loadUrl(HTTP_SERVER + web_url);
		}

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setDomStorageEnabled(true);// 让浏览器来存储页面元素的DOM模型，从而使JavaScript可以执行操作了。
		webView.getSettings().setLoadsImagesAutomatically(true);
		// webView.getSettings().setUseWideViewPort(true);
		// webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setLayoutAlgorithm(
				WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		webView.requestFocus();
		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				loading.setVisibility(View.GONE);
				// getActivity().setProgressBarIndeterminateVisibility(false);
				// this.setSupportProgressBarIndeterminateVisibility(false);
			}

		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				// setSupportProgress(newProgress * 100);
				super.onProgressChanged(view, newProgress);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					if (getIntent().hasExtra("notification")) {
						Intent intent = new Intent(WebActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					} else {
						finish();
					}
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getIntent().hasExtra("notification")) {
				Intent intent = new Intent(WebActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClickAd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClosedAd() {
		// 如果想立即关闭直接调用：
		// adViewLayout.setClosed(true);

		// 弹出对话框，要求二次确认
		Dialog dialog = new AlertDialog.Builder(this).setTitle("确定要关闭广告？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 无论是否关闭广告，请务必调用下一行方法，否则广告将停止切换
						// 传入false，广告将不会关闭
						adViewLayout.setClosed(false);
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 无论是否关闭广告，请务必调用下一行方法，否则广告将停止切换
						// 传入true，广告将关闭
						adViewLayout.setClosed(true);
					}
				}).show();
		// 防止误点击关闭对话框，可能使 adViewLayout.setClosed(boolean);不被调用
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onDisplayAd() {
		// TODO Auto-generated method stub

	}
}

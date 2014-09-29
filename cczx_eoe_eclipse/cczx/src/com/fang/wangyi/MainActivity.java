package com.fang.wangyi;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.beans.news.TabBean;
import com.fang.wangyi.ui.more.LoginActivity;
import com.fang.wangyi.ui.news.NewsFragment;
import com.fang.wangyi.ui.news.TabFragment;
import com.fang.wangyi.ui.pics.PicsFragment;
import com.fang.wangyi.ui.zts.ZTFragment;
import com.fang.wangyi.views.NoScrollViewPager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseFragmentActivity implements Constant {

	public static final String TAG = "MainActivity";

	public static final int LOAD_AD = 10;

	// private Fragment mContent;
	private List<TabBean> tabList;

	private LayoutInflater mLayoutInflater;
	private FrameLayout loading; // 加载页面
	private TabsPagerAdapter tabsPagerAdapter;
	private NoScrollViewPager viewPager; // 不能滚动的ViewPager

	private InterstitialAd interstitial; // 插页式广告

	Dialog alertDialog;

	private UMSocialService mController;
	private FeedbackAgent agent;

	String appId = "wx6c879a055a1b15bb";
	String appSecret = "2446cb817b2de352a391f1a63e3d2176";
	String WEIXIN_URL = "http://www.adview.cn/";
	String WEIXIN_TITLE = "发现一款很实用的新闻资讯APP,小伙伴们快来围观！";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the Above View
		// if (savedInstanceState != null)
		// mContent =
		// getSupportFragmentManager().getFragment(savedInstanceState,
		// "mContent");

		// set the Above View
		setContentView(R.layout.content_frame);

		mLayoutInflater = LayoutInflater.from(this);
		alertDialog = new AlertDialog.Builder(MainActivity.this).setView(
				mLayoutInflater.inflate(R.layout.about_us, null)).create();

		viewPager = (NoScrollViewPager) findViewById(R.id.viewtabpager);
		loading = (FrameLayout) findViewById(R.id.loading);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);

		// customize the SlidingMenu
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getSlidingMenu().setSecondaryMenu(R.layout.right_frame);

		httpTabRequest();

		// 制作插页式广告。
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-6081432241581514/5369364581");

		handler.sendEmptyMessage(LOAD_AD);

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		agent = new FeedbackAgent(this);
		agent.sync();
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		mController
				.setShareContent("小伙伴们,现在有一款很给力的新闻资讯类APP,及时方便的让你足不出户了解天下新闻状态,了解详情请猛点:仿网易");

		mController.setShareImage(new UMImage(this,
				drawableToBitmap(getResources().getDrawable(
						R.drawable.share_default_image))));
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);

		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
		wxHandler.addToSocialSDK();
		wxHandler.setTargetUrl(WEIXIN_URL);
		wxHandler.setTitle(WEIXIN_TITLE);
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		wxCircleHandler.setTargetUrl(WEIXIN_URL);
		wxCircleHandler.setTitle(WEIXIN_TITLE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();

		int height = drawable.getIntrinsicHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height,

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

		: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		drawable.setBounds(0, 0, width, height);

		drawable.draw(canvas);

		return bitmap;

	}

	private android.os.Handler handler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case LOAD_AD:
				// 创建广告请求。
				AdRequest adRequest = new AdRequest.Builder().build();
				// 开始加载插页式广告。
				interstitial.loadAd(adRequest);
				break;

			case LOAD_SUCCESS:
				loading.setVisibility(View.GONE);

				tabsPagerAdapter = new TabsPagerAdapter(
						getSupportFragmentManager());
				Fragment fragment1 = TabFragment.newInstance();
				Bundle data = new Bundle();
				data.putSerializable("tabList", (Serializable) tabList);
				fragment1.setArguments(data);
				tabsPagerAdapter.getFragments().add(fragment1);
				tabsPagerAdapter.getFragments().add(new PicsFragment());
				tabsPagerAdapter.getFragments().add(
						NewsFragment.newInstance("排行", HTTP_TOPS));
				tabsPagerAdapter.getFragments().add(new ZTFragment());

				viewPager.setOffscreenPageLimit(tabsPagerAdapter.getFragments()
						.size());
				viewPager.setAdapter(tabsPagerAdapter);

				// if (mContent == null) {
				// mContent = TabFragment.newInstance();
				// Bundle data = new Bundle();
				// data.putSerializable("tabList", (Serializable) tabList);
				// mContent.setArguments(data);
				// }

				// getSupportFragmentManager()
				// .beginTransaction()
				// .replace(R.id.content_frame, mContent)
				// .commit();

				getSupportFragmentManager()
						.beginTransaction()
						.replace(
								R.id.menu_frame,
								new LeftMenuFragment(tabList,
										getSupportFragmentManager())).commit();

				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.right_frame,
								RightMenuFragment.newInstance("1", "2"))
						.commit();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 显示哪页ViewPager
	 * 
	 * @param position
	 */
	public void setViewPager(int position) {
		viewPager.setCurrentItem(position, false);
		getSlidingMenu().showContent();
	}

	/**
	 * 右侧边栏事件处理
	 * 
	 * @param position
	 */
	public void onItemUser(int position) {
		Intent intent;
		switch (position) {
		// 用户管理
		// case 0:
		// intent = new Intent(MainActivity.this, LoginActivity.class);
		// startActivity(intent);
		// break;
		case 0:
			shareApp();
			// startActivity(new Intent(MainActivity.this,
			// ShareActivity.class));
			break;
		case 1:
			agent.startFeedbackActivity();
			break;
		case 2:
			intent = new Intent(MainActivity.this, WebActivity.class);
			intent.putExtra("title", "关于我们");
			intent.putExtra("url", HTTP_ABOUT);
			// startActivity(intent);
			alertDialog.show();
			break;
		}

		getSlidingMenu().showContent();
	}

	private void shareApp() {
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能");
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
		mController.openShare(MainActivity.this, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int resultCode,
					SocializeEntity entity) {
				if (resultCode == 200)
					Toast.makeText(MainActivity.this, "分享成功",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(MainActivity.this, "分享失败",
							Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 请求首页tab
	 */
	private void httpTabRequest() {
		StringGBKRequest request = new StringGBKRequest(
				HTTP_SERVER + HTTP_TABS, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						XmlPullParser parser = Xml.newPullParser();
						TabBean tabBean = null;
						try {
							// 获取xml输入数据
							parser.setInput(new StringReader(response));
							// 开始解析事件
							int eventType = parser.getEventType();
							// 处理事件，不碰到文档结束就一直处理
							while (eventType != XmlPullParser.END_DOCUMENT) {

								switch (eventType) {
								case XmlPullParser.START_DOCUMENT:
									tabList = new ArrayList<TabBean>();
									break;
								case XmlPullParser.START_TAG:
									String name = parser.getName();
									if (name.equalsIgnoreCase("metadata")) {
										tabBean = new TabBean();
									} else if (tabBean != null) {
										if (name.equalsIgnoreCase("title")) {
											tabBean.setTitle(parser.nextText());
										}
										if (name.equalsIgnoreCase("url")) {
											tabBean.setUrl(parser.nextText());
										}
									}
									break;
								case XmlPullParser.END_TAG:
									if (parser.getName().equalsIgnoreCase(
											"metadata")
											&& tabBean != null) {
										tabList.add(tabBean);
										tabBean = null;
									}
									break;
								case XmlPullParser.END_DOCUMENT:

									break;
								}
								eventType = parser.next();
							}

							handler.sendEmptyMessage(LOAD_SUCCESS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		queue.add(request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// getSlidingMenu().showSecondaryMenu();
		// return true;
		// }
		switch (item.getItemId()) {
		case R.id.action_more:
			getSlidingMenu().showSecondaryMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private long exitTime = 0; // 退出计时

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			Log.d(TAG, "current item === " + viewPager.getCurrentItem());

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// displayInterstitial();
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 在您准备好展示插页式广告时调用displayInterstitial()。
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	public void switchContent(Fragment fragment) {
		// mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	public class TabsPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

		public TabsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public ArrayList<Fragment> getFragments() {
			return this.mFragments;
		}

		@Override
		public Fragment getItem(int position) {
			return this.mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

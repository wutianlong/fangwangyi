package com.fang.wangyi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class ShareActivity extends Activity {

	String appId = "wx6c879a055a1b15bb";
	String appSecret = "2446cb817b2de352a391f1a63e3d2176";
	private UMSocialService mController;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 100) {
				mController.openShare(ShareActivity.this,
						new SnsPostListener() {

							@Override
							public void onStart() {

							}

							@Override
							public void onComplete(SHARE_MEDIA platform,
									int resultCode, SocializeEntity entity) {
								if (resultCode == 200)
									Toast.makeText(ShareActivity.this, "分享成功",
											Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(ShareActivity.this, "分享失败",
											Toast.LENGTH_SHORT).show();
								finish();
							}
						});
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_own);

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
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		mHandler.sendEmptyMessageDelayed(100, 500);

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
}

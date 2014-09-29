package com.fang.wangyi.ui.zts;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.db.sqlite.DbModel;

import org.xmlpull.v1.XmlPullParser;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.BaseActivity;
import com.fang.wangyi.R;
import com.fang.wangyi.WebActivity;
import com.fang.wangyi.adapters.zts.ZTListAdapter;
import com.fang.wangyi.beans.zts.SectionBean;
import com.fang.wangyi.beans.zts.ZTListBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

public class ZTListActivity extends BaseActivity {

	public static final String TAG = "ZTListActivity";

	private String title, url, img;

	private View header;
	private ImageView header_image;
	private PinnedHeaderListView listView;
	private ZTListAdapter newsAdapter;

	private FrameLayout loading;

	private List<ZTListBean> ztsList;
	private List<SectionBean> sectionList;

	private Map<String, List<ZTListBean>> ztsMap;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ztlist);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		img = intent.getStringExtra("img");

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(title);

		loading = (FrameLayout) findViewById(R.id.loading);

		listView = (PinnedHeaderListView) findViewById(R.id.lv_zts_list);
		// listView.setPinHeaders(false);

		listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int section, int position, long id) {
				Intent intent = new Intent(ZTListActivity.this,
						WebActivity.class);
				intent.putExtra(
						"title",
						ztsMap.get(sectionList.get(section).getTag())
								.get(position).getTitle());
				intent.putExtra(
						"url",
						ztsMap.get(sectionList.get(section).getTag())
								.get(position).getUrl());
				startActivity(intent);
			}

			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view,
					int section, long id) {

			}
		});

		header = LayoutInflater.from(this).inflate(R.layout.view_header_zts,
				listView, false);
		header_image = (ImageView) header.findViewById(R.id.image);

		httpRequest(url);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_SUCCESS:
				List<DbModel> list = db
						.findDbModelListBySQL("select distinct sort from zts");
				ztsMap = new HashMap<String, List<ZTListBean>>();
				sectionList = new ArrayList<SectionBean>();
				for (int i = 0; i < list.size(); i++) {
					SectionBean bean = new SectionBean();
					bean.setIndex(i);
					bean.setTag(list.get(i).getString("sort"));
					sectionList.add(bean);
				}
				for (int j = 0; j < sectionList.size(); j++) {
					List<ZTListBean> lists = db.findAllByWhere(
							ZTListBean.class, "sort = '"
									+ sectionList.get(j).getTag() + "'");
					ztsMap.put(sectionList.get(j).getTag(), lists);
				}

				Transformation transformation = new Transformation() {

					@Override
					public Bitmap transform(Bitmap source) {
						int targetWidth = header_image.getWidth();

						double aspectRatio = (double) source.getHeight()
								/ (double) source.getWidth();
						int targetHeight = (int) (targetWidth * aspectRatio);
						Bitmap result = Bitmap.createScaledBitmap(source,
								targetWidth, targetHeight, false);
						if (result != source) {
							// Same bitmap is returned if sizes are the same
							source.recycle();
						}
						return result;
					}

					@Override
					public String key() {
						return "transformation" + " desiredWidth";
					}
				};

				Log.d(TAG, "HTTP_IMAGE + img ==== " + HTTP_IMAGE + img);
				Picasso.with(ZTListActivity.this).load(HTTP_IMAGE + img)
						.placeholder(R.drawable.base_load_big_img)
						.transform(transformation).into(header_image);

				newsAdapter = new ZTListAdapter(ZTListActivity.this,
						sectionList, ztsMap);
				listView.setAdapter(newsAdapter);
				listView.addHeaderView(header);
				newsAdapter.notifyDataSetChanged();

				loading.setVisibility(View.GONE);

				break;
			}
		}
	};

	private void httpRequest(String url) {
		StringGBKRequest request = new StringGBKRequest(HTTP_SERVER + url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "http response === " + response);
						XmlPullParser parser = Xml.newPullParser();
						ZTListBean ztListBean = null;
						try {
							parser.setInput(new StringReader(response));
							int eventType = parser.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
								case XmlPullParser.START_DOCUMENT:
									db.deleteAll(ZTListBean.class);
									ztsList = new ArrayList<ZTListBean>();
									break;
								case XmlPullParser.START_TAG:
									String name = parser.getName();
									if (name.equalsIgnoreCase("metadata")) {
										ztListBean = new ZTListBean();
									} else if (ztListBean != null) {
										if (name.equalsIgnoreCase("aid")) {
											ztListBean.setAid(parser.nextText());
										}
										if (name.equalsIgnoreCase("title")) {
											ztListBean.setTitle(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("url")) {
											ztListBean.setUrl(parser.nextText());
										}
										if (name.equalsIgnoreCase("img")) {
											ztListBean.setImg(parser.nextText());
										}
										if (name.equalsIgnoreCase("info")) {
											ztListBean.setInfo(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("class")) {
											ztListBean.setSort(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("senddate")) {
											ztListBean.setSenddate(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("copen")) {
											ztListBean.setCopen(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("anonymity")) {
											ztListBean.setAnonymity(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("ccount")) {
											ztListBean.setCcount(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("curl")) {
											ztListBean.setCurl(parser
													.nextText());
										}

									}
									break;
								case XmlPullParser.END_TAG:
									if (parser.getName().equalsIgnoreCase(
											"metadata")
											&& ztListBean != null) {
										db.save(ztListBean);
										ztsList.add(ztListBean);
										ztListBean = null;
									}
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
		// getMenuInflater().inflate(R.menu.ztlist, menu);
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
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

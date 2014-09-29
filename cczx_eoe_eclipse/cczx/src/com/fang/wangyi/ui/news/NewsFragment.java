package com.fang.wangyi.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.BaseListFragment;
import com.fang.wangyi.R;
import com.fang.wangyi.WebActivity;
import com.fang.wangyi.adapters.news.NewsAdapter;
import com.fang.wangyi.beans.news.NewsBean;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.xmlpull.v1.XmlPullParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link NewsFragment#newInstance}
 * factory method to create an instance of this fragment.
 */
public class NewsFragment extends BaseListFragment {

	public static final String TAG = "NewsFragment";

	private boolean isInit; // 是否可以开始加载数据

	private PullToRefreshListView pullToRefreshListView;

	private List<NewsBean> newsList;
	private NewsAdapter newsAdapter;

	private String title;
	private String url;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment NewsFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static NewsFragment newInstance(String title, String url) {
		NewsFragment fragment = new NewsFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("url", url);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			title = getArguments().getString("title");
			url = getArguments().getString("url");
		}
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// if (savedInstanceState != null)
		// mColorRes = savedInstanceState.getInt("mColorRes");
		isInit = true;

		View layout = inflater
				.inflate(R.layout.fragment_news, container, false);

		ListView lv = (ListView) layout.findViewById(android.R.id.list);

		pullToRefreshListView = new PullToRefreshListView(getActivity());
		pullToRefreshListView.setLayoutParams(lv.getLayoutParams());
		pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						httpRequest();
					}
				});

		return pullToRefreshListView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setDivider(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (view != null) {

		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), WebActivity.class);
		intent.putExtra("title", newsList.get(position - 1).getSort());
		intent.putExtra("url", newsList.get(position - 1).getUrl());
		getActivity().startActivity(intent);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_SUCCESS:
				newsAdapter = new NewsAdapter(getActivity(), newsList);
				setListAdapter(newsAdapter);
				pullToRefreshListView.onRefreshComplete();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// 判断当前fragment是否显示
		if (getUserVisibleHint()) {
			showData();
		}
		MobclickAgent.onPageStart("NewsFragment"); // 统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("NewsFragment"); // 保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 每次切换fragment时调用的方法
		if (isVisibleToUser) {
			showData();
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @author yubin
	 * @date 2014-1-16
	 */
	private void showData() {
		if (isInit) {
			isInit = false;// 加载数据完成
			// 加载各种数据
			// httpRequest();
			pullToRefreshListView.setRefreshing(true);
		}
	}

	private void httpRequest() {

		StringGBKRequest request = new StringGBKRequest(HTTP_SERVER + url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						Log.d(TAG, "come httpRequest ------ >>>> ");

						XmlPullParser parser = Xml.newPullParser();
						NewsBean newsBean = null;
						try {
							parser.setInput(new StringReader(response));
							int eventType = parser.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
								case XmlPullParser.START_DOCUMENT:
									newsList = new ArrayList<NewsBean>();
									break;
								case XmlPullParser.START_TAG:
									String name = parser.getName();
									if (name.equalsIgnoreCase("metadata")) {
										newsBean = new NewsBean();
									} else if (newsBean != null) {
										if (name.equalsIgnoreCase("aid")) {
											newsBean.setAid(parser.nextText());
										}
										if (name.equalsIgnoreCase("title")) {
											newsBean.setTitle(parser.nextText());
										}
										if (name.equalsIgnoreCase("url")) {
											newsBean.setUrl(parser.nextText());
										}
										if (name.equalsIgnoreCase("img")) {
											newsBean.setImg(parser.nextText());
										}
										if (name.equalsIgnoreCase("info")) {
											newsBean.setInfo(parser.nextText());
										}
										if (name.equalsIgnoreCase("class")) {
											newsBean.setSort(parser.nextText());
										}
										if (name.equalsIgnoreCase("senddate")) {
											newsBean.setSenddate(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("copen")) {
											newsBean.setCopen(parser.nextText());
										}
										if (name.equalsIgnoreCase("anonymity")) {
											newsBean.setAnonymity(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("ccount")) {
											newsBean.setCcount(parser
													.nextText());
										}
										if (name.equalsIgnoreCase("curl")) {
											newsBean.setCurl(parser.nextText());
										}

									}
									break;
								case XmlPullParser.END_TAG:
									if (parser.getName().equalsIgnoreCase(
											"metadata")
											&& newsBean != null) {
										newsList.add(newsBean);
										newsBean = null;
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
						pullToRefreshListView.onRefreshComplete();
						Toast.makeText(getActivity(), "网络请求失败",
								Toast.LENGTH_SHORT).show();
					}
				});
		queue.add(request);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt("mColorRes", mColorRes);
	}

}

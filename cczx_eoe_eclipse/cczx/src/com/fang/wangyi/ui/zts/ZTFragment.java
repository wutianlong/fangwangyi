package com.fang.wangyi.ui.zts;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.BaseListFragment;
import com.fang.wangyi.R;
import com.fang.wangyi.adapters.zts.ZTAdapter;
import com.fang.wangyi.beans.zts.ZTBean;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ZTFragment extends BaseListFragment {

	public static final String TAG = "ZTFragment";

	private boolean isInit; // 是否可以开始加载数据

	private PullToRefreshListView pullToRefreshListView;
	private ZTAdapter ztAdapter;

	private List<ZTBean> ztsList;

	public ZTFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		isInit = true;
		View view = inflater.inflate(R.layout.fragment_zts, container, false);

		ListView lv = (ListView) view.findViewById(android.R.id.list);

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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_SUCCESS:
				ztAdapter = new ZTAdapter(getActivity(), ztsList);
				setListAdapter(ztAdapter);
				pullToRefreshListView.onRefreshComplete();
				break;
			}
		}
	};

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ZTListActivity.class);
		intent.putExtra("title", ztsList.get(position - 1).getTitle());
		intent.putExtra("url", ztsList.get(position - 1).getUrl());
		intent.putExtra("img", ztsList.get(position - 1).getImg());
		getActivity().startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume === >>> " + getUserVisibleHint());
		// 判断当前fragment是否显示
		if (getUserVisibleHint()) {
			showData();
		}
		MobclickAgent.onPageStart("ZTFragment"); // 统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("ZTFragment"); // 保证 onPageEnd 在onPause
												// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 每次切换fragment时调用的方法
		Log.d(TAG, "setUserVisibleHint isVisibleToUser == >> "
				+ isVisibleToUser);
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

		StringGBKRequest request = new StringGBKRequest(HTTP_SERVER + HTTP_ZTS,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "http response === " + response);
						XmlPullParser parser = Xml.newPullParser();
						ZTBean ztBean = null;
						try {
							parser.setInput(new StringReader(response));
							int eventType = parser.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
								case XmlPullParser.START_DOCUMENT:
									ztsList = new ArrayList<ZTBean>();
									break;
								case XmlPullParser.START_TAG:
									String name = parser.getName();
									if (name.equalsIgnoreCase("metadata")) {
										ztBean = new ZTBean();
									} else if (ztBean != null) {

										if (name.equalsIgnoreCase("title")) {
											ztBean.setTitle(parser.nextText());
										}
										if (name.equalsIgnoreCase("url")) {
											ztBean.setUrl(parser.nextText());
										}
										if (name.equalsIgnoreCase("img")) {
											ztBean.setImg(parser.nextText());
										}
										if (name.equalsIgnoreCase("info")) {
											ztBean.setInfo(parser.nextText());
										}
									}
									break;
								case XmlPullParser.END_TAG:
									if (parser.getName().equalsIgnoreCase(
											"metadata")
											&& ztBean != null) {
										ztsList.add(ztBean);
										ztBean = null;
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt("mColorRes", mColorRes);
	}

}

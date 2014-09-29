package com.fang.wangyi.ui.news;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.BaseFragment;
import com.fang.wangyi.BaseListFragment;
import com.fang.wangyi.BasePullToRefreshListFragment;
import com.fang.wangyi.R;
import com.fang.wangyi.WebActivity;
import com.fang.wangyi.adapters.news.NewsAdapter;
import com.fang.wangyi.beans.news.NewsBean;
import com.fang.wangyi.beans.news.NewsHeaderBean;
import com.fang.wangyi.beans.news.TabBean;
import com.fang.wangyi.views.ListViewForScrollView;
import com.fang.wangyi.views.MyViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.fang.wangyi.ui.news.NewsHeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsHeaderFragment extends BaseListFragment {

    public static final String TAG = "NewsHeaderFragment";
    public static final int LOAD_HEADER_SUCCESS = 1;

    private boolean isInit; // 是否可以开始加载数据

    private PullToRefreshListView pullToRefreshListView;

    private View header;
    private MyViewPager viewPager;
    private float xDistance, yDistance;
    private float mLastMotionX, mLastMotionY;
    private boolean mIsBeingDragged = true;

    private List<NewsHeaderBean> newsHeaderList;
    private List<NewsBean> newsList;
    private NewsAdapter newsAdapter;
    private HeaderPagerAdapter headerPagerAdapter;

    private String title;
    private String url;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsHeaderFragment newInstance(String title, String url) {
        NewsHeaderFragment fragment = new NewsHeaderFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isInit = true;

        View layout = inflater.inflate(R.layout.fragment_news_header, container, false);

        ListView lv = (ListView) layout.findViewById(android.R.id.list);

        pullToRefreshListView = new PullToRefreshListView(getActivity());
        pullToRefreshListView.setLayoutParams(lv.getLayoutParams());
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                httpRequest();
                httpHeaderRequest();
            }
        });

        header = inflater.inflate(R.layout.view_header_news_header, null);

        pullToRefreshListView.getRefreshableView().addHeaderView(header);

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
            viewPager = (MyViewPager) header.findViewById(R.id.viewpager);

            /*ViewPager 嵌套 ViewPager 的滑动处理*/
            viewPager.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    viewPager.getGestureDetector().onTouchEvent(event);
                    // TODO Auto-generated method stub
                    final float x = event.getRawX();
                    final float y = event.getRawY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            xDistance = yDistance = 0f;
                            mLastMotionX = x;
                            mLastMotionY = y;
                        case MotionEvent.ACTION_MOVE:
                            final float xDiff = Math.abs(x - mLastMotionX);
                            final float yDiff = Math.abs(y - mLastMotionY);
                            xDistance += xDiff;
                            yDistance += yDiff;

                            float dx = xDistance - yDistance;
                            if (xDistance > yDistance || Math.abs(xDistance - yDistance) < 0.00001f) {
                                mIsBeingDragged = true;
                                mLastMotionX = x;
                                mLastMotionY = y;
                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);
                            } else {
                                mIsBeingDragged = false;
                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if (mIsBeingDragged) {
                                ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });

        /*ViewPager 点击事件*/
            viewPager.setOnSimpleClickListener(new MyViewPager.onSimpleClickListener() {
                @Override
                public void setOnSimpleClickListenr(int position) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("title", newsHeaderList.get(position).getSort());
                    intent.putExtra("url", newsHeaderList.get(position).getUrl());
                    getActivity().startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("title", newsList.get(position - 2).getTitle());
        intent.putExtra("url", newsList.get(position - 2).getUrl());
        getActivity().startActivity(intent);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_HEADER_SUCCESS:
                    httpRequest();
                    break;

                case LOAD_SUCCESS:
                    headerPagerAdapter = new HeaderPagerAdapter(getChildFragmentManager(), newsHeaderList);
                    newsAdapter = new NewsAdapter(getActivity(), newsList);

                    viewPager.setAdapter(headerPagerAdapter);
                    viewPager.setOffscreenPageLimit(newsHeaderList.size());
                    setListAdapter(newsAdapter);

                    pullToRefreshListView.onRefreshComplete();

                    newsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ===--- >>> " + getUserVisibleHint());
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            showData();
        }
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
            isInit = false;//加载数据完成
            // 加载各种数据
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshListView.setRefreshing(true);
                }
            }, 500);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newsHeaderList = null;
        newsList = null;
    }

    private void httpHeaderRequest() {
        StringGBKRequest request_header = new StringGBKRequest(HTTP_SERVER + HTTP_NEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "load httpheaderrequest success ----- >>>>> 1");
                XmlPullParser parser = Xml.newPullParser();
                NewsHeaderBean newsHeaderBean = null;
                try {
                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                newsHeaderList = new ArrayList<NewsHeaderBean>();
                                break;
                            case XmlPullParser.START_TAG:
                                String name = parser.getName();
                                if (name.equalsIgnoreCase("metadata")) {
                                    newsHeaderBean = new NewsHeaderBean();
                                } else if (newsHeaderBean != null) {
                                    if (name.equalsIgnoreCase("aid")) {
                                        newsHeaderBean.setAid(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("title")) {
                                        newsHeaderBean.setTitle(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("url")) {
                                        newsHeaderBean.setUrl(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("img")) {
                                        newsHeaderBean.setImg(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("info")) {
                                        newsHeaderBean.setInfo(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("class")) {
                                        newsHeaderBean.setSort(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("copen")) {
                                        newsHeaderBean.setCopen(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("anonymity")) {
                                        newsHeaderBean.setAnonymity(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("ccount")) {
                                        newsHeaderBean.setCcount(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("curl")) {
                                        newsHeaderBean.setCurl(parser.nextText());
                                    }

                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equalsIgnoreCase("metadata") && newsHeaderBean != null) {
                                    newsHeaderList.add(newsHeaderBean);
                                    newsHeaderBean = null;
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                    handler.sendEmptyMessage(LOAD_HEADER_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullToRefreshListView.onRefreshComplete();
                Toast.makeText(getActivity(), "图片请求失败", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request_header);
    }

    private void httpRequest() {
        Log.d(TAG, "load httprequest success ----- >>>>> 2");
        StringGBKRequest request = new StringGBKRequest(HTTP_SERVER + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                                        newsBean.setSenddate(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("copen")) {
                                        newsBean.setCopen(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("anonymity")) {
                                        newsBean.setAnonymity(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("ccount")) {
                                        newsBean.setCcount(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("curl")) {
                                        newsBean.setCurl(parser.nextText());
                                    }

                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equalsIgnoreCase("metadata") && newsBean != null) {
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
                Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("mColorRes", mColorRes);
    }

    public class HeaderPagerAdapter extends FragmentPagerAdapter {

        private List<NewsHeaderBean> headerList;
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public HeaderPagerAdapter(FragmentManager fm, List<NewsHeaderBean> list) {
            super(fm);
            headerList = list;

            for (int i = 0; i < headerList.size(); i++) {
                fragments.add(HeaderFragment.newInstance(headerList.get(i).getImg(), headerList.get(i).getImg()));
            }
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
//            return HeaderFragment.newInstance(headerList.get(position).getImg(), headerList.get(position).getImg());
        }

        @Override
        public int getCount() {
            return headerList.size();
        }

    }

}

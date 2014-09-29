package com.fang.wangyi.ui.news;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fang.wangyi.BaseFragment;
import com.fang.wangyi.ColorFragment;
import com.fang.wangyi.MainActivity;
import com.fang.wangyi.R;
import com.fang.wangyi.beans.news.TabBean;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link TabFragment#newInstance}
 * factory method to create an instance of this fragment.
 */
public class TabFragment extends BaseFragment {

	public static final String TAG = "TabFragment";

	private View view;
	private ViewPager viewPager;
	private TabPagerAdapter colorPagerAdapter;
	private TabPageIndicator tabPageIndicator;

	public static List<TabBean> mList;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment TabFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TabFragment newInstance() {
		TabFragment fragment = new TabFragment();
		// Bundle args = new Bundle();
		// args.putSerializable("list", (Serializable) list);
		// fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mList = (List<TabBean>) getArguments().getSerializable("tabList");
		}
		setRetainInstance(true);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("tabList", (Serializable) mList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (savedInstanceState != null) {
			mList = (List<TabBean>) savedInstanceState
					.getSerializable("tabList");
		}

		view = inflater.inflate(R.layout.fragment_tab, container, false);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);

		colorPagerAdapter = new TabPagerAdapter(getActivity()
				.getSupportFragmentManager(), mList);
		viewPager.setAdapter(colorPagerAdapter);
		viewPager.setOffscreenPageLimit(mList.size());// 设置ViewPager 缓存页数
		viewPager.setCurrentItem(0);

		tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
		tabPageIndicator.setViewPager(viewPager);

		tabPageIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {

					}

					@Override
					public void onPageSelected(int position) {
						// switch (position) {
						// case 0:
						// ((MainActivity)
						// getActivity()).getSlidingMenu().setTouchModeAbove(
						// SlidingMenu.TOUCHMODE_FULLSCREEN & SlidingMenu.LEFT);
						// break;
						// default:
						// ((MainActivity)
						// getActivity()).getSlidingMenu().setTouchModeAbove(
						// SlidingMenu.TOUCHMODE_MARGIN);
						// break;
						// }
					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("TabFragment"); // 统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("TabFragment"); // 保证 onPageEnd 在onPause
		// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

	public class TabPagerAdapter extends FragmentStatePagerAdapter {

		private List<TabBean> tabList;
		private List<Fragment> fragments = new ArrayList<Fragment>();

		public TabPagerAdapter(FragmentManager fm, List<TabBean> list) {
			super(fm);
			tabList = list;

			for (int i = 0; i < tabList.size(); i++) {
				fragments.add(NewsFragment.newInstance(tabList.get(i)
						.getTitle(), tabList.get(i).getUrl()));
			}
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			if (position == 0) {
				return NewsHeaderFragment.newInstance(tabList.get(position)
						.getTitle(), tabList.get(position).getUrl());
			} else {
				return fragments.get(position);
			}

		}

		@Override
		public int getCount() {
			return tabList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabList.get(position % tabList.size()).getTitle()
					.toUpperCase();
		}
	}

}

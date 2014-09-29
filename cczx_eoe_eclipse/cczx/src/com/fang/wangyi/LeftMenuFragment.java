package com.fang.wangyi;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fang.wangyi.beans.news.TabBean;
import com.umeng.analytics.MobclickAgent;

public class LeftMenuFragment extends ListFragment {

	public static final String TAG = "LeftMenuFragment";

	private FragmentManager fm;

	private MenuAdapter adapter;

	private List<TabBean> tabList;

	public LeftMenuFragment(List<TabBean> list, FragmentManager fragmentManager) {
		this.tabList = list;
		this.fm = fragmentManager;
		Log.d(TAG, "left list size = " + tabList.size());
	}

	public String TITLES[] = new String[] { "新闻", "图片", "排行", "专题" };

	public int IMAGES[] = new int[] { R.drawable.navigation_tab_news,
			R.drawable.navigation_tab_pics, R.drawable.navigation_tab_news,
			R.drawable.navigation_tab_zts };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_left_menu, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new MenuAdapter(getActivity(), TITLES, IMAGES);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

		getListView().setItemChecked(0, true);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LeftMenuFragment"); // 统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("LeftMenuFragment"); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		((MainActivity) getActivity()).setViewPager(position);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}
		// } else if (getActivity() instanceof ResponsiveUIActivity) {
		// ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
		// ra.switchContent(fragment);
		// }
	}

	private class MenuAdapter extends BaseAdapter {

		private String[] titles;
		private int[] images;
		private Context mContext;

		public int selectedPosition;

		public MenuAdapter(Context context, String[] strs, int[] imgs) {
			mContext = context;
			titles = strs;
			images = imgs;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			this.selectedPosition = position;
		}

		@SuppressLint("NewApi") @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.item_left_menu, parent, false);
				holder = new ViewHolder();
				holder.layout = (RelativeLayout) view
						.findViewById(R.id.navi_tab);
				holder.title = (TextView) view.findViewById(R.id.navi_title);
				holder.image = (ImageView) view.findViewById(R.id.navi_icon);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (position == selectedPosition) {
				// holder.layout.setBackgroundResource(R.drawable.pc_main_plugin_bg_repeat);
				holder.layout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.left_menu_list_selector));
			}

			holder.title.setText(titles[position]);
			holder.image.setImageResource(images[position]);
			return view;
		}

		class ViewHolder {
			RelativeLayout layout;
			TextView title;
			ImageView image;
		}
	}

}

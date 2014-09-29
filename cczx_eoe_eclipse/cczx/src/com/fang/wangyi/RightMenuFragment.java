package com.fang.wangyi;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Use the
 * {@link RightMenuFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class RightMenuFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	//"用户管理",
	private String[] titles = new String[] {  "分享", "用户反馈", "关于我们" };

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment RightMenuFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static RightMenuFragment newInstance(String param1, String param2) {
		RightMenuFragment fragment = new RightMenuFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public RightMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RightMenuFragment"); // 统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("RightMenuFragment"); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_right_menu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				((MainActivity) getActivity()).onItemUser(position);
			}
		});
		RightMenuAdapter adapter = new RightMenuAdapter(getActivity(), titles);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private class RightMenuAdapter extends BaseAdapter {

		private String[] titles;
		private Context mContext;

		public int selectedPosition;

		public RightMenuAdapter(Context context, String[] strs) {
			mContext = context;
			titles = strs;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.item_right_menu, parent, false);
				holder = new ViewHolder();
				holder.layout = (RelativeLayout) view
						.findViewById(R.id.navi_tab);
				holder.title = (TextView) view.findViewById(R.id.navi_title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// if (position == selectedPosition) {
			// //
			// holder.layout.setBackgroundResource(R.drawable.pc_main_plugin_bg_repeat);
			// holder.layout.setBackground(mContext.getResources().getDrawable(R.drawable.left_menu_list_selector));
			// }

			holder.title.setText(titles[position]);
			return view;
		}

		class ViewHolder {
			RelativeLayout layout;
			TextView title;
		}
	}

}

package com.fang.wangyi.adapters.zts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.wangyi.Constant;
import com.fang.wangyi.R;
import com.fang.wangyi.beans.news.NewsBean;
import com.fang.wangyi.beans.zts.ZTBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by changliang on 14-6-20.
 */
public class ZTAdapter extends BaseAdapter implements Constant {

    private List<ZTBean> mList;
    private Context mContext;

    public ZTAdapter(Context context, List<ZTBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_zts, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.info = (TextView) view.findViewById(R.id.info);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(mList.get(position).getTitle());
        holder.info.setText(mList.get(position).getInfo());
        return view;
    }

    class ViewHolder {
        TextView title;
        TextView info;
    }
}

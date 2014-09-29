package com.fang.wangyi.adapters.zts;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.wangyi.Constant;
import com.fang.wangyi.R;
import com.fang.wangyi.beans.zts.SectionBean;
import com.fang.wangyi.beans.zts.ZTListBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Map;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

/**
 * Created by changliang on 14-6-27.
 */
public class ZTListAdapter extends SectionedBaseAdapter implements Constant {

    private Context mContext;
    private List<SectionBean> sectionList;
    private Map<String, List<ZTListBean>> ztsMap;

    public ZTListAdapter(Context context,List<SectionBean> list, Map<String, List<ZTListBean>> map) {
        this.mContext = context;
        this.sectionList = list;
        this.ztsMap = map;
    }

    @Override
    public Object getItem(int section, int position) {
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return ztsMap.size();
    }

    @Override
    public int getCountForSection(int section) {
        return ztsMap.get(sectionList.get(section).getTag()).size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_zts_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.info = (TextView) view.findViewById(R.id.info);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = mContext.getResources().getDimensionPixelSize(R.dimen.list_image_width);
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override public String key() {
                return "transformation" + " desiredWidth";
            }
        };

        holder.title.setText(ztsMap.get(sectionList.get(section).getTag()).get(position).getTitle());
        holder.info.setText(ztsMap.get(sectionList.get(section).getTag()).get(position).getInfo());
//        Picasso.with(mContext).load(HTTP_IMAGE + ztsMap.get(sectionList.get(section).getTag()).get(position).getImg()).placeholder(R.drawable.ic_launcher).transform(transformation).into(holder.image);
        Picasso.with(mContext).load(HTTP_IMAGE + ztsMap.get(sectionList.get(section).getTag()).get(position).getImg()).placeholder(R.drawable.base_load_default_img).fit().centerCrop().into(holder.image);
        return view;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        View view = convertView;
        final HeaderViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_zts_section, parent, false);
            holder = new HeaderViewHolder();
            holder.title = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        holder.title.setText(sectionList.get(section).getTag());

        return view;
    }

    class ViewHolder {
        ImageView image;
        TextView title;
        TextView info;
    }

    class HeaderViewHolder {
        TextView title;
    }
}

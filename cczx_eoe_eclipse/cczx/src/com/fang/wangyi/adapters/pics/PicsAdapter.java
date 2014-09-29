package com.fang.wangyi.adapters.pics;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.wangyi.Constant;
import com.fang.wangyi.R;
import com.fang.wangyi.beans.news.NewsBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by changliang on 14-6-20.
 */
public class PicsAdapter extends BaseAdapter implements Constant {

    private List<NewsBean> mList;
    private Context mContext;

    public PicsAdapter(Context context, List<NewsBean> list) {
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
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_pics, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.title = (TextView) view.findViewById(R.id.title);
//            holder.info = (TextView) view.findViewById(R.id.info);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = holder.image.getWidth();

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

        holder.title.setText(mList.get(position).getTitle());
        Picasso.with(mContext).load(HTTP_IMAGE + mList.get(position).getImg()).placeholder(R.drawable.base_load_big_img).transform(transformation).into(holder.image);
//        holder.info.setText(mList.get(position).getInfo());

        return view;
    }

    class ViewHolder {
        ImageView image;
        TextView title;
//        TextView info;
    }
}

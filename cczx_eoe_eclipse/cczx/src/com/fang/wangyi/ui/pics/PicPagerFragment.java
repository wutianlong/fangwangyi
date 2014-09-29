package com.fang.wangyi.ui.pics;



import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.wangyi.BaseFragment;
import com.fang.wangyi.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PicPagerFragment extends BaseFragment {

    public static final String TAG = "PicPagerFragment";

    private boolean isInit; // 是否可以开始加载数据

    private ImageView imageView;
    private TextView infoView;

    private String url, info;

    public static PicPagerFragment newInstance(String info, String url) {
        PicPagerFragment fragment = new PicPagerFragment();
        Bundle args = new Bundle();
        args.putString("info", info);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate -----------> ");
        if (getArguments() != null) {
            info = getArguments().getString("info");
            url = getArguments().getString("url");
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isInit = true;
        View view = inflater.inflate(R.layout.fragment_pic_pager, container, false);
        imageView = (ImageView) view.findViewById(R.id.image);

        infoView = (TextView) view.findViewById(R.id.info);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume === >>> " + getUserVisibleHint());
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次切换fragment时调用的方法
        Log.d(TAG, "setUserVisibleHint isVisibleToUser == >> " + isVisibleToUser);
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
            Log.d(TAG, "HTTP_IMAGE + url === " + HTTP_IMAGE + url);
            Transformation transformation = new Transformation() {

                @Override public Bitmap transform(Bitmap source) {
                    int targetWidth = imageView.getWidth();

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
            Picasso.with(getActivity()).load(HTTP_IMAGE + url).transform(transformation).into(imageView);
            infoView.setText(info);
        }
    }

}

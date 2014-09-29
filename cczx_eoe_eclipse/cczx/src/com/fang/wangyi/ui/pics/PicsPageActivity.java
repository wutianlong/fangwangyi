package com.fang.wangyi.ui.pics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringGBKRequest;
import com.fang.wangyi.BaseActivity;
import com.fang.wangyi.R;
import com.fang.wangyi.beans.pics.PicsBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class PicsPageActivity extends BaseActivity {

    public static final String TAG = "PicsPageActivity";

    private List<PicsBean> picsList;

    private ViewPager viewPager;
    private PicsPagerAdapter picsPagerAdapter;

    private String title, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_page);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(title);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        httpRequest();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    picsPagerAdapter = new PicsPagerAdapter(getSupportFragmentManager(), picsList);
                    viewPager.setAdapter(picsPagerAdapter);
                    viewPager.setOffscreenPageLimit(picsList.size());
                    break;
            }
        }
    };

    private void httpRequest() {
        StringGBKRequest request = new StringGBKRequest(HTTP_SERVER + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                XmlPullParser parser = Xml.newPullParser();
                PicsBean picsBean = null;
                try {
                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                picsList = new ArrayList<PicsBean>();
                                break;
                            case XmlPullParser.START_TAG:
                                String name = parser.getName();
                                if (name.equalsIgnoreCase("metadata")) {
                                    picsBean = new PicsBean();
                                } else if (picsBean != null) {

                                    if (name.equalsIgnoreCase("img")) {
                                        picsBean.setImg(parser.nextText());
                                    }
                                    if (name.equalsIgnoreCase("info")) {
                                        picsBean.setInfo(parser.nextText());
                                    }

                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equalsIgnoreCase("metadata") && picsBean != null) {
                                    picsList.add(picsBean);
                                    picsBean = null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.pics_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class PicsPagerAdapter extends FragmentStatePagerAdapter {

        private List<PicsBean> picsList;
        private List<PicPagerFragment> fragments = new ArrayList<PicPagerFragment>();

        public PicsPagerAdapter(FragmentManager fm, List<PicsBean> list) {
            super(fm);
            picsList = list;

            for (int i = 0; i < picsList.size(); i++) {
                fragments.add(PicPagerFragment.newInstance(picsList.get(i).getInfo(), picsList.get(i).getImg()));
            }
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
                return fragments.get(position);
        }

        @Override
        public int getCount() {
            return picsList.size();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return picsList.get(position % picsList.size()).getTitle().toUpperCase();
//        }
    }
}

package com.paulavasile.dineunite;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulavasile.dineunite.utils.GalleryNavigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by silve on 5/21/2016.
 */
public class ShareDialog extends Dialog{

    public String mMsg = "";

    ViewPager mViewPager;
    GalleryNavigator navigator;
    SlideShowPagerAdapter	mSlideShowPagerAdapter;

    List<String> mLaunchers = new ArrayList<>();

    private LayoutInflater inflater;

    private Map<String, ResolveInfo> map = new HashMap<>();
    PackageManager pm;
    Context mContext;
    public ShareDialog(Context context, String msg) {
        super(context);
        mContext = context;
        mMsg = msg;
    }

    Intent shareIntent;

    private int[] icons = {R.drawable.facebook, R.drawable.ic_launcher, R.drawable.whatsapp, R.drawable.email, R.drawable.skype, R.drawable.messages};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.9f;
        getWindow().setAttributes(lpWindow);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.dialog_share);

        mViewPager = (ViewPager) findViewById(R.id.pageViewer);
        navigator = (GalleryNavigator) findViewById(R.id.int_navigator);

        mSlideShowPagerAdapter = new SlideShowPagerAdapter(mContext);
        mViewPager.setAdapter(mSlideShowPagerAdapter);

        navigator.setVisibility(View.INVISIBLE);


        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        // TODO Auto-generated method stub
                        navigator.setPosition(arg0);
                        navigator.invalidate();
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                        // TODO Auto-generated method stub

                    }
                });

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadData();
    }

    public void loadData()
    {
        pm = mContext.getPackageManager();
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMsg);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



        List<ResolveInfo> launchables = pm.queryIntentActivities(shareIntent, 0);

        for(ResolveInfo info : launchables)
        {
            String strLabel = info.loadLabel(pm).toString();
            if( strLabel.toLowerCase().equals("skype") && map.get("skype") == null)
            {
                map.put("skype", info);
            }
            else if( strLabel.toLowerCase().equals("messages") && map.get("messages") == null)
            {
                map.put("messages", info);
            }
            else if( strLabel.toLowerCase().equals("whatsapp") && map.get("whatsapp") == null)
            {
                map.put("whatsapp", info);
            }
            else if( strLabel.toLowerCase().equals("email") && map.get("email") == null)
            {
                map.put("email", info);
            }
            else if( strLabel.toLowerCase().equals("facebook") && map.get("facebook") == null)
            {
                map.put("facebook", info);
            }
        }
        mLaunchers.add("Facebook");
        mLaunchers.add("DineUnite");
        mLaunchers.add("Whatsapp");
        mLaunchers.add("Email");
        mLaunchers.add("Skype");
        mLaunchers.add("Messages");

        navigator.setSize(1);
        mSlideShowPagerAdapter.notifyDataSetChanged();
    }

    private class SlideShowPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        List<GridAdapter> adapters = new ArrayList<>();
        public SlideShowPagerAdapter(Context context) {

            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.share_page,
                    container, false);

            GridView logo = (GridView) itemView.findViewById(R.id.grid);
            logo.setAdapter(new GridAdapter());
            logo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = view.getTag();
                    if( o instanceof ResolveInfo )
                    {
                        ResolveInfo info = (ResolveInfo)o;
                        ActivityInfo activity = info.activityInfo;
                        ComponentName name = new ComponentName(
                                activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        mContext.startActivity(shareIntent);
                    }
                }
            });
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }


        public class GridAdapter extends BaseAdapter
        {
            public GridAdapter()
            {

            }

            @Override
            public int getCount() {
                return mLaunchers.size();
            }

            @Override
            public Object getItem(int position) {
                return mLaunchers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.row, null);
                }
                TextView label = (TextView) convertView.findViewById(R.id.label);

                String strKey = mLaunchers.get(position);
                label.setText(strKey);

                ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

                icon.setImageResource(icons[position]);

                convertView.setTag(map.get(strKey.toLowerCase()));
                return convertView;
            }
        }
    }



}

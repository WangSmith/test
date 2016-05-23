package com.paulavasile.dineunite.fragments;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.data.NewsInfo;

public class NewsInfoFragment extends BaseFragment {

	ArrayList<NewsInfo> listInfoList = new ArrayList<NewsInfo>();

	View nodataIv;
	ListView infoLv;
	DataAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		init();
		parentView = inflater.inflate(R.layout.fragment_news, container, false);

		nodataIv = parentView.findViewById(R.id.nodataIv);

		infoLv = (ListView) parentView.findViewById(R.id.infoLv);
		adapter = new DataAdapter(mainActivity);
		infoLv.setAdapter(adapter);
		infoLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});

		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateData();
	}

	private void updateData() {
		
	}

	private class DataAdapter extends BaseAdapter {

		Context mContext;

		public DataAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			if (listInfoList == null)
				return 0;
			return listInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return listInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, null);
			}
			
			return convertView;
		}
	}
}

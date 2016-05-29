package com.example.asyntask;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
	private List<Bean> mList;
	private LayoutInflater mInflater;
	private imageLoader mimageLoader;
	private int mStart,mEnd;
	public static String[] URLS;
	private boolean mFirstIn=true;
	public NewsAdapter(Context context,List<Bean> data,ListView listView){
		mList=data;
		mInflater=LayoutInflater.from(context);
		mimageLoader=new imageLoader(listView);
		URLS=new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			URLS[i]=data.get(i).newsIconURl;
		}
		//注册OnScrollListener
		listView.setOnScrollListener(this);
	}
	public int getCount() {
		return mList.size();
	}
	public Object getItem(int position) {
		return mList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		//用ViewHolder,主要是进行一些性能优化,减少一些不必要的重复操作,将v的tag设置为ViewHolder,不为空时即可重复使用
		viewHolder vHolder=null;
		if (v==null) {
			vHolder=new viewHolder();
			v=mInflater.inflate(R.layout.item, null);//laout文件转化成convertview
			vHolder.Icon=(ImageView) v.findViewById(R.id.imageView1);
			vHolder.title=(TextView) v.findViewById(R.id.title);
			vHolder.content=(TextView) v.findViewById(R.id.content);
			v.setTag(vHolder);
		}else {
			vHolder=(viewHolder) v.getTag();
		}
		vHolder.Icon.setImageResource(R.drawable.ic_launcher);
		String url=mList.get(position).newsIconURl;
		vHolder.Icon.setTag(url);
		//通过showimageThread得到newsIconURl加载新图片
//		new imageLoader().showimageThread(vHolder.Icon, url);
		//不做图片缓存时
//		new imageLoader().showImageByAsyncTask(vHolder.Icon, url);
		//mimageLoader.showImageByAsyncTask(vHolder.Icon, url);//做图片缓存时
		vHolder.title.setText(mList.get(position).newsTitle);
		vHolder.content.setText(mList.get(position).newsContent);
		return v;
	}
	class viewHolder{
		public TextView title,content;
		public ImageView Icon;
	}
	@Override
	public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int endVisibleItem) {
		mStart=firstVisibleItem;
		mEnd=firstVisibleItem+visibleItemCount;
		//第一次显示的时候调用
		if (mFirstIn && visibleItemCount>0) {
			mimageLoader.loadImages(mStart,mEnd);
			System.out.println("------------------");
			mFirstIn=false;
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState==SCROLL_STATE_IDLE) {//闲置时
			//加载可见项
			mimageLoader.loadImages(mStart,mEnd);
		}else {
			mimageLoader.cancelAllTasks();
		}
	}
}

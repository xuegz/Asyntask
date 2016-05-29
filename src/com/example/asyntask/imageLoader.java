package com.example.asyntask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;


public class imageLoader {
	private ImageView mimageView;
	private String murl;
	private ListView mlistView;
	private Set<NewsAsyncTack> mtask;
	//����lruCache
	private LruCache<String,Bitmap>lruCache;
	public imageLoader(ListView listView){
		mlistView=listView;
		mtask=new HashSet<NewsAsyncTack>();
		int maxMemory=(int) Runtime.getRuntime().maxMemory();
		int cachSize=maxMemory/4;
		lruCache=new LruCache<String, Bitmap>(cachSize){
			//�ڲ�����sizeOf����ÿһ��ͼƬ�Ļ����С
			protected int sizeOf(String key, Bitmap value) {
				//��ÿ�δ��뻺��ʱ���ã�����ϵͳ���Ż���ͼƬ�ж��
				return value.getByteCount();
			}
		};
	}

	public void addBitmapToCache(String url,Bitmap bitmap){
		if (getBitmapfromCache(url)==null) {//�жϵ�ǰ�����Ƿ����
			lruCache.put(url, bitmap);
		}
	}
	public Bitmap getBitmapfromCache(String url){
		return lruCache.get(url);//�ɽ�lruCache����map
	}
	
	/*@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mimageView.getTag().equals(murl)) {
				mimageView.setImageBitmap((Bitmap)msg.obj);
			}
		}
	};
	
	public void showimageThread(ImageView imageView,final String url){
		mimageView=imageView;
		murl=url;
		new Thread(){
			public void run() {
				super.run();
				Bitmap bitmap=getBitmapFromUrl(url);
				//��ʱ��bitmap����ֱ�Ӹ���ImageView��Ҫ����Message��bitmap��handler���ͳ�ȥ��Handler��ʹ֮�����߳�ui�и���
				Message message=Message.obtain();
				message.obj=bitmap;
				handler.sendMessage(message);
			}
		}.start();
	}*/
	//��url�л�ȡһ��Bitmap
	public Bitmap getBitmapFromUrl(String urlString){
		Bitmap bitmap;
		InputStream inputStream = null;
		try {
			URL url=new URL(urlString);
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			inputStream=new BufferedInputStream(connection.getInputStream());
			bitmap=BitmapFactory.decodeStream(inputStream);
			connection.disconnect();
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;   //ΪʲôҪ����null
	}

	/*public void showImageByAsyncTask(ImageView imageView,String url){  //��Ĭ��ͼƬimageView����Ҫ���ص�ͼƬ��url
		//�ӻ�����ȡ����Ӧ��ͼƬ
		Bitmap bitmap=getBitmapfromCache(url);
		//���û�оʹ���������
		if (bitmap==null) {
//			new NewsAsyncTack(url).execute(url);
			imageView.setImageResource(R.drawable.ic_launcher);
		}else {
			imageView.setImageBitmap(bitmap);
		}
	}*/
	/*
	 * cancelAllTask()��loadImages�����������������ʱ���Ż�
	 */
	public void cancelAllTasks(){
		if (mtask!=null) {
			for (NewsAsyncTack task : mtask) {
				task.cancel(false);
			}
		}
	}
	
	public void loadImages(int start ,int end){
		for (int i = start; i < end; i++) {
			String url=NewsAdapter.URLS[i];
			//�ӻ�����ȡ����Ӧ��ͼƬ
			Bitmap bitmap=getBitmapfromCache(url);
			//���û�оʹ���������
			if (bitmap==null) {
				NewsAsyncTack task=new NewsAsyncTack(url);
				task.execute(url);
				mtask.add(task);
//				new NewsAsyncTack(imageView, url).execute(url);
			}else {
//				imageView.setImageBitmap(bitmap);
				ImageView imageView=(ImageView) mlistView.findViewWithTag(url);
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	private class NewsAsyncTack extends AsyncTask<String, Void, Bitmap>{
		//дһ�����췽������imageview
//		private ImageView mimageView;
		private String murl;
		public NewsAsyncTack(String url){
//			mimageView=imageView;
			murl=url;
		}
		protected Bitmap doInBackground(String... arg0) {
			//			return getBitmapFormUrl(arg0[0]);// ����Ҫlistview�����Ż�����ʱ
			String url=arg0[0];
			//�������ϻ�ȡͼƬ
			Bitmap bitmap=getBitmapFromUrl( url);
			if (bitmap!=null) {
				//�����ڻ����ͼƬ���뻺��
				addBitmapToCache(url, bitmap);
			}
			return bitmap;
		}
		
		protected void onPostExecute(Bitmap bitmap) {//��bitmap���ø�imageview
			super.onPostExecute(bitmap);
//			if (mimageView.getTag().equals(murl)) {
//				mimageView.setImageBitmap(bitmap);
//			}
//			mimageView.setImageBitmap(bitmap);
			ImageView imageView=(ImageView) mlistView.findViewWithTag(murl);
			System.out.println("imageView==null:"+imageView==null);
			System.out.println("bitmap==null:"+bitmap==null);
			if (imageView!=null && bitmap!=null) {
				imageView.setImageBitmap(bitmap);
			}
			mtask.remove(this);  //��ʱAsyncTack�Ѿ�����������mtask����AsyncTack����
		}
	}
}

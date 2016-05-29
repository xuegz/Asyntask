package com.example.asyntask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView listView;
	private static String URL="http://www.imooc.com/api/teacher?type=4&num=30";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView=(ListView) findViewById(R.id.listview);
		new NewsAsynTask().execute(URL);//��һ��NewsAsynTask��һ��url���ݽ�����ʵ�ֶ�url���첽����
	}
	//��ȡjson
	private List<Bean> getJsonData(String url){//�ڶ���getJsonData��urlת��Ϊ����Ҫ��json����
		List<Bean> beanList=new ArrayList<Bean>();
		String jsonString;
		try {
			jsonString = readStream(new java.net.URL(url).openStream());
			JSONObject jsonObject;
			Bean bean;
			jsonObject=new JSONObject(jsonString);
			JSONArray jsonArray=jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject=jsonArray.getJSONObject(i);//jsonArray��ÿһ������jsonObject
				bean=new Bean();//��ʼ��bean
				bean.newsIconURl=jsonObject.getString("picSmall");
				bean.newsTitle=jsonObject.getString("name");
				bean.newsContent=jsonObject.getString("description");
				beanList.add(bean);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return beanList;
	}
	//��ȡjson���ַ���
	private String readStream(InputStream in){//������һ���ֽ���
		InputStreamReader inReader;
		String result="";
		try {
			String line="";
			inReader=new InputStreamReader(in,"utf-8");//ͨ��InputStreamReader���ֽ���ת�����ַ���
			BufferedReader bf=new BufferedReader(inReader);//ͨ��BufferedReader���ַ����ٶ�ȡ����
			while ((line=bf.readLine())!=null) {
				result+=line;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	//ʵ��������첽����
	class NewsAsynTask extends AsyncTask<String, Void, List<Bean>>{//Void ��ʾ����¼�м����
		protected List<Bean> doInBackground(String... param) {
			return getJsonData(param[0]);
		}
		//������ onPostExecute�����ɵ����ݴ��ݸ�NewsAdapter �������ɵ�bean���ø�listview 
			protected void onPostExecute(List<Bean> bean) {
				super.onPostExecute(bean);
				NewsAdapter adapter=new NewsAdapter(MainActivity.this,bean,listView);
				listView.setAdapter(adapter);
			}
	}
}

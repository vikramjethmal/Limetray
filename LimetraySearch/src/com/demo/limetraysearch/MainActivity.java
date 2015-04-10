package com.demo.limetraysearch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.demo.limetray.core.AppConstant;
import com.demo.limetray.core.Engine;
import com.demo.limetraysearch.dao.SearchData;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	Engine engObj = null;
	final static String SearchTerm = "limetray";
	public static Handler uiHandler;                       
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize Engine
        engObj = Engine.getInstance();
        if(!engObj.init(getApplicationContext(),MainActivity.this))
        {
        	Log.e(TAG, "Engine Initialization falied");
        	return;
        }
        
        /**
		 * Event Handler Block Start 
		 */
		uiHandler = new Handler()
		{	
			public void handleMessage(final Message msg) 
			{ 
				if(msg == null)
				{
					Toast.makeText(getApplicationContext(), "Search Response is Null", Toast.LENGTH_SHORT).show();
					return;
				}
				
				switch (msg.what) 
				{
				case Engine.NET_SEARCH_REQ:
				{
					if(msg.obj == null)
					{
						Toast.makeText(getApplicationContext(), "Search Response is Null", Toast.LENGTH_SHORT).show();
						return;
					}
					
					List<SearchData> data = (List<SearchData>)msg.obj;
					ArrayList<String> listData = new ArrayList<String>(); 
					for (SearchData searchData : data) {
						listData.add(searchData.getText());
					}
					
					// send the tweets to the adapter for rendering
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listData);
					ListView lv = (ListView)findViewById(R.id.list_items);
					lv.setAdapter(adapter);
				}
				break;
				case Engine.DATABASE_REQ:
				{
					
				}
				break;
				default:
					break;
					
				}
			}
		};
		
		
    }
    
    public void searchLimetray(View v)
    {
    	String text = ((EditText)findViewById(R.id.editText1)).getText().toString();
    	if(TextUtils.isEmpty(text))
    	{
    		Toast.makeText(getApplicationContext(), "Search field cannot be empty.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	int result = engObj.downloadSearches(text);
		if(result == AppConstant.IN_PROGRESS)
		{
			Toast.makeText(getApplicationContext(), "Download in progress", Toast.LENGTH_SHORT).show();
		}
		else if(result == AppConstant.NET_NOT_AVAILABLE)
		{
			Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getApplicationContext(), "Invalid search request", Toast.LENGTH_SHORT).show();
    }
    
    public void showLimetrayGraph(View v)
    {
    	List<SearchData> dataList = Engine.getInstance().getAllSearchDataList(); 
    	if(dataList != null && dataList.size() > 0)
    	{
    		LimetrayTweetChart atc = new LimetrayTweetChart();
    		Intent intent = atc.execute(getApplicationContext(),dataList);
    		startActivity(intent);
    	}
    	else
    	{
    		Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void searchResponse(int evType, Object obj)
    {
    	if(uiHandler != null)
		{
			Message msgResp = uiHandler.obtainMessage(evType, "Search Response");
			msgResp.obj = obj;
			uiHandler.sendMessage(msgResp);
		}// End of if()
    }
}

package com.demo.limetray.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import com.demo.limetraysearch.MainActivity;
import com.demo.limetraysearch.dao.DaoMaster;
import com.demo.limetraysearch.dao.DaoMaster.DevOpenHelper;
import com.demo.limetraysearch.dao.DaoSession;
import com.demo.limetraysearch.dao.SearchData;
import com.demo.limetraysearch.dao.SearchDataDao;
import com.google.gson.Gson;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;


public class Engine {

	Context appContext;
	private MainActivity screenObj;
	private Handler engineHandler;
	public final static String ENGINETag = "ENGINE";
	public static Engine engObj = null;
	private boolean initFlag = false;
	private SQLiteDatabase 				db; //This variable hold database object
	private DaoMaster 					daoMaster; // This variable hold daoMaster object which is generated by GreenDao.
	private DaoSession 					daoSession; // This variable hold daoSession object which is generated by GreenDao.
	public SearchDataDao 		searchDataDao; // This variable hold Search Data Table object which is generated by GreenDao.

	private final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
	private final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";
	public final static int DATABASE_REQ = 0;
	public final static int NET_SEARCH_REQ = 1;
	String Key = null;
	String Secret = null;



	/**
	 * Constructor of Engine is private to make class singleton
	 */
	private Engine() {

	}

	/**
	 * This function is to get instance of Engine . This is creating new
	 * instance only when Engine instance is null
	 * 
	 * @return Engine Instance
	 */
	public static Engine getInstance() {
		if (engObj == null) {
			System.out.println("getInstance : NULL ENGINE OBJECT ");
			engObj = new Engine();
		}
		return engObj;
	}

	/**
	 * This is initialization function for Engine 
	 * @param Context 
	 * @param ScreenObject 
	 * @return void
	 */ 
	public boolean init(Context context,MainActivity obj) 
	{
		appContext = context;
		screenObj = obj;
		Key = getStringFromManifest("CONSUMER_KEY",appContext);
		Secret = getStringFromManifest("CONSUMER_SECRET",appContext);

		if(!initDB())
		{
			Log.e(ENGINETag,"Unable to initialize database.");
			return false;
		}
		//----- Starting the Looper for Engine Module ----//
		new LoopThread().start();
		initFlag = true;
		return initFlag;
	}// End of init()

	private String getStringFromManifest(String key, Context context) {
		String results = null;

		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			results = (String)ai.metaData.get(key);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return results;
	}

	public boolean initDB()
	{ 
		boolean result = false;

		try
		{
			DevOpenHelper openHelper = new DevOpenHelper(appContext, "Limetray-db", null);
			db = openHelper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			searchDataDao = daoSession.getSearchDataDao();

			result = true;
		}
		catch(SQLException e)
		{
			Log.e("Engine","Database SQLException Initialization Error: "+e);
		}
		catch(Exception e)
		{
			Log.e(ENGINETag,"Database Initialization Error: "+e);
		}

		return result;
	}


	public int addUIEvent(int evType, Object obj)
	{
		if(evType < 0)
			return AppConstant.INVALID_EVENT_TYPE;

		switch (evType) {
		case DATABASE_REQ:
		{

		}
		break;
		case NET_SEARCH_REQ:
		{
			Message msg = engineHandler.obtainMessage(evType,"Search Request");
			msg.obj = obj;
			engineHandler.sendMessage(msg);
		}
		break;

		default:
			break;
		}

		return AppConstant.IN_PROGRESS;
	}

	// download twitter searches after first checking to see if there is a network connection
	public int downloadSearches(String searchterm) {
		ConnectivityManager connMgr = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		int result = AppConstant.IN_PROGRESS;
		if (networkInfo != null && networkInfo.isConnected()) {
			result = addUIEvent(NET_SEARCH_REQ, searchterm);
		} else {
			Log.v(ENGINETag, "No network connection available.");
			return AppConstant.NET_NOT_AVAILABLE;
		}
		return result;
	}

	// converts a string of JSON data into a SearchResults object
	private ArrayList<SearchResults> jsonToSearches(String result) {
		ArrayList<SearchResults> searches = null;
		if (result != null && result.length() > 0) {
			searches = new ArrayList<SearchResults>();
			try {
				Gson gson = new Gson();
				// bring back the entire search object
				SearchResults sr = gson.fromJson(result, SearchResults.class);
				searches.add(sr);
			} catch (IllegalStateException ex) {
			}
		}
		return searches;
	}

	public void notifyUI(int evType,Object obj)
	{
		switch (evType) {
		case NET_SEARCH_REQ:
		{
			screenObj.searchResponse(evType,obj);
		}	
		break;

		default:
			break;
		}
	}


	public class LoopThread extends Thread {

		public void run() 
		{
			//---- Prepare Looper ----//
			Looper.prepare();
			//---- Event handler block ----//
			engineHandler = new Handler() 
			{
				public void handleMessage(Message msg) 
				{					
					switch (msg.what) 
					{
					case DATABASE_REQ: 
					{

					}
					break;
					case NET_SEARCH_REQ: 
					{
						String searchTerms = (String)msg.obj;
						String result = null;
						if (searchTerms.length() > 0) {
							result = getSearchStream(searchTerms);
						}
						
						ArrayList<SearchResults> search = jsonToSearches(result);
						for (SearchResults searchResult : search) {

							if(searchResult != null && (searchResult.getStatuses() != null && searchResult.getStatuses().size() > 0 ))
							{
								for (int i = 0; i < searchResult.getStatuses().size(); i++) {

									Search tweetSearch = searchResult.getStatuses().get(i);
									if(tweetSearch != null)
									{
										SearchData data = new SearchData();
										data.setDateCreated(tweetSearch.getDateCreated());
										data.setId(tweetSearch.getId());
										data.setIdStr(tweetSearch.getIdStr());
										data.setInReplyToScreenName(tweetSearch.getInReplyToScreenName());
										data.setInReplyToStatusId(tweetSearch.getInReplyToStatusId());
										data.setInReplyToStatusIdStr(tweetSearch.getInReplyToStatusIdStr());
										data.setInReplyToUserId(tweetSearch.getInReplyToUserId());
										data.setInReplyToUserIdStr(tweetSearch.getInReplyToUserIdStr());
										data.setIsTruncated(tweetSearch.getIsTruncated());
										data.setText(tweetSearch.getText());
										data.setSource(tweetSearch.getSource());

										
										if(isDataExist(data.getId())){
											searchDataDao.update(data);
										}
										else{
											searchDataDao.insert(data);
										}
									}
								}	
							}
						}
						
						// Send data to UI
						notifyUI(msg.what, getAllSearchDataList());
					}
					break;
					default:
						break;
					}
				}

				
			};
			//----- Start looping the message queue of this thread ----//
    		Looper.loop();
		}
	}
	
	private boolean isDataExist(Long id) 
	{
		List<SearchData> searchData = searchDataDao.queryBuilder().where(SearchDataDao.Properties.Id.eq(id)).list();
		if(searchData == null || searchData.size() == 0)
			return false;
		return true;
	}
	
	public List<SearchData> getAllSearchDataList()
	{
		List<SearchData> dataTable = null;
		try
		{
			dataTable = searchDataDao.queryBuilder().orderDesc(SearchDataDao.Properties.Id).list();
			daoSession.clear();
		}
		catch(Exception e)
		{
			Log.e(ENGINETag,"Data not found in Search data Table");
		}

		return dataTable;
	}

	private String getResponseBody(HttpRequestBase request) {
		StringBuilder sb = new StringBuilder();
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();

			if (statusCode == 200) {

				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				String line = null;
				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}
			} else {
				sb.append(reason);
			}
		} catch (UnsupportedEncodingException ex) {
			Log.e(ENGINETag, "UnsupportedEncodingException is :"+ex);
		} catch (ClientProtocolException ex1) {
			Log.e(ENGINETag, "ClientProtocolException is :"+ex1);
		} catch (IOException ex2) {
			Log.e(ENGINETag, "IOException is :"+ex2);
		}
		Log.i(ENGINETag, sb.toString());
		return sb.toString();
	}

	private String getStream(String url) {
		String results = null;

		// Step 1: Encode consumer key and secret
		try {
			// URL encode the consumer key and secret
			String urlApiKey = URLEncoder.encode(Key, "UTF-8");
			String urlApiSecret = URLEncoder.encode(Secret, "UTF-8");

			// Concatenate the encoded consumer key, a colon character, and the encoded consumer secret
			String combined = urlApiKey + ":" + urlApiSecret;

			// Base64 encode the string
			String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

			// Step 2: Obtain a bearer token
			HttpPost httpPost = new HttpPost(TwitterTokenURL);
			Log.i(ENGINETag, TwitterTokenURL);
			httpPost.setHeader("Authorization", "Basic " + base64Encoded);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
			String rawAuthorization = getResponseBody(httpPost);
			Authenticated auth = jsonToAuthenticated(rawAuthorization);

			// Applications should verify that the value associated with the
			// token_type key of the returned object is bearer
			if (auth != null && auth.token_type.equals("bearer")) {

				// Step 3: Authenticate API requests with bearer token
				HttpGet httpGet = new HttpGet(url);

				// construct a normal HTTPS request and include an Authorization
				// header with the value of Bearer <>
				httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
				httpGet.setHeader("Content-Type", "application/json");
				// update the results with the body of the response
				results = getResponseBody(httpGet);
			}
		} catch (UnsupportedEncodingException ex) {
			Log.e(ENGINETag, "UnsupportedEncodingException is :" +ex);
		} catch (IllegalStateException ex1) {
			Log.e(ENGINETag, "IllegalStateException is :" +ex1);
		}
		return results;
	}

	// convert a JSON authentication object into an Authenticated object
	private Authenticated jsonToAuthenticated(String rawAuthorization) {
		Authenticated auth = null;
		if (rawAuthorization != null && rawAuthorization.length() > 0) {
			try {
				Gson gson = new Gson();
				auth = gson.fromJson(rawAuthorization, Authenticated.class);
			} catch (IllegalStateException ex) {
				// just eat the exception for now, but you'll need to add some handling here
			}
		}
		return auth;
	}


	private String getSearchStream(String searchTerm) {
		String results = null;
		try {
			String encodedUrl = URLEncoder.encode(searchTerm, "UTF-8");
			results = getStream(TwitterSearchURL + encodedUrl);
		} catch (UnsupportedEncodingException ex) {
			Log.e(ENGINETag, "UnsupportedEncodingException is :"+ex);
		} catch (IllegalStateException ex1) {
			Log.e(ENGINETag, "IllegalStateException is :"+ex1);
		}
		return results;
	}
}
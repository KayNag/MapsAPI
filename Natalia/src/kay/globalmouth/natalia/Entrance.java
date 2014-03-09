package kay.globalmouth.natalia;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.Menu;
import android.widget.ListView;

import kay.globalmouth.natalia.tasklist.JsonHandler;
 
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class Entrance extends ListActivity {
 
    private ProgressDialog pDialog;
 
    // URL to get alarms JSON
    private static String url = "http://natalia.globalmouth.com/api/alarms";
 
    // JSON Node names
    private static final String TAG_alarms = "alarms";
    private static final String TAG_ID = "id";
    private static final String LATTITUDE = "lat";
    private static final String LONGITUDE = "long";
    private static final String NAME = "name";
    private static final String TAG_GEOCODE = "geocode";
   // private static final String PHOTO = "photo";
    private static final String TIMESTAMP = "timestamp";
    private static final String PHOTO_HOME = "home";
    private static final String PHOTO_OFFICE = "office";
 
    // alarms JSONArray
    JSONArray alarms = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
 
        contactList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
 
        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String email = ((TextView) view.findViewById(R.id.email))
                        .getText().toString();
                String description = ((TextView) view.findViewById(R.id.mobile))
                        .getText().toString();
 
                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        List_view_selection.class);
                in.putExtra(LATTITUDE, name);
                in.putExtra(LONGITUDE, email);
                in.putExtra(TIMESTAMP, description);
                startActivity(in);
 
            }
        });
 
        // Calling async task to get json
        new Getalarms().execute();
    }
 
    /**
     * Async task class to get json by making HTTP call
     * */
    private class Getalarms extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Entrance.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
        	JsonHandler sh = new JsonHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, JsonHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    alarms = jsonObj.getJSONArray(TAG_alarms);
 
                    // looping through All alarms
                    for (int i = 0; i < alarms.length(); i++) {
                        JSONObject c = alarms.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                        String lat = c.getString(LATTITUDE);
                        String longi = c.getString(LONGITUDE);
                        String name = c.getString(NAME);
                        
                        String geocode = c.getString(TAG_GEOCODE);
 
                        // photo node is JSON Object
                     // JSONObject photo = c.getJSONObject(PHOTO);
                     // String timestamp = photo.getString(TIMESTAMP);
                       
 
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        contact.put(NAME, name);
                        contact.put(LATTITUDE,lat);
                        contact.put(TAG_GEOCODE, geocode);
                     // contact.put(TIMESTAMP, timestamp);
 
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    Entrance.this, contactList,
                    R.layout.listshow, new String[] { NAME, LONGITUDE,
                            TAG_GEOCODE }, new int[] { R.id.name,
                            R.id.email, R.id.mobile });
 
            setListAdapter(adapter);
        }
 
    }
 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entrance, menu);
		return true;
	}

}

package diana.soleil.newshacker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    WebView webView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    public class DownloadJson extends AsyncTask<String,Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String result ="";
                URL url = new URL(strings[0]);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.connect();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data > -1) {
                    char c = (char) data;
                    result += c;
                    data = inputStreamReader.read();
                }
                return  result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Failed";
            } catch (IOException e) {
                e.printStackTrace();
                return  "Failed";
            }

        }
    }
    public String downloadString (String website) {
        String resultMain = "";

        DownloadJson downloadJson = new DownloadJson();
        try {
            resultMain = downloadJson.execute(website).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultMain;
    }
    public void arrayListReturner () {
        ArrayList<String> arrayList2 = new ArrayList<String>();
        String test ="";
        JSONObject jsonObject = null;
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(downloadString("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty"));
            for (int i =0; i <10; i++) {
                jsonObject = new JSONObject(downloadString("https://hacker-news.firebaseio.com/v0/item/"+jsonArray.getString(i)+".json?print=pretty"));
                arrayList2.add( jsonObject.getString("url"));
                test = jsonObject.getString("title");
                arrayList.add(test);
                Log.i("ResultMain" , test);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        JSONObject finalJsonObject = jsonObject;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);
                intent.putExtra("news", arrayList2.get(i));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayListReturner();



    }
}
package lighterletter.com.vineexercise;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private IssueAdapter issueAdapter = null;
    private final String url = "https://api.github.com/repos/rails/rails/issues";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.issues_list_view);

        if (isNetworkAvailable()) {
            new NetworkTask().execute(url);
        } else {
            Toast toast = Toast.makeText(this, "No network available. Try later.", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public class NetworkTask extends AsyncTask<String, Void, Integer> {
        private ArrayList<String> userNames = new ArrayList<String>();
        private ArrayList<String> issueTitles = new ArrayList<String>();
        private ArrayList<String> issueBodies = new ArrayList<String>();
        private ArrayList<String> commentUrls = new ArrayList<String>();

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            try {

                URL url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {

                    Log.v("okay", statusCode + "");
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    parseResult(response);
                    result = 1; // Data!

                } else {

                    result = 0; //"No data :(";

                }

            } catch (Exception e) {
                Log.d("doInBackground", e.getLocalizedMessage());
            }
            return result; //defaults to no data
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer == 1) {
                //success! set the main adapter

                issueAdapter = new IssueAdapter(MainActivity.this, userNames,
                        issueTitles, issueBodies, commentUrls);
                listView.setAdapter(issueAdapter);

            } else {
                Log.e("issuePostExecute", "Failed to fetch data!");
            }

        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            inputStream.close();
            return result;
        }

        private void parseResult(String result) {

            try {

                JSONArray jsonarray = new JSONArray(result);

                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String comments_url = jsonobject.getString("comments_url");
                    commentUrls.add(i, comments_url);

                    String title = jsonobject.getString("title");
                    issueTitles.add(i, title);

                    JSONObject user = jsonobject.getJSONObject("user");
                    String login = user.getString("login");
                    userNames.add(i, "By: " + login);

                    String body = jsonobject.getString("body");
                    issueBodies.add(i, body);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

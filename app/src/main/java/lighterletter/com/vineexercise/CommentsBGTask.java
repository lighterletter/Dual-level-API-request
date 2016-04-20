package lighterletter.com.vineexercise;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

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

/**
 * Created by john on 4/19/16.
 */

public class CommentsBGTask extends AsyncTask<String, Void, Integer> {

    private Context context;
    private ArrayList<String> commentNames = new ArrayList<String>();
    private ArrayList<String> commentBodies = new ArrayList<String>();


    public CommentsBGTask(Context context) {
        this.context = context;
    }

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
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                parseResult(response);
                result = 1;
            } else {
                result = 0;
            }

        } catch (Exception e) {
            Log.d("doInBackground", e.getLocalizedMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer == 1) {
            //success! set comment adapter

            //I logged throughout but left these here for posterity
            Log.i("commentNamePE", "title = <<" + commentNames + ">>");
            Log.i("commentBodyPE", "username = <<" + commentBodies + ">>");

            //create and populate dialog
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.comments_dialog);
            ListView lv = (ListView) dialog.findViewById(R.id.comments_list_view);
            CommentAdapter adapter = new CommentAdapter(context, commentNames, commentBodies);
            lv.setAdapter(adapter);
            dialog.setCancelable(true);

            if (commentBodies.isEmpty()) {
                dialog.setTitle("No Comments Yet");
            } else {
                dialog.setTitle("Comments");
            }
            dialog.show();

        } else {
            Log.e("onPostExecute", "Failed to fetch data!");
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
                JSONObject user = jsonobject.getJSONObject("user");
                String login = user.getString("login");
                commentNames.add(i, "by: " + login);
                String body = jsonobject.getString("body");
                commentBodies.add(i, body);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

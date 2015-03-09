package ru.ilfat.testplusofon;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import ru.ilfat.testplusofon.json.AddRequest;
import ru.ilfat.testplusofon.json.ApiResponse;
import ru.ilfat.testplusofon.json.GetRequest;


public class MainActivity extends ActionBarActivity {

    final String NUMBER_NOT_RECOGNIZED = "Номер не распознан";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        final View progressBar = findViewById(R.id.loadInfoProgress);
        final TextView infoText = (TextView) findViewById(R.id.infoText);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
//                try {
//                    HttpURLConnection connection = (HttpURLConnection) new URL("http://109.107.169.222:8080").openConnection();
//                    connection.setRequestMethod("POST");

//                } catch (MalformedURLException notImportant) {
//                }
//                catch (IOException e)
//                {
//                    return e.getMessage();
//                }

                Long phone = getIntent().getLongExtra("number", 0L);
                if (phone != 0)
                    return postData(phone);
                else return
                        NUMBER_NOT_RECOGNIZED;
            }

            @Override
            protected void onPostExecute(Object o) {
                progressBar.setVisibility(View.GONE);
                infoText.setText((String) o);
            }
        }.execute();
    }

    public String postData(long phone) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://109.107.169.222:8080/get");

        String result = "nonono";
        try {
            httppost.setEntity(new StringEntity(new Gson().toJson(new GetRequest(phone)), "UTF-8"));
//            httppost.setEntity(new StringEntity(new Gson().toJson(new AddRequest(79370049787L, "Ильфат", "Абдуллин")), "UTF-8"));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Header[] headers = response.getHeaders("Content-Length");
            int contentLength = Integer.parseInt(headers[0].getValue());
            byte[] bytes = new byte[contentLength];
            response.getEntity().getContent().read(bytes);
            result = new String(bytes, "UTF-8");

            ApiResponse apiResponse = new Gson().fromJson(result, ApiResponse.class);

            result = apiResponse.getPerson().getSurname() + " " + apiResponse.getPerson().getName();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            result = "Ошибка соединения с сервером";
        }
        catch (NullPointerException e)
        {
            result = NUMBER_NOT_RECOGNIZED;
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


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
import ru.ilfat.testplusofon.json.Person;


public class CallInfoActivity extends ActionBarActivity {
    public final static String EXTRA_PHONE_NUMBER = "phoneNumber";
    final static String NUMBER_NOT_RECOGNIZED = "Номер не распознан";
    final static long NUMBER_EMPTY = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View progressBar = findViewById(R.id.loadInfoProgress);
        final TextView infoText = (TextView) findViewById(R.id.infoText);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String result = NUMBER_NOT_RECOGNIZED;
                Long phone = getIntent().getLongExtra(EXTRA_PHONE_NUMBER, NUMBER_EMPTY);
                if (phone != NUMBER_EMPTY)
                    result = postData(phone);
                return result;
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

        String result = NUMBER_NOT_RECOGNIZED;
        try {
            httppost.setEntity(new StringEntity(new Gson().toJson(new GetRequest(phone)), "UTF-8"));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Header[] headers = response.getHeaders("Content-Length");
            int contentLength = Integer.parseInt(headers[0].getValue());
            byte[] bytes = new byte[contentLength];
            response.getEntity().getContent().read(bytes);
            String requestResult = new String(bytes, "UTF-8");

            ApiResponse apiResponse = new Gson().fromJson(requestResult, ApiResponse.class);
            Person person = apiResponse.getPerson();

            result = person.getSurname() + " " + person.getName();
        } catch (ClientProtocolException e) {
            result = "Ошибка HTTP";
        } catch (IOException e) {
            result = "Ошибка соединения с сервером";
        } catch (NullPointerException e) {
            result = NUMBER_NOT_RECOGNIZED;
        }
        return result;
    }
}


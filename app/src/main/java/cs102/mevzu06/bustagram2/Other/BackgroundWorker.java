package cs102.mevzu06.bustagram2.Other;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Murat on 9.05.2017.
 */

public class BackgroundWorker extends AsyncTask<String,Void,String>
{
    Context context;
    AlertDialog alertDialog;
    public BackgroundWorker(Context ctx) { context = ctx; }


    @Override
    protected String doInBackground(String... params)
    {
        String type = params[0];
        String send_location_url = "http://bustagram.000webhostapp.com/upload.php";
        String update_location_url = "http://bustagram.000webhostapp.com/updatelocation.php";

        if(type.equals("uploadLocation"))
        {
            try {
                String latitude = params[1];
                String longitude = params[2];
                String speed = params[3];
                URL url = new URL(send_location_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")+"&"
                        + URLEncoder.encode("longitude", "UTF-8")+"="+URLEncoder.encode(longitude,"UTF-8")+"&"
                        + URLEncoder.encode("speed", "UTF-8")+"="+URLEncoder.encode(speed,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";

                while((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (type.equals("updateLocation"))
        {
            try
            {
                String latitude = params[1];
                String longitude = params[2];
                String speed = params[3];
                URL url = new URL(update_location_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")+"&"
                        + URLEncoder.encode("longitude", "UTF-8")+"="+URLEncoder.encode(longitude,"UTF-8")+"&"
                        + URLEncoder.encode("speed", "UTF-8")+"="+URLEncoder.encode(speed,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";

                while((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}

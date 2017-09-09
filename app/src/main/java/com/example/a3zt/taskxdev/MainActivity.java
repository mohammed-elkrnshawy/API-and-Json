package com.example.a3zt.taskxdev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=android";
    private ListView listView;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        listView = (ListView)findViewById(R.id.lvBooks);
        new JSONTask().execute(API_URL);

    }

    public class JSONTask extends AsyncTask<String,String, List<BookDetails> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<BookDetails> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("items");

                List<BookDetails> bookDetailsList = new ArrayList<>();

                for(int i=0; i<parentArray.length(); i++) {
                    String Authors="";
                    String Categories="";
                    JSONObject jsonObject = parentArray.getJSONObject(i);
                    JSONObject VolumeInfo = jsonObject.getJSONObject("volumeInfo");
                    JSONObject SaleInfo = jsonObject.getJSONObject("saleInfo");
                    String Title = VolumeInfo.getString("title");
                    String Publisher = VolumeInfo.getString("publisher");
                    String PublisherDate = VolumeInfo.getString("publishedDate");
                    String Description = Description(VolumeInfo);
                    String Info=VolumeInfo.getString("infoLink");
                    try {
                       Authors=GetAuthors(VolumeInfo,"authors");
                        Categories=GetAuthors(VolumeInfo,"categories");;
                    }
                    catch (Exception e)
                    {

                    }
                    String Images = VolumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");



                    BookDetails bookDetails = new BookDetails(Title,Publisher,PublisherDate,Images,ForSale(SaleInfo)
                            ,Description,Authors,Categories,Info);
                    bookDetailsList.add(bookDetails);
                }
                return bookDetailsList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final List<BookDetails> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                BookAdapter adapter = new BookAdapter(getApplicationContext(), R.layout.row, result);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url =result.get(position).Info;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
        protected String GetAuthors(JSONObject jsonObject,String Tag) throws JSONException {
            String list="";
            for (int i=0;i<jsonObject.getJSONArray(Tag).length();i++)
            {
                if(i%2==0)
                list+= jsonObject.getJSONArray(Tag).getString(i).toString();
                else
                    list+= " - "+jsonObject.getJSONArray(Tag).getString(i).toString();
            }
            return list;
        }

        protected String ForSale(JSONObject SaleInfo) throws JSONException {
            String Saleability = SaleInfo.getString("saleability");
            if (Saleability.contains("NOT")) {
                return Saleability;
            } else {
                String Price = SaleInfo.getJSONObject("retailPrice").getString("amount")+" "+SaleInfo.getJSONObject("retailPrice").getString("currencyCode");
                return Price;
            }
        }

        protected String Description(JSONObject VolumeInfo) throws JSONException {
            if (VolumeInfo.has("description")) {
                return VolumeInfo.getString("description");
            } else {
                return "No Description";
            }
        }
    }



}

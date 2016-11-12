package com.example.valeria.waveproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ProgressDialog prgDialog;
    ListView listView;
    String token = "6W9hcvwRvyyZgPu9Odq7ko8DSY8Nfm";
    String id = "89746d57-c25f-4cec-9c63-34d7780b044b";
    String ip = "https://api.waveapps.com";
    List<String> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listview);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization","Bearer "+token);
        client.get(ip+"/businesses/"+id+"/products/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responce = new String(responseBody, "UTF-8");
                    Gson gson = new Gson();
                    System.out.println(responce);
                    Product[] products = gson.fromJson(responce, Product[].class);
                    for (Product product: products){
                        String productitem = product.getName() + ": $" + String.valueOf(product.getPrice());
                        items.add(productitem);
                    }
                    prgDialog.hide();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                    listView.setAdapter(adapter);

                }catch (Exception e) {
                    System.err.println(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    Toast.makeText(getApplicationContext(),"Login Failure!", Toast.LENGTH_SHORT).show();
                }
                // When Http response code is '404'
                else if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    public class Product {
        // Only these two paras are needed
        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public float price;
        public String getName(){return name;}
        public float getPrice(){return price;}
    }
}

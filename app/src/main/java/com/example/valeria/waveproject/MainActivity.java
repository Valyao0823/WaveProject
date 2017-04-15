package com.example.valeria.waveproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.client.SystemDefaultCredentialsProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ProgressDialog prgDialog;
    ListView listView;
    Button button;
    String token = "6W9hcvwRvyyZgPu9Odq7ko8DSY8Nfm";
    String id = "89746d57-c25f-4cec-9c63-34d7780b044b";
    String ip = "https://api.waveapps.com";
    List<String> items = new ArrayList<>();
    SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listview);
        button = (Button)findViewById(R.id.button);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        try {

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization","Bearer "+token);
            client.get(ip+"/businesses/"+id+"/products/", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    System.out.println("OnSuccess is called!!!");
                    try{
                        String responce = new String(responseBody, "UTF-8");
                        Gson gson = new Gson();
                        System.out.println(responce);
                        Product[] products = gson.fromJson(responce, Product[].class);
                        editor = getSharedPreferences("data", 0).edit();
                        editor.putString("item", responce);
                        editor.commit();
                        for (Product product: products){
                            String productitem = product.name + ": $" + String.valueOf(product.price);
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
                    String data = getSharedPreferences("data", 0).getString("item", null);
                    if (data!=null){
                        System.out.println("Failed !!! data is not empty");
                        Toast.makeText(MainActivity.this, "Internet Error, showing the cache", Toast.LENGTH_LONG).show();
                        Gson gson = new Gson();
                        Product[] products = gson.fromJson(data, Product[].class);
                        for (Product product: products){
                            String productitem = product.name + ": $" + String.valueOf(product.price);
                            items.add(productitem);
                        }
                        prgDialog.hide();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);
                    }else {

                        if (statusCode == 403) {
                            Toast.makeText(getApplicationContext(), "Login Failure!", Toast.LENGTH_SHORT).show();
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
                }
            });
        }catch (Exception e){
            System.err.println(e);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clear is called!!!");
                editor = getSharedPreferences("data", 0).edit();
                editor.clear();
                editor.commit();

            }
        });

    }
    public class Product implements Serializable {
        // Only these two paras are needed
        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public float price;
        //public String getName(){return name;}
        //public float getPrice(){return price;}
    }

}

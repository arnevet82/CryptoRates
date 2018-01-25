package com.crypto.velis.cryptorates;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    Coin bitcoin, ethereum, ripple, litecoin, bitcoinCash;
    public static Coin [] coins;
    public static ArrayList<Double>closingRates;
    List<Model> list = new ArrayList<>();
    ListAdapter adapter;
    Button refresh;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        getRates();
        SetCheckRatesAlarm();
        setChangeRatesAlarm();
        listview = (ListView) findViewById(R.id.listview);
        listview.setBackgroundResource(R.drawable.card);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRates();
            }
        });
    }

    public ArrayList<Double> getClosingRates(){

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        closingRates = tinyDB.getListDouble("blah");

        return closingRates;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRates();
    }

    public void saveRates(){
        closingRates.clear();
        for(Coin coin: coins){
            closingRates.add(coin.closingRate);
        }
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putListDouble("blah", closingRates);
    }

    public void getRates(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.coinmarketcap.com/v1/ticker/?limit=10";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Locale us = new Locale("en", "US");
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(us);
                        int []indices = {0, 1, 2, 5, 3};
                        list.clear();
                        try {
                            for(int i = 0; i < coins.length; i++){
                                JSONArray jsonarray = new JSONArray(response);
                                JSONObject obj = jsonarray.getJSONObject(indices[i]);
                                String stringRate = obj.getString("price_usd");
                                double bitRate = Double.parseDouble(stringRate);
                                Coin coin = coins[i];
                                coin.newRate = bitRate;
                                String cryptoRate = formatter.format(bitRate);
                                String flag = "";
                                if(coin.closingRate != 0){
                                    if(coin.newRate < coin.closingRate){
                                        coin.arrow = R.drawable.arrow_down;
                                        coin.pctChange = ((coin.newRate/coin.closingRate)-1)*100;
                                        flag = "down";
                                    }else if (coin.newRate > coin.closingRate) {
                                        coin.arrow = R.drawable.arrow_up;
                                        coin.pctChange = abs(((coin.closingRate/coin.newRate)-1)*100);
                                        flag = "up";
                                    }else{
                                    }
                                }
                                String pct = formatter.format(coin.pctChange);

                                list.add(new Model(coins[i].name, cryptoRate, coin.arrow, coin.icon, pct, flag));
                            }
                            adapter = new ListAdapter(listview.getContext(), list);
                            listview.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SetCheckRatesAlarm() {
        // replace with a button from your own UI
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent _ ) {
                getRates();
                for(Coin coin: coins){

                    coin.closingRate = coin.newRate;
                }
                saveRates();
                getRates();

                context.unregisterReceiver( this ); // this == BroadcastReceiver, not Activity
            }
        };

        this.registerReceiver( receiver, new IntentFilter("com.blah.bloo.somemessage") );

        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.blah.bloo.somemessage"), 0 );
        AlarmManager alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        Locale aLocale = new Locale.Builder().setLanguage("iw").setRegion("IL").build();
        Calendar calendar = Calendar.getInstance(aLocale);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);

        // set alarm to fire 5 sec (1000*5) from now (SystemClock.elapsedRealtime())
        assert alarmManager != null;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                (AlarmManager.INTERVAL_DAY*2), pintent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setChangeRatesAlarm() {
        // replace with a button from your own UI
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive( Context context, Intent _ ) {
                getRates();
                for(int i = 0; i < coins.length; i++){
                    if(coins[i].closingRate*coins[i].changePercentAlert > coins[i].newRate || coins[i].closingRate < coins[i].newRate*coins[i].changePercentAlert){
                        sendNotification("Change in " + coins[i].name + " rates!!!", i+555);
                    }
                }

                context.unregisterReceiver( this ); // this == BroadcastReceiver, not Activity
            }
        };

        this.registerReceiver( receiver, new IntentFilter("com.blah.blah.somemessage") );

        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.blah.blah.somemessage"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        long hour = 60 * 60 * 1000;
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                hour*1,
                pintent);
    }

    public void sendNotification(String title, int id){

        Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(MainActivity.this, 0, notificationIntent, 0);


        NotificationManager notificationManager = (NotificationManager) MainActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(MainActivity.this)
                .setContentTitle(title)
                .setContentText("Click to see the curent rates!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(intent);

        Notification notificationn = notification.getNotification();
        assert notificationManager != null;
        notificationManager.notify(id, notificationn);

    }




    public void init(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.ttf");
        TextView trending = (TextView)findViewById(R.id.trending);
        trending.setTypeface(typeface);

        refresh = (Button)findViewById(R.id.refresh);

        bitcoin = new Coin("Bitcoin", 0, 0, R.drawable.bit_icon2);
        ethereum = new Coin("Ethereum", 0, 0, R.drawable.ethereum_icon);
        ripple = new Coin("Ripple", 0, 0, R.drawable.ripple_icon);
        litecoin = new Coin("Litecoin", 0, 0, R.drawable.lite_icon);
        bitcoinCash = new Coin("Bitcoin Cash", 0, 0, R.drawable.bcash_icon);
        coins = new Coin[]{bitcoin, ethereum, ripple, litecoin, bitcoinCash};

        getClosingRates();

        if(closingRates.size() > 0){
            for(int i = 0; i < closingRates.size(); i++){
                if(closingRates.get(i) != 0){
                    coins[i].closingRate = closingRates.get(i);
                }
            }
        }
    }
}

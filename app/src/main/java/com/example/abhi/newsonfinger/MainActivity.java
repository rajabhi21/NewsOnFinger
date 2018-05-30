package com.example.abhi.newsonfinger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayAdapter adapter;
    String category="";
    String country="";
    public static ArrayList<String>contentUrl=new ArrayList<>();
    ArrayList<String>titles=new ArrayList<>();

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String results="";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url=new URL(strings[0]);
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);


                int data=reader.read();

                while(data!=-1){
                    char current=(char)data;
                    results += current;
                    data=reader.read();
                }
                //Log.i("result",results);

                JSONObject jsonObject=new JSONObject(results);





                        JSONArray jsonArray =jsonObject.getJSONArray("articles");
                        int noOfResult = jsonObject.getInt("totalResults");

                        for(int i=0;i<noOfResult;i++) {

                            jsonObject = jsonArray.getJSONObject(i);
                            if (!jsonObject.isNull("url") && !jsonObject.isNull("title")) {
                                contentUrl.add(jsonObject.getString("url"));
                                titles.add(jsonObject.getString("title"));
                                //Log.i("result",titles.get(i)+contentUrl.get(i));

                            }
                        }



            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView=(ListView)findViewById(R.id.listView);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(adapter);

        updateListView();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),WebActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });




    }

    public void updateListView(){

        DownloadTask downloadTask=new DownloadTask();
        try {
            if (category == "") {
                downloadTask.execute("https://newsapi.org/v2/top-headlines?country=in&apiKey=8dee943c72ce4f14b3bae6d2b1b3055a");
            }
            else{
                titles.clear();
                contentUrl.clear();
                String z= URLEncoder.encode(category,"UTF-8");
                downloadTask.execute("https://newsapi.org/v2/top-headlines?country=in&category="+z+"&apiKey=8dee943c72ce4f14b3bae6d2b1b3055a");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

   @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.general){
            category="general";
        }
        else if (id == R.id.entertainment){
            category="entertainment";
        }
        else if (id == R.id.business){
            category="business";
        }
        else if (id == R.id.health){
            category="health";
        }
        else if(id == R.id.sports){
            category="sports";
        }
        else if (id ==R.id.technology){
            category="technology";
        }
        updateListView();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

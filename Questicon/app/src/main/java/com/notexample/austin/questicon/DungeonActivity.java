package com.notexample.austin.questicon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class DungeonActivity extends AppCompatActivity {
    ArrayList<DungeonModel> dungeonModels;
    CustomAdapterDungeon adapterDungeon;
    ArrayList<DungeonModel> dungeonModels1;
    String theySearched = "";
    int postitionTHis;
    TextToSpeech textToSpeech;
    View.OnClickListener mOnClickListener;
    JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {


        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {


            try {
                JSONArray jsonArray = responseBody.getJSONArray("zones");


                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject dungeon = jsonArray.getJSONObject(i);
                    final JSONObject bosses = jsonArray.getJSONObject(1);

                    if (!dungeon.has("name") || !dungeon.has(("description")) || !dungeon.has(("numPlayers")) ||
                            !dungeon.has(("advisedMinLevel")) || !dungeon.has(("advisedMaxLevel")) ||
                            !dungeon.has(("floors")) || !dungeon.has(("bosses")) || !dungeon.has("location"))
                        continue;
                    final DungeonModel dungeonModel = new DungeonModel(dungeon.getString("name"), dungeon.getString("description"), dungeon.getString("location"),
                            bosses.getString("name"), bosses.getString("description"), dungeon.getString("numPlayers"), dungeon.getString("advisedMaxLevel"),
                            dungeon.getString("advisedMinLevel"), dungeon.getString("floors"));
                    dungeonModels.add(dungeonModel);
                    adapterDungeon.notifyDataSetChanged();
//

                    ListView listViewDungeon = (ListView) findViewById(R.id.listViewDungeon);


                    assert listViewDungeon != null;
                    listViewDungeon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                            postitionTHis = position;
                            if (theySearched.equalsIgnoreCase("")) {
                                final DungeonModel dungeon2 = dungeonModels.get(position);


                                Snackbar.make(findViewById(android.R.id.content), "Speaking text from: " + dungeon2.getNameD(), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Stop speaking", mOnClickListener)
                                        .setActionTextColor(Color.RED)
                                        .show();

                                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {

                                        if (status != TextToSpeech.ERROR) {
                                            textToSpeech.setLanguage(Locale.UK);

                                            textToSpeech.speak(dungeon2.descriptionD.toString(),
                                                    TextToSpeech.QUEUE_FLUSH, null);


                                        }
                                    }
                                });
                            }else{
                                final DungeonModel dungeon2 = dungeonModels1.get(position);


                                Snackbar.make(findViewById(android.R.id.content), "Speaking text from: " + dungeon2.getNameD(), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Stop speaking", mOnClickListener)
                                        .setActionTextColor(Color.RED)
                                        .show();

                                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {

                                        if (status != TextToSpeech.ERROR) {
                                            textToSpeech.setLanguage(Locale.UK);

                                            textToSpeech.speak(dungeon2.descriptionD.toString(),
                                                    TextToSpeech.QUEUE_FLUSH, null);


                                        }
                                    }
                                });

                            }


                        }
                    });
                    mOnClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textToSpeech.stop();
                        }
                    };

                    listViewDungeon.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                       int pos, long arg3) {
                            if (theySearched.equalsIgnoreCase("")) {

                                DungeonModel dungeon = dungeonModels.get(pos);
                                Intent myIntent = new Intent(DungeonActivity.this, DunegonDetailView.class);
                                myIntent.putExtra("position", pos);
                                myIntent.putExtra("des", dungeon.getDescriptionD());
                                myIntent.putExtra("name", dungeon.getNameD());
                                startActivity(myIntent);

                            } else {

                                DungeonModel dungeon = dungeonModels1.get(pos);
                                Intent myIntent = new Intent(DungeonActivity.this, DunegonDetailView.class);
                                myIntent.putExtra("position", pos);
                                myIntent.putExtra("des", dungeon.getDescriptionD());
                                myIntent.putExtra("name", dungeon.getNameD());
                                startActivity(myIntent);

                            }


                            return false;
                        }
                    });
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast.makeText(getApplicationContext(), "Process Not Successful",
                    Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon);

        checkFirstRun();
        dungeon();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hello there!");
        builder.setIcon(R.drawable.gyro_animation);
        builder.setMessage("Loading Dunegeons, just two seconds...");
        builder.setCancelable(false);

        final AlertDialog closedialog= builder.create();

        closedialog.show();

        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                closedialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        inflater.inflate(R.menu.options_menu, menu);


        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                theySearched = newText;

                dungeonModels1 = new ArrayList<>();

                for (int i = 0; i < dungeonModels.size(); i++) {

                    if (dungeonModels.get(i).getNameD().toLowerCase().contains(newText.toLowerCase())) {
                        dungeonModels1.add(dungeonModels.get(i));
                    }

                }
                ListView listViewDungeon = (ListView) findViewById(R.id.listViewDungeon);
                dungeonModels.contains(newText);
                adapterDungeon = new CustomAdapterDungeon(DungeonActivity.this, dungeonModels1);
                listViewDungeon.setAdapter(adapterDungeon);

                adapterDungeon.notifyDataSetChanged();


                return false;
            }
        });


        return true;
    }

    public void dungeon() {

        ListView listViewDungeon = (ListView) findViewById(R.id.listViewDungeon);
        dungeonModels = new ArrayList<>();
        adapterDungeon = new CustomAdapterDungeon(this, dungeonModels);
        listViewDungeon.setAdapter(adapterDungeon);


        final AsyncHttpClient client = new AsyncHttpClient();


        // Not sure why this boolean isn't working but the objective was to make it so that if a user enter's nothing, the search doesn't happen


        client.get("https://us.api.battle.net/wow/zone/?locale=en_US&apikey=wheces9zargz65mhza5jfv9nentuy2gg", jsonHttpResponseHandler);


    }

    public void clickingInfo(MenuItem item) {
        InfoDiaglogue();
    }


    public void InfoDiaglogue() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("So you're a little confused...");
        builder2.setIcon(R.mipmap.ic_launcher_questicon);
        builder2.setCancelable(true);
        builder2.setMessage("A single press on a dungeon:\n\n Will start the text to speech engine and read that dungeons lore. \n\n A long press on a dunegon: \n\n Will open that dungeons detailed informtaion like loot gained from killing the boss or where the dungeon is located.");
        builder2.setPositiveButton(
                "Thanks!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        return;
                    }
                });

        AlertDialog alert12 = builder2.create();
        alert12.show();
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun1", true);
        if (isFirstRun) {

            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("Dear WoW player...");
            builder2.setIcon(R.mipmap.ic_launcher_questicon);
            builder2.setCancelable(true);
            builder2.setMessage("A single press on a dungeon:\n\n Will start the text to speech engine and read that dungeons lore. \n\n A long press on a dunegon: \n\n Will open that dungeons detailed informtaion like loot gained from killing the boss or where the dungeon is located.");
            builder2.setPositiveButton(
                    "Thanks!",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            return;
                        }
                    });

            AlertDialog alert12 = builder2.create();
            alert12.show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun1", false)
                    .apply();
        }
    }


}

//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/ProjectOxford-ClientSDK
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.bdcorps.videonews;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.speech.tts.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    public CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
    private CustomTabsSession mCustomTabsSession;

    public int article = 4;
    public TextView text, titleTextView;
    public ImageView img;

    private TextToSpeech mTts;
    String topicCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        topicCode = intent.getStringExtra("topicCode"); //if it's a string you stored.

        //CCT Connection
        mConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                mCustomTabsSession=getSession();
                mClient.warmup(0);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mClient = null;
                mCustomTabsSession = null;
            }
        };

        //Bind CCT Service
        String packageName = "com.android.chrome";
        CustomTabsClient.bindCustomTabsService(this, packageName, mConnection);

        text = (TextView) findViewById(R.id.textview);
        img = (ImageView) findViewById(R.id.imageview);
        titleTextView = (TextView) findViewById(R.id.title_text_view);


        mTts = new TextToSpeech(this, this);

        Button b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabnews(topicCode);
            }


        });

        Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(text.getText().toString());
            }
        });

        Button b3 = (Button) findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                article++;
                grabnews(topicCode);
            }
        });

        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("SSS", "text on board is =" + editable.toString());
                speak(text.getText().toString());
            }

        });
    }

    public void grabnews(String topicCode) {
        int pos = article;
        try {

    JSONArray a= Config.initNewsRequest(topicCode);

            //  for (int i = 0; i<json.getInt("num_results");i++)
            {
                final JSONObject b = (JSONObject) a.get(article);
                JSONArray c = b.getJSONArray("multimedia");
                if (c.length() > 4) {
                    JSONObject d = (JSONObject) c.get(4);
                    String url = d.getString("url");
                    final String title = b.getString("title");
                    try {
                        new DownloadImageTask(new DownloadImageTask.AsyncResponse() {

                            @Override
                            public void processFinish(Bitmap result) {
                                Paint myRectPaint = new Paint();
                                int x1 = 0;
                                int y1 = 0;
                                int x2 = 500;
                                int y2 = 500;

//Create a new image bitmap and attach a brand new canvas to it
                                Bitmap tempBitmap = Bitmap.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.RGB_565);
                                Canvas tempCanvas = new Canvas(tempBitmap);

//Draw the image bitmap into the cavas
                                tempCanvas.drawBitmap(result, 0, 0, null);

//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
                                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                                titleTextView.setText(title);
                                img.setImageBitmap(tempBitmap);
                            }
                        }, img)
                                .execute(url).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                text.setText(b.getString("abstract"));
                                Log.i("SSS", "abstract is = " + text.getText().toString());
                                canSpeak = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "multimedia does not exist",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    article++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void speak(String s) {
        Log.d("SSS", "to speak: " + s);
        if (s != null) {
            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "completed");
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_SYSTEM);
            am.setStreamVolume(am.STREAM_SYSTEM, amStreamMusicMaxVol, 0);
            mTts.speak(s, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        } else {
            speakNext();
        }
    }

    private void speakNext() {
        article++;
        grabnews(topicCode);
        speak(text.getText().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onInit(int status) {

        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the topicNames will indicate this.
            int result = mTts.setLanguage(Locale.US);

            mTts.setOnUtteranceCompletedListener(this);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e("404", "Language is not available.");
            }
        } else {
            // Initialization failed.
            Log.e("404", "Could not initialize TextToSpeech.");
            // May be its not installed so we prompt it to be installed
            Intent installIntent = new Intent();
            installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }

    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();

        unbindService(mConnection);
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
public boolean canSpeak = true;
    @Override
    public void onUtteranceCompleted(String s) {
        if (s.equals("completed")) {
            article++;
            grabnews(topicCode);
        }
    }

    public void loadCustomTabs(int color, String Url) {
        CustomTabsIntent.Builder mBuilder = new CustomTabsIntent.Builder(mCustomTabsSession);
        //mBuilder.setToolbarColor(ContextCompat.getColor(getBaseContext(), color));
        mBuilder.enableUrlBarHiding();
        mBuilder.setShowTitle(true);
        mBuilder.addDefaultShareMenuItem();

        CustomTabsIntent mIntent = mBuilder.build();
        mIntent.launchUrl(this, Uri.parse(Url));
    }

    private CustomTabsSession getSession() {

        return mClient.newSession(new CustomTabsCallback() {


            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
            }
        });
    }

}

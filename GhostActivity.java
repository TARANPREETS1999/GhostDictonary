/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private static String wordFragment;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private static TextView ghostText;
    private static TextView ghostStatus;
    private static Button challengeBtn;
    private static Button restartBtn;
    private static String wordSelectedByComputer;
    Handler handler = new Handler();
    private String yourWord = null;
    int whoWentFirst;
    private static final String TAG = "GhostActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        AssetManager assetManager = getAssets();

        ghostText = (TextView) findViewById(R.id.ghostText);
        ghostStatus = (TextView) findViewById(R.id.gameStatus);
        challengeBtn=(Button) findViewById(R.id.chlngbtn);
        restartBtn=(Button)findViewById(R.id.restartbtn);

        try {
            InputStream inputStream=assetManager.open("words.txt");
            //dictionary=new SimpleDictionary(inputStream);
            dictionary = new FastDictionary(inputStream);
           //dictionary=new SimpleDictionary(assetManager.open("word.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }


       challengeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               challenge();
           }
       });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart(null);
                createToast("Game restarts...",100);            }
        });


        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        whoWentFirst = userTurn ? 1:0;
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        ghostStatus.setText(COMPUTER_TURN);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wordFragment = String.valueOf(ghostText.getText());
                //wordSelectedByComputer = dictionary.getAnyWordStartingWith(wordFragment);
               wordSelectedByComputer = dictionary.getGoodWordStartingWith(wordFragment, whoWentFirst);
                if(wordSelectedByComputer == "noWord"){
                    createToast("Computer wins! No such word",500);
                    onStart(null);
                }
                else if(wordSelectedByComputer == "sameAsPrefix"){//u caant bluf
                    createToast("Computer wins! You ended the word",1000);
                    onStart(null);
                }
                else if(wordSelectedByComputer.length() != 1){
                    System.out.println("i am called firt---------------------");
                    wordFragment += wordSelectedByComputer.charAt(wordFragment.length());
                    ghostText.setText(String.valueOf(wordFragment));
                }
                else {
                    wordFragment += wordSelectedByComputer;
                    ghostText.setText(String.valueOf(wordFragment));
                }

                // Do computer turn stuff then make it the user's turn again
                userTurn = true;
                ghostStatus.setText(USER_TURN);
            }
        },1000);
    }

    /**
     * Handler for user key presses.
     *
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        char pressedKey = (char) event.getUnicodeChar();
        pressedKey = Character.toLowerCase(pressedKey);

        if(pressedKey >= 'a' && pressedKey <= 'z'){
            wordFragment = String.valueOf(ghostText.getText());
            wordFragment += pressedKey;
            ghostText.setText(wordFragment);
            createToast("Computer's turn",100);
            computerTurn();
            return true;
        }
        else
            return super.onKeyUp(keyCode, event);

    }

    public void challenge(){

        if(wordFragment.length() >= 4 ){
            yourWord = dictionary.getGoodWordStartingWith(wordFragment, whoWentFirst);
            //yourWord = dictionary.getAnyWordStartingWith(wordFragment);
            if(yourWord == "noWord")
                createToast("You Win! No such word",1000);
            else if(yourWord == "sameAsPrefix")
                createToast("You Win! Computer ended the word",1000);
            else if(dictionary.isWord(wordFragment))
                createToast("You Win! Computer ended the word",1000);
            else
                Toast.makeText(getApplication(),"Computer wins. The word was : " + wordSelectedByComputer,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(getApplication(),"You challenged too early. Computer wins. Word is still less then 4 characters",Toast.LENGTH_LONG).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onStart(null);
            }
        },1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("ghost_text",ghostText.getText().toString());
        outState.putString("game_status",ghostStatus.getText().toString());
        outState.putBoolean("userTurn",userTurn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        userTurn = savedInstanceState.getBoolean("userTurn");
        ghostText.setText(savedInstanceState.getString("ghost_text"));
        ghostStatus.setText(savedInstanceState.getString("game_status"));
    }

    public void createToast(String message, int time){
        Toast.makeText(this,message,time).show();
    }

}

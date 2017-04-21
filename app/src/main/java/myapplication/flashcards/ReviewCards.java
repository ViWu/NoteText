package myapplication.flashcards;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ReviewCards extends AppCompatActivity {

    private int index = 0;
    private static ArrayList<Integer> shuffledDeck = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitializeDeck();
        InitializeFields(index);

        ActionBar actionbar = getSupportActionBar();
        MainMenu.initializeToolbar(toolbar, actionbar);

        Button addButton = (Button) findViewById(R.id.revealAnswer);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Animation fade= AnimationUtils.loadAnimation(ReviewCards.this, R.anim.fade_in);
                TextView answerField = (TextView) findViewById(R.id.answer);
                answerField.setTextColor(Color.parseColor("#32CD32"));
                answerField.startAnimation(fade);
                answerField.setText("Answer: " + MainActivity.getAnswers(shuffledDeck.get(index)));
            }

        });

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(index < MainActivity.getSize()-1) {
                    index++;
                    InitializeFields(index);
                }
                else
                    Snackbar.make(v, "No more cards", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        });


        Button prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(index > 0) {
                    index--;
                    InitializeFields(index);
                }
                else
                    Snackbar.make(v, "None have been shown before", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_cards, menu);
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
        if (id == R.id.action_edit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void InitializeFields(int index){
        //Animation slide = AnimationUtils.loadAnimation(ReviewCards.this, R.anim.activity_open_translate);
        Animation fade = AnimationUtils.loadAnimation(ReviewCards.this, R.anim.fade_in);
        TextView questionField = (TextView)findViewById(R.id.question);

        questionField.startAnimation(fade);

        questionField.setText(MainActivity.getQuestions(shuffledDeck.get(index)));
        TextView answerField = (TextView)findViewById(R.id.answer);
        answerField.setText("");
    }

    public void InitializeDeck(){
        Intent shuffleIntent = getIntent();
        String bool = shuffleIntent.getStringExtra("shuffle");
        shuffledDeck.clear();
        for(int i=0;i<MainActivity.getSize();i++){
            shuffledDeck.add(i);
        }
        //shuffle only if true, else keep the arraylist in order
        if(bool.equals("true"))
            Collections.shuffle(shuffledDeck);
    }

}

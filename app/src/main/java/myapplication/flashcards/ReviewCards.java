package myapplication.flashcards;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        Button addButton = (Button) findViewById(R.id.revealAnswer);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView answerField = (TextView) findViewById(R.id.answer);
                answerField.setTextColor(Color.parseColor("#32CD32"));
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
                    Toast.makeText(getApplicationContext(), "No more cards", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "None have been shown before", Toast.LENGTH_SHORT).show();
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

    public void InitializeFields(int index){
        TextView questionField = (TextView)findViewById(R.id.question);
        questionField.setText(MainActivity.getQuestions(shuffledDeck.get(index)));
        TextView answerField = (TextView)findViewById(R.id.answer);
        answerField.setText("");
    }
}

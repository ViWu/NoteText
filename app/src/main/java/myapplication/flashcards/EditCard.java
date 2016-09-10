package myapplication.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class EditCard extends AppCompatActivity {

    private Intent intent = null;
    String setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get Intent from MainActivity
        final Intent editIntent = getIntent();
        setName = editIntent.getStringExtra("setName");

        //set edit text boxes if questions and answers are already saved
        EditText questionSet = (EditText) findViewById(R.id.questionField);
        String str = editIntent.getStringExtra("questions");
        questionSet.setText(str);

        EditText answerSet = (EditText) findViewById(R.id.answerField);
        str = editIntent.getStringExtra("answers");
        answerSet.setText(str);

        //if cancel button is clicked, go back to main activity
        Button Cancel= (Button) findViewById(R.id.cancel);
        Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(EditCard.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }

        });

        //if ok button is clicked, save the questions and answers fields, then go back to main activity
        Button OK= (Button) findViewById(R.id.ok);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText questionBox = (EditText) findViewById(R.id.questionField);
                EditText answerBox = (EditText) findViewById(R.id.answerField);
                String answer = answerBox.getText().toString();
                String question = questionBox.getText().toString();

                int pos = editIntent.getIntExtra("position", 0);
                MainActivity.setAnswers(pos, answer);
                MainActivity.setQuestions(pos,question);

                fileWrite();
                MainActivity.refreshAdapter();

                intent = new Intent(EditCard.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }

        });
    }


    private void fileWrite() {

        try {
            File dir = getFilesDir();
            File file = new File(dir +"/"+ setName+".txt");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0; i <MainActivity.questions.size();i++) {
                bw.write(MainActivity.questions.get(i) + "\n");
                bw.write(MainActivity.answers.get(i) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

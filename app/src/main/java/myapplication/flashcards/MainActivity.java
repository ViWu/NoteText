package myapplication.flashcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<String> questions;
    private static ArrayList<String> answers;
    private ArrayList<Set> Sets;
    private static ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private int extraPosition;
    private int position;
    private String setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Intent editIntent = getIntent();
        setName = editIntent.getStringExtra("name");

        lvItems = (ListView) findViewById(R.id.lvItems);
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();

        //set up adapter and listeners
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, questions);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        //add questions to listView by typing and clicking the add button
        Button addButton = (Button) findViewById(R.id.btnAddItem);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText textField = (EditText) findViewById(R.id.textField);
                String itemText = textField.getText().toString();
                itemsAdapter.add(itemText);
                answers.add("");
                textField.setText("");
                // writeItems();
            }

        });

        fileRead();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_shufflereview) {
            shuffle("true");
            return true;
        }
        if (id == R.id.action_review) {
            shuffle("false");
            return true;
        }
        if (id == R.id.action_sets) {
            Intent intent = new Intent(MainActivity.this, MainMenu.class);
            intent.putExtra("questions", questions);
            intent.putExtra("answers", answers);
            intent.putExtra("position", extraPosition);
           // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            fileWrite();
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Attaches a long click listener and click listener to the listview
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
        @Override

        public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
              // Remove the item within array at position
              position = pos;
              new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    questions.remove(getPos());
                                    answers.remove(getPos());
                                    // Refresh the adapter
                                    itemsAdapter.notifyDataSetChanged();
                                     }
                                })
                     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                            }
                                 })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
                    // Return true consumes the long click event (marks it handled)
                 return true;
                    }
                }
        );
        final Intent[] intent = {null};
        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent[0] = new Intent(MainActivity.this, EditCard.class);
                intent[0].putExtra("questions", questions.get(position));
                intent[0].putExtra("answers", answers.get(position));
                intent[0].putExtra("position", position);
                intent[0].putExtra("setName", setName);
                startActivity(intent[0]);
            }
        });

    }


    /*
    Writes data to internal storage.
    */
    private void fileWrite(){
        try {
            FileOutputStream fOut = openFileOutput(setName+".txt",0);
            String data = "";
            for(int i=0; i <questions.size();i++){
                data = questions.get(i) + "\r\n";
                fOut.write(data.getBytes());
                data = answers.get(i)  + "\r\n";
                fOut.write(data.getBytes());
            }
            fOut.close();
            Toast.makeText(getBaseContext(),"Set saved!",Toast.LENGTH_SHORT).show();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Reads from internal storage, parses the data, then loads into current activity.*/
    private void fileRead(){
        try{
            FileInputStream fin = openFileInput(setName+".txt");
            int c, row=0;
            String data="";

            while( (c = fin.read()) != -1){
                data = data + Character.toString((char)c);
                if(data.contains("\r\n")){
                    data = data.substring(0, data.length()-1);
                    if((row & 1) == 0)
                         itemsAdapter.add(data);
                    else
                        answers.add(data);
                    data = "";
                    row++;
                }
            }

            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //if randomize is true, then shuffle. Else use default order
    private void shuffle(String randomize){
        if (getSize() <= 0)
            Toast.makeText(getApplicationContext(), "No cards to review", Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(MainActivity.this, ReviewCards.class);
            intent.putExtra("shuffle", randomize);
            startActivity(intent);
        }
    }

    protected static void refreshAdapter(){
        itemsAdapter.notifyDataSetChanged();
    }

    protected int getPos() {
        return position;
    }

    protected static void setAnswers(int pos, String str) {
        answers.set(pos, str);
    }

    protected static void setQuestions(int pos, String str) {
        questions.set(pos, str);
    }

    protected static String getAnswers(int pos) {
        return answers.get(pos);
    }

    protected static String getQuestions(int pos) {
        return questions.get(pos);
    }

    protected static int getSize() {
        return questions.size();
    }
}

package myapplication.flashcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<Set> Sets = new ArrayList<Set>();;
    private ArrayList<String> Names, questions, answers;
    private GridView gvItems;
    private int position, QApos;
    Set newSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get info from prev activity
        /*final Intent mainIntent = getIntent();
        QApos = mainIntent.getIntExtra("position",0);
        questions = mainIntent.getStringArrayListExtra("questions");
        answers = mainIntent.getStringArrayListExtra("answers");*/

        //set up grid and insert new set
        gvItems = (GridView) findViewById(R.id.gvItems);
        Names = new ArrayList<String>();                        //Represents each item in gridView
        newSet = new Set();
        /*newSet.setQuestions(questions);
        newSet.setAnswers(answers);
        Sets.add(QApos, newSet);*/


        //set up adapter for grid
        itemsAdapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_list_item_1, Names);
        gvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        //add button: Creates a popup window to type in name of new set
        Button addButton = (Button) findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainMenu.this);
                final EditText edittext = new EditText(MainMenu.this);
                alert.setMessage("Enter Name of New Set");
                alert.setTitle("Create New Set");

                alert.setView(edittext);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String itemText = edittext.getText().toString();
                        itemsAdapter.add(itemText);
                        newSet.setName(itemText);
                        Sets.add(newSet);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing
                    }
                });

                alert.show();
            }

        });
        loadInternalStorage();
    }

    /*Load all files from internal storage that end in ".txt" */
    private void loadInternalStorage(){
        File dir = getFilesDir();
        File[] subFiles = dir.listFiles();
        String fileName;
        int pos;

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if(extension.equals("txt")) {
                    Toast.makeText(getBaseContext(), file.getName(), Toast.LENGTH_SHORT).show();
                    fileName = file.getName();
                    pos = fileName.lastIndexOf(".");
                    if (pos > 0)
                        fileName = fileName.substring(0, pos);
                    itemsAdapter.add(fileName);
                    newSet.setName(fileName);
                    Sets.add(newSet);
                }
            }
        }
    }

    /*Delete existing file from storage*/
    public boolean deleteFile(String name){
        File dir = getFilesDir();
        File[] subFiles = dir.listFiles();
        String fileName;
        int pos;

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                fileName = file.getName();
                if (fileName.equals(name+".txt")) {
                    file.delete();
                    return true;
                }
            }
        }
        return false;
    }

    private void setupListViewListener() {
        gvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                // Remove the item within array at position
                position = pos;
                new AlertDialog.Builder(MainMenu.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // continue with delete
                                deleteFile(Names.get(position));
                                Names.remove(getPos());
                                Sets.remove(getPos());
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
                //writeItems();
                // Return true consumes the long click event (marks it handled)
                return true;
            }
        }
        );
        final Intent[] intent = {null};
        gvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent[0] = new Intent(MainMenu.this, MainActivity.class);
                intent[0].putExtra("name", Names.get(position));
                /*intent[0].putExtra("set", Sets.get(position));
                intent[0].putExtra("pos", position);*/
                //intent[0].setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent[0]);
            }
        });

    }

    protected int getPos() {
        return position;
    }

}

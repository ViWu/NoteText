package myapplication.flashcards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private static ArrayAdapter<String> itemsAdapter;
    private ArrayList<Set> Sets = new ArrayList<Set>();;
    private ArrayList<String> Names;
    private GridView gvItems;
    private int position;
    private int setCount = 0;
    Set newSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        initializeToolbar(toolbar, actionbar);

        //set up grid and insert new set
        gvItems = (GridView) findViewById(R.id.gvItems);
        Names = new ArrayList<String>();                        //Represents each item in gridView
        newSet = new Set();

        //set up adapter for grid
        itemsAdapter = new ArrayAdapter<String>(MainMenu.this, R.layout.grid_item, Names);
        gvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        //create a floating action button to help the user
        FloatingActionButton help = (FloatingActionButton) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Click on the name of the set to edit. Hold down to delete/rename. To add sets, tap the button to the left.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //add button: Creates a popup window to type in name of new set
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainMenu.this);
                final EditText edittext = new EditText(MainMenu.this);
                alert.setMessage("Enter Name of New Set");
                alert.setTitle("Create New Set");

                alert.setView(edittext);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String itemText = edittext.getText().toString();
                        fileCreate(itemText, view);
                        newSet.setName(itemText);
                        Sets.add(newSet);
                        gvItems.smoothScrollToPosition(itemsAdapter.getCount()-1);
                        setCount++;
                        checkNoSetExists();
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
        checkNoSetExists();
    }

    /*Load all files from internal storage that end in ".txt"
    * and whose name is not 0 characters without the extension*/

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
                if(extension.equals("txt") && file.getName().length() > 4) {
                    fileName = file.getName();
                    pos = fileName.lastIndexOf(".");
                    if (pos > 0)
                        fileName = fileName.substring(0, pos);
                    itemsAdapter.add(fileName);
                    newSet.setName(fileName);
                    Sets.add(newSet);
                    setCount++;
                }
            }
        }
    }

    /*Locates file and delete existing file from storage if remove is set to true. */
    public boolean checkFileExists(String setName,  boolean remove){
        File dir = getFilesDir();
        File[] subFiles = dir.listFiles();
        String fileName;

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                fileName = file.getName();
                if (fileName.equals(setName+".txt")) {
                    if(remove) {
                        file.delete();
                        setCount--;
                        checkNoSetExists();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /*Creates file containing set data unless name already exists*/
    public void fileCreate(String setName, View v){
        try {
            boolean exists = checkFileExists(setName, false);
            if(!exists) {
                FileOutputStream fOut = openFileOutput(setName + ".txt", 0);
                fOut.close();
                itemsAdapter.add(setName);
                Snackbar.make(v, "New set: "+ setName +" has been created!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainMenu.this);
                alert.setMessage("Set name already exists or invalid name!");
                alert.setTitle("Error Message");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void fileRename(String setName, String newName){
        File dir = getFilesDir();
        File[] subFiles = dir.listFiles();
        String fileName;

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                fileName = file.getName();
                if (fileName.equals(setName+".txt")) {
                    File tempFile = new File(dir +"/"+ newName+".txt");
                    boolean exists = checkFileExists(newName, false);
                    if (!exists) {
                        file.renameTo(tempFile);
                        Names.remove(getPos());
                        itemsAdapter.add(newName);
                        itemsAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "Renamed to " + newName + "!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getBaseContext(),"Rename failed! " +
                                "Set already exists!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Adds app icon to toolbar and adjusts placement of title
    public static void initializeToolbar(Toolbar toolbar, ActionBar actionbar){
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setLogo(R.mipmap.ic_launcher);
        actionbar.setDisplayUseLogoEnabled(true);
        String title = "   " + toolbar.getTitle();
        actionbar.setTitle(title);
    }

    //Check if number of sets is zero. If true, show message
    private void checkNoSetExists(){
        TextView msg = (TextView) findViewById(R.id.empty);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        if (setCount == 0) {
            msg.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);
        }
        else {
            msg.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
        }
    }


    // Attaches a long click listener and click listener to the gridview
    private void setupListViewListener() {
        gvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                // Remove the item within array at position or rename the item
                position = pos;
                final String setName = Names.get(pos);
                String arr[] = {"Delete", "Rename"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                builder.setTitle("Select an Action")
                        .setItems(arr, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    deleteDialog();
                                }
                                else if (which == 1) {
                                    renameDialog(setName);
                                }
                            }
                        })
                        .show();
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
                startActivity(intent[0]);
                overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
            }
        });
    }

    private void deleteDialog(){

        new AlertDialog.Builder(MainMenu.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                        checkFileExists(Names.get(position), true);
                        Names.remove(getPos());
                        Sets.remove(getPos());
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(),"Set succesfully deleted!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void renameDialog(String setName){

        final EditText textField = new EditText(MainMenu.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainMenu.this);
        alert.setMessage("Enter New Name of the Set");
        alert.setTitle("Rename Set");

        alert.setView(textField);

        textField.setText(setName);
        int cursorPos = setName.length();
        textField.setSelection(cursorPos);
        textField.setSelectAllOnFocus(true);

        textField.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String newName = textField.getText().toString();
                fileRename(Names.get(position), newName);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.RESULT_UNCHANGED_SHOWN);

            }
        });

        alert.show();
    }



    protected int getPos() {
        return position;
    }

}

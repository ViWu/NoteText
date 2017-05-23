package myapplication.flashcards;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static ArrayList<String> questions;
    protected static ArrayList<String> answers;
    private static ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private int position;
    private String setName;
    private static final int DIALOG_ID = 0;
    private int setHour, setMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent editIntent = getIntent();
        setName = editIntent.getStringExtra("name");
        getSupportActionBar().setTitle(setName);

        ActionBar actionbar = getSupportActionBar();
        MainMenu.initializeToolbar(toolbar, actionbar);

        lvItems = (ListView) findViewById(R.id.lvItems);
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();

        //set up adapter and listeners
        itemsAdapter = new ArrayAdapter<String>(this, R.layout.lv_item, questions);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        //add questions to listView by typing and clicking the add button
        Button addButton = (Button) findViewById(R.id.btnAddItem);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //EditText textField = (EditText) findViewById(R.id.textField);
                    final DynamicEditText expandedField = (DynamicEditText) findViewById(R.id.expandedTextField);
                    String itemText = expandedField.getText().toString();
                    itemText= itemText.replace('\n',' ');
                    itemsAdapter.add(itemText);
                    answers.add("");
                    expandedField.setText("");
                    lvItems.setSelection(itemsAdapter.getCount() - 1);
                    checkNoQuestionsExists();

                }

            });
        }

        final DynamicEditText expandedField = (DynamicEditText) findViewById(R.id.expandedTextField);
        final EditText textField = (EditText) findViewById(R.id.textField);
        final View rectCollapsed = (View) findViewById(R.id.rect_collapsed);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rectCollapsed.post(new Runnable() {
            @Override
            public void run() {
                rectCollapsed.getLayoutParams().height = textField.getHeight() + 100;
            }
        });


        assert expandedField != null;
        expandedField.setVisibility(View.GONE);
        textField.setCursorVisible(true);

        onExpandListener(expandedField, textField, rectCollapsed);
        onCollapseListener(expandedField, textField, rectCollapsed);
        setUpDrawer(toolbar);

        fileRead();
        checkNoQuestionsExists();
    }

    public void onExpandListener(final EditText expandedField, final EditText textField, final View rectCollapsed){

        textField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    Log.d("STATE", "Text field clicked!");
                    expandedField.getLayoutParams().height = textField.getHeight() * 2;
                    rectCollapsed.getLayoutParams().height = textField.getHeight() * 2 + 100;
                    expandedField.setVisibility(View.VISIBLE);

                    expandedField.requestFocus();
                    InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(expandedField, InputMethodManager.SHOW_IMPLICIT);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvItems.getLayoutParams();
                    params.addRule(RelativeLayout.ABOVE, R.id.expandedTextField);
                }

                return true;
            }
        });
    }

    public void onCollapseListener(final EditText expandedField, final EditText textField, final View rectCollapsed){

        expandedField.setTag(expandedField.getVisibility());
        ViewTreeObserver observer = expandedField.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = expandedField.getVisibility();
                if((int)expandedField.getTag() != newVis) {
                    expandedField.setTag(expandedField.getVisibility());
                    if(expandedField.getVisibility()== View.GONE) {

                        Log.d("STATE", "COLLAPSED");
                        String content = expandedField.getText().toString();
                        textField.setText(content);
                        textField.requestFocus();
                        rectCollapsed.getLayoutParams().height = textField.getHeight() + 100;

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvItems.getLayoutParams();
                        View root = findViewById(android.R.id.content);
                        int heightDiff = root.getRootView().getHeight()- root.getHeight();
                        params.height += heightDiff;
                        lvItems.setLayoutParams(params);
                        params.addRule(RelativeLayout.ABOVE, R.id.textField);


                    }
                }
            }
        });

    }

    public void setUpDrawer(Toolbar toolbar){
        //set up drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view_all_sets) {
            Intent intent = new Intent(MainActivity.this, MainMenu.class);
            intent.putExtra("questions", questions);
            intent.putExtra("answers", answers);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            fileWrite();
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

        } else if (id == R.id.nav_shuffle_review) {
            shuffle("true");

        } else if (id == R.id.nav_review) {
            shuffle("false");

        } else if (id == R.id.nav_notification) {
            setNotification();

        } /*else if (id == R.id.nav_settings) {
            Toast.makeText(getBaseContext(),"Settings!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);

        }*/ else if (id == R.id.nav_save) {
            fileWrite();
            Toast.makeText(getBaseContext(),"Set saved!",Toast.LENGTH_SHORT).show();
        }
       /* else if (id == R.id.nav_share) {
            Toast.makeText(getBaseContext(),"Share!!",Toast.LENGTH_SHORT).show();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*Allows the time picker to set when the notification takes place*/
    public void setNotification(){
        createTimer(DIALOG_ID).show();
    }

    protected Dialog createTimer(int id){
        if(id == DIALOG_ID)
            return new TimePickerDialog(MainActivity.this, timePickerListener, setHour, setMinute, false);
        return null;
    }

    /*Timepicker dialog that allows user to set the time for notification*/
    protected TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay){

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            setHour = hourOfDay;
            setMinute = minuteOfDay;

            hour = setHour - hour;
            minute = setMinute - minute;

            if(hour < 0 || (hour == 0 && minute < 0)) {
                hour += 24;
            }

            String msg = "Time to review " + setName +"!";
            int delay = hour * 60 * 60 * 1000 + minute * 60 * 1000;

            Toast.makeText(MainActivity.this,
                    "difference in time: "+ hour + " hours, " + minute + " minutes", Toast.LENGTH_SHORT).show();

            pushNotification(msg, delay);
        }
    };


    /*Pushes the notification when the set time has elapsed. */

    private void pushNotification(String content, int delay) {

        long totalDelay = SystemClock.elapsedRealtime() + delay;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle("Scheduled Notification")
                .setContentText(content)
                .setSound(soundUri)
                .setSmallIcon(R.mipmap.ic_launcher);


        Intent notificationIntent = new Intent(MainActivity.this, mBroadcastReceiver.class);
        //notificationIntent.putExtra(mBroadcastReceiver.getNotificationID(), 1);
        notificationIntent.putExtra(mBroadcastReceiver.getNotification(), notification.build());
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(MainActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, totalDelay, pendingIntent);

    }

    //Check if number of questions is zero. If true, show message
    private void checkNoQuestionsExists(){
        TextView msg = (TextView) findViewById(R.id.msg);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        assert msg != null;
        assert icon != null;


        int height = getScreenHeight();

        Bitmap bitmap = scaleBitmap(icon, height/8, height/8);
        icon.setImageBitmap(bitmap);

        if (questions.size() == 0) {
            msg.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);
        }
        else {
            msg.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
        }
    }


    //converts imageView icon into bitmap to scale dimensions
    public Bitmap scaleBitmap(ImageView icon, int height, int width){

        //convert imageview -> bimap -> drawable to scale image
        Drawable drawableIcon = icon.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawableIcon).getBitmap();
        drawableIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, height, width, true));

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawableIcon;
        bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    public int getScreenHeight(){

        //get height of the device's screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        return height;
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
                         //continue with delete
                            questions.remove(getPos());
                            answers.remove(getPos());
                        //Refresh the adapter
                            itemsAdapter.notifyDataSetChanged();
                            checkNoQuestionsExists();
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
        });

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

    private void fileWrite() {

        try {
            File dir = getFilesDir();
            File file = new File(dir + "/" + setName + ".txt");
            FileWriter  fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0; i <questions.size();i++) {
                bw.write(questions.get(i) + "\n");
                bw.write(answers.get(i) + "\n");
            }
            bw.close();
        } catch (IOException e) {
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
                if(data.contains("\n")){
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

    //if randomize is true, then shuffle when switching to new activity. Else use default order
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

package myapplication.flashcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class DynamicEditText extends EditText {

    Context context;

    public DynamicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Detect back press to hide soft keyboard
            InputMethodManager mgr = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);

            final DynamicEditText expandedField = (DynamicEditText) findViewById(R.id.expandedTextField);
            String content = expandedField.getText().toString();

            //Don't collapse if there's content
            if(content.length() == 0)
                expandedField.setVisibility(View.GONE);

            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
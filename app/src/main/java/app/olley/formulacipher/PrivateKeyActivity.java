/* PrivateKeyActivity
    This activity is associated with activity_privatekey
    It is only used to display and save the user-set private key
    The private key is separated into key1, key2 and key3 - b, c and m are the variable names
    The use of the variables and the private key are described in the MainActivity class
*/

package app.olley.formulacipher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PrivateKeyActivity extends AppCompatActivity {

    ArrayList<Integer> b = new ArrayList<>(9);
    ArrayList<Integer> c = new ArrayList<>(9);
    ArrayList<Integer> m = new ArrayList<>(9);
    ArrayList<Integer> xnul = new ArrayList<>(3);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_privatekey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder helpAlert = new AlertDialog.Builder(this);
                helpAlert.setMessage(getText(R.string.help_text))
                        .setTitle(getString(R.string.help))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                helpAlert.show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatekey);

        final Button setBtn = (Button) findViewById(R.id.setBtn);
        final EditText key1Box = (EditText) findViewById(R.id.key1Box);
        final EditText key2Box = (EditText) findViewById(R.id.key2Box);
        final EditText key3Box = (EditText) findViewById(R.id.key3Box);
        final TextView console = (TextView) findViewById(R.id.privKeyConsoleText);
        assert setBtn != null;
        assert key1Box != null;
        assert key2Box != null;
        assert key3Box != null;
        assert console != null;
        console.setSingleLine(false);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        final SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);  // setup access to shared preferences
        String key1text = "";
        String key2text = "";
        String key3text = "";
        for (int i = 0; i < 9; i++) {
            String address1 = "b" + (i + '0');
            String address2 = "c" + (i + '0');
            String address3 = "m" + (i + '0');
            b.add(sharedPref.getInt(address1, getString(R.string.b_default).charAt(i) - '0'));
            c.add(sharedPref.getInt(address2, getString(R.string.c_default).charAt(i) - '0'));
            m.add(sharedPref.getInt(address3, getString(R.string.m_default).charAt(i) - '0'));
            key1text += b.get(i);
            key2text += c.get(i);
            key3text += m.get(i);
        }
        key1Box.setText(key1text);
        key2Box.setText(key2text);
        key3Box.setText(key3text);

        for (int i = 0; i < 3; i++) {
            xnul.add(0);
        }

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key1Box.getText().length() != 9) {
                    Calendar calendar = Calendar.getInstance();
                    String formattedDate = simpleDateFormat.format(calendar.getTime());
                    String text = formattedDate + " " + getString(R.string.error1) + "\n" + console.getText().toString();
                    console.setText(text);
                }
                else {
                    boolean error = false;
                    SharedPreferences.Editor editor = sharedPref.edit();    // write-access to shared preferences
                    for (int i = 0; i < 9; i++) {
                        if (key1Box.getText().charAt(i) - '0' != 0 && key2Box.getText().charAt(i) - '0' != 0 && key3Box.getText().charAt(i) - '0' != 0) {
                            String address1 = "b" + (i + '0');
                            String address2 = "c" + (i + '0');
                            String address3 = "m" + (i + '0');
                            b.set(i, key1Box.getText().charAt(i) - '0');
                            c.set(i, key2Box.getText().charAt(i) - '0');
                            m.set(i, key3Box.getText().charAt(i) - '0');
                            editor.putInt(address1, b.get(i));  // keys are saved to shared prefs
                            editor.putInt(address2, c.get(i));
                            editor.putInt(address3, m.get(i));
                        }
                        else {
                            error = true;
                            break;
                        }
                    }
                    if (error) {    // private key rules are explained in values/strings.xml or in the help dialog when running the app
                        Calendar calendar = Calendar.getInstance();
                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                        String text = formattedDate + " " + getString(R.string.error2) + "\n" + console.getText().toString();
                        console.setText(text);
                    }
                    else {
                        int index = 0;
                        for (int i = 1; i <= 9; i++) {
                            if (!b.contains(i) && !c.contains(i) && !m.contains(i)) {
                                if (index < 3) {
                                    xnul.set(index, i);
                                    index++;
                                }
                                else {
                                    error = true;
                                    break;
                                }
                            }
                        }
                        if (error) {
                            Calendar calendar = Calendar.getInstance();
                            String formattedDate = simpleDateFormat.format(calendar.getTime());
                            String text = formattedDate + " " + getString(R.string.error2) + "\n" + console.getText().toString();
                            console.setText(text);
                        }
                        else {
                            if (index == 3) {
                                editor.apply();
                                Calendar calendar = Calendar.getInstance();
                                String formattedDate = simpleDateFormat.format(calendar.getTime());
                                String text = formattedDate + " " + getString(R.string.success3) + "\n" + console.getText().toString();
                                console.setText(text);
                            }
                            else {
                                Calendar calendar = Calendar.getInstance();
                                String formattedDate = simpleDateFormat.format(calendar.getTime());
                                String text = formattedDate + " " + getString(R.string.error2) + "\n" + console.getText().toString();
                                console.setText(text);
                            }
                        }
                    }
                }
            }
        });
    }
}

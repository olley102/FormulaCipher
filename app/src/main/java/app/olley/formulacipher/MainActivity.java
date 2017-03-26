package app.olley.formulacipher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ArrayList<Character> alpha = new ArrayList<>();
    ArrayList<Character> lalpha = new ArrayList<>();
    
    /* alpha is the alphabet used to indicate what characters are supported
        unfortunately, only a very limited number of characters are supported
        due to this being only a test program
    */

    // private key
    ArrayList<Integer> b = new ArrayList<>(9);
    ArrayList<Integer> c = new ArrayList<>(9);
    ArrayList<Integer> m = new ArrayList<>(9);
    ArrayList<Integer> xnul = new ArrayList<>(3);   // xnuls are used to confuse an interceptor

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_privatekey:    // user has chosen to access the private key layout
                Intent intent = new Intent(this, PrivateKeyActivity.class); // go to PrivateKeyActivity
                startActivity(intent);
                return true;
            case R.id.action_help2: // user clicked on the help button
                AlertDialog.Builder helpAlert = new AlertDialog.Builder(this);
                helpAlert.setMessage(getText(R.string.help2_text))
                        .setTitle(getString(R.string.help))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                helpAlert.show();   // show help dialog
                return true;
            case R.id.action_about: // user clicked on About in the drop-down menu
                AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
                aboutAlert.setMessage(getText(R.string.about_text))
                        .setTitle(getString(R.string.about))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                aboutAlert.show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main layout is associated and is used when app is started

        final EditText ptBox = (EditText) findViewById(R.id.key1Box);   // plaintext box
        final EditText ctBox = (EditText) findViewById(R.id.key2Box);   // ciphertext box
        final EditText keyBox = (EditText) findViewById(R.id.key3Box);  // public key box
        final TextView console = (TextView) findViewById(R.id.consoleText); // setup layout components for use
        assert ptBox != null;
        assert ctBox != null;
        assert keyBox != null;
        assert console != null; // console is the shaded box at the bottom of the layout, not an IDE console
        console.setSingleLine(false);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss"); // used by console

        for (int i = 0; i < getString(R.string.validchars).length(); i++) {
            alpha.add(getString(R.string.validchars).charAt(i));    // setup alphabets for encryption and decryption
            if (i < getString(R.string.validlchars).length()) lalpha.add(getString(R.string.validlchars).charAt(i));
        }
        for (int i = 0; i < 9; i++) {
            if (i < 3) {
                xnul.add(0);
            }
            b.add(0);   // inital formula parameters
            c.add(0);
            m.add(0);
        }

        final SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);  // setup access to user-saved private key

        Button copyptBtn = (Button) findViewById(R.id.copyptBtn);
        Button copyctBtn = (Button) findViewById(R.id.copyctBtn);
        Button copykeyBtn = (Button) findViewById(R.id.copykeyBtn);
        assert copyptBtn != null;
        assert copyctBtn != null;
        assert copykeyBtn != null;

        copyptBtn.setOnClickListener(new View.OnClickListener() {   // setup copy button actions
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("plaintext", ptBox.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.copypttoast, Toast.LENGTH_SHORT);
                toast.show();   // display to user that text has been copied to clipboard
            }
        });
        copyctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ciphertext", ctBox.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.copycttoast, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        copykeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("publickey", keyBox.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.copykeytoast, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button convertBtn = (Button) findViewById(R.id.convertBtn);
        assert convertBtn != null;
        convertBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 9; i++) {
                    String address1 = "b" + (i + '0');
                    String address2 = "c" + (i + '0');
                    String address3 = "m" + (i + '0');
                    b.set(i, sharedPref.getInt(address1, getString(R.string.b_default).charAt(i) - '0'));   // get saved private key
                    c.set(i, sharedPref.getInt(address2, getString(R.string.c_default).charAt(i) - '0'));
                    m.set(i, sharedPref.getInt(address3, getString(R.string.m_default).charAt(i) - '0'));
                }
                
                int xindex = 0; // count of how many exception-nulls found
                for (int i = 1; i <= 9; i++) {
                    if ((!b.contains(i) || !c.contains(i) || !m.contains(i)) && !xnul.contains(i)) {
                        if (xindex < 3) {
                            xnul.set(xindex, i);    // xnuls are computed by the digits between 1 and 9 that are not used in private key
                            xindex++;
                        }
                    }
                }
                
                String plaintext = ptBox.getText().toString();  // get text typed by user
                String ciphertext = ctBox.getText().toString();
                String publickey = keyBox.getText().toString();
                
                if (plaintext.matches("")) {    // if plaintext is empty, but ciphertext and public key are not, start decryption
                    if (ciphertext.matches("") || publickey.matches("")) {  // ciphertext and public key should not be empty for decryption
                        Calendar calendar = Calendar.getInstance(); // setup error to print to console
                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                        String text = formattedDate + " " + getString(R.string.error1) + "\n" + console.getText().toString();
                        console.setText(text);
                    }
                    else {  // start decryption
                        
                        ArrayList<Integer> keyb = new ArrayList<>(ciphertext.length() / 2);
                        ArrayList<Integer> keyc = new ArrayList<>(ciphertext.length() / 2);
                        ArrayList<Integer> keym = new ArrayList<>(ciphertext.length() / 2);
                        
                        for (int i = 0; i < ciphertext.length() / 2; i++) { // ciphertext.length() / 2 == plaintext.length()
                            /* NOTE:
                                - keyb, keyc and keym are not the same as b, c and m
                                - b, c and m are key1, key2 and key3 in the private key menu
                                - keyb, keyc and keym are the useful digits in the public key
                                - the key_ arrays are used in the decryption formula
                                - the b, c and m arrays are used in the encryption formula
                            */
                            keyb.add(0);    // fill arrays with 0s to be used
                            keyc.add(0);
                            keym.add(0);
                        }

                        int keyindex = 0;   // index of the plaintext character being computed
                        boolean error = false;

                        for (int i = 0; i < 27 * (ciphertext.length() / 2); i++) {
                            if (!(xnul.contains(publickey.charAt(i) - '0'))) {
                                if (publickey.charAt(i) - '0' == i - (keyindex * 27) + 1) { // matching the public key digit to its index in the public key
                                    keyb.set(keyindex, publickey.charAt(i) - '0');
                                }
                                else if (publickey.charAt(i) - '0' == i - (keyindex * 27) - 8) {
                                    keyc.set(keyindex, publickey.charAt(i) - '0');
                                }
                                else if (publickey.charAt(i) - '0' == i - (keyindex * 27) - 17) {
                                    keym.set(keyindex, publickey.charAt(i) - '0');
                                    keyindex++;
                                }
                                else if (alpha.indexOf(publickey.charAt(i)) < 27 || alpha.indexOf(publickey.charAt(i)) > 35) {
                                    error = true;
                                    break;
                                }
                            }
                        }

                        for (int i = 0; i < ciphertext.length(); i++) {
                            if (!alpha.contains(ciphertext.charAt(i))) {
                                error = true;
                                break;
                            }
                        }

                        if (error) {
                            Calendar calendar = Calendar.getInstance();
                            String formattedDate = simpleDateFormat.format(calendar.getTime());
                            String text = formattedDate + " " + getString(R.string.error2) + "\n" + console.getText().toString();
                            console.setText(text);
                        }
                        else {
                            ArrayList<Character> pt = new ArrayList<>(ciphertext.length() / 2);

                            for (int index = 0; index < ciphertext.length() / 2; index++) {
                                try {
                                    pt.add((alpha.get(((((alpha.indexOf(ciphertext.charAt(index * 2)) * 39) + alpha.indexOf(ciphertext.charAt((index * 2) + 1))) / keym.get(index)) - keyc.get(index)) / keyb.get(index))));
                                    // the above line is the decryption formula. When changing the encryption formula, make sure this is the reverse
                                }
                                catch (Exception e) {   // likely to be divide-by-zero error
                                    error = true;
                                    break;
                                }
                                String text = ptBox.getText().toString() + pt.get(index);
                                ptBox.setText(text);
                            }
                            if (error) {
                                ptBox.setText("");
                                Calendar calendar = Calendar.getInstance();
                                String formattedDate = simpleDateFormat.format(calendar.getTime());
                                String successtext = formattedDate + " " + getString(R.string.error3) + "\n" + console.getText().toString();
                                console.setText(successtext);
                            }
                            else {
                                Calendar calendar = Calendar.getInstance();
                                String formattedDate = simpleDateFormat.format(calendar.getTime());
                                String successtext = formattedDate + " " + getString(R.string.success2) + "\n" + console.getText().toString();
                                console.setText(successtext);
                            }
                        }
                    }
                }
                else {  // start encryption
                    if (!(ciphertext.matches("") && publickey.matches(""))) {   // ciphertext and public key should be empty
                        Calendar calendar = Calendar.getInstance();
                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                        String text = formattedDate + " " + getString(R.string.error1) + "\n" + console.getText().toString();
                        console.setText(text);
                    }
                    else {
                        ArrayList<Integer> dec = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> key = new ArrayList<>(27 * plaintext.length());
                        for (int i = 0; i < 27 * plaintext.length(); i++) {
                            key.add(0); // setup public key
                        }
                        ArrayList<Integer> keyb = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> keyc = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> keym = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> cipherdec = new ArrayList<>(plaintext.length());
                        ArrayList<Character> ct = new ArrayList<>(plaintext.length() * 2);
                        for (int i = 0; i < plaintext.length() * 2; i++) {
                            ct.add('A');    // setup ciphertext
                        }

                        boolean error = false;
                        for (int i = 0; i < plaintext.length(); i++) {
                            if (alpha.contains(plaintext.charAt(i))) {
                                dec.add(alpha.indexOf(plaintext.charAt(i)));    // convert characters into corresponding indeces of the alphabet (see top of program for use of alpha)
                            }
                            else if (lalpha.contains(plaintext.charAt(i))) {
                                dec.add(lalpha.indexOf(plaintext.charAt(i)));
                            }
                            else {
                                error = true;
                                break;
                            }
                            
                            Random random = new Random();   // generate public key
                            keyb.add(b.get(random.nextInt(9)));
                            keyc.add(c.get(random.nextInt(9)));
                            keym.add(m.get(random.nextInt(9)));
                            key.set((i * 27) + keyb.get(i) - 1, keyb.get(i));
                            key.set((i * 27) + keyc.get(i) + 8, keyc.get(i));
                            key.set((i * 27) + keym.get(i) + 17, keym.get(i));
                            cipherdec.add(keym.get(i) * ((keyb.get(i) * dec.get(i)) + keyc.get(i)));    // formula: m*((b*d)+c)
                            // the above line is the encryption formula. This can be changed but make sure the decryption formula is the reverse
                            
                            int ans = cipherdec.get(i);
                            ArrayList<Integer> rems = new ArrayList<>(2);
                            for (int count = 0; count < 2; count++) {   // convert to base-39 in terms of the alphabet used at the top of this file
                                rems.add(ans % 39);
                                ans /= 39;
                            }
                            for (int count = 1; count >= 0; count--) {
                                ct.set((i * 2) + 1 - count, alpha.get(rems.get(count)));
                            }
                        }

                        if (error) {
                            Calendar calendar = Calendar.getInstance();
                            String formattedDate = simpleDateFormat.format(calendar.getTime());
                            String errortext = formattedDate + " " + getString(R.string.error2) + "\n" + console.getText().toString();
                            console.setText(errortext);
                        }
                        else {
                            for (int ctindex = 0; ctindex < plaintext.length() * 2; ctindex++) {
                                String cttext = ctBox.getText().toString() + ct.get(ctindex);
                                ctBox.setText(cttext);
                            }

                            Random random = new Random();   // add nulls to public key
                            for (int i = 0; i < 27 * plaintext.length(); i++) {
                                if (key.get(i) == 0) {
                                    int nulkey;
                                    if (i - ((i / 27) * 27) >= 0 && i - ((i / 27) * 27) <= 8) {
                                        if (random.nextInt(2) == 0) {
                                            nulkey = b.get(random.nextInt(9));
                                            while (nulkey == i - ((i / 27) * 27) + 1) {
                                                nulkey = b.get(random.nextInt(9));
                                            }
                                        }
                                        else {
                                            nulkey = xnul.get(random.nextInt(3));
                                        }
                                        key.set(i, nulkey);
                                    }
                                    else if (i - ((i / 27) * 27) >= 9 && i - ((i / 27) * 27) <= 17) {
                                        if (random.nextInt(2) == 0) {
                                            nulkey = c.get(random.nextInt(9));
                                            while (nulkey == i - ((i / 27) * 27) - 8) {
                                                nulkey = c.get(random.nextInt(9));
                                            }
                                        }
                                        else {
                                            nulkey = xnul.get(random.nextInt(3));
                                        }
                                        key.set(i, nulkey);
                                    }
                                    else {
                                        if (random.nextInt(2) == 0) {
                                            nulkey = m.get(random.nextInt(9));
                                            while (nulkey == i - ((i / 27) * 27) - 17) {
                                                nulkey = m.get(random.nextInt(9));
                                            }
                                        }
                                        else {
                                            nulkey = xnul.get(random.nextInt(3));
                                        }
                                        key.set(i, nulkey);
                                    }
                                }
                                String keytext = keyBox.getText().toString() + key.get(i).toString();
                                keyBox.setText(keytext);
                            }

                            Calendar calendar = Calendar.getInstance();
                            String formattedDate = simpleDateFormat.format(calendar.getTime());
                            String successtext = formattedDate + " " + getString(R.string.success1) + "\n" + console.getText().toString();
                            console.setText(successtext);
                        }
                    }
                }
            }
        });
    }


}

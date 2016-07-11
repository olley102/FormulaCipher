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

    //private key
    ArrayList<Integer> b = new ArrayList<>(9);
    ArrayList<Integer> c = new ArrayList<>(9);
    ArrayList<Integer> m = new ArrayList<>(9);
    ArrayList<Integer> xnul = new ArrayList<>(3);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_privatekey:
                Intent intent = new Intent(this, PrivateKeyActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_help2:
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
                helpAlert.show();
                return true;
            case R.id.action_about:
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
        setContentView(R.layout.activity_main);

        final EditText ptBox = (EditText) findViewById(R.id.key1Box);
        final EditText ctBox = (EditText) findViewById(R.id.key2Box);
        final EditText keyBox = (EditText) findViewById(R.id.key3Box);
        final TextView console = (TextView) findViewById(R.id.consoleText);
        assert ptBox != null;
        assert ctBox != null;
        assert keyBox != null;
        assert console != null;
        console.setSingleLine(false);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        for (int i = 0; i < getString(R.string.validchars).length(); i++) {
            alpha.add(getString(R.string.validchars).charAt(i));
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

        final SharedPreferences sharedPref = getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);

        Button copyptBtn = (Button) findViewById(R.id.copyptBtn);
        Button copyctBtn = (Button) findViewById(R.id.copyctBtn);
        Button copykeyBtn = (Button) findViewById(R.id.copykeyBtn);
        assert copyptBtn != null;
        assert copyctBtn != null;
        assert copykeyBtn != null;

        copyptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("plaintext", ptBox.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.copypttoast, Toast.LENGTH_SHORT);
                toast.show();
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
                    b.set(i, sharedPref.getInt(address1, getString(R.string.b_default).charAt(i) - '0'));   // get saved data
                    c.set(i, sharedPref.getInt(address2, getString(R.string.c_default).charAt(i) - '0'));
                    m.set(i, sharedPref.getInt(address3, getString(R.string.m_default).charAt(i) - '0'));
                }
                int xindex = 0;
                for (int i = 1; i <= 9; i++) {
                    if ((!b.contains(i) || !c.contains(i) || !m.contains(i)) && !xnul.contains(i)) {
                        if (xindex < 3) {
                            xnul.set(xindex, i);
                            xindex++;
                        }
                    }
                }
                String plaintext = ptBox.getText().toString();
                String ciphertext = ctBox.getText().toString();
                String publickey = keyBox.getText().toString();
                if (plaintext.matches("")) {
                    if (ciphertext.matches("") || publickey.matches("")) {
                        Calendar calendar = Calendar.getInstance();
                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                        String text = formattedDate + " " + getString(R.string.error1) + "\n" + console.getText().toString();
                        console.setText(text);
                    }
                    else {
                        ArrayList<Integer> keyb = new ArrayList<>(ciphertext.length() / 2);
                        ArrayList<Integer> keyc = new ArrayList<>(ciphertext.length() / 2);
                        ArrayList<Integer> keym = new ArrayList<>(ciphertext.length() / 2);
                        for (int i = 0; i < ciphertext.length() / 2; i++) {
                            keyb.add(0);
                            keyc.add(0);
                            keym.add(0);
                        }

                        int keyindex = 0;
                        boolean error = false;

                        for (int i = 0; i < 27 * (ciphertext.length() / 2); i++) {
                            if (!(xnul.contains(publickey.charAt(i) - '0'))) {
                                if (publickey.charAt(i) - '0' == i - (keyindex * 27) + 1) {
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
                                }
                                catch (Exception e) {
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
                else {
                    if (!(ciphertext.matches("") && publickey.matches(""))) {
                        Calendar calendar = Calendar.getInstance();
                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                        String text = formattedDate + " " + getString(R.string.error1) + "\n" + console.getText().toString();
                        console.setText(text);
                    }
                    else {
                        ArrayList<Integer> dec = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> key = new ArrayList<>(27 * plaintext.length());
                        for (int i = 0; i < 27 * plaintext.length(); i++) {
                            key.add(0);
                        }
                        ArrayList<Integer> keyb = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> keyc = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> keym = new ArrayList<>(plaintext.length());
                        ArrayList<Integer> cipherdec = new ArrayList<>(plaintext.length());
                        ArrayList<Character> ct = new ArrayList<>(plaintext.length() * 2);
                        for (int i = 0; i < plaintext.length() * 2; i++) {
                            ct.add('A');
                        }

                        boolean error = false;
                        for (int i = 0; i < plaintext.length(); i++) {
                            if (alpha.contains(plaintext.charAt(i))) {
                                dec.add(alpha.indexOf(plaintext.charAt(i)));    // convert characters into corresponding indeces
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

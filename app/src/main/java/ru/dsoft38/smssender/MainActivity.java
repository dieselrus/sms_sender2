package ru.dsoft38.smssender;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;


public class MainActivity extends ActionBarActivity {

    ImageButton btnBrowse;
    ImageButton btnStart;
    ImageButton btnPause;
    ImageButton btnStop;
    ImageButton btnClean;

    static TextView tvPhoneNumberListFilePatch;
    static TextView tvPhoneNumberCount;
    TextView tvPhoneNumberPathFile;
    TextView tvMessage;

    EditText editMessageTest;

    ProgressBar progressBar;

    int CurrentIndexSMS = 0;
    int maxSMSLen = 160;
    int smsCount = 1;
    boolean isStop = false;

    static List<String> strNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBrowse   = (ImageButton) findViewById(R.id.imgButtonBrowse);
        btnStart    = (ImageButton) findViewById(R.id.imgButtonSend);
        btnPause    = (ImageButton) findViewById(R.id.imgButtonPause);
        btnStop     = (ImageButton) findViewById(R.id.imgButtonStop);
        btnClean    = (ImageButton) findViewById(R.id.imgButtonClean);

        editMessageTest = (EditText) findViewById(R.id.editMessageText);

        tvPhoneNumberListFilePatch  = (TextView) findViewById(R.id.tvPhoneNumberListPath);
        tvPhoneNumberPathFile       = (TextView) findViewById(R.id.tvPhoneNumberPathFile);
        tvPhoneNumberCount          = (TextView) findViewById(R.id.tvPhoneNumCount);
        tvMessage                   = (TextView) findViewById(R.id.tvMessage);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

// Назначаем обработчик нажатия на кнопку Отправить СМС
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (btnStart.isEnabled()) {
                    // Выбран файл с номерами телефонов
                    if (tvPhoneNumberListFilePatch.getText().length() == 0 || strNumbers.size() == 0) {
                        Toast.makeText(getApplicationContext(),
                                "Выберите файл со списком номеров для отправки!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Проверим введен ли текст СМС
                    if (editMessageTest.getText().length() < 2) {
                        Toast.makeText(getApplicationContext(),
                                "Введите текст СМС!",
                                Toast.LENGTH_LONG).show();
                        return;
                    } else {

                        // Делаем кнопку не активной
                        btnBrowse.setEnabled(false);
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(true);
                        btnPause.setEnabled(true);
                        btnClean.setEnabled(false);
                        editMessageTest.setEnabled(false);

                        btnBrowse.setBackgroundResource(R.drawable.browse_down);
                        btnStart.setBackgroundResource(R.drawable.play_down);
                        btnStop.setBackgroundResource(R.drawable.stop_up);
                        btnPause.setBackgroundResource(R.drawable.pausa_up);
                        btnClean.setBackgroundResource(R.drawable.clean_down);
                    }
                }
            }
        });

        // Назначаем обработчик нажатия на кнопку выбора файла
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (btnBrowse.isEnabled()) {
                    OpenFileDialog fd = new OpenFileDialog(MainActivity.this).setFilter(".*\\.txt");
                    fd.show();

                    // Чтение списка номеров из файла
                    //strNumbers = readFile(tvPhoneNumberListFilePatch.getText().toString());

                    //tvPhoneNumberListFilePatch.setText(fd.getContext().);
                    //tvPhoneNumberPathFile.setText(getResources().getString(R.string.phoneNumberList) + " (" + String.valueOf(strNumbers.size()) + ")");

                    btnStart.setEnabled(true);
                    btnPause.setEnabled(false);
                    btnStop.setEnabled(false);

                    btnStart.setBackgroundResource(R.drawable.play_up);
                    btnStop.setBackgroundResource(R.drawable.stop_down);
                    btnPause.setBackgroundResource(R.drawable.pausa_down);
                }
            }
        });

        // Назначаем обработчик нажатия на кнопку приостановки отправки
        btnPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (btnPause.isEnabled()) {
                    // Останавливаем отправку
                    isStop = true;

                    btnStart.setEnabled(true);
                    btnPause.setEnabled(false);
                    btnStop.setEnabled(true);

                    btnStart.setBackgroundResource(R.drawable.play_up);
                    btnStop.setBackgroundResource(R.drawable.stop_up);
                    btnPause.setBackgroundResource(R.drawable.pausa_down);
                }
            }
        });

        // Назначаем обработчик нажатия на кнопку остановки отправки
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (btnStop.isEnabled()) {
                    // Останавливаем отправку
                    isStop = true;
                    CurrentIndexSMS = 0;

                    btnStart.setEnabled(true);
                    btnPause.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnBrowse.setEnabled(true);
                    btnClean.setEnabled(true);
                    editMessageTest.setEnabled(true);

                    btnStart.setBackgroundResource(R.drawable.play_up);
                    btnStop.setBackgroundResource(R.drawable.stop_down);
                    btnPause.setBackgroundResource(R.drawable.pausa_down);
                    btnBrowse.setBackgroundResource(R.drawable.browse_up);
                    btnClean.setBackgroundResource(R.drawable.clean_up);
                }
            }
        });

        //Обработка ввода символов в текстовое поле для текста СМС
        editMessageTest.addTextChangedListener(new TextWatcher()  {
            @Override
            public void afterTextChanged(Editable s) {
                if(editMessageTest.getText().toString().length() > 0) {
                    btnClean.setEnabled(true);
                    btnClean.setBackgroundResource(R.drawable.clean_up);
                } else {
                    btnClean.setEnabled(false);
                    btnClean.setBackgroundResource(R.drawable.clean_down);
                }

                //imgStatus.setVisibility(View.INVISIBLE);
                //tvMessageText.setText(getResources().getString(R.string.MessageText) +
                // " (" + String.valueOf(MAX_LENGTH_SMS - strMyName.length() - txtSMSText.length()) + ")");

                //String strCurrentSMS = "1";

                if(isCyrillic(editMessageTest.getText().toString())){
                    maxSMSLen = 70;
                } else {
                    maxSMSLen = 160;
                }

                smsCount = (int)(editMessageTest.getText().length() / maxSMSLen) + 1;
                String strCurrentSMS = String.valueOf(smsCount);

                String totalSMSLen = String.valueOf(editMessageTest.getText().length());

                // SMS Sender Pro
                //tvMessage.setText(getResources().getString(R.string.messageText) + " (" + totalSMSLen + "/" + strCurrentSMS + ")");

                //tvMessage.setText(getResources().getString(R.string.messageText) + " (" + totalSMSLen + "/" + maxSMSLen + ")");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

        });

        // Очистка тескта СМС
        btnClean.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (btnClean.isEnabled()) {
                    editMessageTest.setText("");
                }
            }
        });
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

        return super.onOptionsItemSelected(item);
    }

    // Чтение файла с номерами
    static List<String> readFile(String filePath){

        List<String> strNumbers = new Vector<String>();
        //File sdcard = Environment.getExternalStorageDirectory();

        //Создаём объект файла
        //File file = new File(sdcard, filePath);
        File file = new File(filePath);

        //Read text from file
        //StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                //text.append(line);
                //text.append('\n');
                strNumbers.add(line.trim());
            }
        }
        catch (IOException e) {
            Log.d("Data", e.getMessage().toString());
        }

        //Log.d("Data", text);

        return strNumbers;
    }

    // Определение языка (Кирилица или нет)
    boolean isCyrillic(String _str){
        for(int i = 0; i < _str.length(); i++){
            //String hexCode = Integer.toHexString(_str.codePointAt(i)).toUpperCase();
            int hexCode = _str.codePointAt(i);
            //Log.d("Data", String.valueOf(hexCode));

            if(hexCode > 1040 && hexCode < 1103){
                return true;
            }

        }
        return false;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    static public void setFilePath(String path){
        // Чтение списка номеров из файла
        strNumbers = readFile(path);

        tvPhoneNumberCount.setText("(" + String.valueOf(strNumbers.size()) + ")");
        tvPhoneNumberListFilePatch.setText(path);
    }
}

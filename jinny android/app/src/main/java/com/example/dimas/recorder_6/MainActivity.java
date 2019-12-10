package com.example.dimas.recorder_6;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.jar.Manifest;

import android.app.Activity;
//import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // проверка, вошел ли пользователь
    Boolean authorized = false;
    Boolean completeShift = false;
    String Name;
    String Password;
    int request_result = 0;
    String URL_path = ""; // Подставьте сюда url
    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FLAG = "flag";
    public static final String APP_PREFERENCES_COMPLETE_SHIFT = "completeShift";
    public static final String APP_PREFERENCES_NAME = "name";
    public static final String APP_PREFERENCES_PASSWORD = "password";

    private  static  final int REQUEST_RECORD_AUDIO = 10001;
    //private  static  final int REQUEST_WRITE_EXTERNAL_STORAGE = 10001;
    Integer freq = 16000;

    Button startRec, stopRec, playBack;

    Boolean recording;
    private static final String RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO;
    //private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isPermissionGranted(RECORD_AUDIO_PERMISSION))
            requestMultiplePermissions();

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        startRec = findViewById(R.id.startrec);
        stopRec = findViewById(R.id.stoprec);
        playBack = findViewById(R.id.playback);

        startRec.setOnClickListener(startRecOnClickListener);
        stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_result){
            Name = data.getStringExtra(LoginActivity.Name);
            Password = data.getStringExtra(LoginActivity.Password);
            authorized = true;
            completeShift = true;
            putValues();

            HttpURLConnection conn;

            if (resultCode == 1)
                URL_path += "login";
            if (resultCode == 2)
                URL_path += "register";

            try {
                String post_url = URL_path
                        + "login=" + URLEncoder.encode(Name, "UTF-8")
                        + "&password=" + URLEncoder.encode(Password, "UTF-8")
                        + "&lang=ru_RU.UTF-8";
                try{
                    URL url = new URL(post_url);
                    //TextView tw = findViewById(R.id.textView);
                    //tw.setText(post_url);
                } catch (MalformedURLException e){
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
    }

    // модель ответа сервера
   /* public class AddEmailResult {
        @SerializedName("email")
        public String email;
        @SerializedName("primary")
        public Boolean primary;
        @SerializedName("verified")
        public Boolean verified;
    }*/

    // интерфейс, через который будет происходить взаимодействие с сервером.
   // public interface Server{
     //   @POST("user/addemail")
    //    Call<List<AddEmailResult>> addEmail(@Body List<String> emails)
   // }


    OnClickListener startRecOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            Thread recordThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    recording = true;
                    startRecord();
                }

            });

            recordThread.start();

        }
    };

    OnClickListener stopRecOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            recording = false;
        }
    };

    OnClickListener playBackOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            playRecord();
        }

    };

    // проверка: получены ли разрешения
    private boolean isPermissionGranted(String permission) {
        // проверяем разрешение
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        // true - если есть, false - если нет
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.CHANGE_NETWORK_STATE,
                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED
                },
                REQUEST_RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // проверка по запрашиваемому коду
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!authorized) {
                    authorized = true;
                    SharedPreferences.Editor editor_a = mSettings.edit();
                    editor_a.putBoolean(APP_PREFERENCES_FLAG, authorized);
                    editor_a.apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, request_result);
                }
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // начать запись
    private void startRecord() {

        File file = new File(Environment.getExternalStorageDirectory(),
                "test-6.pcm");

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(
                    bufferedOutputStream);

            int minBufferSize = AudioRecord
                    .getMinBufferSize(freq, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, freq,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

            audioRecord.startRecording();

            while (recording) {
                int numberOfShort = audioRecord.read(audioData, 0,
                        minBufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();

         //   rawToWave(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // воспроизвести последнюю запись
    void playRecord() {

        File file = new File(Environment.getExternalStorageDirectory(),
                "test-6.pcm");

        int shortSizeInBytes = Short.SIZE / Byte.SIZE;

        int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    inputStream);
            DataInputStream dataInputStream = new DataInputStream(
                    bufferedInputStream);

            int i = 0;
            while (dataInputStream.available() > 0) {
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    freq, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void onClickTY(View view) {
        TextView tw = findViewById(R.id.textView);
        String Text;
        if (authorized){
            Text = "True";
        } else{
            Text = "False";
        }
        Text += " + " + Name + " + " + Password;
        tw.setText(Text);
    }*/

    protected void onPause(){
        super.onPause();
        if (completeShift) {
            putValues();
        }
    }

    protected void putValues(){
        SharedPreferences.Editor editor_a = mSettings.edit();
        SharedPreferences.Editor editor_c = mSettings.edit();
        SharedPreferences.Editor editor_n = mSettings.edit();
        SharedPreferences.Editor editor_p = mSettings.edit();
        editor_a.putBoolean(APP_PREFERENCES_FLAG, authorized);
        editor_c.putBoolean(APP_PREFERENCES_COMPLETE_SHIFT, completeShift);
        editor_n.putString(APP_PREFERENCES_NAME, Name);
        editor_p.putString(APP_PREFERENCES_PASSWORD, Password);
        editor_a.apply();
        editor_c.apply();
        editor_n.apply();
        editor_p.apply();
    }

    protected void onResume() {
        super.onResume();
        if (mSettings.contains(APP_PREFERENCES_FLAG) && mSettings.contains(APP_PREFERENCES_COMPLETE_SHIFT)) {
            completeShift = mSettings.getBoolean(APP_PREFERENCES_COMPLETE_SHIFT, false);
            if (completeShift) {
                authorized = mSettings.getBoolean(APP_PREFERENCES_FLAG, false);
                Name = mSettings.getString(APP_PREFERENCES_NAME, "noName");
                Password = mSettings.getString(APP_PREFERENCES_PASSWORD, "noPassword");
            }
        }
        // если авторизация не произведена
        if (isPermissionGranted(RECORD_AUDIO_PERMISSION) && !authorized){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, request_result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void OnClickSignOut(MenuItem item) {
        authorized = false;
        completeShift = false;
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, request_result);
    }

    /*private void rawToWave(final File rawFile) throws IOException {
        File waveFile = new File(Environment.getExternalStorageDirectory(),
                "test-6.wav");
        waveFile.createNewFile();

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
            input.skip(44);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 44100); // sample rate
            writeInt(output, freq * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            output.write(rawData);

            //output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }*/

}
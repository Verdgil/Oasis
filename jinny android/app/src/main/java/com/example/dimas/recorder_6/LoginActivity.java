package com.example.dimas.recorder_6;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {

    private EditText mNameView;
    private EditText mPasswordView;
    public final static String Name = "name";
    public final static String Password = "password";

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNameView = findViewById(R.id.name);
        mPasswordView = findViewById(R.id.password);
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void onClickIn(View view) {
        if (mNameView.length() == 0 || mPasswordView.length() == 0)
            showToast("Логин не может быть пустым");
        else{
            //String URL_path = "";
            intent.putExtra(Name, mNameView.getText().toString());
            intent.putExtra(Password, mPasswordView.getText().toString());
            showToast("Вход выполнен");
            setResult(1, intent);
            finish();
        }
    }

    public void onClickUp(View view) {
        if (mNameView.length() == 0 || mPasswordView.length() == 0)
            showToast("Логин не может быть пустым");
        else{
            //String URL_path = "";
            intent.putExtra("name", mNameView.getText().toString());
            intent.putExtra("password", mPasswordView.getText().toString());
            showToast("Регистрация прошла успешно");
            setResult(2, intent);
            finish();
        }
    }


}


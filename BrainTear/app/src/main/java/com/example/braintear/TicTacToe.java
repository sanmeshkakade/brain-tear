package com.example.braintear;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TicTacToe extends AppCompatActivity {
    public static String Player;
    public FragmentManager fm = getSupportFragmentManager();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);
        SharedPreferences pref = getSharedPreferences(SelectGameActivity.PreferenceFile,MODE_PRIVATE);
        Player = pref.getString("Player","");

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container,new fragment_connection());
        ft.commit();
    }
}

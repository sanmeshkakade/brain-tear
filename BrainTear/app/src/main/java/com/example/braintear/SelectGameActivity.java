
package com.example.braintear;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SelectGameActivity extends AppCompatActivity {

    public static String PreferenceFile = "general_settings";
    private static String playerName = "";

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_game_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView v = (CardView) findViewById(R.id.tictactoe_item);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TicTacToe.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_item:
                askName();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void askName() {
        final Utils.InputDialog d = new Utils.InputDialog(this);
        d.setTitle("Enter your name");
        d.setInput(playerName);
        d.show();
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerName = d.getInput();
                        savePreferences();
                    }
                });
            }
        });
    }

    private void savePreferences(){
        SharedPreferences preferences = getSharedPreferences(PreferenceFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Player", playerName).apply();
    }
}

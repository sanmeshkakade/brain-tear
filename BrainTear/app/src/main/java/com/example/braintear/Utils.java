package com.example.braintear;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.lang.Math;

import androidx.annotation.NonNull;

public class Utils {

    public static class InputDialog extends Dialog{
        private Context mContext;
        private EditText edtInput;
        private Button btnOk;
        private ImageButton btnRandom;
        private TextView txtTitle;

        public InputDialog(@NonNull Context context) {
            super(context);
            setContentView(R.layout.dialog_name);
            mContext = context;

            btnOk = findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputDialog.this.dismiss();
                }
            });
            btnRandom = findViewById(R.id.btnRandom);
            btnRandom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] stringArray = mContext.getResources().getStringArray(R.array.randomNames);
                    edtInput.setText(stringArray[Utils.getRandomInteger(stringArray.length)]);
                }
            });
            edtInput = findViewById(R.id.edtInput);
            txtTitle = findViewById(R.id.txtTitle);
        }

        @Override
        public void setTitle(CharSequence title) {
            txtTitle.setText(title);
        }

        public String getInput() {
            return edtInput.getText().toString();
        }

        public void setInput(String input) {
            edtInput.setText(input);
        }
    }

    private static int getRandomInteger(int length) {
        return (int)(Math.random()*length);
    }


}

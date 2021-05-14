package com.example.braintear;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.net.InetAddresses;

import java.util.Arrays;

public class fragment_connection extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection,container,false);

        EditText ipEditText = (EditText) rootView.findViewById(R.id.ip_address);
        EditText portEditText = (EditText) rootView.findViewById(R.id.port_number);

        TextView notif = (TextView) rootView.findViewById(R.id.notification);

        Button connectButton = (Button) rootView.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String IPAddress = ipEditText.getText().toString();
                String Port = portEditText.getText().toString();
                if(InetAddresses.isInetAddress(IPAddress)){
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container,new fragment_game(IPAddress,Port),"myGameFragment");
                    ft.commit();
                }
                else{
                    Log.e(getClass().getName(),"error in ipAddress");
                    notif.setText(R.string.incorrect_IP_address);
                }

            }
        });
        return rootView;
    }

}

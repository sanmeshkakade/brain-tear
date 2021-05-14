package com.example.braintear;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import tech.gusavila92.websocketclient.WebSocketClient;

public class fragment_game extends Fragment {

    public  String ipAddress="192.168.10.5";
    public  String port="3001";
    static ImageView[] blocks;
    ImageView self_icon, opponent_icon;
    TextView self, opponent, selfScore, opponentScore, notification;
    ScrollView scrollView;
    LinearLayout chatFeed;
    EditText input;
    Button messageSendButton;
    TicTacToeClient client = new TicTacToeClient();
    String oppName = "Opponent";

    public fragment_game(String ipAddress, String port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        initGui(rootView);
        return rootView;
    }

    public void initGui(View rootView) {
        blocks = new ImageView[]{(ImageView) rootView.findViewById(R.id.block_1),
                (ImageView) rootView.findViewById(R.id.block_2),
                (ImageView) rootView.findViewById(R.id.block_3),
                (ImageView) rootView.findViewById(R.id.block_4),
                (ImageView) rootView.findViewById(R.id.block_5),
                (ImageView) rootView.findViewById(R.id.block_6),
                (ImageView) rootView.findViewById(R.id.block_7),
                (ImageView) rootView.findViewById(R.id.block_8),
                (ImageView) rootView.findViewById(R.id.block_9)};

        self = (TextView) rootView.findViewById(R.id.self_name);
        self.setText(TicTacToe.Player);

        self_icon = (ImageView) rootView.findViewById(R.id.self_icon);

        opponent = (TextView) rootView.findViewById(R.id.opponent_name);
        opponent.setText(R.string.waiting_opponent);

        opponent_icon = (ImageView) rootView.findViewById(R.id.opponent_icon);



        notification = (TextView) rootView.findViewById(R.id.notification_feed);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollable);

        chatFeed = (LinearLayout) rootView.findViewById(R.id.chat_feed_linear_layout);
        messageSendButton = (Button) rootView.findViewById(R.id.chat_send_button);
        input = (EditText) rootView.findViewById(R.id.input_chat);

        resetGame();
        client.createWebSocketClient();


        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = input.getText().toString();
                client.webSocketClient.send("M" + messageText);
                messageText = TicTacToe.Player+":"+messageText;
                addView(messageText);
            }
        });


    }

    public void resetGame() {
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].setImageResource(R.color.black);
            int finalI = i;
            blocks[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.webSocketClient.send(String.valueOf(finalI));
                }
            });


        }
    }

    public void addView(String messageText) {
        TextView messageView = new TextView(getContext());
        messageView.setText(messageText);
        chatFeed.addView(messageView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public class TicTacToeClient extends Activity {
        boolean gameOver = false;
        char playerMark;

        public WebSocketClient webSocketClient;

        public void createWebSocketClient() {
            URI uri;
            try {
                // Connect to local host
                Log.e("IP","ws://"+ipAddress+":"+port+"/websocket");
                uri = new URI("ws://"+ipAddress+":"+port+"/websocket");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen() {
                    Log.e("WebSocket", " Session is starting");

                }

                @Override
                public void onTextReceived(String s) {
                    doAction(s);
                }

                @Override
                public void onBinaryReceived(byte[] data) {
                }

                @Override
                public void onPingReceived(byte[] data) {
                }

                @Override
                public void onPongReceived(byte[] data) {
                }

                @Override
                public void onException(Exception e) {
                    Log.e("Exception",e.toString());
                }

                @Override
                public void onCloseReceived() {
                    Log.e("WebSocket", " Closed ");
                    System.out.println("onCloseReceived");
                }
            };
//                webSocketClient.setConnectTimeout(10000);
//                webSocketClient.setReadTimeout(60000);
//                webSocketClient.enableAutomaticReconnection(300000);
            webSocketClient.connect();
        }

        public void doAction(String s) {
            if (s.startsWith("M")) {
                String oppMsg = s.substring(1);
                oppMsg =  oppMsg;
                try {
                    String finalOppMsg =  oppMsg;
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment_game.this.addView(finalOppMsg);
                        }
                    });
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }

            }
            if (s.startsWith("User")) {
                String oppName = s.substring(4);
                Log.e("received",oppName);
                try {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment_game.this.opponent.setText(oppName);
                        }
                    });
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }

            }

            if (s.startsWith("VALID")) {
                int posMe = (int) s.charAt(5)-48;
                Log.e("valid",s);
                Log.e("move", String.valueOf(posMe));
                if(playerMark == 'X')
                    try {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    fragment_game.this.notification.setText(R.string.valid_move);
                                    fragment_game.blocks[posMe].setImageResource(R.drawable.icons8x);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.toString());
                    }
                else{
                    try {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment_game.this.notification.setText(R.string.valid_move);
                                fragment_game.blocks[posMe].setImageResource(R.drawable.icons8o);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.toString());
                    }
                }
            }

            if (s.startsWith("Invalid")) {
                try {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment_game.this.notification.setText(s);
                        }
                    });
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }
                Log.e("Invalid", s);
            }

            if (s.startsWith("Opponent")) {
                Log.e("Opponent", s);
                int posOpp = (int) s.charAt(8) - 48;
                try {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(playerMark == 'X')
                                fragment_game.blocks[posOpp].setImageResource(R.drawable.icons8o);
                            else
                                fragment_game.blocks[posOpp].setImageResource(R.drawable.icons8x);
                        }
                    });
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }
            }

            if (s.startsWith("Welcome")) {
                Log.e("Mark", s);
                playerMark = s.charAt(8);
                if(playerMark=='X'){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment_game.this.self_icon.setImageResource(R.drawable.icons8x);
                            fragment_game.this.opponent_icon.setImageResource(R.drawable.icons8o);

                        }
                    });

                }else if(playerMark == 'O'){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment_game.this.self_icon.setImageResource(R.drawable.icons8o);
                            fragment_game.this.opponent_icon.setImageResource(R.drawable.icons8x);

                        }
                    });

                }

            }

            if (s.startsWith("Your") || s.startsWith("X will")) {
                Log.e("Connection", s);
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            opponent.setText(oppName);
                        }
                    });
                } catch(Exception e){
                    Log.e("error in turn",e.toString());
                }

                if (s.startsWith("Your")) {
                    playerMark = 'X';
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment_game.this.self_icon.setImageResource(R.drawable.icons8x);
                                fragment_game.this.opponent_icon.setImageResource(R.drawable.icons8o);
                                fragment_game.this.notification.setText(R.string.your_turn);
                            }
                        });
                    } catch(Exception e){
                        Log.e("error in turn",e.toString());
                    }

                } else {
                    playerMark = 'O';
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment_game.this.self_icon.setImageResource(R.drawable.icons8o);
                                fragment_game.this.opponent_icon.setImageResource(R.drawable.icons8x);
                                fragment_game.this.notification.setText(R.string.opponents_turn);
                            }
                        });
                    } catch(Exception e){
                        Log.e("error in turn",e.toString());
                    }

                }
            }

            if (s.startsWith("Winner") || s.startsWith("Defeat") || s.startsWith("Tie")) {

                Log.e("gameOver", s);
                gameOver = true;

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            notification.setText(s);

                        } catch (Exception e) {
                            Log.e(this.getClass().getName(),e.toString());
                        }

                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(this.getClass().getName(),e.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetGame();
                        gameOver = false;
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_container,new fragment_connection(),"myGameFragment");
                        ft.commit();
                    }
                });

            }

            if (s.startsWith("OTHER_PLAYER_LEFT")) {

                String msg = "Your Opponent Bailed , So I Guess You Are the Winner";
                gameOver = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment_game.this.notification.setText(msg);
                        resetGame();
                    }
                });
                Log.e("OTHER_PLAYER_LEFT", msg);

            }
            String userName = "User"+TicTacToe.Player;
            client.webSocketClient.send(userName);
        }

    }

}
package com.example.lim.rkeyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;


public class MainActivity extends ActionBarActivity {

    int idx;

    LinkedList<Byte> inputSnapshots = new LinkedList<Byte>();

    private static final int CONNECT_SUCCESS_TOAST = 1;
    private static final int CONNECT_FAILED_TOAST = -1;

    private Socket socket;

    private Context mContext;

    private Button[] buttonArr = new Button[10];

    private DataOutputStream networkWriter;

    private String ip = ""; // IP
    private int port = 9007; // PORT번호


    private SendMassgeHandler mHandler;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mHandler = new SendMassgeHandler();

        Button connBtn = (Button) findViewById(R.id.connButton);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Connect");
                alert.setMessage("insert IP address to connect");

                // Set an EditText view to get user input
                final EditText input = new EditText(mContext);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        ip = value.toString();
                        try {

                            new networkThread().start();

                            setButton();

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });


                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                alert.show();
            }
        });




        new sendThread().start();


    }

    private class SendMassgeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CONNECT_SUCCESS_TOAST:

                    Toast.makeText(mContext,"Connect Success - "+ip,Toast.LENGTH_SHORT).show();

                    break;

                case CONNECT_FAILED_TOAST:

                    Toast.makeText(mContext,"Connect Failed - "+ip,Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }

    };

    private void setButton(){

        Button left = (Button) findViewById(R.id.button);
        Button up = (Button) findViewById(R.id.button2);
        Button down = (Button) findViewById(R.id.button3);
        Button right = (Button) findViewById(R.id.button4);
        Button a = (Button) findViewById(R.id.buttonA);
        Button s = (Button) findViewById(R.id.buttonS);
        Button z = (Button) findViewById(R.id.buttonZ);
        Button x = (Button) findViewById(R.id.buttonX);
        Button coin = (Button) findViewById(R.id.buttonCoin);
        Button _1p = (Button) findViewById(R.id.button1P);

        buttonArr[0] = left;
        buttonArr[1] = up;
        buttonArr[2] = down;
        buttonArr[3] = right;
        buttonArr[4] = a;
        buttonArr[5] = s;
        buttonArr[6] = z;
        buttonArr[7] = x;
        buttonArr[8] = coin;
        buttonArr[9] = _1p;



        for (idx = 0; idx < 10 ; idx ++){
            buttonArr[idx].setOnTouchListener(new View.OnTouchListener() {

                int btnIdx = idx;

                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        inputSnapshots.add((byte)(btnIdx+10));
                    }

                    if(event.getAction() == MotionEvent.ACTION_UP){
                        inputSnapshots.add((byte)(btnIdx+20));
                    }


                    return false;
                }
            });
        }

    }

    private class sendThread extends Thread{

        public sendThread(){
            super();
        }

        public void run(){

            try{

                while(true){
                    if(inputSnapshots.size() > 0){
                        Log.d("DATA SEND","SEND");
                        networkWriter.write(inputSnapshots.pop());
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        }

    }


    private class networkThread extends Thread {

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                Log.d("NETWORK","NETWORK WORKING WELL");

                networkWriter = new DataOutputStream(socket.getOutputStream());
                Log.d("NETWORK","DATA STREAM WELL");

                mHandler.sendEmptyMessage(CONNECT_SUCCESS_TOAST);

            } catch (IOException e) {
                Log.e("NETWORK","NETWORK WORKING ERROR");

                mHandler.sendEmptyMessage(CONNECT_FAILED_TOAST);

                e.printStackTrace();
            }
        }
    }



}

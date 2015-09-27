package com.example.ta.bluetoothactivity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by ta on 2015/09/25.
 */
public class SerialThread extends Thread{

    private boolean thread_flag = false;
    private BluetoothSocket bluetoothSocket;
    private BluetoothActivity bluetoothActivity;

    private final static String TAG = "SerialThread";

    public SerialThread(BluetoothSocket bluetoothSocket,BluetoothActivity bluetoothActivity){
        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothActivity = bluetoothActivity;
        thread_flag = true;
    }

    public void run(){

        InputStream inputStream = null;

        try{
            inputStream = bluetoothSocket.getInputStream();

            int receiveDataLength;
            byte[] receiveData = new byte[8];
            for(int i=0; i<8; i++){
                receiveData[i] = 0x0f;
            }
            Log.d(TAG,"thread start");

            while(thread_flag){

				/*---これ以降が通信の処理になる---------------------------------------------------*/

                receiveDataLength = inputStream.read(receiveData);

                bluetoothActivity.updata(receiveData,receiveDataLength);

                /*--------------------------------------------------------------------------------*/
            }
        }catch(Exception e){
            thread_flag = false;
            try{
                bluetoothSocket.close();
            }catch(Exception ee){
            }
        }
    }

}

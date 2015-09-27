package com.example.ta.bluetoothactivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button1;
    private boolean findFlag;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private SerialThread serialThread;

    private static final String TAG = "BluetoothActivity";
    private final String DEVICE_NAME = "kzmPi-0";
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_bluetooth);
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
    }

    /*---Activityが終了したときにスレッドを止めbluetoothSocketを解除する--------------------------*/

    public void onDestroy(){
        super.onDestroy();
        serialThread = null;
        try{
            bluetoothSocket.close();
        }catch(Exception e){
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    public void updata(byte[] receiveData,int receiveDataLength){
        Log.d(TAG,"receiveDataLength:" + receiveDataLength);
        for(int i=0; i<receiveDataLength; i++){
            Log.d(TAG,"receiveData(" + i + "):" + receiveData[i]);
        }
    }

    public void onClick(View view){
        Log.d(TAG,"onClick");
        switch (view.getId()){
            case R.id.button1:

            /*---Bluetoothデバイスを検索してbluetoothDeviceに代入-------------------------------------*/

                findFlag = false;
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : devices){
                    Log.d(TAG,"device name:" + device.getName());
                    if(device.getName().equals(DEVICE_NAME)){
                        Log.d(TAG,"find bluetooth device:" + DEVICE_NAME);
                        Toast.makeText(this,"デバイスを発見できました",Toast.LENGTH_SHORT).show();
                        bluetoothDevice = device;
                        findFlag = true;
                    }
                }

        /*----------------------------------------------------------------------------------------*/

        /*---デバイスと接続し通信を始める---------------------------------------------------------*/

                if(findFlag){
                    try{
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                        bluetoothSocket.connect();
                        Log.d(TAG, "complete connecting");
                        Toast.makeText(this,"デバイスとの接続が完了しました",Toast.LENGTH_SHORT).show();

                        serialThread = new SerialThread(bluetoothSocket,this);
                        serialThread.start();
                        Log.d(TAG, "start communicating");
                        Toast.makeText(this, "通信を開始します", Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        Toast.makeText(this, "デバイスと接続できませんでした", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"デバイスを見つけられませんでした",Toast.LENGTH_LONG).show();
                }

        /*----------------------------------------------------------------------------------------*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
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
}

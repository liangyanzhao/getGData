package com.example.ziyan.getgdata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public SensorManager sensorManager;
    public MySensorListener sensorListener;
    public boolean isStart = false;
    public File file;
    public List<Double> list;
    public String path;
    public int count = 0;
    private int max_count = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        // 创建文件
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath()+"/TestData2000.txt";
        file = new File(path);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void myOnClick(View view) {
        if (!isStart) {
            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            sensorListener = new MySensorListener();
            sensorManager.registerListener(sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    sensorManager.SENSOR_DELAY_FASTEST);
            isStart = true;
            ((TextView)findViewById(R.id.Result)).setText("Collecting Data");
        }
    }

    public void mySaveFile() {
        Gson gson = new Gson();
        String fileString = gson.toJson(list);

        try{
            FileOutputStream fout = new FileOutputStream(file);
            byte [] bytes = fileString.getBytes();
            fout.write(bytes);
            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    public class MySensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER && count < max_count) {
                // Log.e("sensor", "sensor data changed");
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                double temp = Math.sqrt(x * x + y * y + z * z);
                // Log.e("data", x + " " + y + " " + z + " " + temp);
                ((TextView)(findViewById(R.id.X))).setText("X:" + x);
                ((TextView)(findViewById(R.id.Y))).setText("Y:" + y);
                ((TextView)(findViewById(R.id.Z))).setText("Z:" + z);
                ((TextView)(findViewById(R.id.S))).setText("A:" + (float)temp);

                list.add(temp);

                count++;

                ((TextView)findViewById(R.id.Result)).setText("Collecting Data : " + count);

            } else {
                sensorManager.unregisterListener(sensorListener);
                isStart = false;

                // 保存文件
                mySaveFile();

                ((TextView)findViewById(R.id.Result)).setText("File Saved");

                // 重新初始化
                list.clear();
                count = 0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}

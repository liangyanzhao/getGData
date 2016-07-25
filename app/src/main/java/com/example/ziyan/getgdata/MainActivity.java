package com.example.ziyan.getgdata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public SensorManager sensorManager;
    public MySensorListener sensorListener;
    public boolean isStart = false;
    public File file;
    public List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        /*String path = Environment.getDataDirectory() + "/AccData.txt";
        file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }*/

        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath()+"/MyTestData.txt";
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
                    sensorManager.SENSOR_DELAY_NORMAL);
            isStart = true;
        } else {
            sensorManager.unregisterListener(sensorListener);
            isStart = false;

            Gson gson = new Gson();
            String jsonList = gson.toJson(list);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = jsonList.getBytes();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class MySensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
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

                list.add(Double.toString(temp));

                /*BufferedWriter bufferedWriter = null;
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(file, true)));
                    bufferedWriter.write(temp + ",");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }*/

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}

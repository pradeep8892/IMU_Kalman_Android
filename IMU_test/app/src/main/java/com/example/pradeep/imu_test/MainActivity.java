package com.example.pradeep.imu_test;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private TextView xText, yText, zText;
    private Sensor mySensor, magnetoMeter, gyroScope;
    private SensorManager SM;
    private float[] rawAccData = new float[3];
    private float[] rawAccFilteredData = new float[3];
    private float[] P_xyz_acc=new float[3]; // for Kalman filter
    /**short array that represents raw magnetometer data from sensor*/
    private float[] rawMagData = new float[3];
    private float[] rawMagFilteredData = new float[3];
    private float[] P_xyz_mag = new float[3]; // for Kalman filter
    private float[] calibratedMagData = new float[3];

    //private SensorManager sensorManager;

   // private Sensor accelerometer;
    private Sensor head;
   // private Sensor gyro;
    float linear_acc_x = 0;
    float linear_acc_y = 0;
    float linear_acc_z = 0;

    float heading = 0;

    float gyro_x = 0;
    float gyro_y = 0;
    float gyro_z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create sensor manager

        SM  = (SensorManager)getSystemService(SENSOR_SERVICE);

       //Accelerometer sensor
        mySensor  = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       // magnetoMeter = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroScope = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        head = SM.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //register Sensor Listener

        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
        //SM.registerListener(this,magnetoMeter,SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this,gyroScope,SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this,head,SensorManager.SENSOR_DELAY_NORMAL);


        //Assign text View

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

       if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            linear_acc_x = event.values[0];
            linear_acc_y = event.values[1];
            linear_acc_z = event.values[2];
        }


         else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyro_x = event.values[0];
            gyro_y = event.values[1];
            gyro_z = event.values[2];
        }
         else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            heading = Math.round(event.values[0]);
            if(heading >= 270){
                heading = heading + 90;
                heading = heading - 360;
            }
            else{
                heading = heading + 90;
            }
        }
       // String setTextText = "Heading: " + heading + " Speed: " + speed;
        //tv.setText(setTextText);

       /* xText.setText("X="+ linear_acc_x+"gx =: " + gyro_x+"heading = "+ heading);
        yText.setText("Y:=" + linear_acc_y + "gy= "  + gyro_y);
        zText.setText("Z:=" + linear_acc_z + "gz= " + gyro_z);*/
        float rawData[] = new float[3];
        rawData[0]=linear_acc_x;
        rawData[1] = linear_acc_y;
        rawData[2] = linear_acc_z;
        float[] accData = new float[3];
///filtered accelerometer data using kalman filter
        kalmanFilter(rawData, accData, P_xyz_mag, 0.2f, 1f);

     /*   if(isOrientationUp){
            accData[0] = (float) ((accData[2])/(sqrt(pow(accData[0],2)+pow(accData[1],2)+pow(accData[2],2))));
        } else{
            accData[0]=(float) ((accData[2])/(sqrt(pow(accData[0],2)+pow(accData[1],2)+pow(accData[2],2))));
        }*/
        xText.setText("X="+ accData[0]);//+"gx =: " + gyro_x+"heading = "+ heading);
        yText.setText("Y:=" + accData[1] );//+ "gy= "  + gyro_y);
        zText.setText("Z:=" + accData[2]);// + "gz= " + gyro_z);


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /*
     * Method performs Kalman filtering on specified data
     * @param float array of length 3 representing 3 axis sensor data
     * @param outputFilteredData in this array output result will be stored. Previous value is used to compute current value
     * @param P_xyz probabilities array of length array which is changed after each iteration
     * @param Q Kalman filter Q parameter
     * @param Delta Kalman filter delta parameter
     */
    public static void kalmanFilter(float[] inpuRawData, float[] outputFilteredData, float[] P_xyz, float Q, float delta){
        float Pn;
        float K;
        for(int i=0; i<3; i++){
            Pn=P_xyz[i]+Q;
            K=Pn/(Pn+delta);
            outputFilteredData[i]=outputFilteredData[i]+K*(inpuRawData[i]-outputFilteredData[i]);
            P_xyz[i]=(1-K)*Pn;
        }
    }

   /* public MainActivity(int identifier, boolean isOrientationUp){
        for(int i=0; i<3; i++){
            P_xyz_acc[i]=1;
            P_xyz_mag[i]=1;
        }
        this.identifier = identifier;
        this.isOrientationUp = isOrientationUp;
    }*/
    /**returns identifier of sensor object
     * @return identifier returns identifier of accelerometer*/
   /* public synchronized int getIdentifier(){
        return identifier;
    }
    /**
     * return true if sensor orientation is up and false if orientation of sensor is down
     * @return isOrientationUp identifier indicates if sensor is up or down
     **/
   /* public synchronized boolean isOrientationUp(){
        return isOrientationUp;
    }
    /**@return rawData[0] returns raw X axis value of accelerometer*/


}

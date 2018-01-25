package e.com.timejonegmt;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    String TIME_SERVER = "time-a.nist.gov";
    NTPUDPClient timeClient = new NTPUDPClient();
    Handler handler = new Handler();
    TextView tvTime;
    Button btStartStop;
    Date time;
    boolean isClockRunning =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime =(TextView)findViewById(R.id.tvTime);
        btStartStop =(Button)findViewById(R.id.btStartStop);

        btStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isClockRunning)
                {
                    btStartStop.setText("START");
                }
                else
                {
                    btStartStop.setText("STOP");
                }
                isClockRunning= !isClockRunning;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(myRunnable, 100);
                    }
                }).start();

            }
        });





    }

    Runnable myRunnable= new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getByName(TIME_SERVER);
                        TimeInfo timeInfo = timeClient.getTime(inetAddress);
//                    http://www.javadocexamples.com/java_source/examples/ntp/NTPClient.java.html
                        long returnTime = timeInfo.getMessage().getReceiveTimeStamp().getTime();
                        time = new Date(returnTime);
                        Log.e("Time from " , TIME_SERVER + ": " + time);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTime.setText(time+"");
                            }
                        });

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            if(isClockRunning)
                handler.postDelayed(this,2000);
        }
    };
}

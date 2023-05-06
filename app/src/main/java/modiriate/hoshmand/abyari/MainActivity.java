package modiriate.hoshmand.abyari;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private TextView txtName1, txtName2, txtName3, txth, txtt;
    ImageButton imglh, imgrh, imglt, imgrt, imgset;
    public int numberh = 10, numbert = 1;
    ImageView imgspri, imglamp;
    ImageView imgair_on, imgair_off, imglamp_on, imglamp_off;
    Button imghum;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private Socket socket;
    String IP = "192.168.1.1";
    int PORT = 5000;
    String[] strmsg = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove this line if you don't want AndroidIDE to show this app's logs
        
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        setContentView(R.layout.main);
        
        //Typeface titr = Typeface.createFromAsset(getAssets(), "font/koodak.ttf");
        txtName1 = (TextView) findViewById(R.id.textView1);
        txtName2 = (TextView) findViewById(R.id.textView2);
        txtName3 = (TextView) findViewById(R.id.textView3);
        txth = (TextView) findViewById(R.id.textView4);
        txtt = (TextView) findViewById(R.id.textView5);
        imglh = (ImageButton) findViewById(R.id.imageButton1);
        imgrh = (ImageButton) findViewById(R.id.imageButton2);
        imglt = (ImageButton) findViewById(R.id.imageButton3);
        imgrt = (ImageButton) findViewById(R.id.imageButton4);
        imgset = (ImageButton) findViewById(R.id.imgset);
        imgspri = (ImageView) findViewById(R.id.imgsprinkler);
        imglamp = (ImageView) findViewById(R.id.imglamp);
        imghum = (Button) findViewById(R.id.imghumidity);

        imgair_on = (ImageView) findViewById(R.id.imgirrigation);
        imgair_off = (ImageView) findViewById(R.id.imgirrigatioff);
        imglamp_on = (ImageView) findViewById(R.id.imglampon);
        imglamp_off = (ImageView) findViewById(R.id.imglampoff);
        //txtName1.setTypeface(titr);
        //txtName2.setTypeface(titr);
        //txtName3.setTypeface(titr);

        imgrh.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberh += 10;
                    if (numberh > 100) numberh = 10;
                    txth.setText(String.valueOf(numberh));
                }
            });

        imglh.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberh -= 10;
                    if (numberh < 10) numberh = 100;
                    txth.setText(String.valueOf(numberh));
                }
            });
        imgrt.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numbert++;
                    if (numbert > 10) numbert = 1;
                    txtt.setText(String.valueOf(numbert));
                }
            });

        imglt.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numbert--;
                    if (numbert < 1) numbert = 10;
                    txtt.setText(String.valueOf(numbert));
                }
            });

        imgset.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String Set = "";
                    if (numberh == 10) Set = "SET-1020-" + String.valueOf(numbert);
                    else if (numberh == 20) Set = "SET-1010-" + String.valueOf(numbert);
                    else if (numberh == 30) Set = "SET-1000-" + String.valueOf(numbert);
                    else if (numberh == 40) Set = "SET-990-" + String.valueOf(numbert);
                    else if (numberh == 50) Set = "SET-980-" + String.valueOf(numbert);
                    else if (numberh == 60) Set = "SET-970-" + String.valueOf(numbert);
                    else if (numberh == 70) Set = "SET-960-" + String.valueOf(numbert);
                    else if (numberh == 80) Set = "SET-950-" + String.valueOf(numbert);
                    else if (numberh == 90) Set = "SET-940-" + String.valueOf(numbert);
                    else Set = "SET-930-" + String.valueOf(numbert);

                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                    myClientTask.execute(Set);
                }
            });

        imgair_on.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                    myClientTask.execute("START");
                }
            });

        imgair_off.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                    myClientTask.execute("STOP");
                }
            });

        imglamp_on.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                    myClientTask.execute("ON");
                }
            });

        imglamp_off.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                    myClientTask.execute("OF");
                }
            });

        Thread Thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    MyClientTask myClientTask = new MyClientTask(IP, PORT);
                                    myClientTask.execute("GET");
                                }
                            });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        Thread2.start();
    }

    ////////////////////////////////////////////////// MyClientTask
    public class MyClientTask extends AsyncTask<String, Void, Void> {
        String dstAddress;
        int dstPort;
        String response;
        private String message;

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                socket = new Socket(dstAddress, dstPort);
                socket.setSoTimeout(10000);
                if (socket != null) {
                    printwriter = new PrintWriter(socket.getOutputStream(), true);
                    InputStreamReader inputStreamReader =
                        new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    printwriter.write(params[0]);
                    printwriter.flush();
                    // t2.start();
                } else {
                    System.out.println("Server has not bean started on port 5000.");
                }
                while (true) {
                    try {
                        if (bufferedReader.ready()) {
                            message = bufferedReader.readLine();
                            publishProgress(null);
                            // socket.close();
                            return null;
                        }
                    } catch (SocketTimeoutException e) {
                        if (socket.isConnected()) {
                            try {
                                // msglearn.setText("Check Device");
                                socket.close();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                    }
                }
            } catch (SocketTimeoutException e) {
                try {
                    if (socket != null) {
                        // Checkphone.setText("Check Device");
                        socket.close();
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // textView.setText(message);
            CheckEvent(message);
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ////////////////////////////////////////////////// END MyClientTask
    private void CheckEvent(String result) {
        if (result.startsWith("DATA-")) {
            int i;
            String[] strlink1 = result.split("-");
            for (i = 0; i < 5; i++) strmsg[i] = strlink1[i];
            changhestutus();
        }
    }

    private void changhestutus() {
        int humget = Integer.valueOf(strmsg[1]);
        int humset = Integer.valueOf(strmsg[2]);
        if ((930 > humget) || (humget < 940)) imghum.setText("100");
        else if ((940 > humget) || (humget < 950)) imghum.setText("90");
        else if ((950 > humget) || (humget < 960)) imghum.setText("80");
        else if ((960 > humget) || (humget < 970)) imghum.setText("70");
        else if ((970 > humget) || (humget < 980)) imghum.setText("60");
        else if ((980 > humget) || (humget < 990)) imghum.setText("50");
        else if ((990 > humget) || (humget < 1000)) imghum.setText("40");
        else if ((1000 > humget) || (humget < 1010)) imghum.setText("30");
        else if ((1010 > humget) || (humget < 1020)) imghum.setText("20");
        else imghum.setText("10");
        if (humset <= humget) imgspri.setImageResource(R.drawable.sprinkler_on);
        else imgspri.setImageResource(R.drawable.sprinkler_off);
        if (strmsg[4].toString().equals("0")) imglamp.setImageResource(R.drawable.lamp_off);
        else imglamp.setImageResource(R.drawable.lamp_on);
    }
}

package biz.integsys.autopatch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AudioMonitorListener {
    private final String TAG = "MainActivity";
    private AudioMonitor audioMonitor = new AudioMonitor(this);
    private Number[] am = new Number[AudioMonitor.BUFFER_SIZE];
    private static final int RECORD_AUDIO_PERMISSION = 1;
    private static final int CALL_PHONE_PERMISSION = 2;
    private Switch enableSwitch;
    private XYPlot plot;
    private LineAndPointFormatter series1Format;
    private enum State {IDLE, WAITPHONENUM, INCALL};
    private State state = State.IDLE;
    private String dialString;
    private java.util.Timer idleTimeout;
    private java.util.Timer waitTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableSwitch = (Switch) findViewById(R.id.enable);
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    audioMonitor.start();
                else
                    audioMonitor.stop();
            }
        });
//        enableSwitch.setChecked(true); //XXX for development only

        int recordAudioPermCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO);
        if (recordAudioPermCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CALL_PHONE},
                    RECORD_AUDIO_PERMISSION);
        }
        audioMonitor.init();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean recordPerm = false;
        boolean callPerm = false;
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    recordPerm = true;
                    audioMonitor.init();
                }
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    callPerm = true;
                }
                enableSwitch.setEnabled(recordPerm && callPerm);
            }
        }
    }

    /**
     * Finite state machine which can only change state vis-a-vie DTMFs or timeouts
     * @param dtmf
     * DTMFs received by the AudioMonitor
     */
    public void receivedDTMF(char dtmf) {

        Log.i(TAG, "result: " + dtmf);
        switch (state) {
            case IDLE:
                dialString = "";
                if (dtmf == '*') state = state.WAITPHONENUM;
                idleTimeout = new Timer();
                idleTimeout.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        state = state.IDLE;
                    }
                }, 10000);
                break;
            case WAITPHONENUM:
                if (dtmf == '#') state = state.IDLE;
                if ((dtmf >= '0') && (dtmf <= '9'))
                    dialString += dtmf;
                if (dialString.length() == 7) {
                    Uri number = Uri.parse("tel:"+dialString);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, number);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Figure out how to get AS to stop bitching and remove this
                        return;
                    }
                    startActivity(callIntent); //TODO: AFAIK one cannot get here w/o the permissions
                    state = State.INCALL;
                }
                break;
            case INCALL:
                // XXX: Time to get my telephony manager on
                break;

        }
        if (dtmf == '*') {

        }

    }

}

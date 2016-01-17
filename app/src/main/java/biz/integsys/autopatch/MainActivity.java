package biz.integsys.autopatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AudioMonitorListener {
    private final String TAG = "MainActivity";
    private AudioMonitor audioMonitor= new AudioMonitor(this);
    private Number[] am = new Number[AudioMonitor.BUFFER_SIZE];
    private static final int RECORD_AUDIO_PERMISSION = 1;
    private Switch enableSwitch;
    private XYPlot plot;
    private LineAndPointFormatter series1Format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO_PERMISSION);
        }
        audioMonitor.init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        plot = (XYPlot) findViewById(R.id.plot);
        series1Format = new LineAndPointFormatter(Color.RED, null, null, null);
        plot.setUserRangeOrigin(0);
        plot.setRangeBoundaries(-10,10, BoundaryMode.FIXED);
        plot.getGraphWidget().setDomainLabelOrientation(45);
        plot.getGraphWidget().setDomainTickLabelHorizontalOffset(15);
        plot.getGraphWidget().setDomainTickLabelVerticalOffset(15);
        plot.getLegendWidget().setVisible(false);

        Button showSampleButton = (Button) findViewById(R.id.showSampleButton);
        showSampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread (new Runnable() {
                    @Override
                    public void run() {
                        am = audioMonitor.getAmplitude();
                        XYSeries series1 = new SimpleXYSeries(Arrays.asList(am),
                                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
                        plot.addSeries(series1, series1Format);
                    }
                }).run();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    audioMonitor.init();
                } else {
                    // permission denied
                    enableSwitch.setEnabled(false);
                }
                return;
            }
        }
    }
    public void transformedResult(float result[]) {
        //Log.i(TAG, "result: " + result.toString());

    }

}

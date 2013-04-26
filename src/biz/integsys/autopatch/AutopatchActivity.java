package biz.integsys.autopatch;

import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import biz.integsys.autopatch.R.id;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AutopatchActivity extends Activity {
    ArrayBlockingQueue<short[]> samples = new ArrayBlockingQueue<short[]>(1);
    ArrayBlockingQueue<double[]> spectrum = new ArrayBlockingQueue<double[]>(1);
    SampleTask sampleTask;
    FFTTask fftTask;
    ImageView oscilloscopeView;
    OscilloscopeTask oscilloscopeTask;
    ImageView spectrumView;
    SpectrumTask spectrumTask;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autopatch);
        oscilloscopeView = (ImageView)findViewById(id.oscilloscope);
        spectrumView = (ImageView)findViewById(id.spectrum);
        
        sampleTask = new SampleTask(samples);
        fftTask = new FFTTask(samples, spectrum);
        
        oscilloscopeTask = new OscilloscopeTask(samples, oscilloscopeView);
        spectrumTask = new SpectrumTask(spectrum, spectrumView);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        sampleTask.start();
        fftTask.start();
        oscilloscopeTask.start();
        spectrumTask.start();
        super.onResume();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        sampleTask.stop();
        fftTask.stop();
        oscilloscopeTask.stop();
        spectrumTask.stop();
        super.onPause();
    }

}

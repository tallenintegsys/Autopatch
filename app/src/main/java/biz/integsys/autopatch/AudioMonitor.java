package biz.integsys.autopatch;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by tallen on 12/11/15.
 * This monitors the mic and sends callbacks based on what it "hears" e.g. DTMFs
 */
public class AudioMonitor {
    private final String TAG = "AudioMonitor";
    private final AudioRecord audioRecord;
    private Thread monitorThread;
    private final float[] recordBuffer;
    private final float[] re = new float[32768];
    private float[] im = new float[32768];
    private final float[] zero = new float[32768];
    private final Float[] amplitude = new Float[32768];
    private final FFT fft = new FFT(32768);
    private boolean enable;
    private AudioMonitorListener listener = null;

    public AudioMonitor() {
        int recordBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);
        recordBuffer = new float[recordBufferSize];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, recordBufferSize);
        ///XXX need to check that permissions were okay etc...
        int state;
        while ((state = audioRecord.getState()) != AudioRecord.STATE_INITIALIZED) {
            Log.d(TAG, "AudioRecord state: " + state);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public AudioMonitor(AudioMonitorListener listener) {
        this();
        this.listener = listener;
    }

    public void start() {
        enable = true;
        audioRecord.startRecording();
        monitorThread = new Thread(new Runnable() {
            public void run() {
                do {
                    int read = audioRecord.read(recordBuffer, 0, 44100, AudioRecord.READ_BLOCKING);
                    Log.d(TAG, "read " + read + " floats.");
                    im = zero.clone(); //memset, I hope?
                    System.arraycopy(recordBuffer, 0, re, 0, 32768); //memset, I presume
                    fft.fft(re, im);
                    if (listener != null)
                        listener.transformedResult(re); //XXX do this as a thread???
                } while (enable);
                for (int i=0; i < 32768; i++) {
                    if ((Math.abs(im[i]) > 1000) || (Math.abs(re[i]) > 1000))
                        Log.v(TAG, "i="+i+"   x="+ im[i]+"   y="+ re[i]);
                    audioRecord.stop();
                }
            }
        });
        monitorThread.start();
    }

    public void stop() {
        //monitorThread.interrupt();
        enable = false;
    }

    public synchronized Float[] getAmplitude() {
        updateAmplitude();
        return amplitude;
    }

    private synchronized void updateAmplitude() {
        for (int i = 0; i < re.length; i++)
            amplitude[i] = (float) Math.cos(i/32768);
    }
}

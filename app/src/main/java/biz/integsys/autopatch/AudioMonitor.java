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
    private final AudioRecord audioRecord;
    private Thread monitorThread;
    private final float[] recordBuffer = new float[44100];
    private final double[] re = new double[32768];
    private double[] im = new double[32768];
    private final double[] zero = new double[32768];
    private final FFT fft = new FFT(32768);
    private boolean enable;

    public AudioMonitor() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, 44100);
        ///XXX need to check that permissions were okay etc...
        int state = audioRecord.getState();
        Log.d("AudioMonitor", "state: " + state);
        for (int i=0; i < im.length; i++)
            zero[i]=0; //do i actually need to do this?
    }

    public void start() {
        enable = true;
        audioRecord.startRecording();
        monitorThread = new Thread(new Runnable() {
            public void run() {
                do {
                    int read = audioRecord.read(recordBuffer, 0, 44100, AudioRecord.READ_BLOCKING);
                    Log.d("AudioMonitor", "read " + read + " floats.");
                    im = zero.clone(); //memset, I hope?
                    for (int i=0; i < 32768; i++)
                        re[i] = recordBuffer[i];
                    fft.fft(re, im);
                    //Log.d("AudioMonitor", "FFT done.");
                } while (enable);
                for (int i=0; i < 32768; i++) {
                    if ((Math.abs(im[i]) > 1000) || (Math.abs(re[i]) > 1000))
                        Log.v("AudioMonitor", "i="+i+"   x="+ im[i]+"   y="+ re[i]);
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
}

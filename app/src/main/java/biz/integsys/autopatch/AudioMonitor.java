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

    public AudioMonitor() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, 44100);
        ///XXX need to check that permissions were okay etc...
        int state = audioRecord.getState();
        Log.d("AudioMonitor", "state: "+ state);
    }

    public void start() {
        audioRecord.startRecording();
        monitorThread = new Thread(new Runnable() {
            public void run() {
                while (!monitorThread.isInterrupted()) {
                    int read = audioRecord.read(recordBuffer, 0, 44100, AudioRecord.READ_BLOCKING);
                    Log.d("AudioMonitor", "read " + read + " floats.");
                }
            }
        });
        monitorThread.start();
    }

    public void stop() {
        monitorThread.interrupt();
        audioRecord.stop();
    }
}

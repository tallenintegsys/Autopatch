package biz.integsys.autopatch;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by tallen on 12/11/15.
 * This monitors the mic and sends callbacks based on what it "hears" e.g. DTMFs
 */
class AudioMonitor {
    private final String TAG = "AudioMonitor";
    private AudioRecord audioRecord;
    public static final int BUFFER_SIZE = 16384;
    private static final int SAMPLE_RATE = 44100;
    public static final int STATE_INITIALIZED = AudioRecord.STATE_INITIALIZED;
    private final float[] recordBuffer= new float[BUFFER_SIZE];
    private final float[] re = new float[BUFFER_SIZE];
    private float[] im = new float[BUFFER_SIZE];
    private final float[] zero = new float[BUFFER_SIZE];
    private final FFT fft = new FFT(BUFFER_SIZE);
    private volatile boolean enable;
    private AudioMonitorListener listener = null;

    /**
     * This class monitors the mic and sends any DTMFs it hears to the listener.
     * @param listener
     * will be called with any DTMFs received
     */
    public AudioMonitor(AudioMonitorListener listener) {
        this.listener = listener;
    }

    /**
     * varify we can get access to the mic
     * @return
     * STATE_INITIALIZED on success
     */
    public int init() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, BUFFER_SIZE);
        return audioRecord.getState();
    }

    /**
     * Begin monitoring the mic for DTMF tones
     */
    public void start() {
        enable = true;
        audioRecord.startRecording();
        Thread monitorThread = new Thread(new Runnable() {
            public void run() {
                char lastDtmf = ' ';
                do {
                    audioRecord.read(recordBuffer, 0, BUFFER_SIZE, AudioRecord.READ_BLOCKING);
                    im = zero.clone(); //memset, I hope?
                    System.arraycopy(recordBuffer, 0, re, 0, BUFFER_SIZE); //memset, I presume
                    fft.fft(re, im);
                    int f1 = 0, f2 = 0;
                    char dtmf = ' ';
                    for (int i = 0; i < BUFFER_SIZE/2; i++) {
                        if ((Math.abs(im[i]) > 10)) {
                            if ((i > 258) && (i < 261))
                                f1 = 697;
                            if ((i > 285) && (i < 289))
                                f1 = 770;
                            if ((i > 315) && (i < 318))
                                f1 = 852;
                            if ((i > 349) && (i < 352))
                                f1 = 941;
                            if ((i > 446) && (i < 451))
                                f2 = 1209;
                            if ((i > 493) && (i < 505))
                                f2 = 1336;
                            if ((i > 544) && (i < 553))
                                f2 = 1477;
                            if ((i > 605) && (i < 608))
                                f2 = 1633;
                        }
                    }
                    if ((f1 == 697) && (f2 == 1209))
                        dtmf = '1';
                    if ((f1 == 697) && (f2 == 1336))
                        dtmf = '2';
                    if ((f1 == 697) && (f2 == 1477))
                        dtmf = '3';
                    if ((f1 == 697) && (f2 == 1633))
                        dtmf = 'A';
                    if ((f1 == 770) && (f2 == 1209))
                        dtmf = '4';
                    if ((f1 == 770) && (f2 == 1336))
                        dtmf = '5';
                    if ((f1 == 770) && (f2 == 1477))
                        dtmf = '6';
                    if ((f1 == 770) && (f2 == 1633))
                        dtmf = 'B';
                    if ((f1 == 852) && (f2 == 1209))
                        dtmf = '7';
                    if ((f1 == 852) && (f2 == 1336))
                        dtmf = '8';
                    if ((f1 == 852) && (f2 == 1477))
                        dtmf = '9';
                    if ((f1 == 852) && (f2 == 1633))
                        dtmf = 'C';
                    if ((f1 == 941) && (f2 == 1209))
                        dtmf = '*';
                    if ((f1 == 941) && (f2 == 1336))
                        dtmf = '0';
                    if ((f1 == 941) && (f2 == 1477))
                        dtmf = '#';
                    if ((f1 == 941) && (f2 == 1633))
                        dtmf = 'D';

                    if (dtmf == ' ') {
                        lastDtmf = ' ';
                    } else {
                        if (listener != null)
                            if (dtmf != lastDtmf) {
                                lastDtmf = dtmf;
                                listener.receivedDTMF(dtmf);
                            }
                    }
                } while (enable);
                audioRecord.stop();
            }
        });
        monitorThread.start();
    }

    /**
     * stops listening to the mic
     */
    public void stop() {
        enable = false;
    }
}

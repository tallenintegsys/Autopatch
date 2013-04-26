/**
 * 
 */
package biz.integsys.autopatch;

import java.util.concurrent.ArrayBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author tallen
 *
 */
public class SampleTask implements Runnable {
    private Thread thread;
    private int frequency = 44100;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int blockSize = 512;
    private ArrayBlockingQueue<short[]> blockingQueue;
    
    public SampleTask(ArrayBlockingQueue<short[]> blockingQueue) {
        this.blockingQueue = blockingQueue;
        thread = new Thread(this);
        thread.setName("SampleTask");
    }

    @Override
    public void run() {
        int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
        try {
            short[] buffer = new short[blockSize];
            audioRecord.startRecording();

            while (true)
            {
                audioRecord.read(buffer, 0, blockSize);
                //for (int t=0; t<blockSize; t++) buffer[t] = (short) (Math.cos(697*4*3.14159* t/blockSize)*32760/2 + Math.cos(1209*4*3.14159* t/blockSize)*32760/2);
                blockingQueue.put(buffer);
                
                if (thread.isInterrupted())
                    break;
            }
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
        audioRecord.stop();
    }

    public void start() {
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();
    }
}



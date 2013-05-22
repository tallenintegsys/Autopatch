/**
 * 
 */
package biz.integsys.autopatch;

import java.util.concurrent.ArrayBlockingQueue;

import biz.integsys.autopatch.util.FFT;

/**
 * @author tallen
 *
 */
public class FFTTask implements Runnable {
    private Thread thread;
    private int blockSize = 512; //this should match the oscilloscope width
    private ArrayBlockingQueue<short[]> spectrum;
    private ArrayBlockingQueue<double[]> magnitudes;
    
    public FFTTask(ArrayBlockingQueue<short[]> spectrum, ArrayBlockingQueue<double[]> magnitudes) {
        this.spectrum = spectrum;
        this.magnitudes = magnitudes;
        thread = new Thread(this);
        thread.setName("SampleTask");
    }

    @Override
    public void run() {
        FFT fft = new FFT(blockSize);
        double re[] = new double[blockSize];
        double im[] = new double[blockSize];

        try {
            while (true) {
                short[] sample = spectrum.take(); 
                for (int i=0; i<blockSize; i++) {
                    re[i] = sample[i]/256;
                    im[i] = 0;
                }
                fft.fft(re,im);
                magnitudes.put(im);

                if (thread.isInterrupted())
                    break;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void start() {
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();
    }
}



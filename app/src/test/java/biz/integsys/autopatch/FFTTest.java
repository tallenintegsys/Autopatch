package biz.integsys.autopatch;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jshoop on 1/9/16.
 */
public class FFTTest {

    final int SAMPLESIZE = 32768;
    final int SAMPLERATE = 44100;
    private final float[] re = new float[SAMPLESIZE];
    private float[] im = new float[SAMPLESIZE];
    private final FFT Fft = new FFT(SAMPLESIZE);

    @Before
    public void setUp() throws Exception {
        for (int i=0; i < SAMPLESIZE; i++) {
            re[i] = (float)Math.sin(440 * i / SAMPLERATE);
            im[i] = 0;
        }
    }

        @Test
    public void testFft() throws Exception {
        Fft.fft(re, im);
        System.out.print("testFft");
    }

    @After
    public void tearDown() throws Exception {
    for (int i=0; i < SAMPLESIZE; i++)
        System.out.print("i: " + i + "   re: " + re[i] + "   im: " + im[i]+"\n");

    }
}
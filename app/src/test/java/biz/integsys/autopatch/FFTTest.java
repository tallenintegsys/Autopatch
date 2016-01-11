package biz.integsys.autopatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jshoop on 1/9/16.
 */
public class FFTTest {

    private final float[] re = new float[AudioMonitor.SAMPLE_SIZE];
    private final float[] im = new float[AudioMonitor.SAMPLE_SIZE];
    private final FFT Fft = new FFT(AudioMonitor.SAMPLE_SIZE);

    @Before
    public void setUp() throws Exception {
        for (int i=0; i < AudioMonitor.SAMPLE_SIZE; i++) {
            re[i] = (float)Math.sin(440 * i / AudioMonitor.SAMPLE_RATE);
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
    for (int i=0; i < AudioMonitor.SAMPLE_SIZE; i++)
        System.out.print("i: " + i + "   re: " + re[i] + "   im: " + im[i]+"\n");

    }
}
package biz.integsys.autopatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
        File file = new File("fft.csv");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            for (int i = 0; i < re.length; i++) {
                outputStreamWriter.write("" + im[i] + ", " + re[i] + "\n");
            }
            outputStreamWriter.close();
            System.out.print("fftTest: "+file.getCanonicalPath());
        } catch (IOException e) {
            System.out.print("testFft exception: " + e);
        }

    }

    @After
    public void tearDown() throws Exception {
        /*
    for (int i=0; i < AudioMonitor.SAMPLE_SIZE; i++)
        System.out.print("i: " + i + "   re: " + re[i] + "   im: " + im[i]+"\n");
*/
    }
}
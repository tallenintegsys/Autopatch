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

    private final float[] sin = new float[44100];
    private final float[] zero= new float[44100];
    private final float[] re = new float[AudioMonitor.SAMPLE_SIZE];
    private final float[] im = new float[AudioMonitor.SAMPLE_SIZE];
    private final FFT Fft = new FFT(AudioMonitor.SAMPLE_SIZE);

    @Before
    public void setUp() throws Exception {
        for (int i=0; i < 44100; i++) {
            final float f = 5000;
            sin[i] = (float)Math.sin(f * 2*Math.PI * i / 44100);
            zero[i] = 0;
            //System.out.print("re: "+re[i]+"   im:"+im[i]);
        }
    }

    @Test
    public void testFft() throws Exception {

        System.arraycopy(sin,0,re,0,AudioMonitor.SAMPLE_SIZE);
        System.arraycopy(zero,0,im,0,AudioMonitor.SAMPLE_SIZE);
        Fft.fft(re, im);
        System.out.print("testFft");
        File file = new File("fft.csv");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            for (int i = 0; i < re.length; i++) {
                    /*
                    even though we sample at 44100 we only use 32768 samples due
                    to the 2^x limitation of the algorithm so we're down-sampling
                    it's cheaper, computationally, to just shift the graph
                    */
                int j = i * 32768/44100;
                outputStreamWriter.write("" + sin[j] + ", " + im[j] + ", " + re[j] + "\n");
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
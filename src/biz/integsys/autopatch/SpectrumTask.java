package biz.integsys.autopatch;

import java.util.concurrent.ArrayBlockingQueue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author tallen
 *
 */
public class SpectrumTask implements Runnable {
    private Thread thread;
    private ArrayBlockingQueue<double[]> spectrum;
    private ImageView spectrumView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int width = 512;
    private int height = 256;
    

    public SpectrumTask(ArrayBlockingQueue<double[]> spectrum, ImageView spectrumView) {
        this.spectrum = spectrum;
        this.spectrumView = spectrumView;
        
        thread = new Thread(this);
        thread.setName("SpectrumTask");  
    }

    public void start() {
        thread.start();
    }
    
    @Override
    public void run() {
        try {
            Log.d("AutoPatch", "width: "+spectrumView.getWidth()+"  height: "+spectrumView.getHeight());
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            this.spectrumView.setImageBitmap(bitmap);
            
            while(true) {
                double real[] = spectrum.take();
                //String line = "";
                for (int x = 0; x < width-1; ++x) 
                {
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(x,0, x,height, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(x, height - Math.round(real[x]/10),x+1, height - Math.round(real[x+1]/10), paint);
                    //if (real[x] > .01) line += x+"="+Math.round(real[x]*height)+" ";
                }
                //Log.d("AutoPatch", line);
                spectrumView.postInvalidate();
                
                if (thread.isInterrupted())
                    break;
                
                //Thread.sleep(25);
            }
         
        } catch (Throwable t) {
            Log.e("OscilloscopeTask", "Failed");
        }
    }

    public void stop() {
        thread.interrupt();
    }
}

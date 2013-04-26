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
public class OscilloscopeTask implements Runnable {
    private Thread thread;
    private ArrayBlockingQueue<short[]> blockingQueue;
    private ImageView oscilloscopeView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int width = 512;
    private int height = 256;
    

    public OscilloscopeTask(ArrayBlockingQueue<short[]> blockingQueue, ImageView oscilloscopeView) {
        this.blockingQueue = blockingQueue;
        this.oscilloscopeView = oscilloscopeView;
        
        Log.d("AutoPatch", "width: "+oscilloscopeView.getWidth()+"  height: "+oscilloscopeView.getHeight());
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        this.oscilloscopeView.setImageBitmap(bitmap);
        
        thread = new Thread(this);
        thread.setName("OscilloscopeTask");  
    }

    public void start() {
        thread.start();
    }
    
    @Override
    public void run() {
        try {
            while(true) {
                short sweep[] = blockingQueue.take();
                for (int x = 0; x < sweep.length; x++) 
                {
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(x,0, x,height, paint);
                    paint.setColor(Color.RED);
                    canvas.drawPoint(x,height/2, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawPoint(x, height/2-sweep[x]/128, paint);
                }
                
                oscilloscopeView.postInvalidate();
                
                if (thread.isInterrupted())
                    break;
                
                //Thread.sleep(1000);
            }
         
        } catch (Throwable t) {
            Log.e("OscilloscopeTask", ""+t);
        }
    }

    public void stop() {
        thread.interrupt();
    }
}

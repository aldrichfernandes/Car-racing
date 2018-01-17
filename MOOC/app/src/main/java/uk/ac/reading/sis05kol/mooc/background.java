package uk.ac.reading.sis05kol.mooc;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import uk.ac.reading.sis05kol.mooc.GameView;

public class background  {

    private Bitmap image;
    private int x, y, dx;

    public background(Bitmap res)
    {
        image = res;
        dx = GameView.MOVESPEED;
    }
    public void update()
    {
        x+=dx;
        if(x<-GameView.WIDTH){
            x=0;
        }
    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y,null);
        if(x<0)
        {
            canvas.drawBitmap(image, x+GameView.WIDTH, y, null);
        }
    }

}
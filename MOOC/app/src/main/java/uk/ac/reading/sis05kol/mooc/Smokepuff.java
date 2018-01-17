package uk.ac.reading.sis05kol.mooc;

/**
 * Created by ALDRICH FERNANDES on 18/04/2016.
 */
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Smokepuff extends GameObject{ // extends the game object which will leave a trail of smoke behind the car//
    public int r;
    public Smokepuff(int x, int y)
    {
        r = 3; // defining the radius of the smoke puff//

        super.x = x;
        super.y = y;
    }
    public void update()
    {
        x-=8;
    }
    public void dodraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // colour of the smoke puff//
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-r, y-r, r, paint);
        canvas.drawCircle(x-r+3, y-r-2,r,paint);
        canvas.drawCircle(x-r+2, y-r+2, r, paint);
    }

}
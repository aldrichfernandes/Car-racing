package uk.ac.reading.sis05kol.mooc;

/**
 * Created by ALDRICH FERNANDES on 18/04/2016.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.animation.Animation;

import java.util.Random;

public class Obstacle extends GameObject{
    private int score;
    private int speed;
    private Random rand = new Random();
    private Car animation = new Car();
    private Bitmap spritesheet;

    public Obstacle(Bitmap res, int x, int y, int w, int h, int s, int numFrames) // the car will loose points and destory if the car hits this obstacle//
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 12 + (int) (rand.nextDouble()*score/15);

        //will limit the speed of the obstacle
        if(speed>25)speed = 40;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length;i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(50-speed);

    }
    public void update()
    {
        x-=speed;
        animation.update();
    }
    public void dodraw(Canvas canvas)
    {
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch(Exception e){}
    }

    @Override
    public int getWidth()
    {

        return width-15;
    }

}

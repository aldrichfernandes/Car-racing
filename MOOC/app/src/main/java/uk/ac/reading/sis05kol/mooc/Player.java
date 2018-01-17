package uk.ac.reading.sis05kol.mooc;

/**
 * Created by ALDRICH FERNANDES on 15/04/2016.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Player extends GameObject{
    private Bitmap spritesheet;
    private int score;
    private double dya; // would provide acceleration for the car //
    private boolean up;
    private boolean playing;
    private Car animation = new Car();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames) {

        x = 100;
        y = GameView.HEIGHT / 2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(15);
        startTime = System.nanoTime();

    }

    public void setUp(boolean b){up = b;}

    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/100000; // scores goes up by this value if the car does not crash //
        if(elapsed>50)
        {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if(up){
            dy = (int)(dya-=1.5);

        }
        else{
            dy = (int)(dya+=1.5);
        }

        if(dy>14)dy = 12;
        if(dy<-14)dy = -12;

        y += dy*3;
        dy = 0;
    }

    public void dodraw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }
    public int getScore(){return score;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean b){playing = b;}
    public void resetDYA(){dya = 0;}
    public void resetScore(){score = 0;}
}

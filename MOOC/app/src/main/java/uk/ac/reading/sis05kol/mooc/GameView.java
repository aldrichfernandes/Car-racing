package uk.ac.reading.sis05kol.mooc;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
	private volatile GameThread thread;

	//private SensorEventListener sensorAccelerometer;

	//Handle communication from the GameThread to the View/Activity Thread
	public static final int WIDTH = 320;
	public static final int HEIGHT =480;
	public static final int MOVESPEED = -5;
	private long smokeStartTime;
	private long ObstacleStartTime;


	private Handler mHandler;
	private background bg;
	private Player player;
/* stores the smoke trails behind the cars in an array list
 *  */
	private ArrayList<Smokepuff> smoke;
	private ArrayList<Obstacle> obstacles;
	private Random rand = new Random();

	//Pointers to the views
	private TextView mScoreView;
	private TextView mStatusView;

    Sensor accelerometer;
    Sensor magnetometer;


	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		//Set up a handler for messages from GameThread
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message m) {
				if(m.getData().getBoolean("score")) {
					mScoreView.setText(m.getData().getString("text"));
				}
				else {
					//So it is a status
                    int i = m.getData().getInt("viz");
                    switch(i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }

                    mStatusView.setText(m.getData().getString("text"));
				}
 			}
		};
	}
	
	//Used to release any resources.
	public void cleanup() {
		this.thread.setRunning(false);
		this.thread.cleanup();

		this.removeCallbacks(thread);
		thread = null;
		
		this.setOnTouchListener(null);
		
		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}
	
	/*
	 * Setters and Getters
	 */

	public void setThread(GameThread newThread) {

		thread = newThread;

		setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
		});

        setClickable(true);
		setFocusable(true);
	}
	
	public GameThread getThread() {
		return thread;
	}

	public TextView getStatusView() {
		return mStatusView;
	}

	public void setStatusView(TextView mStatusView) {
		this.mStatusView = mStatusView;
	}
	
	public TextView getScoreView() {
		return mScoreView;
	}

	public void setScoreView(TextView mScoreView) {
		this.mScoreView = mScoreView;
	}
	

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	
	/*
	 * Screen functions
	 */
	
	//ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(thread!=null) {
			if (!hasWindowFocus)
				thread.pause();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {

		//bg = new background (BitmapFactory.decodeResource(getResources(), R.drawable.bkg));//
		bg = new background(BitmapFactory.decodeResource(getResources(), R.drawable.bkg));
		player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.car1), 65, 25, 1); // instantiting the player//
		smoke = new ArrayList<Smokepuff>();
		obstacles = new ArrayList<Obstacle>();

		smokeStartTime=  System.nanoTime();
		ObstacleStartTime = System.nanoTime();



		if(thread!=null) {
			thread.setRunning(true);
			
			if(thread.getState() == Thread.State.NEW){
				//Just start the new thread
				thread.start();
			}
			else {
				if(thread.getState() == Thread.State.TERMINATED){
					//Start a new thread
					//Should be this to update screen with old game: new GameThread(this, thread);
					//The method should set all fields in new thread to the value of old thread's fields 
					thread = new TheGame(this); 
					thread.setRunning(true);
					thread.start();
				}
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			if(!player.getPlaying())
			{
				player.setPlaying(true);
			}
			else
			{
				player.setUp(true);
			}
			return true;
		}
		if(event.getAction()==MotionEvent.ACTION_UP)
		{
			player.setUp(false);
			return true;
		}

		return super.onTouchEvent(event);
	}



	public void update() // so the smoke follows the direction of the car and increases in density as the the speed of the car increases//
	{
		if(player.getPlaying()) {

			bg.update();
			player.update();

			long missileElapsed = (System.nanoTime()-ObstacleStartTime)/10000000;
			if(missileElapsed >(2000 - player.getScore()/4)){

				System.out.println("creating obstacles");
				//the obstacles will start form the middle of the road//
				if(obstacles.size()==0)
				{
					obstacles.add(new Obstacle(BitmapFactory.decodeResource(getResources(), R.drawable.
							obstacle), WIDTH + 10, HEIGHT / 2, 45, 15, player.getScore(), 13));
				}
				else
				{

					obstacles.add(new Obstacle(BitmapFactory.decodeResource(getResources(), R.drawable.obstacle),
							WIDTH + 12, (int) (rand.nextDouble() * (HEIGHT)), 30, 12, player.getScore(), 10));
				}

				//reset timer
				ObstacleStartTime = System.nanoTime();
			}
			//loop through every missile and check collision and remove
			for(int i = 0; i<obstacles.size();i++)
			{
				//updates the obstacles every time the car hits the obstacle//
				obstacles.get(i).update();

				if(collision(obstacles.get(i),player))
				{
					obstacles.remove(i);
					player.setPlaying(false);
					break;
				}
				//removes the obstacles at the end of the screen//
				if(obstacles.get(i).getX()<-100)
				{
					obstacles.remove(i);
					break;
				}
			}



			long elapsed = (System.nanoTime() - smokeStartTime)/10000000;
			if(elapsed > 120){
				smoke.add(new Smokepuff(player.getX(), player.getY()+6));
				smokeStartTime = System.nanoTime();
			}

			for(int i = 0; i<smoke.size();i++)// remove the smoke after the car has gone forward//
			{
				smoke.get(i).update();
				if(smoke.get(i).getX()<-25)
				{
					smoke.remove(i);
				}
			}
		}
	}

	public boolean collision(GameObject a, GameObject b)
	{
		if(Rect.intersects(a.getRectangle(), b.getRectangle()))
		{
			return true;
		}
		return false;
	}

	public void dodraw(Canvas canvas)
	{
		final float scaleFactorX = getWidth()/(WIDTH*1.f);
		final float scaleFactorY = getHeight()/(HEIGHT*1.f);
		if(canvas!=null) {
			final int savedState = canvas.save();
			canvas.scale(scaleFactorX, scaleFactorY);
			bg.draw(canvas);
			player.dodraw(canvas);
			for(Smokepuff sp: smoke)
			{
				sp.dodraw(canvas);
			}

			for(Obstacle o: obstacles)
			{
				o.dodraw(canvas);
			}




			canvas.restoreToCount(savedState);
		}
	}




	//This is run whenever the phone is touched by the user


	//Always called once after surfaceCreated. Tell the GameThread the actual size
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(thread!=null) {
			thread.setSurfaceSize(width, height);
		}
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused on even stopped.
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		boolean retry = true;
		if(thread!=null) {
			thread.setRunning(false);
		}
		
		//join the thread with this thread
		while (retry) {
			try {
				if(thread!=null) {
					thread.join();
				}
				retry = false;
			} 
			catch (InterruptedException e) {
				//naugthy, ought to do something...
			}
		}
	}
	
	/*
	 * Accelerometer
	 */

	public void startSensor(SensorManager sm) {

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }
	
	public void removeSensor(SensorManager sm) {
        sm.unregisterListener(this);

        accelerometer = null;
        magnetometer = null;
	}

    //A sensor has changed, let the thread take care of it
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(thread!=null) {
            thread.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.

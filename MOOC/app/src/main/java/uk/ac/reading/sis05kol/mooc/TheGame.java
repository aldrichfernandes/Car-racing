package uk.ac.reading.sis05kol.mooc;

//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TheGame extends GameThread  {

    //Will store the image of a ball
    private Bitmap mBall;
    private Bitmap mBall2;
    //The X and Y position of the ball on the screen (middle of ball)
    private float mBallX = 0;
    private float mBallY = 0;

    private float mBallX2 = 0;
    private float mBallY2 = 0;
    //The speed (pixel/second) of the ball in direction X and Y
    private float mBallSpeedX = 0;
    private float mBallSpeedY = 0;
    private float mBallSpeedX2 = 0;
    private float mBallSpeedY2= 0;

    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView) {
        //House keeping
        super(gameView);

        //Prepare the image so we can draw it on the screen (using a canvas)
        mBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.car1);
        mBall2 = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.car2);
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {
        //Initialise speeds
        mBallSpeedX = 100;
        mBallSpeedY = 100;
        mBallSpeedX2= -90;
        mBallSpeedY2 = -90;
        //Place the ball in the middle of the screen.
        //mBall.Width() and mBall.getHeigh() gives us the height and width of the image of the ball
        mBallX = mCanvasWidth / 2;
        mBallY = mCanvasHeight / 2;
        mBallX2 = mCanvasWidth / 2;
        mBallY2 = mCanvasHeight / 2;
    }


	@Override
	protected void actionOnTouch(float x, float y) {
		//Increase/decrease the speed of the ball making the ball move towards the touch
		mBallSpeedX = x - mBallX;
		mBallSpeedY = y - mBallY;
	}

	

	//This is run whenever the phone moves around its axises 
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {

		/*Increase/decrease the speed of the ball.
		If the ball moves too fast try and decrease 70f
		If the ball moves too slow try and increase 70f */


		mBallSpeedX = mBallSpeedX + 70f * xDirection;
		mBallSpeedY = mBallSpeedY - 70f * yDirection;
        mBallSpeedX2 = mBallSpeedX2 + 70f * xDirection;
        mBallSpeedY2 = mBallSpeedY2 - 70f * yDirection;
	}


    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {
        if (mBallX > mCanvasWidth || mBallX <0)
        {
            mBallSpeedX = -mBallSpeedX;
        }

        if (mBallY >mCanvasHeight || mBallY <0)
        {
            mBallSpeedY = -mBallSpeedY;
        }

        if (mBallX2 > mCanvasWidth || mBallX2 <0)
        {
            mBallSpeedX2 = -mBallSpeedX2;
        }

        if (mBallY2 >mCanvasHeight || mBallY2 <0)
        {
            mBallSpeedY2 = -mBallSpeedY2;
        }
        //Move the ball's X and Y using the speed (pixel/sec)
        mBallX = mBallX + secondsElapsed * mBallSpeedX;
        mBallY = mBallY + secondsElapsed * mBallSpeedY;
        mBallX2 = mBallX2 + secondsElapsed * mBallSpeedX2;
        mBallY2 = mBallY2 + secondsElapsed * mBallSpeedY2;

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

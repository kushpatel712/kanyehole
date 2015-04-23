package com.dropout.kanyehole;

import android.app.Application;
import android.graphics.Canvas;
import android.test.ApplicationTestCase;
import android.view.View;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    Application mApplication;
    public ApplicationTest() {
        super(Application.class);
    }
    @Override
    protected void setUp() throws Exception {
        createApplication();
        mApplication = getApplication();

    }
    public void testSelfTest() throws Exception {
        //mApplication = null;
        assertNotNull(mApplication);
    }


    /**
     * ARC TESTS START HERE
     *
     *
     */
    public void testArcNotNull(){
        Arc arc = Arc.getInstance();
        assertNotNull(arc);
    }
    public void testArcSetSpeed(){
        Arc arc=Arc.getInstance();
        arc.setSpeed(1,2);
        assertEquals(1.0,arc.getXSpeed(),.005);
        assertEquals(2.0,arc.getYSpeed(),.005);
    }
    public void testArcSetPosition(){
        Arc arc=Arc.getInstance();
        arc.setPosition(120, 245);
        assertEquals(120,arc.getXPosition(),.005);
        assertEquals(245.0,arc.getYPosition(),.005);
    }

    public void testArcView(){
        ObjectView aView= new ObjectView(this.getContext(),2);
        assertNotNull(aView);
    }
    public void testArcUpdatePosition(){
        Arc arc=Arc.getInstance();
        ObjectView aView= new ObjectView(this.getContext(),2);
        float xpos = arc.getXPosition();
        float ypos = arc.getYPosition();
        float xspd = arc.getXSpeed();
        float yspd = arc.getYSpeed();
        double angle = arc.getAngle();
        arc.updatePosition(aView);
        assertEquals(xpos+xspd,arc.getXPosition(),.005);
        assertEquals(ypos+yspd,arc.getYPosition(),.005);
        assertEquals(angle+xspd,arc.getAngle(),.005);
    }

    public void testArcDraw(){
        Arc arc=Arc.getInstance();
        Canvas canvas = new Canvas();
        arc.draw(canvas, 100, 100); //calls onDraw
        assertEquals(arc.drawn, true);
    }
    /**
     * ARC TESTS END HERE
     *
     *
     */

    /**
     * OBSTACLE TESTS START HERE
     *
     *
     */
    public void testObstacleNotNull(){
        Obstacle obs = new Obstacle(0,100,100);
        assertNotNull(obs);
    }
    public void testObstacleSetSpeed(){
        Obstacle obs = new Obstacle(0,100,100);
        obs.setSpeed(1,2);
        assertEquals(1 + (float) (50 * Math.sin(obs.getAngle())),obs.getXSpeed(),.005);
        assertEquals(2 + (float)(50 * Math.cos(obs.getAngle())),obs.getYSpeed(),.005);
    }
    public void testObstacleSetPosition(){
        Obstacle obs = new Obstacle(0,100,100);
        obs.setPosition(120, 245);

        assertEquals(120,obs.getXPosition(),.005);
        assertEquals(245.0,obs.getYPosition(),.005);
    }

    public void testObstacleView(){
        ObjectView aView= new ObjectView(this.getContext(),2);
        assertNotNull(aView);
    }

    public void testObstacleUpdatePosition(){
        Obstacle obs = new Obstacle(0,100,100);
        ObjectView aView= new ObjectView(this.getContext(),2);
        float xpos = obs.getXPosition();
        float ypos = obs.getYPosition();
        float xspd = obs.getXSpeed();
        float yspd = obs.getYSpeed();
        double angle = obs.getAngle();
        obs.updatePosition(aView);
        assertEquals(Math.abs((xpos +xspd)%100),obs.getXPosition(),.005);
        assertEquals(Math.abs((ypos +yspd)%100),obs.getYPosition(),.005);
        assertEquals(angle,obs.getAngle(),.005);
    }

    public void testObstacleDraw(){
        Obstacle obs = new Obstacle(0,100,100);
        Canvas canvas = new Canvas();
        obs.draw(canvas, 100, 100); //calls onDraw
        assertEquals(obs.drawn, true);
    }
    /**
     * OBSTACLE TESTS END HERE
     *
     *
     */

    public void testObstacleFactory(){
        ObjectView aView= new ObjectView(this.getContext(),2);
        Obstacle obs = (Obstacle)ObstacleFactory.getObstacle("Red",100,100,90);
        assertNotNull(obs);
        assertEquals(90.0,obs.getAngle(),.005);
    }
    public void testObstacleGeneration(){
        ObjectView aView= new ObjectView(this.getContext(),2);
        ObstacleGenerator oGen= new ObstacleGenerator();
        oGen.generate(aView,100,200,5);
        assertEquals(5, aView.getListLength());
    }
    public void testObjectViewOnDraw(){
        ObjectView aView= new ObjectView(this.getContext(),2);
        ObstacleGenerator oGen= new ObstacleGenerator();
        Obstacle obs = new Obstacle(0,100,100);
        Obstacle obs2 = new Obstacle(0,100,100);
        Arc arc=Arc.getInstance();
        aView.registerObstacle(obs);
        aView.registerObstacle(arc);
        aView.onDraw(new Canvas());
        assertEquals(true,arc.drawn);
        assertEquals(true,obs.drawn);
        assertEquals(false,obs2.drawn);
    }


    public void testUnPause() throws InterruptedException{
        GameActivity ga = new GameActivity();
        ga.notPaused=false;
        ga.hits = 1;
        ga.pause(new View(this.getContext()));
        assertTrue(ga.notPaused);
        assertEquals(ga.hits, 2);
        // System.out.println("passed");
    }
    public void testPause() throws InterruptedException{
        GameActivity ga = new GameActivity();
        ga.notPaused=true;
        ga.hits = 0;
        ga.pause(new View(this.getContext()));
        assertFalse(ga.notPaused);
        assertEquals(ga.hits, 1);
//        GameActivity ga = new GameActivity();
//        ga.notPaused=true;
//        Thread.sleep(3000);
//        assertTrue(ga.notPaused);
//        ga.notPaused=false;
//        Thread.sleep(3000);
//        assertFalse(ga.notPaused);
       // System.out.println("passed");
    }

}
package com.dropout.kanyehole;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

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
    public void testArc(){
        Arc arc = Arc.getInstance();
        assertNotNull(arc);
    }
    public void testSetSpeed(){
        Arc arc=Arc.getInstance();
        arc.setSpeed(1,2);
        assertEquals(1.0,arc.getXSpeed(),.005);
        assertEquals(2.0,arc.getYSpeed(),.005);
    }
    public void testSetPosition(){
        Arc arc=Arc.getInstance();
        arc.setPosition(120, 245);
        assertEquals(120,arc.getXPosition(),.005);
        assertEquals(245.0,arc.getYPosition(),.005);
    }

    public void testArcView(){
        ArcView aView= new ArcView(this.getContext(), 1, 2, 3,2, 10.0);
        assertNotNull(aView);
    }

    public void testObstacleGeneration(){
        ArcView aView= new ArcView(this.getContext(), 1, 2, 3,2, 10.0);
        ObstacleGenerator oGen= new ObstacleGenerator();
        oGen.generate(aView,100,200);
        assertNotNull(oGen);
    }

    public void testObstacleFactory(){
        ArcView aView= new ArcView(this.getContext(), 1, 2, 3,2, 10.0);
        ObstacleGenerator oGen= new ObstacleGenerator();
        oGen.generate(aView,100,200);

    }

    public void testPause() throws InterruptedException{
        GameActivity ga = new GameActivity();
        ga.notPaused=true;
        Thread.sleep(3000);
        assertTrue(ga.notPaused);
        ga.notPaused=false;
        Thread.sleep(3000);
        assertFalse(ga.notPaused);
       // System.out.println("passed");
    }

}
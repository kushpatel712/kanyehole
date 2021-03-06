/* Much of the initialization for OPENGL renderer and paramters for drawing, as well as the
    way for drawing using vertices, indexes and uv matrixes taken/modified from a set of GLES2.0
    Tutorials that can be found (part 1-6) at:
    
    http://androidblog.reindustries.com/a-real-open-gl-es-2-0-2d-tutorial-part-1/

 */
package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This Class is for the opengl renderer
 * It basically draws/renders all the images for the game activity and taylor mode
 * Todo: make class less static based for objects to draw
 */
public class SurfaceRenderer implements Renderer {
    // arrays and buffers for drawing
    public static float vertices[]; //vertices of image
    public static short indices[]; //match up with vertices
    public static float uvs[]; //coords of section of texture map
    // Matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;
    GameActivity ga;
    TayActivity ta;
    // screenresolution
    float mScreenWidth;
    float mScreenHeight;
    float ssu = 1.0f;
    float ssx = 1.0f;
    float ssy = 1.0f;
    float swp = 320.0f;
    float shp = 480.0f;
    Context mContext;
    long mLastTime;
    //Texture array for all the textures saved
    private int[] texturenames = new int[10];
    private boolean taylormode = false;

    //Constructor, taymode makes it render for taylor mode
    public SurfaceRenderer(Context c, int height, int width, boolean taymode, GameActivity g, TayActivity t) {
        ga = g;
        ta = t;
        taylormode = taymode;
        mScreenHeight = height;
        mScreenWidth = width;
        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;
    }


    public void onResume() {
        mLastTime = System.currentTimeMillis();
    }

    //Draw current frame, a lot of this is from the tutorial
    @Override
    public void onDrawFrame(GL10 unused) {

        //////Initialization timing

        // Get the current time
        long now = System.currentTimeMillis();
        // We should make sure we are valid and sane
        if (mLastTime > now) return;
        // Get the amount of time the last frame took.
        long elapsed = now - mLastTime;
        // clear Screen and Depth Buffer,
        // we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //////Setting up and actually drawing

        //Draw background
        SetupBGTriangle();
        SetupBGImage();
        Render(mtrxProjectionAndView, 3);

        //draw tunnel
        SetupCircImage();
        SetupCircTriangle();
        Render(mtrxProjectionAndView, 9);

        //draw the rhythm text : (miss,great,perfect)
        int hittype;
        if (taylormode) {
            hittype = ta.hittype.get();
        } else {
            hittype = ga.hittype.get();
        }
        if (hittype != -1) {
            UpdateHitTriangle();
            UpdateHitImage();
            if (hittype == 0)
                Render(mtrxProjectionAndView, 6);
            else if (hittype == 1)
                Render(mtrxProjectionAndView, 7);
            else if (hittype == 2)
                Render(mtrxProjectionAndView, 8);
        }

        //Draw the arrows
        UpdateArrowTriangle();
        UpdateArrowImage();
        Render(mtrxProjectionAndView, 0);

        //Draw the obstacles
        UpdateObstacleTriangle();
        UpdateObstacleImage();
        Render(mtrxProjectionAndView, 2);

        //Draw the arc
        UpdateArcTriangle();
        UpdateArcImage();
        Render(mtrxProjectionAndView, 1);

        // Save the current time to see how long it took <img src="http://androidblog.reindustries.com/wp-includes/images/smilies/icon_smile.gif" alt=":)" class="wp-smiley"> .
        mLastTime = now;

    }

    //The actual rendering using arrays/buffers, most of this taken from tutorial,
    //but adjusted for multitexturing with the index
    private void Render(float[] m, int index) {

        //Bind the indexed texture to draw with
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[index]);
        // get handle to vertex shader's vPosition member
        int mPositionHandle =
                GLES20.glGetAttribLocation(GraphicTools.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(GraphicTools.sp_Image,
                "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(GraphicTools.sp_Image,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(GraphicTools.sp_Image,
                "s_texture");

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, index);

        // Draw the triangles
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);

    }

    //Initialize the matrices and scaling
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // We need to know the current width and height.
        mScreenWidth = width;
        mScreenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
        // Setup our scaling system
        SetupScaling();
    }

    //initialize scaling and textures
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Setup our scaling system
        SetupScaling();

        SetupImages();
        drawSettings(gl, config);
    }

    //Initialize the shaders and colors
    public void drawSettings(GL10 gl, EGLConfig config) {
        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        gl.glClearColor(0, 0, 0, 0);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        // Create the shaders, solid color
        int vertexShader = GraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                GraphicTools.vs_SolidColor);
        int fragmentShader = GraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                GraphicTools.fs_SolidColor);

        GraphicTools.sp_SolidColor = GLES20.glCreateProgram();
        GLES20.glAttachShader(GraphicTools.sp_SolidColor, vertexShader);
        GLES20.glAttachShader(GraphicTools.sp_SolidColor, fragmentShader);
        GLES20.glLinkProgram(GraphicTools.sp_SolidColor);

        // Create the shaders, images
        vertexShader = GraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                GraphicTools.vs_Image);
        fragmentShader = GraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                GraphicTools.fs_Image);

        GraphicTools.sp_Image = GLES20.glCreateProgram();
        GLES20.glAttachShader(GraphicTools.sp_Image, vertexShader);
        GLES20.glAttachShader(GraphicTools.sp_Image, fragmentShader);
        GLES20.glLinkProgram(GraphicTools.sp_Image);

        // Set our shader programm
        GLES20.glUseProgram(GraphicTools.sp_Image);
    }


    //Initialize scaling
    public void SetupScaling() {
        // The screen resolutions
        swp = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        shp = (int) (mContext.getResources().getDisplayMetrics().heightPixels);

        //scale based on the screen size
        ssx = swp / 320.0f;
        ssy = shp / 480.0f;

        // Get uniform scaler
        if (ssx > ssy)
            ssu = ssy;
        else
            ssu = ssx;
        Buttons.obsSize = 30.0f * ssu;
    }

    //Sets the triangles for the rhythm message
    public void UpdateHitTriangle() {
        vertices = new float[(1) * 4 * 3];
        float xoff = 200.0f * ssu;
        float yoff = 60.0f * ssu;
        float x = swp * (1.0f / 2.0f) - xoff / 2;
        float y = shp - shp * (2.0f / 3.0f) - yoff / 2;
        vertices[0] = x;
        vertices[1] = y + yoff;
        vertices[2] = 0f;
        vertices[3] = x;
        vertices[4] = y;
        vertices[5] = 0f;
        vertices[6] = x + xoff;
        vertices[7] = y;
        vertices[8] = 0f;
        vertices[9] = x + xoff;
        vertices[10] = y + yoff;
        vertices[11] = 0f;

        // The indices for all textured quads
        indices = new short[(1) * 6];
        int last = 0;
        indices[0] = (short) (last + 0);
        indices[1] = (short) (last + 1);
        indices[2] = (short) (last + 2);
        indices[3] = (short) (last + 0);
        indices[4] = (short) (last + 2);
        indices[5] = (short) (last + 3);
        last = last + 4;

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texturemap coords for the rhythm message
    public void UpdateHitImage() {
        uvs = new float[]{
                0.0f, 0.0f,
                0.0f, 1f,
                1f, 1f,
                1f, 0.0f
        };
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);
    }

    //Sets the triangles for the arc
    public void UpdateArcTriangle() {
        Drawable arc;
        if (taylormode) {
            arc = ta.tayArc;
        } else {
            arc = ga.kanyeArc;
        }
        vertices = new float[(1) * 4 * 3];
        if (arc == null)
            return;
        float x = arc.getXPosition();
        float y = shp - arc.getYPosition();
        vertices[0] = x - 15.0f * ssu;
        vertices[1] = y + 15.0f * ssu;
        vertices[2] = 0f;
        vertices[3] = x - 15.0f * ssu;
        vertices[4] = y - 15.0f * ssu;
        vertices[5] = 0f;
        vertices[6] = x + 15.0f * ssu;
        vertices[7] = y - 15.0f * ssu;
        vertices[8] = 0f;
        vertices[9] = x + 15.0f * ssu;
        vertices[10] = y + 15.0f * ssu;
        vertices[11] = 0f;


        // The indices for all textured quads
        indices = new short[(1) * 6];
        int last = 0;
        // We need to set the new indices for the new quad
        indices[0] = (short) (last + 0);
        indices[1] = (short) (last + 1);
        indices[2] = (short) (last + 2);
        indices[3] = (short) (last + 0);
        indices[4] = (short) (last + 2);
        indices[5] = (short) (last + 3);

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texturemap coords for the arc
    public void UpdateArcImage() {
        Drawable arc;
        if (taylormode) {
            arc = ta.tayArc;
        } else {
            arc = ga.kanyeArc;
        }
        // Our collection of vertices
        uvs = new float[(1) * 4 * 2];
        if (arc == null)
            return;
        int type = 0;
        // Adding the UV's using the offsets
        uvs[0] = type * (float) 1.0 / 3;
        uvs[1] = type * 1f;
        uvs[2] = type * (float) 1.0 / 3;
        uvs[3] = (type + 1) * 1f;
        uvs[4] = (type + 1) * (float) 1.0 / 3;
        uvs[5] = (type + 1) * 1f;
        uvs[6] = (type + 1) * (float) 1.0 / 3;
        uvs[7] = type * 1f;


        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

    }

    //Sets the triangles for the arrows
    public void UpdateArrowTriangle() {
        ArrayList<Drawable> drawList;
        if (taylormode) {
            drawList = new ArrayList<Drawable>(ta.drawList);
        } else {
            drawList = new ArrayList<Drawable>(ga.drawList);
        }
        // Our collection of vertices
        int num = 0;
        vertices = new float[(drawList.size()) * 4 * 3];
        for (int i = 0; i < drawList.size(); i++) {
            int index = i;
            Drawable draw = drawList.get(i);
            if (draw == null || !draw.getClass().equals(Arrow.class))
                continue;
            num++;
            float x = draw.getXPosition();
            float y = shp - draw.getYPosition();
            vertices[(index * 12) + 0] = x;
            vertices[(index * 12) + 1] = y + Buttons.arrow_height;//(60.0f*ssu);
            vertices[(index * 12) + 2] = 0f;
            vertices[(index * 12) + 3] = x;
            vertices[(index * 12) + 4] = y;
            vertices[(index * 12) + 5] = 0f;
            vertices[(index * 12) + 6] = x + Buttons.arrow_width;//(60.0f*ssu);
            vertices[(index * 12) + 7] = y;
            vertices[(index * 12) + 8] = 0f;
            vertices[(index * 12) + 9] = x + Buttons.arrow_width;//(60.0f*ssu);
            vertices[(index * 12) + 10] = y + Buttons.arrow_height;//(60.0f*ssu);
            vertices[(index * 12) + 11] = 0f;
        }

        // The indices for all textured quads
        indices = new short[(drawList.size()) * 6];
        int last = 0;
        for (int i = 0; i < drawList.size(); i++) {
            Drawable draw = drawList.get(i);
            if (draw == null || !draw.getClass().equals(Arrow.class))
                continue;
            int index = i;
            // We need to set the new indices for the new quad
            indices[(index * 6) + 0] = (short) (last + 0);
            indices[(index * 6) + 1] = (short) (last + 1);
            indices[(index * 6) + 2] = (short) (last + 2);
            indices[(index * 6) + 3] = (short) (last + 0);
            indices[(index * 6) + 4] = (short) (last + 2);
            indices[(index * 6) + 5] = (short) (last + 3);

            // Our indices are connected to the vertices so we need to keep them
            // in the correct order.
            // normal quad = 0,1,2,0,2,3 so the next one will be 4,5,6,4,6,7
            last = last + 4;
        }
        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texture map slices for the arrows
    public void UpdateArrowImage() {
        ArrayList<Drawable> drawList;
        if (taylormode) {
            drawList = new ArrayList<Drawable>(ta.drawList);
        } else {
            drawList = new ArrayList<Drawable>(ga.drawList);
        }
        // Our collection of vertices
        uvs = new float[(drawList.size()) * 4 * 2];
        for (int i = 0; i < drawList.size(); i++) {
            int index = i;
            Drawable draw = drawList.get(i);
            if (draw == null)
                continue;
            int type = 0;
            if (draw.getClass().equals(Arc.class)) {
                continue;
            } else if (draw.getClass().equals(Arrow.class)) {
                Arrow a = (Arrow) draw;
                if (a.getAngle() == 0) {
                    type = 2;
                } else if (a.getAngle() == 90) {
                    type = 3;
                } else if (a.getAngle() == 180) {
                    type = 1;
                } else if (a.getAngle() == 270) {
                    type = 0;
                }
            }
            // Adding the UV's using the offsets
            uvs[(index * 8) + 0] = type * (float) 1.0 / 4;
            uvs[(index * 8) + 1] = type * 1f;
            uvs[(index * 8) + 2] = type * (float) 1.0 / 4;
            uvs[(index * 8) + 3] = (type + 1) * 1f;
            uvs[(index * 8) + 4] = (type + 1) * (float) 1.0 / 4;
            uvs[(index * 8) + 5] = (type + 1) * 1f;
            uvs[(index * 8) + 6] = (type + 1) * (float) 1.0 / 4;
            uvs[(index * 8) + 7] = type * 1f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

    }

    //Sets the triangles for the obstacles
    public void UpdateObstacleTriangle() {
        ArrayList<Drawable> obsList;
        if (taylormode) {
            obsList = new ArrayList<Drawable>(ta.obsList);
        } else {
            obsList = new ArrayList<Drawable>(ga.obsList);
        }
        // Our collection of vertices
        vertices = new float[(obsList.size()) * 4 * 3];
        for (int i = 0; i < obsList.size(); i++) {
            Drawable draw = obsList.get(i);
            if (draw == null)
                continue;
            Obstacle obs = (Obstacle) draw;
            float distance = (float) obs.getDist();
            float scaler = distance / (mScreenWidth * .5f * .6f);
            if (scaler > 1)
                scaler = 1;
            float x = draw.getXPosition();
            float y = shp - draw.getYPosition();
            vertices[(i * 12) + 0] = x - (scaler * 15.0f * ssu);
            vertices[(i * 12) + 1] = y + (scaler * 15.0f * ssu);
            vertices[(i * 12) + 2] = 0f;
            vertices[(i * 12) + 3] = x - (scaler * 15.0f * ssu);
            vertices[(i * 12) + 4] = y - (scaler * 15.0f * ssu);
            vertices[(i * 12) + 5] = 0f;
            vertices[(i * 12) + 6] = x + (scaler * 15.0f * ssu);
            vertices[(i * 12) + 7] = y - (scaler * 15.0f * ssu);
            vertices[(i * 12) + 8] = 0f;
            vertices[(i * 12) + 9] = x + (scaler * 15.0f * ssu);
            vertices[(i * 12) + 10] = y + (scaler * 15.0f * ssu);
            vertices[(i * 12) + 11] = 0f;
        }
        // The indices for all textured quads
        indices = new short[(obsList.size()) * 6];
        int last = 0;
        for (int i = 0; i < obsList.size(); i++) {
            // We need to set the new indices for the new quad
            indices[(i * 6) + 0] = (short) (last + 0);
            indices[(i * 6) + 1] = (short) (last + 1);
            indices[(i * 6) + 2] = (short) (last + 2);
            indices[(i * 6) + 3] = (short) (last + 0);
            indices[(i * 6) + 4] = (short) (last + 2);
            indices[(i * 6) + 5] = (short) (last + 3);

            // Our indices are connected to the vertices so we need to keep them
            // in the correct order.
            // normal quad = 0,1,2,0,2,3 so the next one will be 4,5,6,4,6,7
            last = last + 4;
        }

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texturemap slices for the obstacles
    public void UpdateObstacleImage() {
        ArrayList<Drawable> obsList;
        if (taylormode) {
            obsList = new ArrayList<Drawable>(ta.obsList);
        } else {
            obsList = new ArrayList<Drawable>(ga.obsList);
        }
        // Our collection of vertices
        uvs = new float[(obsList.size()) * 4 * 2];
        for (int i = 0; i < obsList.size(); i++) {
            Drawable draw = obsList.get(i);
            if (draw == null)
                continue;
            Obstacle o = (Obstacle) draw;
            int type = 0;
            if (o.touch) {
                if (o.flash)
                    type = 1;
                else
                    type = 2;
                o.flash = !o.flash;
            }
            uvs[(i * 8) + 0] = type * (float) 1.0 / 3;
            uvs[(i * 8) + 1] = type * 1f;
            uvs[(i * 8) + 2] = type * (float) 1.0 / 3;
            uvs[(i * 8) + 3] = (type + 1) * 1f;
            uvs[(i * 8) + 4] = (type + 1) * (float) 1.0 / 3;
            uvs[(i * 8) + 5] = (type + 1) * 1f;
            uvs[(i * 8) + 6] = (type + 1) * (float) 1.0 / 3;
            uvs[(i * 8) + 7] = type * 1f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

    }

    //Sets the triangles background
    public void SetupBGTriangle() {
        vertices = new float[(1) * 4 * 3];
        vertices[0] = 0;
        vertices[1] = 0 + shp;//shp
        vertices[2] = 0f;
        vertices[3] = 0;
        vertices[4] = 0;
        vertices[5] = 0f;
        vertices[6] = 0 + swp; // swp
        vertices[7] = 0;
        vertices[8] = 0f;
        vertices[9] = 0 + swp; //swp
        vertices[10] = 0 + shp; //shp
        vertices[11] = 0f;

        // The indices for all textured quads
        indices = new short[(1) * 6];
        int last = 0;
        // We need to set the new indices for the new quad
        indices[0] = (short) (last + 0);
        indices[1] = (short) (last + 1);
        indices[2] = (short) (last + 2);
        indices[3] = (short) (last + 0);
        indices[4] = (short) (last + 2);
        indices[5] = (short) (last + 3);

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texturemap coords for the background
    public void SetupBGImage() {

        uvs = new float[]{
                0.0f, 0.0f,
                0.0f, 1f,
                1f, 1f,
                1f, 0.0f
        };
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

    }

    //Sets the triangles for the tunnel
    public void SetupCircTriangle() {
        float centerx = swp / 2;
        float centery = shp / 4 + shp / 12;
        float radius = centerx * .7f;
        float topx = centerx - radius;
        float topy = shp - centery + radius;
        vertices = new float[(1) * 4 * 3];
        vertices[0] = topx;
        vertices[1] = topy;//shp
        vertices[2] = 0f;
        vertices[3] = topx;
        vertices[4] = topy - radius * 2;
        vertices[5] = 0f;
        vertices[6] = topx + radius * 2; // swp
        vertices[7] = topy - radius * 2;
        vertices[8] = 0f;
        vertices[9] = topx + radius * 2; //swp
        vertices[10] = topy; //shp
        vertices[11] = 0f;

        // The indices for all textured quads
        indices = new short[(1) * 6];
        int last = 0;
        // We need to set the new indices for the new quad
        indices[0] = (short) (last + 0);
        indices[1] = (short) (last + 1);
        indices[2] = (short) (last + 2);
        indices[3] = (short) (last + 0);
        indices[4] = (short) (last + 2);
        indices[5] = (short) (last + 3);

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    //Sets the texturemap coords for the tunnel
    public void SetupCircImage() {

        uvs = new float[]{
                0.0f, 0.0f,
                0.0f, 1f,
                1f, 1f,
                1f, 0.0f
        };
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

    }

    //Initializes the Textures
    public void SetupImages() {
        System.out.println("index:" + Customize.index);
        System.out.println("Color:" + Customize.kanyeNames.get(Customize.index));
        if (taylormode) {
            GLES20.glGenTextures(10, texturenames, 0);
            bitmapToTexture("drawable/arrowmap", texturenames, 0);
            bitmapToTexture("drawable/taylorheadmap", texturenames, 1);
            bitmapToTexture("drawable/" + Customize.kanyeNames.get(Customize.index), texturenames, 2);
            bitmapToTexture("drawable/bgsmalltaylor", texturenames, 3);
            bitmapToTexture("drawable/bgsmall", texturenames, 4);
            bitmapToTexture("drawable/imagemap", texturenames, 5);
            bitmapToTexture("drawable/miss", texturenames, 6);
            bitmapToTexture("drawable/great", texturenames, 7);
            bitmapToTexture("drawable/perfect", texturenames, 8);
            bitmapToTexture("drawable/tunnel", texturenames, 9);
        } else {
            GLES20.glGenTextures(10, texturenames, 0);
            bitmapToTexture("drawable/arrowmap", texturenames, 0);
            bitmapToTexture("drawable/" + Customize.kanyeNames.get(Customize.index), texturenames, 1);
            bitmapToTexture("drawable/taylorheadmap", texturenames, 2);
            bitmapToTexture("drawable/bgsmall", texturenames, 3);
            bitmapToTexture("drawable/bgsmalltaylor", texturenames, 4);
            bitmapToTexture("drawable/imagemap", texturenames, 5);
            bitmapToTexture("drawable/miss", texturenames, 6);
            bitmapToTexture("drawable/great", texturenames, 7);
            bitmapToTexture("drawable/perfect", texturenames, 8);
            bitmapToTexture("drawable/tunnel", texturenames, 9);
        }


    }

    //Binds the bitmap to the texturename index
    public void bitmapToTexture(String idstr, int[] textures, int index) {
        // Generate Textures, if more needed, alter these numbers.

        // Retrieve our image from resources.
        int id = mContext.getResources().getIdentifier(idstr, null, mContext.getPackageName());
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[index]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();
    }

}
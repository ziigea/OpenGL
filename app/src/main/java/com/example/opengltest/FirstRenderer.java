package com.example.opengltest;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class FirstRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "FirstRenderer";

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

//系统调用这个方法一次创建时GLSurfaceView。使用此方法来执行只需要发生一次的操作，比如设置OpenGL的环境参数或初始化的OpenGL图形对象。

        glClearColor(184.0f / 255.0f, 213.0f / 255.0f, 238.0f / 255.0f, 1.0f);//设置清除颜色，颜色范围0-1

        //glClearDepth(1.0);//指定深度缓冲区中每个像素需要的值
        //glClear(GL_DEPTH_BUFFER_BIT);//清除深度缓冲区

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

//系统调用上的每个重绘此方法GLSurfaceView。使用此方法作为主要执行点用于绘制（和重新绘制）的图形对象。
        glClear(GL_COLOR_BUFFER_BIT);//把窗口清除为当前颜色
    }

}
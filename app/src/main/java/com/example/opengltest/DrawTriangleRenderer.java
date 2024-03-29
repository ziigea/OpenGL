package com.example.opengltest;

import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;


public class DrawTriangleRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "FirstRenderer";
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer mVertexData;
    private int mShaderProgram;

    private int uColorLocation;
    private int aPositionLocation;

    private float[] mTrianglePoints = {-0.5f, -0.5f, 0.5f, -0.5f, 0f, 0.5f};

    //顶点着色器
    private String mVertexShaderCode =
            "attribute vec4 a_Position;     \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "    gl_Position = a_Position;  \n" +
                    "}   \n";
    //片段着色器
    private String mFragmentShaderCode =
            "precision mediump float; \n" +
                    "uniform vec4 u_Color;          \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "    gl_FragColor = u_Color;    \n" +
                    "}";



    DrawTriangleRenderer() {
        mVertexData = ByteBuffer
                //分配本地内存，不会被垃圾回收器处理
                .allocateDirect(mTrianglePoints.length * BYTES_PER_FLOAT)
                //告诉字节缓冲区，按本地字节序组织内容
                .order(ByteOrder.nativeOrder())
                //得到反应底层字节实例
                .asFloatBuffer();

        mVertexData.put(mTrianglePoints);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(184.0f/255.0f, 213.0f/255.0f, 238.0f/255.0f, 1.0f);

        int vertexShader = compileShader(GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, mFragmentShaderCode);
        //连接顶点着色器和片段着色器
        mShaderProgram = linkProgram(vertexShader,fragmentShader);
        //告诉OpenGL绘制到屏幕上要 使用这里的程序
        glUseProgram(mShaderProgram);


        uColorLocation = glGetUniformLocation(mShaderProgram, "u_Color");
        //获取u_Color这个uniform的位置，存入uColorLocation
        uColorLocation = glGetUniformLocation(mShaderProgram, "u_Color");
        //获取属性的位置
        aPositionLocation = glGetAttribLocation(mShaderProgram, "a_Position");

        // 关联属性与顶点数组
        mVertexData.position(0);//内部指针从开头读取
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT,
                false, 0,mVertexData);
        glEnableVertexAttribArray(aPositionLocation);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);//白色


    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 3);//从顶点数组下标0开始，绘制3个顶点

    }


    /**
     * Compiles a shader, returning the OpenGL object ID.
     */
    private static int compileShader(int type, String shaderCode) {

        // Create a new shader object.
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            Log.w(TAG, "Could not create new shader.");
            return 0;
        }

        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode);

        // Compile the shader.
        glCompileShader(shaderObjectId);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        // Print the shader info log to the Android log output.
        Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                + glGetShaderInfoLog(shaderObjectId));

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);
            Log.w(TAG, "Compilation of shader failed.");
            return 0;
        }

        // Return the shader object ID.
        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {

        // Create a new program object.
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program");
            return 0;
        }

        // Attach the vertex shader to the program.
        glAttachShader(programObjectId, vertexShaderId);
        // Attach the fragment shader to the program.
        glAttachShader(programObjectId, fragmentShaderId);

        // Link the two shaders together into a program.
        glLinkProgram(programObjectId);

        // Get the link status.
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        // Print the program info log to the Android log output.
        Log.v(TAG, "Results of linking program:\n"
                + glGetProgramInfoLog(programObjectId));

        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            Log.w(TAG, "Linking of program failed.");
            return 0;
        }

        // Return the program object ID.
        return programObjectId;
    }
}

package com.jqueue.openglapp.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.jqueue.openglapp.BuildConfig
import com.jqueue.openglapp.R
import com.jqueue.openglapp.utils.OpenGLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirRender(val context: Context) : GLSurfaceView.Renderer {

    companion object {
        val POSITION_COMPONENT_COUNT = 2
        const val U_COLOR = "u_Color"
        const val V_POSITION = "vPosition"
        const val POINT_SIZE = "pointSize"
    }

    var uColorLocation: Int = 0
    var vPositionLocation: Int = 0
    var pointSizeLocation: Int = 0

    private var vertexShader: Int = 0
    private var fragmentShader: Int = 0
    private var program: Int = 0

    val vertex: FloatArray = FloatArray(8).apply {
        set(3, 14.toFloat())
        set(4, 9.toFloat())
        set(5, 14.toFloat())
        set(6, 9.toFloat())
    }

    private val twoTriangles: FloatArray = FloatArray(22).apply {
        set(0, (-0.5).toFloat())
        set(1, (-0.5).toFloat())
        set(2, (0.5).toFloat())
        set(3, (0.5).toFloat())
        set(4, (-0.5).toFloat())
        set(5, (0.5).toFloat())

        set(6, (-0.5).toFloat())
        set(7, (-0.5).toFloat())
        set(8, (0.5).toFloat())
        set(9, (-0.5).toFloat())
        set(10, (0.5).toFloat())
        set(11, (0.5).toFloat())

        set(12, (-0.5).toFloat())
        set(14, 0.5.toFloat())

        set(17, (-0.25).toFloat())
        set(19, 0.25.toFloat())


    }

    private val bg_rect = FloatArray(12).apply {
        set(0, (-0.55).toFloat())
        set(1, (-0.55).toFloat())
        set(2, (0.55).toFloat())
        set(3, (0.55).toFloat())
        set(4, (-0.55).toFloat())
        set(5, (0.55).toFloat())

        set(6, (-0.55).toFloat())
        set(7, (-0.55).toFloat())
        set(8, (0.55).toFloat())
        set(9, (-0.55).toFloat())
        set(10, (0.55).toFloat())
        set(11, (0.55).toFloat())
    }

    private val vertexData = ByteBuffer
        .allocateDirect(twoTriangles.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val bg_reactData = ByteBuffer
        .allocateDirect(bg_rect.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    init {
        vertexData.put(twoTriangles)
        bg_reactData.put(bg_rect)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //绘制桌子边框
        bg_reactData.position(0)
        GLES20.glVertexAttribPointer(
            vPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            bg_reactData
        )
        GLES20.glEnableVertexAttribArray(vPositionLocation)
        GLES20.glUniform4f(uColorLocation, 1.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            vPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(vPositionLocation)
        //绘制桌子
        GLES20.glUniform4f(uColorLocation, 1.toFloat(), 1.toFloat(), 1.toFloat(), 1.toFloat())
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        //绘制分隔线
        GLES20.glUniform4f(uColorLocation, 1.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        //绘制点
        GLES20.glVertexAttrib1f(pointSizeLocation, 10.toFloat())
        GLES20.glUniform4f(uColorLocation, 0.toFloat(), 0.toFloat(), 1.toFloat(), 1.toFloat())
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        GLES20.glUniform4f(uColorLocation, 1.toFloat(), 0.toFloat(), 0.toFloat(), 1.toFloat())
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)

        GLES20.glVertexAttrib1f(pointSizeLocation, 60.toFloat())
        GLES20.glUniform4f(uColorLocation, 0.toFloat(), 1.toFloat(), 0.toFloat(), 0.toFloat())
        GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())
        initProgram()
        if (BuildConfig.DEBUG) {
            OpenGLUtils.validateProgram(program)
        }
        GLES20.glUseProgram(program)
        initLocation()
    }

    private fun initProgram() {
        vertexShader = OpenGLUtils.compileVertextShader(
            OpenGLUtils.readShaderFromRawResource(
                context,
                R.raw.air_vertex_shader
            )
        )

        fragmentShader = OpenGLUtils.compileFragmentShader(
            OpenGLUtils.readShaderFromRawResource(
                context,
                R.raw.air_fragment_shader
            )
        )
        program = OpenGLUtils.linkProgrem(vertexShader, fragmentShader)
    }

    private fun initLocation() {
        uColorLocation = GLES20.glGetUniformLocation(program,
            U_COLOR
        )
        vPositionLocation = GLES20.glGetAttribLocation(program,
            V_POSITION
        )
        pointSizeLocation = GLES20.glGetAttribLocation(program,
            POINT_SIZE
        )
    }
}
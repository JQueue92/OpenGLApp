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

class AirSmoothShaderRenderer(private val context: Context) : GLSurfaceView.Renderer {

    companion object {
        const val POSITION_COMPONENT_COUNT = 2
        const val COLOR_COMPONENT_COUNT = 3
        const val V_POSITION = "vPosition"
        const val POINT_SIZE = "pointSize"
        const val A_COLOR = "a_Color"

        const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * 4
    }

    var vPositionLocation: Int = 0
    var pointSizeLocation: Int = 0
    var aColorLocation: Int = 0

    private var vertexShader: Int = 0
    private var fragmentShader: Int = 0
    private var program: Int = 0

    private val twoTriangles = arrayOf(
        0F, 0F, 1F, 1F, 1F,
        -0.5F, -0.5F, 0.7F, 0.7F, 0.7F,
        0.5F, -0.5F, 0.7F, 0.7F, 0.7F,
        0.5F, 0.5F, 0.7F, 0.7F, 0.7F,
        -0.5F, 0.5F, 0.7F, 0.7F, 0.7F,
        -0.5F, -0.5F, 0.7F, 0.7F, 0.7F,
        -0.5F, 0F, 1F, 0F, 0F,
        0.5F, 0F, 1F, 0F, 0F,
        0F, -0.25F, 0F, 0F, 1F,
        0F, 0.25F, 1F, 0F, 0F,
        0F, 0F, 0F, 1F, 0F
    ).toFloatArray()

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

        //绘制桌子
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)

        //绘制分隔线
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        //绘制点
        GLES20.glVertexAttrib1f(pointSizeLocation, 10.toFloat())
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)

        GLES20.glVertexAttrib1f(pointSizeLocation, 60.toFloat())
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
                R.raw.air_smooth_vertex_shader
            )
        )

        fragmentShader = OpenGLUtils.compileFragmentShader(
            OpenGLUtils.readShaderFromRawResource(
                context,
                R.raw.air_smooth_fragment_shader
            )
        )
        program = OpenGLUtils.linkProgrem(vertexShader, fragmentShader)
    }

    private fun initLocation() {
        aColorLocation = GLES20.glGetAttribLocation(
            program,
            A_COLOR
        )
        vPositionLocation = GLES20.glGetAttribLocation(
            program,
            V_POSITION
        )
        pointSizeLocation = GLES20.glGetAttribLocation(
            program,
            POINT_SIZE
        )

        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            vPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(vPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aColorLocation)
    }
}
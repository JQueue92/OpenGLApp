package com.jqueue.openglapp.utils

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.microedition.khronos.opengles.GL10

object OpenGLUtils {
    private const val TAG = "OpenGLUtils"
    val externalOESTextureID: Int
        get() {
            val texture = IntArray(1)
            GLES20.glGenTextures(1, texture, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0])
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE
            )
            return texture[0]
        }

    fun loadShader(type: Int, source: String?): Int {
        // 1. create shader
        var shader = GLES20.glCreateShader(type)
        if (shader == GLES20.GL_NONE) {
            Log.e(TAG, "create shared failed! type: $type")
            return GLES20.GL_NONE
        }
        // 2. load shader source
        GLES20.glShaderSource(shader, source)
        // 3. compile shared source
        GLES20.glCompileShader(shader)
        // 4. check compile status
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == GLES20.GL_FALSE) { // compile failed
            Log.e(TAG, "Error compiling shader. type: $type:")
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader) // delete shader
            shader = GLES20.GL_NONE
        }
        return shader
    }

    fun readShaderFromRawResource(context: Context, resourceId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(
            resourceId
        )
        val inputStreamReader = InputStreamReader(
            inputStream
        )
        val bufferedReader = BufferedReader(
            inputStreamReader
        )
        var nextLine: String?
        val body = StringBuilder()
        try {
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                if (!nextLine!!.startsWith("#")) {
                    body.append(nextLine)
                    body.append('\n')
                }
            }
        } catch (e: IOException) {
            return ""
        }
        return body.toString()
    }

    fun compileVertextShader(shader: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shader)
    }

    fun compileFragmentShader(shader: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shader)
    }

    private fun compileShader(type: Int, shader: String): Int {
        val shaderObjectId = GLES20.glCreateShader(type)//创建找色器对象
        if (shaderObjectId == GLES20.GL_NONE) {
            LogUtil.d(msg = "shader object created failed")
            return shaderObjectId
        }

        //编译着色器代码
        GLES20.glShaderSource(shaderObjectId, shader)//上传shader源代码并关联着色器对象
        GLES20.glCompileShader(shaderObjectId)//编译与着色器对象关联的shader源代码

        val compileState = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileState, 0)

        if (compileState[0] == GLES20.GL_FALSE) {
            GLES20.glDeleteShader(shaderObjectId)//如果编译失败，删除着色器对象
            LogUtil.d(msg = "compile  shader failed")
            LogUtil.d(msg = "${GLES20.glGetShaderInfoLog(shaderObjectId)}")
        }

        return shaderObjectId
    }

    fun linkProgrem(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val program = GLES20.glCreateProgram()//创建OpenGL 程序
        if (program == 0) {
            LogUtil.d(msg = "program create failed")
            return program
        }
        //将顶点着色器和片段着色器附加到OpenGL 程序上
        GLES20.glAttachShader(program, vertexShaderId)
        GLES20.glAttachShader(program, fragmentShaderId)

        GLES20.glLinkProgram(program)//将这些着色器联合起来
        //获取程序连接状态
        val linkState = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkState, 0)

        //验证状态
        if (linkState[0] == GLES20.GL_FALSE) {
            GLES20.glDeleteProgram(program)
            LogUtil.d(msg = "program link failed")
            LogUtil.d(msg = "${GLES20.glGetProgramInfoLog(program)}")
            return 0
        }
        return program
    }

    fun validateProgram(program: Int): Boolean {
        GLES20.glValidateProgram(program)
        val programState = IntArray(1)

        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, programState, 0)

        LogUtil.d(msg = "${GLES20.glGetProgramInfoLog(program)}")

        return programState[0] != 0
    }


    fun createProgram(vertexSource: String?, fragmentSource: String?): Int {
        // 1. load shader
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == GLES20.GL_NONE) {
            Log.e(TAG, "load vertex shader failed! ")
            return GLES20.GL_NONE
        }
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == GLES20.GL_NONE) {
            Log.e(TAG, "load fragment shader failed! ")
            return GLES20.GL_NONE
        }
        // 2. create gl program
        val program = GLES20.glCreateProgram()
        if (program == GLES20.GL_NONE) {
            Log.e(TAG, "create program failed! ")
            return GLES20.GL_NONE
        }
        // 3. attach shader
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        // we can delete shader after attach
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
        // 4. link program
        GLES20.glLinkProgram(program)
        // 5. check link status
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == GLES20.GL_FALSE) { // link failed
            Log.e(TAG, "Error link program: ")
            Log.e(TAG, GLES20.glGetProgramInfoLog(program))
            GLES20.glDeleteProgram(program) // delete program
            return GLES20.GL_NONE
        }
        return program
    }
}
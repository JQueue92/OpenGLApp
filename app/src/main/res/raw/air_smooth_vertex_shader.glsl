#version 120
#对于每个顶点，该程序都会执行一次，在vPosition中接受当前点的位置
#vec4 是包含四个分量的向量, 分别为x, y, z, w;默认情况下opengl把x, y, z定义为0，把w定义为1

attribute vec4 vPosition;
attribute float pointSize;
attribute vec4 a_Color;
varying vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_Position = vPosition;
    gl_PointSize = pointSize;
}

#version 120
precision mediump float;

uniform vec4 u_Color;

#uniform会让每个顶点使用同一个值

void main() {
    gl_FragColor = u_Color;
}

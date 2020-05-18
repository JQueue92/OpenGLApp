#version 120
precision mediump float;

#uniform vec4 u_Color;
#uniform会让每个顶点使用同一个值

varying vec4 v_Color;

void main() {
    gl_FragColor = v_Color;
}

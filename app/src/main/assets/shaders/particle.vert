#version 300 es
precision highp float;
layout(location=0) in vec3 aPosition; layout(location=1) in vec4 aColor; layout(location=2) in float aSize;
uniform mat4 uView; uniform mat4 uProjection; out vec4 vColor;
void main(){vColor=aColor;gl_PointSize=aSize*30.0;gl_Position=uProjection*uView*vec4(aPosition,1.0);}

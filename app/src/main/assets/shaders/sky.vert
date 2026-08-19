#version 300 es
precision highp float;
layout(location=0) in vec3 aPosition; uniform mat4 uProjection; uniform mat4 uView; out vec3 vDir;
void main(){vDir=aPosition;mat4 vn=uView;vn[3]=vec4(0.0,0.0,0.0,1.0);gl_Position=(uProjection*vn*vec4(aPosition*50.0,1.0)).xyww;}

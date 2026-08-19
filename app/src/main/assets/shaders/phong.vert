#version 300 es
precision highp float;
layout(location=0) in vec3 aPosition; layout(location=1) in vec3 aNormal; layout(location=2) in vec2 aTexCoord; layout(location=3) in vec4 aColor;
uniform mat4 uModel; uniform mat4 uView; uniform mat4 uProjection; uniform mat3 uNormalMatrix;
out vec3 vNormal; out vec3 vWorldPos; out vec2 vTexCoord; out vec4 vColor;
void main(){vec4 wp=uModel*vec4(aPosition,1.0);vWorldPos=wp.xyz;vNormal=normalize(uNormalMatrix*aNormal);vTexCoord=aTexCoord;vColor=aColor;gl_Position=uProjection*uView*wp;}

#version 300 es
precision highp float;
in vec4 vColor; out vec4 fragColor;
void main(){vec2 uv=gl_PointCoord-0.5;float d=length(uv);if(d>0.5)discard;float a=smoothstep(0.5,0.0,d)*vColor.a;fragColor=vec4(vColor.rgb,a);}

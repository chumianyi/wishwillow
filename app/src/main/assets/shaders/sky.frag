#version 300 es
precision highp float;
in vec3 vDir; uniform vec3 uTopColor; uniform vec3 uBottomColor; uniform vec3 uSunDir; out vec4 fragColor;
void main(){vec3 d=normalize(vDir);float t=clamp(d.y*0.5+0.5,0.0,1.0);vec3 c=mix(uBottomColor,uTopColor,t);float sun=max(dot(d,normalize(uSunDir)),0.0);c+=vec3(1.0,0.95,0.8)*pow(sun,64.0)*0.5;fragColor=vec4(c,1.0);}

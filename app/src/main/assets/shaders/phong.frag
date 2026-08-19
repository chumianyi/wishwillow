#version 300 es
precision highp float;
in vec3 vNormal; in vec3 vWorldPos; in vec2 vTexCoord; in vec4 vColor;
uniform vec3 uLightDir; uniform vec3 uAmbient; uniform vec3 uDiffuse; uniform vec3 uSpecular; uniform float uShininess; uniform vec3 uCameraPos; uniform sampler2D uTexture; uniform int uUseTexture; uniform vec3 uTint; uniform float uAlpha;
out vec4 fragColor;
void main(){vec3 N=normalize(vNormal);vec3 L=normalize(-uLightDir);vec3 V=normalize(uCameraPos-vWorldPos);vec3 H=normalize(L+V);float diff=max(dot(N,L),0.0);float spec=pow(max(dot(N,H),0.0),uShininess);vec3 base=vColor.rgb*uTint;if(uUseTexture==1){vec4 tc=texture(uTexture,vTexCoord);base*=tc.rgb;}vec3 res=(uAmbient+uDiffuse*diff+uSpecular*spec*0.3)*base;float a=uAlpha*vColor.a;if(uUseTexture==1)a*=texture(uTexture,vTexCoord).a;fragColor=vec4(res,a);}

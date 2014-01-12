varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec2 u_renderSize;
uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
uniform vec2 u_cameraPos;
uniform float u_fadeAmount;
uniform vec2 u_scrollAmount;
uniform vec2 u_textureSize;
uniform vec4 u_fogColor;

void main() {

    vec2 pos = (gl_FragCoord.xy + u_scrollAmount + u_cameraPos)/(u_textureSize*4.0);//u_renderSize;
    pos = mod(pos, 1.0);
    vec4 color = u_fadeAmount*texture2D(u_texture0, pos) + (1.0 - u_fadeAmount)*texture2D(u_texture1, pos);
    gl_FragColor = color*1.2*u_fogColor;
}
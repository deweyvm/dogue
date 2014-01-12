varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec2 u_renderSize;
uniform sampler2D u_texture;

void main() {
    vec2 pos = gl_FragCoord.xy/(u_renderSize);
    gl_FragColor = texture2D(u_texture, pos);
}
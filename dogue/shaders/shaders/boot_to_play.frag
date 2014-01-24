varying vec4 v_color;
varying vec2 v_texCoords;


uniform float u_time;
uniform float u_startTime;
uniform vec2 u_renderSize;
uniform sampler2D u_bootTexture;
uniform sampler2D u_worldTexture;
uniform sampler2D u_maskTexture;

float rand(vec2 seed) {
    return fract(sin(dot(seed.xy, vec2(12.9898,78.233 + u_time))) * 43758.5453);
}

void main() {
    vec2 pos = gl_FragCoord.xy/(u_renderSize);
    vec4 mask = texture2D(u_maskTexture, pos);
    if (mask.r > 0.5) {
        gl_FragColor = texture2D(u_worldTexture, pos);
    } else {
        gl_FragColor = texture2D(u_bootTexture, pos);
    }
}
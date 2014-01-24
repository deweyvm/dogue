varying vec4 v_color;
varying vec2 v_texCoords;


uniform float u_time;
uniform vec2 u_renderSize;
uniform sampler2D u_texture;
uniform int u_brighten;


float rand(vec2 seed) {
    return fract(sin(dot(seed.xy, vec2(12.9898,78.233 + u_time))) * 43758.5453);
}


vec4 crt(vec4 color, vec4 coord) {
    vec4 outColor;

    float first = mod(coord.x + coord.y,3.0);
    if (int(first) == 0) {
        outColor = vec4(color.r, 0, 0, 1.0);
    } else if (int(first) == 1) {
        outColor = vec4(0, color.g, 0, 1.0);
    } else {
        outColor = vec4(0, 0, color.b, 1.0);
    }
    return outColor;
}

void main() {
    vec2 pos = gl_FragCoord.xy/(u_renderSize);
    vec4 color = texture2D(u_texture, pos);
    if (u_brighten == 0) {
        gl_FragColor = (0.5*color + 0.5*crt(color, gl_FragCoord));
    } else {
        gl_FragColor = (0.60*color + 0.40*crt(color, gl_FragCoord));
    }

    //gl_FragColor = texture2D(u_texture, pos);
}
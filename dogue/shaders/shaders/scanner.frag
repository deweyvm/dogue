varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec2 u_renderSize;
uniform sampler2D u_worldTexture;
uniform sampler2D u_scannableTexture;
uniform float u_radius;
uniform float u_progress;
uniform vec2 u_playerPos;
uniform float u_time;

float rand(vec2 seed) {
    return fract(sin(dot(seed.xy, vec2(12.9898,78.233 + u_time))) * 43758.5453);
}

float distance(vec2 pt1, vec2 pt2) {
    float dx = pt1.x - pt2.x;
    float dy = (pt1.y - pt2.y)/1.7777;
    return sqrt(dx*dx + dy*dy);
}

void main() {
    vec2 pos = gl_FragCoord.xy/u_renderSize;

    float dist = distance(pos, u_playerPos);
    float span = 0.2*(1.0 - u_progress);

    if (u_radius > 0.0 && dist < u_radius && dist > u_radius - span) {
        vec4 scanColor = texture2D(u_scannableTexture, pos);
        if (scanColor.rgb != vec3(0.0,0.0,0.0)) {
            gl_FragColor = scanColor*vec4(0.0, 1.0, 0.0, 1.0);
        } else {
            float mid = u_radius - span/2.0;
            vec2 p = u_playerPos - pos;
            float diff = abs(mid - dist);
            float amt = span/2.0 - diff;
            float x = rand(gl_FragCoord.xy);
            float y = rand(gl_FragCoord.xy + vec2(1.0,1.0));

            gl_FragColor = texture2D(u_worldTexture, pos + p*amt + (amt)*(1.0-amt)*vec2(x,y));
        }
    } else {
        gl_FragColor = texture2D(u_worldTexture, pos);
    }

}
varying vec4 v_color;

uniform vec2 u_screenSize;
uniform int u_fluidType;
uniform sampler2D u_texture0;
uniform sampler2D u_sceneTexture;

uniform float u_time;

float rand(vec2 seed) {
    return fract(sin(dot(seed.xy, vec2(12.9898,78.233 + u_time))) * 43758.5453);
}

vec4 blur(vec2 uv) {
    float xStep = 1.0/u_screenSize.x;
    float yStep = 1.0/u_screenSize.y;
    vec4 tl = texture2D(u_texture0, uv);
    vec4 tr = texture2D(u_texture0, uv + vec2(xStep, 0.0));
    vec4 bl = texture2D(u_texture0, uv + vec2(0.0,   yStep));
    vec4 br = texture2D(u_texture0, uv + vec2(xStep, yStep));
    vec2 f = fract(vec2(uv.x * xStep, uv.y * yStep));
    vec4 tA = mix(tl, tr, f.x);
    vec4 tB = mix(bl, br, f.x);
    vec4 c = mix(tA, tB, f.y);
    return c;
}

void main() {
    float xWave = 0.05*sin(u_time/10.0 + gl_FragCoord.y/10.0);
    float yWave = 0.05*cos(u_time/12.0 + gl_FragCoord.x/10.0);
    vec2 uv =  gl_FragCoord.xy/u_screenSize + 0.003*vec2(xWave +rand(vec2(xWave, yWave)), yWave+rand(vec2(yWave, xWave)));
    vec4 c = blur(uv);
    int g = int(c.g*10.0);
    int b = int(c.b*10.0);
    bool doSwirl   = (g == 5 || g == 4);
    vec4 bright    = vec4(0.7, 0.7, 0.7, 1.0);
    vec4 swirl     = vec4(0.1, 0.1, 0.1, 1.0);
    vec4 fringe    = vec4(0.3, 0.3, 0.3, 1.0);
    vec4 brightAlt = vec4(0.5, 0.5, 0.5, 1.0);
    bool lava = u_fluidType == 0;

    if (c.r > 0.5) {
        //c = swirl;
    } else if (g > 4) {
        c = bright;
    } else if (g > 2) {
        c = brightAlt;
    } else {
        //c = swirl;
        c = texture2D(u_sceneTexture, uv);
    }
    
    gl_FragColor = c + rand(gl_FragCoord.xy)*swirl;
}
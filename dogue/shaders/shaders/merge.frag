uniform vec2 u_renderSize;
uniform float u_upscaleFactor;


uniform sampler2D u_sceneTexture;
uniform sampler2D u_shadowTexture;
uniform float u_darknessAmount;


void main() {
    vec2 pos = gl_FragCoord.xy/u_renderSize;
    vec4 shadowColor = texture2D(u_shadowTexture, pos);

    gl_FragColor = texture2D(u_sceneTexture, pos);
    if (shadowColor.r != 1.0) {
        gl_FragColor *= u_darknessAmount;
    }
}

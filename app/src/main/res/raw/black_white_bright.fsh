precision mediump float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec4 tex = texture2D( iChannel0, texCoord );

	float brightness = 1.3;
	float gray_scale = dot(tex.xyz, vec3(0.299, 0.587, 0.114));

	vec4 col = vec4(vec3(gray_scale), tex.a);

	col.x = col.x * brightness;
	col.y = col.y * brightness;
	col.z = col.z * brightness;

    fragColor = col;
}

void main() {
	mainImage(gl_FragColor, texCoord);
}
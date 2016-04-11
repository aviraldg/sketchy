package com.aviraldg.sketchy;

import com.aviraldg.sketchy.util.MyDebugRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Scaling;

import java.util.HashSet;

public class SketchyGame extends ApplicationAdapter {
	public static final String NAME = "World of Sketch";

	SpriteBatch batch;
	Texture img;
    private MyDebugRenderer debugRenderer;
    private World world;
    private OrthographicCamera camera;
    private Texture camTexture;

    public HashSet<ApplicationListener> adapters = new HashSet<ApplicationListener>();
    static final String TAG = "SketchyGame";

    @Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("paper.jpg");

        world = new World(new Vector2(0, -10), true);
        debugRenderer = new MyDebugRenderer();

        camera = new OrthographicCamera();
        MusicPlayer musicPlayer = new MusicPlayer();
        musicPlayer.play();

        initObjects();
        for(ApplicationListener adapter: adapters) {
            adapter.create();
        }
    }

    private void initObjects() {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
// Set our body's starting position in the world
        bodyDef.position.set(100, 300);
        bodyDef.angularVelocity = -0.4f;

// Create our body in the world using our body definition
        Body body = world.createBody(bodyDef);

// Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(40f);

// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.9f; // Make it bounce a little bit

// Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
        // Create our body definition
        BodyDef groundBodyDef =new BodyDef();
// Set its world position
        groundBodyDef.position.set(new Vector2(450, 10));

// Create a body from the defintion and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

// Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(400, 50.0f);
// Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);
// Clean up after ourselves
        groundBox.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Vector2 size = Scaling.fit.apply(800, 600, width, height);
        int viewportX = (int)(width - size.x) / 2;
        int viewportY = (int)(height - size.y) / 2;
        int viewportWidth = (int)size.x;
        int viewportHeight = (int)size.y;
        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        for(ApplicationListener adapter: adapters) {
            adapter.resize(width, height);
        }
    }

    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
        }
    }

    /**
     * Returns a GL texture name (used by Android for camera preview)
     * @return int
     */
    int obtainGlTexture() {
        if(camTexture == null)
            camTexture = new Texture(2048, 2048, Pixmap.Format.RGB888);

        camTexture.draw(new Pixmap(Gdx.files.internal("badlogic.jpg")), 0, 0);

        return camTexture.getTextureObjectHandle();
    }

    @Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for(int i=0; i<10; i++) {
            for(int j=0; j<10; j++) {
                batch.draw(img, i*img.getWidth(), j*img.getHeight());
            }
        }

        if(camTexture != null) {
            Gdx.app.log(TAG, String.format("glTexture: %d", obtainGlTexture()));
            batch.draw(camTexture, 0, 0);
        }
        batch.end();

        debugRenderer.render(world, camera.combined);
        doPhysicsStep(1f);
	}
}

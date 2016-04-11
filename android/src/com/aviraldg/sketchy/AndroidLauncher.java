package com.aviraldg.sketchy;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;

import android.util.Log;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.aviraldg.sketchy.SketchyGame;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class AndroidLauncher extends AndroidApplication implements LifecycleListener, Camera.PreviewCallback, ApplicationListener {
    static final String TAG = "AndroidLauncher";

    private Camera camera;
    private SketchyGame game;
    private SurfaceTexture surfaceTexture;

    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        game = new SketchyGame();
        game.adapters.add(this);
		initialize(game, config);
        addLifecycleListener(this);
	}

    private void init() {
        try {
            camera = Camera.open();
        } catch (Exception e) {
            error(TAG, e.toString());
        }

        surfaceTexture = new SurfaceTexture(game.obtainGlTexture());
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            error(TAG, e.toString());
        }
        camera.setPreviewCallback(this);
        camera.startPreview();

    }

    @Override
    public void create() {
        init();
    }

    @Override
    public void resize(int width, int height) {
        camera.stopPreview();
        camera.release();
        try {
            camera = Camera.open();
        } catch (Exception e) {
            error(TAG, e.toString());
        }
        surfaceTexture = new SurfaceTexture(game.obtainGlTexture());
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            error(TAG, e.toString());
        }

        camera.setPreviewCallback(this);
        camera.startPreview();
    }

    @Override
    public void render() {
        surfaceTexture.updateTexImage();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG, "onPreviewFrame");
    }
}

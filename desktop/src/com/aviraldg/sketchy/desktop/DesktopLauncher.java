package com.aviraldg.sketchy.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.aviraldg.sketchy.SketchyGame;

public class DesktopLauncher {
    public static void main (String[] arg) {
        SketchyGame game = new SketchyGame();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = SketchyGame.NAME;
        config.addIcon("launcher.png", Files.FileType.Local);
        config.width = 800;
        config.height = 600;
		LwjglApplication app = new LwjglApplication(game, config);
	}
}

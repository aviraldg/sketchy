package com.aviraldg.sketchy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

class MusicPlayer implements Music.OnCompletionListener {
    private static final String[] TRACKS = {
        "country",
        "fair",
        "farm",
        "mousetrap",
        "silly",
        "toys"
    };

    private Music music;
    private int trackId = 0;

    void play() {
        music = Gdx.audio.newMusic(
                Gdx.files.internal(
                        String.format("music/%s.ogg", TRACKS[trackId])
                )
        );
        music.setOnCompletionListener(this);
        music.play();
    }

    @Override
    public void onCompletion(Music music) {
        trackId = (trackId + 1) % TRACKS.length;
        music.dispose();
        play();
    }
}

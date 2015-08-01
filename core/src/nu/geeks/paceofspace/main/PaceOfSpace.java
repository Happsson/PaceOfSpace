package nu.geeks.paceofspace.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nu.geeks.paceofspace.handler.GameHandler;

public class PaceOfSpace extends Game {
    @Override
    public void create () {
        GameHandler.changeScreen("Play");
    }

    @Override
    public void render () {
        super.render();
    }
}

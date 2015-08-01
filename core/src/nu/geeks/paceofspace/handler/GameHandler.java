package nu.geeks.paceofspace.handler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import nu.geeks.paceofspace.screens.Play;

/**
 * Created by hannespa on 15-07-31.
 */
public class GameHandler {

    public static void changeScreen(String screen){
        if(screen.equals("Play")){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
        }
    }
}

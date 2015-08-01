package nu.geeks.paceofspace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import nu.geeks.paceofspace.handler.GameHandler;
import nu.geeks.paceofspace.util.mMath;

/**
 * Created by hannespa on 15-07-31.
 */
public class Play implements Screen {

    private World world;
    private OrthographicCamera camera;
    private Box2DDebugRenderer render;
    private Body player, mouse;
    private Array<Body> planets;
    private final float PPM = 100;
    private Random rand = new Random();
    private int planetNumber = 0;
    private boolean drawLine = true, leftPressed = false, rightPressed = false, forwardPressed = false, playerOnPlanet = false;
    private ShapeRenderer sr;
    private Vector2 impulseDraw;
    private Array<Vector2> previousState;
    private SpriteBatch sb;
    private BitmapFont font;
    private float speed = 0;
    private float playerRotation = 0;
    private mMath math = new mMath();
    private Array<Vector2> playerGravityImpulses;

    @Override
    public void show() {
        setupWorld();
        createPlayer();
        createUniverse(new Vector2(0,0));
        /*
        createUniverse(new Vector2(100000,20000));
        createUniverse(new Vector2(-100000,20000));
        createUniverse(new Vector2(100000,-20000));
        createUniverse(new Vector2(-100000,-20000));
        createUniverse(new Vector2(0,-200000));
        createUniverse(new Vector2(0,200000));
        */
        setupImpulseArray();

        setInputProcessor();
    }

    private void createUniverse(Vector2 position) {
        Body planet;
        Body star;
        FixtureDef fdef = new FixtureDef();
        BodyDef bdef = new BodyDef();
        CircleShape shape = new CircleShape();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;

        Vector2 posStar = new Vector2(position.x / PPM, position.y / PPM);
        Vector2 posPlanet = new Vector2((5000f + position.x) / PPM, (position.y) / PPM);
        float sizeStar = 500f;
        float sizePlanet = 100f;

        shape.setRadius(sizeStar / PPM);
        fdef.density = ((4*3.141592f*sizeStar*sizeStar*sizeStar) / 3);
        fdef.shape = shape;
        bdef.position.set(posStar);
        star = world.createBody(bdef);
        star.createFixture(fdef);
        planets.add(star);
        planetNumber++;
        previousState.add(posStar);

        shape.setRadius(sizePlanet / PPM);
        fdef.shape = shape;
        bdef.position.set(posPlanet);
        planet = world.createBody(bdef);
        planet.createFixture(fdef);
        planet.applyForceToCenter(new Vector2(0,6e11f), true);
        planets.add(planet);
        planetNumber++;
        previousState.add(posPlanet);

        shape.dispose();
    }

    private void setupImpulseArray() {
        playerGravityImpulses = new Array<Vector2>();
        for(int i = 0; i<world.getBodyCount() - 1; i++){
            playerGravityImpulses.add(new Vector2(0,0));
        }
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

            @Override
            public boolean scrolled(int amount) {
                camera.zoom += (amount / 5f);
                camera.update();
                return super.scrolled(amount);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {

                Vector3 mousePos = new Vector3(screenX, screenY, 0);
                camera.unproject(mousePos);

                //				applyForcefromtouch(new Vector2(mousePos.x, mousePos.y));

                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch(keycode){
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                    case Input.Keys.UP:
                        forwardPressed = true;
                        break;

                    case Input.Keys.SPACE:
                        boost();
                        break;
                    case Input.Keys.ESCAPE:
                        dispose();
                        GameHandler.changeScreen("Play");
                }
                return super.keyDown(keycode);
            }
            @Override
            public boolean keyUp(int keycode) {

                switch(keycode){
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        //					player.applyAngularImpulse(-9e5f, true);
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        //					player.applyAngularImpulse(9e5f, true);
                        break;
                    case Input.Keys.UP:
                        forwardPressed = false;
                        break;


                }
                return super.keyUp(keycode);
            }
        }));
    }

    private void boost() {
        Vector2 angle = new Vector2(2e10f,2e10f);
        angle.rotate((float) Math.toDegrees(player.getAngle()) + 45);
        player.applyForceToCenter(angle, true);
    }

    private void createPlayer() {
        FixtureDef fdef = new FixtureDef();
        BodyDef bdef = new BodyDef();
        float size = 100;


        PolygonShape shape = new PolygonShape();
        Vector2[] verticis = new Vector2[3];
        verticis[0] = new Vector2(-150f/PPM,0);
        verticis[1] = new Vector2(0,200f/PPM);
        verticis[2] = new Vector2(150f/PPM,0);
        shape.set(verticis);
        fdef.shape = shape;

        Vector2 pos = new Vector2(0,5000f / PPM);
        bdef.fixedRotation = true;
        bdef.position.set(pos);
        bdef.type = BodyDef.BodyType.DynamicBody;
        fdef.density = ((4*3.141592f*size*size*size) / 3);
        player = world.createBody(bdef);
        player.createFixture(fdef);
        previousState.add(pos);

        shape.dispose();
    }

    private void setupWorld() {

        world = new World(new Vector2(0,0), true);
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight()/PPM);
        camera.zoom = 20;
        camera.update();
        render = new Box2DDebugRenderer();
        planets = new Array<Body>();
        sr = new ShapeRenderer();
        impulseDraw = new Vector2();
        previousState = new Array<Vector2>();
        sb = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.setUseIntegerPositions(true);

    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(player.getPosition(), 0);
        camera.update();
        world.step(1 / 60f, 1, 1);
        render.render(world, camera.combined);
        sr.setProjectionMatrix(camera.combined);
        sb.begin();
        
        font.draw(sb, "Speed: " +
                Math.round(Math.abs(player.getLinearVelocity().x) +
                        Math.abs(player.getLinearVelocity().y)), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        sb.end();
        controlPlayer();
        hitCheck();
        gamePhysics();


    }

    private void gamePhysics() {

        //TODO - gå igenom det här, se om det går att optimera.

        //tar fram alla kroppar i en array
        Array<Body> allBodies = new Array<Body>();
        world.getBodies(allBodies);



        for(int i = 0; i < allBodies.size; i++){
            //if(allBodies.get(i).equals(player)) speed = player.getPosition().dst(previousState.get(i));

            //fˆr varje kropp i, kolla dragningen till samtliga kroppar j.
            Vector2 impulseAdd = new Vector2(0,0);
            for(int j = 0; j < allBodies.size; j++){

                //r = avstÂndet mellan kropp i och j
                float r = allBodies.get(i).getPosition().dst(allBodies.get(j).getPosition());
                //om r == 0 ‰r i och j samma kropp.
                if(r > 0){
                    Vector2 posFirst = allBodies.get(i).getPosition();
                    Vector2 posSecond = allBodies.get(j).getPosition();
                    //tar fram m1 och m2 (satt i bdef som density fˆr varje kropp)
                    float m1 = allBodies.get(i).getMass();
                    float m2 = allBodies.get(j).getMass();

                    //newtons gravitationslag
                    float force = (float) 6.67e-8 * ((m1*m2)/(r*r));

                    //tar fram riktningen fˆr impulsen
                    Vector2 impulse = new Vector2();
                    impulse.x = posSecond.x - posFirst.x;
                    impulse.y = posSecond.y - posFirst.y;

                    //om impulsen gäller spelaren, förbered att rita ut
                    if(allBodies.get(i).equals(player)){
                        Vector2 playerGravityDraw = new Vector2(impulse.x, impulse.y);
                        playerGravityDraw.scl(1 / 300f);
                        playerGravityDraw.clamp(1, 50);
                        playerGravityDraw.add(allBodies.get(i).getPosition());

//						System.out.println("j: " + j);
                        /*
                        if(j!=14){
                            playerGravityImpulses.set(j, playerGravityDraw);
                        }else{
                            playerGravityImpulses.set(3, playerGravityDraw);
                        }
                        */
                    }

                    //normaliserar vektorn och multiplicerar med force
                    impulse.nor();
                    impulse.scl(force);

                    //adderar impulsen till impulseAdd
                    impulseAdd.add(impulse);
                }
            }

            //kalkylerar nuvarande riktning, baserat pÂ positionen nu och positionen fˆregÂende render-cykel.
            Vector2 last = new Vector2(
                    allBodies.get(i).getPosition().x - previousState.get(i).x,
                    allBodies.get(i).getPosition().y - previousState.get(i).y
            );
            last.nor();
            last.scl(4);
            last.add(allBodies.get(i).getPosition());



            //applicerar F
            allBodies.get(i).applyForceToCenter(impulseAdd, true);
            //ritar ut vektorer pÂ sk‰rmen;
            impulseAdd.scl(1 / 2000000f);
            impulseAdd.clamp(2f, 50f);
            impulseDraw = impulseAdd.add(allBodies.get(i).getPosition());

            if(allBodies.get(i).equals(player)){
                sr.begin(ShapeRenderer.ShapeType.Line);

                sr.setColor(Color.GREEN);
                sr.line(player.getPosition(), last);
                sr.setColor(Color.YELLOW);
                sr.line(allBodies.get(i).getPosition(), impulseDraw);
                sr.setColor(Color.WHITE);
                for(Vector2 impulse : playerGravityImpulses){
                    sr.line(player.getPosition(), impulse);
                }
                sr.end();
            }


            previousState.get(i).set(allBodies.get(i).getPosition());

        }

        for(Body body : allBodies){
            if(math.hitCheck(player.getPosition(), planets.get(0).getPosition(), 610f / PPM) ||
                    math.hitCheck(player.getPosition(), planets.get(1).getPosition(), 110f / PPM)){
                playerOnPlanet = true;
            }else{
                playerOnPlanet = false;
            }

        }

    }

    private void hitCheck() {
        //TODO - what here?
    }

    private void controlPlayer() {
        if(leftPressed){
            playerRotation = player.getAngle() + .05f;
            player.setTransform(player.getPosition(), playerRotation);
            //			player.applyAngularImpulse(5e5f, true);
        }
        if(rightPressed){
            playerRotation = player.getAngle() - .05f;
            player.setTransform(player.getPosition(), playerRotation);
            //			player.applyAngularImpulse(-5e5f, true);
        }
        if(forwardPressed){
            Vector2 angle = new Vector2(1e8f,1e8f);
            angle.rotate((float) Math.toDegrees(player.getAngle()) + 45);
            player.applyForceToCenter(angle, true);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        world.dispose();
        font.dispose();
        render.dispose();
        sb.dispose();
        sr.dispose();
    }
}

package de.spaceStudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.spaceStudio.MainClient;
import de.spaceStudio.client.util.Global;
import de.spaceStudio.server.model.Planet;
import de.spaceStudio.server.model.Section;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.service.CombatService;

import java.util.ArrayList;


public class CombatScreen extends BaseScreen {


    private Stage stage;
    private Skin skin;
    private Viewport viewport;
    private SpriteBatch batch;

    private Texture playerShip;
    private Texture enemyShip;
    private Texture engine1, engine2, oxygen, piloting;
    private Texture engineer, pilot, hull;
    private Texture background;
    private TextButton fire;

    private Texture fuze, fuz2, explosion;
    int fuzeOffset;
    boolean isFired;
    private boolean isExploied;
    private boolean isTargetSelected;
    private boolean w, d, o;
    private ShapeRenderer shapeRenderer;
    private Skin sgxSkin, skinButton;
    private TextureAtlas gamePlayAtlas;
    private AssetManager assetManager = null;
    boolean isWin;

    //private TextButton engine, weapon, o2;
    private ImageButton engine, weapon, cockpit;
    int disappear = 570;

    Ship shipPlayer = Global.currentShip;

    Sound rocketLaunch;

    ShipSelectScreen shipSelectScreen;

    //
    CombatService cs = new CombatService();
    Planet planet = Global.currentPlanet;
    Ship gegnerShip;
    Section section1Gegner;
    Section section2Gegner;
    Section section3Gegner;
    //

    public CombatScreen(MainClient game) {
        super(game);

        viewport = new FitViewport(BaseScreen.WIDTH, BaseScreen.HEIGHT);
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        skinButton = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        background = new Texture(Gdx.files.internal("Client/core/assets/data/CombatBG.jpg"));
        playerShip = new Texture(Gdx.files.internal("Client/core/assets/blueships_fulled.png"));
        enemyShip = new Texture(Gdx.files.internal("Client/core/assets/data/ships/enemy1.png"));
        hull = new Texture(Gdx.files.internal("Client/core/assets/hull1.png"));
        shapeRenderer = new ShapeRenderer();
        final Drawable engine_sym = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/EnginesSymbol.png")));
        final Drawable engine_red = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/EngineRed.png")));

        final Drawable cockpit_nat = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/PilotingSymbol.png")));
        final Drawable cockpit_red = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/PilotingRed.png")));

        rocketLaunch = Gdx.audio.newSound(Gdx.files.internal("Client/core/assets/data/music/shoot.wav"));
        fuze = new Texture(Gdx.files.internal("Client/core/assets/data/missille_out.png"));
        fuz2 = new Texture(Gdx.files.internal("Client/core/assets/data/missille_in.png"));
        explosion = new Texture(Gdx.files.internal("Client/core/assets/data/explosion1_0024.png"));
        fuzeOffset = 570;

        if (planet.getName().equals("p1")) {

        } else if (planet.getName().equals("p2")) {
            gegnerShip = Global.shipGegner1;
        } else if (planet.getName().equals("p3")) {
            gegnerShip = Global.shipGegner2;
        } else if (planet.getName().equals("p4")) {

        } else if (planet.getName().equals("p5")) {

        }
        ArrayList<Section> sections = Global.sectionofShip(gegnerShip);
        section1Gegner = sections.get(0);
        section2Gegner = sections.get(1);
        section3Gegner = sections.get(2);
        engine = new ImageButton(engine_sym);
        engine.setPosition(1075, 440);
        engine.setSize(1000, 100);
        engine.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isTargetSelected = true;
                w = true;
                o = false;
                d = false;
                engine.getStyle().imageUp = engine_red;

            }
        });


        cockpit = new ImageButton(cockpit_nat);
        cockpit.setPosition(1075,660);
        cockpit.setSize(1000,100);
        cockpit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isTargetSelected = true;
                w = false;
                o = true;
                d = false;
                cockpit.getStyle().imageUp = cockpit_red;

            }
        });


        Gdx.input.setInputProcessor(stage);

        fire = new TextButton("Fire",skinButton,"small");
        fire.setPosition(800,200);
        fire.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logicOfFire();
                //hoverListener(fire);
                rocketLaunch.play();
                isFired = true;
            }
        });


         stage.addActor(fire);
         stage.addActor(engine);
         stage.addActor(cockpit);

    }

    private void logicOfFire() {
        if (w) {
            if (gegnerShip.getName().equals("Shipgegner1")) {
                Global.shipGegner1.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner1.getHp());
                Global.section3Gegner.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner2")) {
                Global.shipGegner2.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner2.getHp());
                Global.section3Gegner2.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner3")) {
                Global.shipGegner3.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner3.getHp());
                Global.section3Gegner3.setUsable(false);
            }
            section3Gegner.setUsable(false);
            Global.currentWeapon.setObjectiv(section3Gegner);
        } else if (d) {
            if (gegnerShip.getName().equals("Shipgegner1")) {
                Global.shipGegner1.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner1.getHp());
                Global.section1Gegner.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner2")) {
                Global.shipGegner2.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner2.getHp());
                Global.section1Gegner2.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner3")) {
                Global.shipGegner3.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner3.getHp());
                Global.section1Gegner3.setUsable(false);
            }
            section1Gegner.setUsable(false);
            Global.currentWeapon.setObjectiv(section2Gegner);
        } else if (o) {
            if (gegnerShip.getName().equals("Shipgegner1")) {
                Global.shipGegner1.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner1.getHp());
                Global.section2Gegner.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner2")) {
                Global.shipGegner2.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner2.getHp());
                Global.section2Gegner2.setUsable(false);
            } else if (gegnerShip.getName().equals("Shipgegner3")) {
                Global.shipGegner3.setHp(gegnerShip.getHp() - Global.weapon.getDamage());
                gegnerShip.setHp(Global.shipGegner3.getHp());
                Global.section2Gegner3.setUsable(false);
            }
            section2Gegner.setUsable(false);
            Global.currentWeapon.setObjectiv(section1Gegner);
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cs.makeAShot(Global.currentWeapon, Net.HttpMethods.POST);
    }


    private void hoverListener(final ImageButton imageButton) {
        imageButton.addListener(new HoverListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                imageButton.setName("selected");

            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);

            }
        });
    }

int counter=0;

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();

        stage.getBatch().draw(background, 0, 0, BaseScreen.WIDTH, BaseScreen.HEIGHT);
        stage.getBatch().draw(playerShip, 300,300,700,700);
        stage.getBatch().draw(enemyShip, 1300,370,550,550);
        stage.getBatch().draw(hull, 0,1020,500,50);
        stage.getBatch().draw(fuze,disappear,422,400,50);
        if(isFired && isTargetSelected) {
            fuzeOffset++;
            disappear=BaseScreen.WIDTH;
            stage.getBatch().draw(fuz2,+fuzeOffset,422,400,50);

            if(fuzeOffset == 700){
                fuzeOffset = BaseScreen.WIDTH;
                isExploied = true;
            }
        }

        if(isExploied)
        {


            stage.getBatch().draw(explosion,1515,422,100,100);
        }

        stage.getBatch().end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.MAGENTA);
        shapeRenderer.line(0,298, BaseScreen.WIDTH,298);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0,296, BaseScreen.WIDTH,296);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(0,294, BaseScreen.WIDTH,294);
        shapeRenderer.setColor(Color.CORAL);
        shapeRenderer.line(0,292, BaseScreen.WIDTH,292);

        // shapeRenderer.
        shapeRenderer.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width,height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        
        skin.dispose();
        shapeRenderer.dispose();
        rocketLaunch.dispose();
        fuze.dispose();
        fuz2.dispose();
        stage.dispose();
    }
}

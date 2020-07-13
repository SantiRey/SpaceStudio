package de.spaceStudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.spaceStudio.MainClient;
import de.spaceStudio.assets.StyleNames;
import de.spaceStudio.client.util.Global;
import de.spaceStudio.server.model.Planet;
import de.spaceStudio.server.model.Section;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.server.model.Weapon;
import de.spaceStudio.service.CombatService;
import de.spaceStudio.util.GdxUtils;

import java.util.ArrayList;

/**
 * NewGameScreen class
 */
public class CombatScreen extends ScreenAdapter {


    private MainClient universeMap;
    private final AssetManager assetManager;
    private MainClient mainClient;
    private Viewport viewport;
    private Stage stage;

    private Skin sgxSkin, sgxSkin2;
    private TextureAtlas gamePlayAtlas;


    private Sound click;


    private Skin skin;
    private Texture playerShip;
    private Texture enemyShip;
    private Texture hull;
    private Texture background;
    private TextButton enableShield, enableEnemyShield;

    private Texture missilleRight,  explosion, missilleLeft, weaponSystem;
    int fuzeOffsetright,fuzeOffsetLeft;
    private boolean isTargetSelected, isTargetEngine, isTargetCockpit, isTargetWeapon;
    private ShapeRenderer shapeRenderer;
    private Skin  skinButton;
    boolean isWin;
    private boolean w, d, o;

    Texture bullet, shield;
    private boolean isShieldEnabled,  isEnemyShield;

    private ImageButton engine, weaponSection,cockpit;
    private int disappearRight = 570;
    private int disappearLeft = 570;
    public static final int SPEED = 450;
    private int counterEngine = 0;
    private int counterCockpit = 0;
    private int counterWeapon = 0;

    Weapon weapon = Global.weapon;
    float x=0;

    Sound rocketLaunch;
    Ship shipPlayer = Global.currentShip;

    ArrayList<Bullet> bullets;
    ArrayList<Bullet> bulletsEnemy;
    ShipSelectScreen shipSelectScreen;
    //
    CombatService cs = new CombatService();
    Planet planet = Global.currentPlanet;
    Ship gegnerShip;
    Section section1Gegner;
    Section section2Gegner;
    Section section3Gegner;




    public CombatScreen(MainClient mainClient) {
        this.universeMap = mainClient;
        this.mainClient = mainClient;
        assetManager = universeMap.getAssetManager();
    }

    @Override
    public void show() {

        viewport = new FitViewport(BaseScreen.WIDTH, BaseScreen.HEIGHT);
        stage = new Stage(viewport, universeMap.getBatch());
        click = Gdx.audio.newSound(Gdx.files.internal("Client/core/assets/data/music/mouseclick.wav"));

        sgxSkin2 = new Skin(Gdx.files.internal("Client/core/assets/ownAssets/sgx/skin/sgx-ui.json"));





        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        skinButton = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        background = new Texture("Client/core/assets/combatAssets/CombatBG.jpg");
        playerShip = new Texture("Client/core/assets/combatAssets/blueships_fulled.png");
        enemyShip = new Texture("Client/core/assets/combatAssets/enemy1.png");
        missilleRight =  new Texture("Client/core/assets/combatAssets/missille_out.png");
        missilleLeft = new Texture("Client/core/assets/combatAssets/missille_out.png");
        shield = new Texture("Client/core/assets/combatAssets/shield_2.png");
        explosion = new Texture("Client/core/assets/combatAssets/explosion1_0024.png");
        bullet = new Texture("Client/core/assets/combatAssets/bullet.png");

        shapeRenderer = new ShapeRenderer();
        final Drawable engine_sym = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/enginesSymbol.png"));
        final Drawable engine_red = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/engineRed.png"));
        final Drawable cockpit_nat = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/PilotingSymbol.png"));
        final Drawable cockpit_red = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/PilotingRed.png"));

        final Drawable weapon_section = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/weaponEnemy.png"));
        final Drawable weapon_section_red = new TextureRegionDrawable(new Texture("Client/core/assets/combatAssets/weapon_red.png"));

        rocketLaunch = Gdx.audio.newSound(Gdx.files.internal("Client/core/assets/data/music/shoot.wav"));
        fuzeOffsetright = 570;
        fuzeOffsetLeft = 570;


        fuzeOffsetright = 570;
        fuzeOffsetLeft = 570;

        bullets = new ArrayList<>();
        bulletsEnemy = new ArrayList<>();

 /*       if (planet.getName().equals("p1")) {
            gegnerShip = Global.shipGegner1;
        } else if (planet.getName().equals("p2")) {
            gegnerShip = Global.shipGegner1;
        } else if (planet.getName().equals("p3")) {
            gegnerShip = Global.shipGegner2;
        } else if (planet.getName().equals("p4")) {
            gegnerShip = Global.shipGegner2;
        } else if (planet.getName().equals("p5")) {

        }
        ArrayList<Section> sections = Global.sectionofShip(gegnerShip);
        section1Gegner = sections.get(0);
        section2Gegner = sections.get(1);
        section3Gegner = sections.get(2);*/
        engine = new ImageButton(engine_sym);
        engine.setPosition(1075,440);
        engine.setSize(1000,100);
        engine.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isTargetEngine = true;
                isTargetSelected=true;
                w = true;
                o = false;
                d = false;
                engine.getStyle().imageUp = engine_red;

            }
        });


        weaponSection = new ImageButton(weapon_section);
        weaponSection.setPosition(1450,500);
        weaponSection.setSize(100,100);
        weaponSection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                w = true;
                o = false;
                d = false;
                isTargetSelected = true;
                isTargetWeapon = true;
                weaponSection.getStyle().imageUp = weapon_section_red;

            }
        });

        cockpit = new ImageButton(cockpit_nat);
        cockpit.setPosition(1075,660);
        cockpit.setSize(1000,100);
        cockpit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isTargetSelected = true;
                isTargetCockpit = true;

                w = false;
                o = true;
                d = false;
                cockpit.getStyle().imageUp = cockpit_red;

            }
        });

        Gdx.input.setInputProcessor(stage);
        enableShield = new TextButton("Activate Shield",sgxSkin2,StyleNames.EMPHASISTEXTBUTTON);
        enableShield.setPosition(BaseScreen.WIDTH-1500,200);
        enableShield.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(isShieldEnabled) isShieldEnabled=false;
                else isShieldEnabled = true;
            }
        });


        enableEnemyShield = new TextButton("Enemy Shield",sgxSkin2,StyleNames.EMPHASISTEXTBUTTON);
        enableEnemyShield.setPosition(BaseScreen.WIDTH-400,200);
        enableEnemyShield.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(isEnemyShield) isEnemyShield=false;
                else isEnemyShield = true;

            }
        });



        TextButton escape = new TextButton(" Escape ", sgxSkin2, StyleNames.EMPHASISTEXTBUTTON);
        escape.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Global.IS_SINGLE_PLAYER = false;
                mainClient.setScreen(new ShipSelectScreen(mainClient));
            }
        });
        escape.setPosition(100,100);


        stage.addActor(enableEnemyShield);
        stage.addActor(engine);
        stage.addActor(cockpit);
        stage.addActor(enableShield);
        stage.addActor(weaponSection);



        stage.addActor(escape);

        Gdx.input.setInputProcessor(stage);
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



    // Called when the screen should render itself.
    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();

        stage.getBatch().draw(background, 0, 0, BaseScreen.WIDTH, BaseScreen.HEIGHT);
        stage.getBatch().draw(playerShip, 300,300,700,700);
        stage.getBatch().draw(enemyShip, 1300,370,550,550);
        stage.getBatch().draw(missilleRight,disappearRight,422,400,50);
        stage.getBatch().draw(missilleLeft,disappearLeft,825,400,50);


        //Create and launch missiles
        if(Gdx.input.isKeyJustPressed(Input.Keys.A) && isTargetCockpit){
            counterCockpit++;
            if(counterCockpit < 3){
                bullets.add(new Bullet(590,843))  ;
                rocketLaunch.play();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D) && isTargetEngine){
            counterEngine++;
            if(counterEngine < 4){
                bullets.add(new Bullet(590,444));
                rocketLaunch.play();
            }

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && isTargetWeapon){
            counterWeapon++;
            if(counterWeapon < 4){
                bullets.add(new Bullet(590,444));
                //bullets.add(new Bullet(590,843));
                rocketLaunch.play();
            }

        }
        //shield for player
        if(isShieldEnabled) stage.getBatch().draw(shield,70,150,1100,1000);
        //shield for enemy
        if(isEnemyShield) stage.getBatch().draw(shield,1120,150,900,1000);

        //explosion on player
        if(counterEngine >= 2)
            stage.getBatch().draw(explosion,550,520,100,100);

        //explosion on enemy's engine
        if( counterEngine >= 3 && !isEnemyShield){
            stage.getBatch().draw(explosion,1515,422,100,100);
        }

        if( counterWeapon >= 3 && !isEnemyShield){
            stage.getBatch().draw(explosion,1450,500,100,100);
        }
        //explosion on enemy's cockpit
        if( counterCockpit >= 2 && !isEnemyShield){
            stage.getBatch().draw(explosion,1515,690,100,100);
        }
        //update bullets
        ArrayList<Bullet> bulletToRemove = new ArrayList<>();
        for (Bullet bullet:bullets) {
            bullet.update(delta);
            if(bullet.remove){
                bulletToRemove.add(bullet);

            }
        }

        bulletToRemove.removeAll(bulletToRemove);

        stage.getBatch().end();
        mainClient.getBatch().begin();
        for (Bullet bullet:bullets ) {
            bullet.render(mainClient.getBatch());
        } mainClient.getBatch().end();


        stage.act();
        stage.draw();
    }

    // Called when the Application is resized.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    // Called when the Application is paused, usually when it's not active or visible on-screen.
    @Override
    public void pause() {
    }

    // Called when the Application is resumed from a paused state, usually when it regains focus.
    @Override
    public void resume() {
    }

    // Called when this screen is no longer the current screen for a Game.
    @Override
    public void hide() {
        dispose();
    }

    // Called when the Application is destroyed.
    @Override
    public void dispose() {
        stage.dispose();
    }
}

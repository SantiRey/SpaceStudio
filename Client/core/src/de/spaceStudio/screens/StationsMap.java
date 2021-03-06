package de.spaceStudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import de.spaceStudio.MainClient;
import de.spaceStudio.client.util.Global;
import de.spaceStudio.client.util.RequestUtils;
import de.spaceStudio.server.model.*;
import de.spaceStudio.service.Jumpservices;
import thirdParties.GifDecoder;

import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static de.spaceStudio.client.util.Global.multiPlayerSessionID;
import static de.spaceStudio.client.util.RequestUtils.setupRequest;
import static de.spaceStudio.client.util.Global.stationListU2;

public class StationsMap extends BaseScreen {

    private final static Logger LOG = Logger.getLogger(StationsMap.class.getName());
    private static final int POSX = 100;
    private static final int POSY = 200;
    private static final int PLANET_SIZEX = 100;
    private static final int PLANET_SIZEY = 100;
    final TextArea textAreaUN, textAreaVIS, textAreaShop;
    private final Stage stage;
    private final Skin skin;
    private final Texture background;
    private final Viewport viewport;
    private final MainClient mainClient;
    private final String unvisited = "unvisited planet";
    private final String visited = "visited planet";
    private final String shopText = "Shopping mall";
    private final Jumpservices jumpservices = new Jumpservices();
    private final StopAbstract currentStop = Global.planet1;
    //
    private final boolean isGameSaved = false;
    Animation<TextureRegion> start_ship;
    boolean isLast, test;
    List<Pair> coord = new ArrayList<>();
    private ImageButton planet1ImgBTN, planet2ImgBTN, planet3ImgBTN, planet4ImgBTN, planet5ImageBTN, planet9ImageBTN;
    private ImageButton startPoint;
    private ImageButton shopImg;
    private boolean isPlanet;
    private int counter = 0;
    private float state = 0.0f;
    private ShipSelectScreen shipSelectScreen;
    //
    private Boolean control = false;
    private TextButton saveGameButton;
    private List<Ship> shipList = new ArrayList<>();
    private Sound click;
    private float xShip = 240;
    private float yShip = 150;
    private boolean killTimer = false;


    public StationsMap(final MainClient game) {
        super(game);
        this.mainClient = game;
        viewport = new FitViewport(BaseScreen.WIDTH, BaseScreen.HEIGHT);
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        background = new Texture(Gdx.files.internal("Client/core/assets/data/maps/sky-map.jpg"));

        start_ship = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("Client/core/assets/data/gifs/ZDci.gif").read());
        final Drawable drawable_station_unvisited = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/data/stations/unvisited-removebg-preview.png")));
        final Drawable drawable_station_visited = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/data/stations/visited-removebg-preview.png")));
        final Drawable shopStationIcon = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/data/stations/shopping.png")));
        final Drawable planet_9 = new TextureRegionDrawable(new Texture(Gdx.files.internal("Client/core/assets/data/stations/planet9.png")));


        coord.add(new Pair(160f, 200f));  // Start Point
        coord.add(new Pair(250f, 700f));  // Planet 1
        coord.add(new Pair(500f, 550f));  // Planet 2
        coord.add(new Pair(600f, 800f));  // Planet 3
        coord.add(new Pair(900f, 550f));  // Planet 4
        coord.add(new Pair(1200f, 700f));  // Planet 5
        coord.add(new Pair(1200f, 800f));
        coord.add(new Pair(1050f,470f)); // planet 9

        textAreaUN = new TextArea(unvisited, skin);
        textAreaVIS = new TextArea(visited, skin);
        textAreaShop = new TextArea(shopText, skin);

        if (hasPlayerVisitedStation(Global.planet1)) {
            planet1(drawable_station_visited, textAreaVIS);
        } else {
            planet1(drawable_station_unvisited, textAreaUN);
        }
        if (hasPlayerVisitedStation(Global.planet2)) {
            planet2(drawable_station_visited, textAreaVIS);
        } else {
            planet2(drawable_station_unvisited, textAreaUN);
        }
        if (hasPlayerVisitedStation(Global.planet3)) {
            planet3(drawable_station_visited, textAreaVIS);
        } else {
            planet3(drawable_station_unvisited, textAreaUN);
        }
        if (hasPlayerVisitedStation(Global.planet4)) {
            planet4(drawable_station_visited, textAreaVIS);
        } else {
            planet4(drawable_station_unvisited, textAreaUN);
        }
        if (hasPlayerVisitedStation(Global.planet5)) {
            planet5(drawable_station_visited, textAreaVIS);
        } else {
            planet5(drawable_station_unvisited, textAreaUN);
        }
        planet9(planet_9, textAreaUN);

        shopStation(shopStationIcon);
        setStartPoint(drawable_station_unvisited);


        if (!Global.IS_SINGLE_PLAYER) {
            RequestUtils.hasLanded(Global.currentPlayer);
            Global.multiPlayerGameStarted = true;
        }

        stage.addActor(planet1ImgBTN);
        stage.addActor(planet2ImgBTN);
        stage.addActor(planet3ImgBTN);
        stage.addActor(planet5ImageBTN);

        if (!Global.ISEASY) {
            stage.addActor(planet5ImageBTN);
            stage.addActor(planet4ImgBTN);
        }
        if (!Global.IS_SINGLE_PLAYER) {
            stage.addActor(planet9ImageBTN);
        }
        stage.addActor(startPoint);
        stage.addActor(shopImg);

    }

    private void shopStation(Drawable shopStationIcon) {

        shopImg = new ImageButton((shopStationIcon));
        shopImg.setPosition(400, 900);
        shopImg.setSize(PLANET_SIZEX, PLANET_SIZEX);
        hoverListener(shopImg, textAreaShop);
        shopImg.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Global.currentStop = Global.station1;
                isPlanet = false;

                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {

                        if (Objects.equals(obj.toString(), "true")) {
                            counter++;
                            hoverListener(shopImg, textAreaShop);
                            jumpService(Global.station1);
                        }
                    }
                };

                actionDialog(dialog, "Shopping Mall --> Lets shop like there's no tomorrow!!\n"
                        + "moves in to the mall\n" + "Are you sure you want to jump there");

            }
        });


    }

    private void setStartPoint(Drawable drawable_station_unvisited) {
        startPoint = new ImageButton((drawable_station_unvisited));
        startPoint.setPosition(coord.get(0).getLeft(), coord.get(0).getRight());  //hikeButton is an ImageButton

        startPoint.setSize(PLANET_SIZEX, PLANET_SIZEY);
        startPoint.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final Dialog dialog = new Dialog("Start Point", skin, "dialog") {
                    public void result(Object obj) {
                        if (Objects.equals(obj.toString(), "true")) ;

                    }
                };
                dialog.text("Here is the start point");

                dialog.key(Input.Keys.ENTER, true);
                dialog.key(Input.Keys.ESCAPE, false);
                dialog.show(stage);

            }
        });

    }


    public boolean hasPlayerVisitedStation(StopAbstract s) {
        de.spaceStudio.server.model.Actor p = Global.combatActors.get(Global.currentPlayer.getId());
        LOG.info("::::::::::::::: " + s.getName() );

        for (GameRound g :
                Global.playerRounds) {
            System.out.println(g.getCurrentStop().getName());
            if (g.getCurrentStop().getName().equals(s.getName())) {
                System.out.println("true" + "****************************");
                return true;
            }
        }
        return false;
    }

    private void planet1(Drawable drawable, TextArea textArea) {
        planet1ImgBTN = new ImageButton((drawable));
        planet1ImgBTN.setPosition(coord.get(1).getLeft(), coord.get(1).getRight());
        planet1ImgBTN.setSize(PLANET_SIZEX, PLANET_SIZEY);


        hoverListener(planet1ImgBTN, textArea);
        final Planet planet = Global.planet1;
        planet1ImgBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Global.currentStop = Global.planet1;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {
                        if (Objects.equals(obj.toString(), "true")) {
                            hoverListener(planet1ImgBTN, textAreaVIS);
                            Global.currentStopNumber = 1;
                            Global.VISITED_PLANET_COUNTER++;
                            jumpService(planet);
                        }
                    }
                };
                actionDialog(dialog, "On this planet live very dangerous Dragons\n"  +
                        "Let's figure out, whether they are at home?\n"+
                        "Wanna  feel your heartbeat?,then keep on travelling...");
            }
        });

    }

    private void planet2(Drawable drawable, TextArea textArea) {
        planet2ImgBTN = new ImageButton((drawable));
        planet2ImgBTN.setPosition(coord.get(2).getLeft(), coord.get(2).getRight());
        planet2ImgBTN.setSize(PLANET_SIZEX, PLANET_SIZEX);
        hoverListener(planet2ImgBTN, textArea);
        final Planet planet = Global.planet2;
        planet2ImgBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPlanet = true;
                Global.currentStop = Global.planet2;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {

                        if (Objects.equals(obj.toString(), "true")) {
                            counter++;
                            hoverListener(planet2ImgBTN, textAreaVIS);
                            Global.currentStopNumber = 2;
                            Global.VISITED_PLANET_COUNTER++;

                            jumpService(planet);
                        }
                    }
                };

                actionDialog(dialog, "This planet belongs to Clara.\n"
                        + " She is a very dangerous Queen\n" +
                        " The Queen will destroy every single visitor\n" +
                        " Be careful!!!\n" +
                        "Or proof your encourage\n" + "Are you sure you want to jump there?");

            }
        });


    }


    private void planet3(Drawable drawable, TextArea textArea) {
        planet3ImgBTN = new ImageButton((drawable));
        planet3ImgBTN.setPosition(coord.get(3).getLeft(), coord.get(3).getRight());  //hikeButton is an ImageButton
        planet3ImgBTN.setSize(PLANET_SIZEX, PLANET_SIZEY);
        hoverListener(planet3ImgBTN, textArea);
        planet3ImgBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPlanet = true;
                System.out.println(counter+"::::::::::::::..");
                Global.currentStop = Global.planet3;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {
                        if (Objects.equals(obj.toString(), "true")) {

                            hoverListener(planet3ImgBTN, textAreaVIS);
                            Global.currentStopNumber = 3;
                            Global.VISITED_PLANET_COUNTER++;

                            jumpService(Global.planet3);

                        }

                    }
                };
                actionDialog(dialog, "This planet is so foggy, you never know what will occur\n" +
                        " Are you ready to take a risk?\n" +
                        "move to unknown adventure\n" +
                        "Keep going?");

            }
        });


    }


    private void planet4(Drawable drawable, TextArea textArea) {
        isPlanet = true;
        planet4ImgBTN = new ImageButton((drawable));
        planet4ImgBTN.setPosition(coord.get(4).getLeft(), coord.get(4).getRight());
        planet4ImgBTN.setSize(PLANET_SIZEX, PLANET_SIZEX);
        hoverListener(planet4ImgBTN, textArea);
        planet4ImgBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPlanet = true;
                Global.currentStop = Global.planet4;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {
                        if (Objects.equals(obj.toString(), "true")) {
                            hoverListener(planet4ImgBTN, textAreaVIS);
                            Global.currentStopNumber = 4;
                            Global.VISITED_PLANET_COUNTER++;

                            jumpService(Global.planet4);

                        }
                    }
                };
                actionDialog(dialog, "This planet is fucking GEIL.\n" +
                        " It perfect to do a sightseeing tour \n" +
                        " Don't give a shit on unexpected events\n" +
                        "\nAgree?");

            }
        });
    }

    private void planet5(Drawable drawable, TextArea textArea) {
        isPlanet = true;
        planet5ImageBTN = new ImageButton((drawable));
        planet5ImageBTN.setPosition(coord.get(5).getLeft(), coord.get(5).getRight());
        planet5ImageBTN.setSize(PLANET_SIZEX, PLANET_SIZEX);
        hoverListener(planet5ImageBTN, textArea);
        planet5ImageBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Global.currentStop = Global.planet5;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {
                    public void result(Object obj) {

                        if (Objects.equals(obj.toString(), "true")) {
                            isLast = true;
                        }

                    }
                };
                if (Global.VISITED_PLANET_COUNTER >= 2) {
                    dialog.text("You are allow to travel last planet");
                    dialog.button("JUMP", true);
                    dialog.key(Input.Keys.ENTER, true);
                    hoverListener(planet5ImageBTN, textAreaVIS);
                    Global.currentStopNumber = 5;
                    jumpService(Global.planet5);

                } else {
                    dialog.text("Before you travel the HELL, you have to visit other planets. Muhahahahaha");
                    dialog.button("BACK", false);
                    dialog.key(Input.Keys.ESCAPE, false);
                }

                //actionDialog(dialog, "?");
                dialog.show(stage);

            }
        });

    }
    private void planet9(Drawable drawable, TextArea textArea) {
        isPlanet = true;
        planet9ImageBTN = new ImageButton((drawable));
        planet9ImageBTN.setPosition(coord.get(7).getLeft(), coord.get(7).getRight());
        planet9ImageBTN.setSize(PLANET_SIZEX, PLANET_SIZEX);
        hoverListener(planet9ImageBTN, textArea);
        planet9ImageBTN.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Global.currentStop = Global.planet9;
                final Dialog dialog = new Dialog("Information", skin, "dialog") {};
                    dialog.text("You have now challenged the other Player");
                    dialog.button("Start Fight", true);
                    dialog.key(Input.Keys.ENTER, true);
                    hoverListener(planet9ImageBTN, textAreaVIS);
                    Global.loadingFightLocation = true;
                    RequestUtils.startFightOnline();

                //actionDialog(dialog, "?");
                dialog.show(stage);

            }
        });

    }
    /**
     * Make the Player Jump to abstract Stop
     * It Places in as the only Ship in set Ships
     *
     * @param stopAbstract where one Goes
     */
    private void jumpService(StopAbstract stopAbstract) {
        stopAbstract.setShips(List.of(Global.currentShipPlayer));
        ArrayList<StopAbstract> toChange = new ArrayList<>();
        toChange.add(currentStop);
        toChange.add(stopAbstract);
        if (shipList.isEmpty()) {
            makeJumpRequest(toChange, Net.HttpMethods.POST);
        }
    }

    public void makeJumpRequest(Object requestObject, String method) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOG.info("failed to serialise json");
        }
        final String url = Global.SERVER_URL + Global.MAKEJUMP_CREATION_ENDPOINT;
        final Net.HttpRequest request = setupRequest(url, requestJson, method);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    LOG.info("Request Failed");
                }
                LOG.info("statusCode of the Jump: " + statusCode);
                String shipsList = httpResponse.getResultAsString();
                try {
                    shipList = objectMapper.readValue(shipsList, new TypeReference<List<Ship>>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                RequestUtils.findGameRoundsByActor(Global.currentPlayer);
                // Multiplayer Step 1
                Global.loadingFightLocation =  true;

            }

            public void failed(Throwable t) {
                LOG.info("Request Failed Completely");
            }

            @Override
            public void cancelled() {
                LOG.info("request cancelled");
            }
        });
    }


    private void actionDialog(Dialog dialog, String action) {
        dialog.text(action);
        dialog.button("JUMP", true);
        dialog.button("BACK", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
        dialog.show(stage);
    }


    private void saveMessageDialog(Dialog dialog, String action) {
        dialog.text(action);
        dialog.button("OK", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
        click.play();
        dialog.show(stage);
    }


    private void hoverListener(final ImageButton img, final TextArea textArea) {
        img.addListener(new HoverListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                img.addActor(textArea);

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                textArea.remove();

            }
        });
    }

    public void renderSaveGameButton() {
        saveGameButton = new TextButton("Save game", skin);
        saveGameButton.setTransform(true);
        saveGameButton.setScaleX(1.8f);
        saveGameButton.setScaleY(1.5f);
        saveGameButton.setPosition(BaseScreen.WIDTH - 250, BaseScreen.HEIGHT - 180);
        saveGameButton.getLabel().setColor(Color.WHITE);
        saveGameButton.getLabel().setFontScale(1.25f, 1.25f);
        saveGameButton.setSize(90, 50);

        click = Gdx.audio.newSound(Gdx.files.internal("Client/core/assets/data/music/mouseclick.wav"));

        saveGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LOG.info("Button CLicked");
                click.play();
                Gson gson = new Gson();
                // Prepare singlePlayer to upload to server
                injectAllData();
                String requestBody = gson.toJson(Global.singlePlayerGame);
                final String url = Global.SERVER_URL + Global.PLAYER_SAVE_GAME + Global.currentPlayer.getName();
                Net.HttpRequest request = setupRequest(url, requestBody, Net.HttpMethods.POST);
                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        final Dialog dialog = new Dialog("Save game", skin, "dialog");
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        String responseJson = httpResponse.getResultAsString();
                        if (responseJson.equals("202 ACCEPTED")) {
                            LOG.info("Success save game " + statusCode);
                            saveMessageDialog(dialog, " Saving Game was Successful ");
                        } else {
                            LOG.info("Error saving game");
                            saveMessageDialog(dialog, " Saving Game was not Successful ");
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                    }

                    @Override
                    public void cancelled() {
                    }
                });


            }
        });
        stage.addActor(saveGameButton);
    }

    @Override
    public void show() {
        super.show();
        if (Global.IS_SINGLE_PLAYER) {
            renderSaveGameButton();
        } else {
        scheduleLobby();
        }

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        state += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.01f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, BaseScreen.WIDTH, BaseScreen.HEIGHT);

        if (Global.isOnlineFight) {
            killTimer = true;
            game.setScreen(new TravelScreen(game));
        }

        xShip = coord.get(Global.currentStopNumber).getLeft();
        yShip = coord.get(Global.currentStopNumber).getRight();
        stage.getBatch().draw(start_ship.getKeyFrame(state), xShip, yShip, 150, 150);
        Gdx.input.setInputProcessor(stage);
        stage.getBatch().end();
        stage.act();
        stage.draw();
        if (!shipList.isEmpty() && !control) {
            try {
                Global.currentShipPlayer = shipList.get(1);
                Global.currentShipGegner = shipList.get(0);
                Global.currentGegner = Global.currentShipGegner.getOwner();
            } catch (Exception e) {
                Global.currentShipPlayer = shipList.get(0);
                Global.currentShipGegner = null;
            }
            control = true;

            if (isPlanet) {
                mainClient.setScreen(new TravelScreen(game));
            } else {
                game.setScreen(new ShopScreen(game));
            }
        }


    }


    /**
     * Ask server every 5 seconds
     */
    private void scheduleLobby() {
        Timer schedule = new Timer();
        schedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (killTimer) {
                    schedule.cancel();
                    schedule.purge();
                    LOG.info("Timer killed");
                } else {
                    LOG.info("Fetching data from server...");
                    LOG.info(multiPlayerSessionID);

                    // Multiplayer Step 2. Can I Land
                    RequestUtils.hasFightStarted();

                }
            }
        }, 1000, 5000);
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);

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
        stage.dispose();
        skin.dispose();
        background.dispose();
        click.dispose();
    }

    /**
     *
     */
    public static void injectAllData() {
        Global.singlePlayerGame.setPlayerShip(Global.currentShipPlayer);
        Global.singlePlayerGame.setLastScreen("MAP");
        Global.singlePlayerGame.setShipSection1(Global.section1);
        Global.singlePlayerGame.setShipSection2(Global.section2);
        Global.singlePlayerGame.setShipSection3(Global.section3);
        Global.singlePlayerGame.setShipSection4(Global.section4);
        Global.singlePlayerGame.setShipSection5(Global.section5);
        Global.singlePlayerGame.setShipSection6(Global.section6);
        Global.singlePlayerGame.setStationListU2(stationListU2);
        Global.singlePlayerGame.setPlanet1(Global.planet1);
        Global.singlePlayerGame.setPlanet2(Global.planet2);
        Global.singlePlayerGame.setPlanet3(Global.planet3);
        Global.singlePlayerGame.setPlanet4(Global.planet4);
        Global.singlePlayerGame.setPlanet5(Global.planet5);
        Global.singlePlayerGame.setPlanet6(Global.planet6);
        Global.singlePlayerGame.setGameRounds(Global.playerRounds);
    }


}

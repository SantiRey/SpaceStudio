package de.spaceStudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import de.spaceStudio.MainClient;
import de.spaceStudio.client.util.Global;
import de.spaceStudio.client.util.RequestUtils;
import de.spaceStudio.server.model.CrewMember;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.server.model.Weapon;

import java.util.Random;


public class StopScreen extends ScreenAdapter {
    private final int dammagePrice = 5;
    private final int coolDownPrice = 4;
    private final int accuracyPrice = 6;
    MainClient game;
    boolean enemyNearBy = Global.currentShipGegner != null;
    private Stage stage;
    private Skin skin;
    private Label statsLabel;

    public StopScreen(MainClient game) {
        super();
        this.game = game;
        RequestUtils.sectionsByShip(Global.currentShipPlayer);
        RequestUtils.sectionsByShip(Global.currentShipGegner);
        RequestUtils.weaponsByShip(Global.currentShipGegner);
        RequestUtils.weaponsByShip(Global.currentShipPlayer);
    }

    /**
     * Generate a random Number
     *
     * @param min lowest
     * @param max highest
     * @return a number inside the bounds
     */
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * Return the Text to the event which is executed prior
     *
     * @param event which is selected
     * @return what happens
     */
    String event_description(int event) {
        String result = "You go Blind";
        switch (event) {
            case 0:
                int life = Global.currentShipPlayer.getHp() + getRandomNumberInRange(1, 50);
                Global.currentShipPlayer.setHp(life);
                result = "You got lucky. You find dearly needed Spare Parts. Current HP = " + life;
                break;


            case 1:
                int n = 0;
                int weapon_dammage = Global.currentShipPlayer.getHp() + getRandomNumberInRange(1, 50);
                Weapon wepaon = Global.weaponListPlayer.get(n);
                wepaon.setDamage(weapon_dammage);
                Global.weaponListPlayer.set(n, wepaon);
                result = "You meet an abandoned Ship, you find a strange Weapon";
                break;

            case 2:
                int dammage = Global.currentShipPlayer.getHp() - getRandomNumberInRange(1, 50);
                Global.currentShipPlayer.setHp(dammage);
                result = "Just when you wanted to Dock at the Station you get hit by a Comet";
                // Loose live
                break;

            case 3:
                int shield = Global.currentShipPlayer.getShield() + getRandomNumberInRange(1, 50);
                Global.currentShipPlayer.setShield(shield);
                result = "At the abandoned Space Dock there is a Big Box. \n It contains an extra Shield\nShield = " + shield;
                break;

            case 4:
                int shieldDammage = Global.currentShipPlayer.getShield() - getRandomNumberInRange(1, 50);
                Global.currentShipPlayer.setShield(shieldDammage);
                result = "You approach the Station. \n A bomb explodes directly next to the Cockpit. \nShield = " + shieldDammage;
                break;

            case 5:
                int health = getRandomNumberInRange(10, 100);
                CrewMember c = new CrewMember();
                c.setHealth(health);
                //c.getImg() // FIXME which List
                c.setName("Hubert"); // TODO Liam add faker
                c.setCurrentSection(Global.sectionsPlayerList.get(0));
                // FIXME update Backend
                break;

            default:
                result = "Nothing happens. You glance at the vast expanse of Space";
                break;
        }


        return result;
    }


    String stop_descripton(int event) {
        String result;
        switch (event) {
            case 0:
                result = "In front of you there is just baren Landscape. \n Has there been any Life here?";
                break;

            case 1:
                result = "Before you lies a Blue Laggon, It looks like an oasis";
                break;

            case 2:
                result = "Your emergency Beeper goes of... There is a Ship wreck nearby";
                break;

            default:
                result = "Nothing is here, just the existential void of being";
                break;
        }


        return result;
    }

    private String getStats(Ship s) {
        StringBuilder sb = new StringBuilder();

        sb.append("Life :" + s.getHp() + "\n");
        sb.append("Shield :" + s.getShield() + "\n");
        sb.append("Money: " + s.getMoney() + "\n");
        sb.append("Power: " + s.getPower() + "\n");

        return sb.toString();
    }


    /**
     * Random events decide, what will haben by loading the event Methods  and setting global Values
     * A terminal loads a new Screen
     * Capitals are non Terminals, non Capitals are not
     * Leave = A
     * A =  | flee | fight | shop | upgrade
     * Flee = stationMap
     * Shop = enter | exit
     * Fight = enemy | noEnemy
     * Stay = stationMap
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));


        int row_height = Gdx.graphics.getWidth() / 12;
        int col_width = Gdx.graphics.getWidth() / 12;
        Label.LabelStyle label1Style = new Label.LabelStyle();

        BitmapFont myFont = new BitmapFont(Gdx.files.internal("bitmap/amble.fnt"));
        label1Style.font = myFont;
        label1Style.fontColor = Color.RED;

        statsLabel = new Label(getStats(Global.currentShipPlayer), label1Style);
        statsLabel.setSize(Gdx.graphics.getWidth(), row_height);
        statsLabel.setPosition(50, Gdx.graphics.getHeight() - row_height * 6);
        statsLabel.setAlignment(Align.topLeft);


        final int number = getRandomNumberInRange(0, 3);


        new Dialog("You have a arrived at a new Stop", skin) {

            {
                text(stop_descripton(number));
                button("Leave", "false").getButtonTable().row();
                button("Explore", "true");
            }

            @Override
            protected void result(final Object object) {
                if (object.toString() == "true") {
                    final int event_number = getRandomNumberInRange(0, 3);
                    new Dialog("After a while:", skin) {

                        {
                            // TODO UNTIL SERVER VALIDATION THIS NUMBER WILL BE SIX-> DEFAULT
                            String outcome = event_description(3);
                            text(outcome);
                            button("Flee", 1l);
                            button("Fight", 2l);
                            button("Shop", 3l);
                            button("Upgrade", 4l);

                        }

                        @Override
                        protected void result(Object object) {

                            if (object.equals(3l)) {
                                new Dialog("Go to Shop", skin) {

                                    {
                                        text("Do you want to go to the Shop");
                                        button("Enter Shop", 1l).getButtonTable().row();
                                        button("NO", 0l);

                                    }

                                    @Override
                                    protected void result(Object object) {
                                        if (object.equals("FIXME")) {
                                            //   game.setScreen(new (...); // FIXME go to Shop
                                        }
                                    }
                                }.show(stage);

                            } else if (object.equals(2l)) {
                                // TODO Set FightScreen
                                if (!enemyNearBy) { // FIXME no enemy
                                    // IF there is an enemy
                                    new Dialog("On the Way Back you Stop...", skin) {

                                        {
                                            text("It has been a long Day You are Tired");
                                            button("View Map", 1l).getButtonTable().row();

                                        }

                                        @Override
                                        protected void result(Object object) {
                                            game.setScreen(new StationsMap(game));
                                        }

                                    }.show(stage);

                                } else {
                                    new Dialog("Enemy Ship approaches", skin) {

                                        {
                                            text("Your radar has picked up an unknown ship on its radar");
                                            button("Fight");
                                        }

                                        @Override
                                        protected void result(Object object) {
                                            game.setScreen(new CombatScreen(game));
                                        }
                                    }.show(stage);

                                }

                            } else if (object.equals(1l)) {
                                new Dialog("Go back to Map", skin) {

                                    {
                                        text("You have seen Enough. You go back to your Cockpit");
                                        button("View Map", 1l).getButtonTable().row();

                                    }

                                    @Override
                                    protected void result(Object object) {
                                        game.setScreen(new StationsMap(game));
                                    }
                                }.show(stage);
                            } else if (object.equals(4l)) {
                                new Dialog("What do you want to Upgrade?", skin) {

                                    {
                                        int price_dammage = Global.weaponListPlayer.size() * dammagePrice;
                                        int price_accuracy = Global.weaponListPlayer.size() * accuracyPrice;
                                        int price_coolDown = Global.weaponListPlayer.size() * coolDownPrice;
                                        int price_life = 20;
                                        int price_shield = 15;

                                        text("You can Upgrade all Weapons or one at a time");
                                        text("You have " + Global.currentShipPlayer.getMoney() +  "Money");

                                        if (Global.currentShipPlayer.getMoney() >= price_dammage) {
                                            button("+10% Dammage for  all (" + price_dammage + ")", 1l).getButtonTable().row();
                                        }
                                        if (Global.currentShipPlayer.getMoney() >= price_coolDown) {
                                            button("+10% Accuracy for  all (" + price_accuracy + ")", 2l).getButtonTable().row();
                                        }
                                        if (Global.currentShipPlayer.getMoney() >= price_accuracy) {
                                            button("-10% Cooldown for  all (" + price_coolDown + ")", 2l).getButtonTable().row();
                                        }
                                        if (Global.currentShipPlayer.getMoney() >= price_life) {
                                            button("+10% Life (" + price_life + ")", 4l).getButtonTable().row();
                                        }

                                        if (Global.currentShipPlayer.getMoney() >= price_shield) {
                                            button("+10 Shield  (" + price_shield + ")", 5l).getButtonTable().row();
                                        }

                                        button("Single Upgrade TODO", 0l);
                                    }

                                    @Override
                                    protected void result(Object object) {
                                        if (object.equals(0L)) {
                                            new Dialog("Go back to Map", skin) {

                                                {
                                                    text("You have seen Enough. You go back to your Cockpit");
                                                    button("View Map", 1l).getButtonTable().row();

                                                }

                                                @Override
                                                protected void result(Object object) {
                                                    game.setScreen(new StationsMap(game));
                                                }
                                            }.show(stage);
                                        } else  if (object.equals(1l)) {
                                            for (Weapon w :
                                                    Global.weaponListPlayer) {
                                                w.setDamage((int) (w.getDamage() + w.getDamage() * 0.1)); // FIXME if dammage is below 10 this will fail
                                            }
                                        } else if (object.equals(2l)) {
                                            for (Weapon w :
                                                    Global.weaponListPlayer) {
                                                w.setCoolDown((int) (w.getCoolDown() + w.getCoolDown() * 0.1)); // FIXME if dammage is below 10 this will fail
                                            }
                                        } else if (object.equals(3l)) {
                                            for (Weapon w :
                                                    Global.weaponListPlayer) {
                                                w.setCoolDown((int) (w.getCoolDown() + w.getCoolDown() * 0.1)); // FIXME if dammage is below 10 this will fail
                                            }
                                        } else if (object.equals(4l)) {
                                            Global.currentShipPlayer.setHp(Global.currentShipPlayer.getHp() + (int) (Global.currentShipPlayer.getHp() * 0.1f) );
                                        } else if (object.equals(5l)) {
                                            Global.currentShipPlayer.setShield(Global.currentShipPlayer.getShield() + 10);
                                        }


                                    // Where does this go
                                  new Dialog("Upgrade Succesfull", skin) {

                                        {
                                            text("You the eager Mechnanic has finished");
                                            button("View Map", 1l).getButtonTable().row();

                                        }

                                        @Override
                                        protected void result(Object object) {
                                            game.setScreen(new StationsMap(game));
                                        }
                                    }.show(stage);

                                  }
                                }.show(stage);
                            }

                        }
                    }.show(stage);
                } else if (object.toString() == "false") {
                    new Dialog("Go back to Map", skin) {

                        {
                            text("You have seen Enough. You go back to your Cockpit");
                            button("View Map", 1l).getButtonTable().row();

                        }

                        @Override
                        protected void result(Object object) {
                            game.setScreen(new StationsMap(game));
                        }
                    }.show(stage);
                }

            }
        }.show(stage);
        stage.addActor(statsLabel);
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0.23f, 0.34f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        statsLabel.setText(getStats(Global.currentShipPlayer));
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }
}

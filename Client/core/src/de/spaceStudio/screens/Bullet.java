package de.spaceStudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {

    public static final int SPEED = 550;
    public static int DEFAULT_X = 200;
    public static int DEFAULT_Y = 200;
    private static Texture bullet;
    private static Texture explosion = new Texture("Client/core/assets/combatAssets/explosion1_0024.png");
    ;
    public boolean remove = false;
    protected boolean isExploded;
    float x, y;

    public Bullet(float x, float y) {
        this.y = y;
        this.x = x;
        if (bullet == null) {
            bullet = new Texture("Client/core/assets/combatAssets/bullet.png");
        }
    }

    public void update(float deltaTime) {
        x += SPEED * deltaTime;
        if (x > Gdx.graphics.getWidth() - 600) {
            remove = true;
            System.out.println(String.format("Explosion at %s, %s", this.x, this.y));
            remove = true;
        }
    }

    public void updateTo(float deltaTime) {
        x -= SPEED * deltaTime;
        if (x > Gdx.graphics.getWidth() - 500) {
            remove = true;
        }
    }


    public void render(SpriteBatch batch) {

        batch.draw(bullet, x, y);


    }


}

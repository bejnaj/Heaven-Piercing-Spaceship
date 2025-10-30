package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {

    private float xSpeed;
    private float ySpeed;
    private boolean destroyed = false;
    private Sprite spr;

    public Bullet(float x, float y, float xSpeed, float ySpeed, Texture tx) {
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setOriginCenter();
        // opcional: ajustar tamaño si tu tx tiene distinto scale
        // spr.setSize(10, 10);

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;

        // rotación inicial: atan2(y,x) -> grados; ajustar para que 0º apunte hacia arriba si lo necesitas
        float ang = (float) Math.toDegrees(Math.atan2(ySpeed, xSpeed)); // 0 = right
        // convertimos a convención donde 0 = up (como usaste en la nave), restamos 90
        spr.setRotation(ang - 90f);
    }

    // actualización por frame (usa delta time para movimiento consistente)
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        spr.setPosition(spr.getX() + xSpeed * delta, spr.getY() + ySpeed * delta);

        if (spr.getX() + spr.getWidth() < 0 || spr.getX() > Gdx.graphics.getWidth()) {
            destroyed = true;
        }
        if (spr.getY() + spr.getHeight() < 0 || spr.getY() > Gdx.graphics.getHeight()) {
            destroyed = true;
        }
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public boolean checkCollision(Ball2 b2) {
        if (spr.getBoundingRectangle().overlaps(b2.getArea())) {
            this.destroyed = true;
            return true;
        }
        return false;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    // permite que otras clases ajusten la rotación visualmente
    public void setRotation(float degrees) {
        spr.setRotation(degrees);
    }

    // getters para posición y velocidad si los necesitas
    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }
}

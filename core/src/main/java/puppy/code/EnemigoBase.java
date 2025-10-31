package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EnemigoBase implements Atacable, Actualizable {
    protected int vidas;
    protected Sprite spr;
    protected int xSpeed;
    protected int ySpeed;
    protected boolean debil = false;
    public EnemigoBase(int vidas, Texture textura, int x, int y, int size, int xSpeed, int ySpeed) {
        this.vidas = vidas;
        this.spr = new Sprite(textura);
        this.spr.setSize(size, size);
        this.spr.setOriginCenter();

        // Validar que no quede fuera de pantalla
        if (x - size < 0) x += size;
        if (x + size > Gdx.graphics.getWidth()) x -= size;
        if (y - size < 0) y += size;
        if (y + size > Gdx.graphics.getHeight()) y -= size;

        this.spr.setPosition(x, y);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void update() {
        float x = spr.getX() + xSpeed;
        float y = spr.getY() + ySpeed;

        // Rebote en bordes
        if (x < 0 || x + spr.getWidth() > Gdx.graphics.getWidth()) xSpeed *= -1;
        if (y < 0 || y + spr.getHeight() > Gdx.graphics.getHeight()) ySpeed *= -1;

        spr.setPosition(spr.getX() + xSpeed, spr.getY() + ySpeed);
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }

    public void checkCollision(EnemigoBase otro) {
        if (spr.getBoundingRectangle().overlaps(otro.getArea())) {
            // Rebote simple
            if (xSpeed == 0) xSpeed += otro.xSpeed / 2;
            if (otro.xSpeed == 0) otro.xSpeed += xSpeed / 2;
            xSpeed = -xSpeed;
            otro.xSpeed = -otro.xSpeed;

            if (ySpeed == 0) ySpeed += otro.ySpeed / 2;
            if (otro.ySpeed == 0) otro.ySpeed += ySpeed / 2;
            ySpeed = -ySpeed;
            otro.ySpeed = -otro.ySpeed;
        }
    }

    @Override
    public void recibirDano(int cantidad) {
        vidas -= cantidad;
    }

    @Override
    public boolean estaDestruido() {
        return vidas <= 0;
    }

    @Override
    public abstract void actualizarSprite();

    // Getters y setters útiles
    public int getXSpeed() { return xSpeed; }
    public void setXSpeed(int xSpeed) { this.xSpeed = xSpeed; }

    public int getYSpeed() { return ySpeed; }
    public void setYSpeed(int ySpeed) { this.ySpeed = ySpeed; }

    public int getVidas() { return vidas; }
    public void setVidas(int v) { vidas = v; }

    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }
}

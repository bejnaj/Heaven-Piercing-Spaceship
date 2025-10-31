package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public abstract class NaveBase implements Conectable, Atacable, Actualizable {
    protected int vidas;
    protected Sprite spr;
    protected Texture texturaNave;
    protected Texture texturaBala;
    protected Sound sonidoHerido;
    protected Sound sonidoBala;

    protected boolean destruida = false;
    protected boolean herido = false;
    protected int tiempoHerido = 0;
    protected int tiempoHeridoMax = 50;

    protected float xVel = 0f;
    protected float yVel = 0f;
    protected float MAX_SPEED = 500f;
    protected float ACCELERATION = 1500f;
    protected float FRICTION = 1200f;

    protected float shootCooldown = 0f;
    protected float shootCooldownMax = 0.25f;

    protected int lastFacingDir = 1; // 0=up, 1=down, 2=left, 3=right

    public NaveBase(int vidas, Texture texturaNave, Texture texturaBala, Sound sonidoHerido, Sound sonidoBala, float x, float y) {
        this.vidas = vidas;
        this.texturaNave = texturaNave;
        this.texturaBala = texturaBala;
        this.sonidoHerido = sonidoHerido;
        this.sonidoBala = sonidoBala;

        this.spr = new Sprite(texturaNave);
        spr.setSize(45, 45);
        spr.setOriginCenter();
        spr.setPosition(x, y);
    }

    public void setPosition(float x, float y) {
        spr.setPosition(x, y);
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        float delta = Gdx.graphics.getDeltaTime();

        if (shootCooldown > 0f) shootCooldown -= delta;

        if (!herido) {
            // Movimiento con teclado
            float ax = 0f, ay = 0f;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) ax = -1f;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ax = 1f;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) ay = 1f;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) ay = -1f;

            if (ax != 0f && ay != 0f) {
                float inv = 1f / (float) Math.sqrt(2.0);
                ax *= inv;
                ay *= inv;
            }

            xVel += ax * ACCELERATION * delta;
            yVel += ay * ACCELERATION * delta;

            if (ax == 0f) xVel = aplicarFriccion(xVel, delta);
            if (ay == 0f) yVel = aplicarFriccion(yVel, delta);

            float speed = (float) Math.sqrt(xVel * xVel + yVel * yVel);
            if (speed > MAX_SPEED) {
                float scale = MAX_SPEED / speed;
                xVel *= scale;
                yVel *= scale;
            }

            float newX = spr.getX() + xVel * delta;
            float newY = spr.getY() + yVel * delta;

            newX = MathUtils.clamp(newX, 0, Gdx.graphics.getWidth() - spr.getWidth());
            newY = MathUtils.clamp(newY, 0, Gdx.graphics.getHeight() - spr.getHeight());

            spr.setPosition(newX, newY);
            actualizarRotacion();

            spr.draw(batch);

            // Disparo con SPACE
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && shootCooldown <= 0f) {
                Bullet bala = disparar();
                if (bala != null) {
                    juego.agregarBala(bala);
                    if (sonidoBala != null) sonidoBala.play();
                }
            }

        } else {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(spr.getX());
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }

    private float aplicarFriccion(float vel, float delta) {
        if (vel > 0f) {
            vel -= FRICTION * delta;
            if (vel < 0f) vel = 0f;
        } else if (vel < 0f) {
            vel += FRICTION * delta;
            if (vel > 0f) vel = 0f;
        }
        return vel;
    }

    private void actualizarRotacion() {
        float vx = xVel;
        float vy = yVel;
        float eps = 1f;
        if (Math.abs(vx) > eps || Math.abs(vy) > eps) {
            float ang = (float) Math.toDegrees(Math.atan2(vy, vx));
            spr.setRotation(ang - 90f);
            if (Math.abs(vx) > Math.abs(vy)) {
                lastFacingDir = vx > 0 ? 3 : 2;
            } else {
                lastFacingDir = vy > 0 ? 0 : 1;
            }
        } else {
            switch (lastFacingDir) {
                case 0: spr.setRotation(0f); break;
                case 1: spr.setRotation(180f); break;
                case 2: spr.setRotation(90f); break;
                case 3: spr.setRotation(-90f); break;
            }
        }
    }

    public Bullet disparar() {
        float rot = spr.getRotation();
        float ang = rot + 90f;
        double rad = Math.toRadians(ang);
        float bulletSpeed = 400f;
        float vx = (float) Math.cos(rad) * bulletSpeed;
        float vy = (float) Math.sin(rad) * bulletSpeed;

        float bx = spr.getX() + spr.getWidth() / 2 - 5;
        float by = spr.getY() + spr.getHeight() / 2 - 5;

        Bullet bala = new Bullet(bx, by, vx, vy, texturaBala);
        bala.setRotation(rot);
        shootCooldown = shootCooldownMax;
        return bala;
    }

    public boolean checkCollision(EnemigoBase enemigo) {
        if (!herido && enemigo.getArea().overlaps(spr.getBoundingRectangle())) {
            if (xVel == 0) xVel += enemigo.getXSpeed() / 2f;
            if (enemigo.getXSpeed() == 0) enemigo.setXSpeed(enemigo.getXSpeed() + (int) xVel / 2);
            xVel = -xVel;
            enemigo.setXSpeed(-enemigo.getXSpeed());

            if (yVel == 0) yVel += enemigo.getYSpeed() / 2f;
            if (enemigo.getYSpeed() == 0) enemigo.setYSpeed(enemigo.getYSpeed() + (int) yVel / 2);
            yVel = -yVel;
            enemigo.setYSpeed(-enemigo.getYSpeed());

            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            if (sonidoHerido != null) sonidoHerido.play();
            if (vidas <= 0) destruida = true;
            return true;
        }
        return false;
    }

    @Override
    public void recibirDano(int cantidad) {
        vidas -= cantidad;
    }

    @Override
    public boolean estaDestruido() {
        return !herido && destruida;
    }

    @Override
    public void conectar(Conectable otro) {
        // lógica por defecto
    }

    @Override
    public abstract void actualizarSprite();

    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public int getX() { return (int) spr.getX(); }
    public int getY() { return (int) spr.getY(); }
    public void setVidas(int v) { vidas = v; }
}

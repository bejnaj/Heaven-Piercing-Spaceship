package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Nave4 {
    private float xVel = 0f;
    private float yVel = 0f;
    private static final float MAX_SPEED = 500f;
    private static final float ACCELERATION = 1500f;
    private static final float FRICTION = 1200f;

    private boolean destruida = false;
    private int vidas = 3;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;      // textura única para bala
    private Texture txNave;      // textura única para nave

    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido = 0;

    private float shootCooldown = 0f;
    private final float shootCooldownMax = 0.25f;
    private int lastFacingDir = 1; // dirección visual actual (por defecto DOWN)

    public Nave4(int x, int y, Texture txNave, Sound soundChoque, Texture txBala, Sound soundBala) {
        this.txNave = txNave;
        this.txBala = txBala;
        this.sonidoHerido = soundChoque;
        this.soundBala = soundBala;

        this.spr = new Sprite(txNave);
        spr.setPosition(x, y);
        spr.setSize(45, 45);
        spr.setOriginCenter();            // importante para rotar alrededor del centro
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        float delta = Gdx.graphics.getDeltaTime();
        float x = spr.getX();
        float y = spr.getY();

        if (shootCooldown > 0f) shootCooldown -= delta;

        if (!herido) {
            // MOVIMIENTO
            boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);

            float ax = 0f;
            float ay = 0f;
            if (left && !right) ax = -1f;
            if (right && !left) ax = 1f;
            if (up && !down) ay = 1f;
            if (down && !up) ay = -1f;

            if (ax != 0f && ay != 0f) {
                float inv = 1f / (float) Math.sqrt(2.0);
                ax *= inv;
                ay *= inv;
            }

            xVel += ax * ACCELERATION * delta;
            yVel += ay * ACCELERATION * delta;

            // fricción
            if (ax == 0f) {
                if (xVel > 0f) { xVel -= FRICTION * delta; if (xVel < 0f) xVel = 0f; }
                else if (xVel < 0f) { xVel += FRICTION * delta; if (xVel > 0f) xVel = 0f; }
            }
            if (ay == 0f) {
                if (yVel > 0f) { yVel -= FRICTION * delta; if (yVel < 0f) yVel = 0f; }
                else if (yVel < 0f) { yVel += FRICTION * delta; if (yVel > 0f) yVel = 0f; }
            }

            // limitar velocidad
            float speed = (float) Math.sqrt(xVel * xVel + yVel * yVel);
            if (speed > MAX_SPEED) {
                float scale = MAX_SPEED / speed;
                xVel *= scale;
                yVel *= scale;
            }

            // posición
            float newX = spr.getX() + xVel * delta;
            float newY = spr.getY() + yVel * delta;

            // clamp pantalla
            if (newX < 0) newX = 0;
            if (newX + spr.getWidth() > Gdx.graphics.getWidth()) newX = Gdx.graphics.getWidth() - spr.getWidth();
            if (newY < 0) newY = 0;
            if (newY + spr.getHeight() > Gdx.graphics.getHeight()) newY = Gdx.graphics.getHeight() - spr.getHeight();

            spr.setPosition(newX, newY);

            // actualizar rotación visual según velocidad (si está en movimiento)
            updateShipRotation();

            spr.draw(batch);

            // DISPARO: ahora con SPACE en la dirección que apunta la nave
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && shootCooldown <= 0f) {
                // obtener la rotación actual del sprite (0 = up, 90 = left, -90 = right, 180/-180 = down)
                float rot = spr.getRotation();
                // reconvertir a ángulo matemático desde +X: orig = rot + 90 (ver updateShipRotation)
                float origDeg = rot + 90f;
                double rad = Math.toRadians(origDeg);
                float bulletSpeed = 400f; // velocidad de la bala

                float vx = (float) Math.cos(rad) * bulletSpeed;
                float vy = (float) Math.sin(rad) * bulletSpeed;

                // posicionar la bala en el centro de la nave (ajustar según tamaño de la textura)
                float bx = spr.getX() + spr.getWidth() / 2 - 5;
                float by = spr.getY() + spr.getHeight() / 2 - 5;

                Bullet bala = new Bullet(bx, by, vx, vy, txBala);
                try { bala.setRotation(rot); } catch (Throwable ignored) {}
                juego.agregarBala(bala);
                if (soundBala != null) soundBala.play();
                shootCooldown = shootCooldownMax;
            }
        } else {
            // estado herido
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(x);
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }

    private void updateShipRotation() {
        // si la velocidad es significativa, usa la dirección de la velocidad
        float vx = xVel;
        float vy = yVel;
        float eps = 1f;
        if (Math.abs(vx) > eps || Math.abs(vy) > eps) {
            // calcular ángulo en grados: 0 = up, 90 = right, 180 = down, -90 = left
            float ang = (float) Math.toDegrees(Math.atan2(vy, vx)); // atan2(y,x) => 0° = +x (right)
            // convertimos para que 0 sea up:
            float angUpBased = ang - 90f;
            spr.setRotation(angUpBased);
            // actualizar lastFacingDir según el ángulo (opcional, útil si estás quieto)
            if (Math.abs(vx) > Math.abs(vy)) {
                lastFacingDir = vx > 0 ? 3 : 2;
            } else {
                lastFacingDir = vy > 0 ? 0 : 1;
            }
        } else {
            // si está casi quieto, mantener la última dirección visual
            switch (lastFacingDir) {
                case 0: spr.setRotation(0f); break;    // up
                case 1: spr.setRotation(180f); break;  // down
                case 2: spr.setRotation(90f); break;   // left (rotación visual elegida)
                case 3: spr.setRotation(-90f); break;  // right
            }
        }
    }

    public boolean checkCollision(Ball2 b) {
        if (!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            if (xVel == 0) xVel += b.getXSpeed() / 2;
            if (b.getXSpeed() == 0) b.setXSpeed(b.getXSpeed() + (int) xVel / 2);
            xVel = -xVel;
            b.setXSpeed(-b.getXSpeed());

            if (yVel == 0) yVel += b.getySpeed() / 2;
            if (b.getySpeed() == 0) b.setySpeed(b.getySpeed() + (int) yVel / 2);
            yVel = -yVel;
            b.setySpeed(-b.getySpeed());

            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            if (sonidoHerido != null) sonidoHerido.play();
            if (vidas <= 0) destruida = true;
            return true;
        }
        return false;
    }

    // getters/setters...
    public boolean estaDestruido() { return !herido && destruida; }
    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public int getX() { return (int) spr.getX(); }
    public int getY() { return (int) spr.getY(); }
    public void setVidas(int v) { vidas = v; }
}

package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public abstract class NaveBase implements Atacable, Actualizable {
    protected int vidas;
    protected Sprite spr;
    protected Texture texturaNave;
    protected Texture texturaNaveDebil;
    protected Texture texturaBala;
    protected Sound sonidoHerido;
    protected Sound sonidoBala;
    protected PantallaJuego juego;

    protected boolean transformada = false;
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

    protected int lastFacingDir = 1;

    public boolean estaTransformada() {
        return transformada;
    }

    public NaveBase(int vidas, Texture texturaNave, Texture texturaNaveDebil, Texture texturaBala, Sound sonidoHerido, Sound sonidoBala, float x, float y) {
        this.vidas = vidas;
        this.texturaNave = texturaNave;
        this.texturaNaveDebil = texturaNaveDebil;
        this.texturaBala = texturaBala;
        this.sonidoHerido = sonidoHerido;
        this.sonidoBala = sonidoBala;

        this.spr = new Sprite(texturaNave);
        spr.setSize(45, 45);
        spr.setOriginCenter();
        spr.setPosition(x, y);
    }

    public void setJuego(PantallaJuego juego) {
        this.juego = juego;
    }

    public void setPosition(float x, float y) {
        spr.setPosition(x, y);
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        float delta = Gdx.graphics.getDeltaTime();

        if (shootCooldown > 0f) shootCooldown -= delta;

        if (!herido) {
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

            // Aplicar textura según vida antes de dibujar
            aplicarTexturaSegunVida();

            spr.draw(batch);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && shootCooldown <= 0f) {
                Bullet bala = disparar();
                if (bala != null) {
                    juego.agregarBala(bala);
                    if (sonidoBala != null) sonidoBala.play();
                }
            }

        } else {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            // Aplicar textura también cuando está herido
            aplicarTexturaSegunVida();
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
                case 0:
                    spr.setRotation(0f);
                    break;
                case 1:
                    spr.setRotation(180f);
                    break;
                case 2:
                    spr.setRotation(90f);
                    break;
                case 3:
                    spr.setRotation(-90f);
                    break;
            }
        }
    }
    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }
    /**
     * Cambia la textura del sprite entre la normal y la débil según el porcentaje de vida.
     * Por defecto se usa <=25% para la versión débil.
     * Subclases pueden sobrescribir esta lógica si quieren otro umbral o efectos.
     */
    protected void aplicarTexturaSegunVida() {
        if (texturaNaveDebil == null || texturaNave == null) return;

        float porcentaje = (float) vidas / Math.max(1, getVidaMaximaEstimada());
        if (porcentaje <= 0.25f) {
            if (spr.getTexture() != texturaNaveDebil) {
                spr.setRegion(texturaNaveDebil);
            }
        } else {
            if (spr.getTexture() != texturaNave) {
                spr.setRegion(texturaNave);
            }
        }
    }

    /**
     * Método auxiliar para estimar vida máxima si no hay API explícita.
     * Puedes sobrescribirlo en subclases para retornar el valor correcto (por ejemplo vidaMaxima).
     */
    protected int getVidaMaximaEstimada() {
        // Por defecto asumimos 3 vidas si no se redefine en la subclase
        return Math.max(vidas, 10);
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
            // Nota: la llamada a conectar se aconseja hacer desde PantallaJuego antes de checkCollision
            if (estaTransformada()) {
                return true;
            }
            // Si no se transformó, aplicar daño
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
    public boolean conectar(Atacable otro) {
        if (otro instanceof EnemigoBase) {
            EnemigoBase enemigo = (EnemigoBase) otro;
            float porcentaje = (float) enemigo.getVidas() / Math.max(1, enemigo.getVidaMaxima());
            if (porcentaje <= 0.25f && juego != null) {
                if (enemigo instanceof EnemigoNormal) {
                    // Usar las texturas provistas por el juego para evitar recargas
                    Texture texNaveEN = juego.getTexturaNaveENormal();
                    Texture texNaveENDebil = juego.getTexturaNaveENormalDebil();
                    Texture texBalaEN = juego.getTexturaBalaENormal();
                    Sound sHerido = juego.getSonidoHerido();
                    Sound sBala = juego.getSonidoBala();

                    NaveBase nueva = new NaveENormal(
                        spr.getX(), spr.getY(),
                        texNaveEN, texNaveENDebil ,sHerido, texBalaEN, sBala
                    );
                    nueva.setVidas(this.vidas);
                    nueva.setJuego(juego);
                    juego.transformarEn(nueva);
                    this.transformada = true;
                    return true;
                }
            }
        }
        return false; // no se transformó
    }

    @Override
    public abstract void actualizarSprite();

    public boolean estaHerido() {
        return herido;
    }

    public int getVidas() {
        return vidas;
    }

    public int getX() {
        return (int) spr.getX();
    }

    public int getY() {
        return (int) spr.getY();
    }

    public void setVidas(int v) {
        vidas = v;
    }
}

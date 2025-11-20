package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EnemigoBase implements Atacable, Actualizable {
    protected int vidas;
    protected int vidaMaxima;
    protected Sprite spr;
    protected boolean debil = false;

    // PATRÓN STRATEGY
    protected EstrategiaMovimiento estrategia;

    public EnemigoBase(EstrategiaMovimiento estrategia, int vidas, Texture textura, int x, int y, int size) {
        this.vidas = vidas;
        this.vidaMaxima = vidas;
        this.estrategia = estrategia;

        this.spr = new Sprite(textura);
        this.spr.setSize(size, size);
        this.spr.setOriginCenter();
        this.spr.setPosition(x, y);
    }

    // PATRÓN TEMPLATE METHOD: Método 'final' que orquesta el update
    @Override
    public final void update() {
        // PASO 1: Mover usando la estrategia
        if (estrategia != null) {
            estrategia.mover(spr, Gdx.graphics.getDeltaTime());
        }

        // PASO 2: Actualizar visuales (implementado por los hijos)
        actualizarSprite();
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }

    // Colisión entre enemigos (rebote)
    public void checkCollision(EnemigoBase otro) {
        if (spr.getBoundingRectangle().overlaps(otro.getArea())) {
            // Usamos los getters/setters que delegan a la estrategia
            int miVX = this.getXSpeed();
            int miVY = this.getYSpeed();
            int otroVX = otro.getXSpeed();
            int otroVY = otro.getYSpeed();

            // Lógica de intercambio de velocidad (rebote simple)
            if (miVX == 0) miVX += otroVX / 2;
            if (otroVX == 0) otroVX += miVX / 2;

            // Invertir velocidades
            this.setXSpeed(-miVX);
            otro.setXSpeed(-otroVX);

            if (miVY == 0) miVY += otroVY / 2;
            if (otroVY == 0) otroVY += miVY / 2;

            // Invertir velocidades verticales
            this.setYSpeed(-miVY);
            otro.setYSpeed(-otroVY);
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

    @Override
    public boolean conectar(Atacable otro) {
        // Lógica por defecto
        return false;
    }

    // --- GETTERS Y SETTERS DELEGADOS A LA ESTRATEGIA ---
    // Estos métodos permiten que otras clases (como NaveBase) interactúen con la velocidad
    // aunque internamente sea manejada por la estrategia.

    public int getXSpeed() {
        if (estrategia instanceof MovimientoRebote) {
            return (int) ((MovimientoRebote) estrategia).getXSpeed();
        }
        return 0;
    }

    public int getYSpeed() {
        if (estrategia instanceof MovimientoRebote) {
            return (int) ((MovimientoRebote) estrategia).getYSpeed();
        }
        return 0;
    }

    public void setXSpeed(int x) {
        if (estrategia instanceof MovimientoRebote) {
            ((MovimientoRebote) estrategia).setXSpeed(x);
        }
    }

    public void setYSpeed(int y) {
        if (estrategia instanceof MovimientoRebote) {
            ((MovimientoRebote) estrategia).setYSpeed(y);
        }
    }

    // --- OTROS GETTERS ---

    public int getVidaMaxima() { return vidaMaxima; }
    public int getVidas() { return vidas; }
    public void setVidas(int v) { vidas = v; }

    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }
}

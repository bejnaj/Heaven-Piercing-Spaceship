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

    // Texturas guardadas en el padre para gestionar el daño automáticamente
    protected Texture texNormal;
    protected Texture texDebil;

    public EnemigoBase(EstrategiaMovimiento estrategia, int vidas, Texture tNormal, Texture tDebil, int x, int y, int size) {
        this.estrategia = estrategia;
        this.vidas = vidas;
        this.vidaMaxima = vidas;
        this.texNormal = tNormal;
        this.texDebil = tDebil;

        this.spr = new Sprite(tNormal);
        this.spr.setSize(size, size);
        this.spr.setOriginCenter();
        this.spr.setPosition(x, y);
    }

    // --- PATRÓN TEMPLATE METHOD ---
    // Este método es FINAL. Define el orden exacto de ejecución.
    @Override
    public final void update() {
        float delta = Gdx.graphics.getDeltaTime();

        // PASO 1: Moverse (Delegado a Strategy)
        if (estrategia != null) {
            estrategia.mover(spr, delta);
        }

        // PASO 2: Gestión visual de daño (Lógica común)
        gestionarCambioTextura();

        // PASO 3: Comportamiento único (Hook abstracto)
        realizarComportamientoEspecifico();
    }

    // Implementación del Paso 2 (Común para todos)
    private void gestionarCambioTextura() {
        float porcentaje = (float) vidas / vidaMaxima;
        if (porcentaje <= 0.25f && !debil) {
            if (texDebil != null) spr.setTexture(texDebil);
            debil = true;
        } else if (porcentaje > 0.25f && debil) {
            if (texNormal != null) spr.setTexture(texNormal);
            debil = false;
        }
    }

    // Declaración del Paso 3 (Cada hijo debe definir qué hace aquí)
    public abstract void realizarComportamientoEspecifico();

    // --- MÉTODOS COMUNES ---
    @Override
    public void draw(SpriteBatch batch) { spr.draw(batch); }
    @Override
    public Rectangle getArea() { return spr.getBoundingRectangle(); }
    @Override
    public void recibirDano(int cantidad) { vidas -= cantidad; }
    @Override
    public boolean estaDestruido() { return vidas <= 0; }

    // Getters delegados a Strategy
    public int getXSpeed() {
        if (estrategia instanceof MovimientoRebote) return (int)((MovimientoRebote)estrategia).getXSpeed();
        return 0;
    }
    public int getYSpeed() {
        if (estrategia instanceof MovimientoRebote) return (int)((MovimientoRebote)estrategia).getYSpeed();
        return 0;
    }
    public void setXSpeed(int x) {
        if (estrategia instanceof MovimientoRebote) ((MovimientoRebote)estrategia).setXSpeed(x);
    }
    public void setYSpeed(int y) {
        if (estrategia instanceof MovimientoRebote) ((MovimientoRebote)estrategia).setYSpeed(y);
    }

    // Getters simples
    public int getVidas() { return vidas; }
    public int getVidaMaxima() { return vidaMaxima; }
    public void setVidas(int v) { this.vidas = v; }
    public float getX() { return spr.getX(); }
    public float getY() { return spr.getY(); }

    @Override public void actualizarSprite() {} // Ya no se usa externamente, lo maneja el update
    @Override public boolean conectar(Atacable otro) { return false; }
}

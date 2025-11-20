package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class BalaBase {
    protected Sprite spr;
    protected int dano;          // Requisito: Daño variable
    protected boolean destroyed = false;
    protected EstrategiaMovimiento estrategia; // Requisito: Reutilizar movimiento

    /**
     * Constructor Maestro
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho (Requisito: Tamaño variable)
     * @param height Alto (Requisito: Tamaño variable)
     * @param dano Daño que causa al impactar
     * @param tx Textura de la bala
     * @param estrategia Cómo se mueve (Requisito: Reutilizar lógica)
     */
    public BalaBase(float x, float y, int width, int height, int dano, Texture tx, EstrategiaMovimiento estrategia) {
        this.spr = new Sprite(tx);
        this.spr.setPosition(x, y);
        this.spr.setSize(width, height); // Configuramos el tamaño
        this.spr.setOriginCenter();

        this.dano = dano;
        this.estrategia = estrategia;
    }

    // Template Method para el ciclo de vida
    public final void update() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Reutilizamos la lógica de movimiento de los enemigos
        if (estrategia != null) {
            estrategia.mover(spr, delta);
        }

        // 2. Verificación de bordes (común para todas las balas)
        if (spr.getY() > Gdx.graphics.getHeight() || spr.getY() < 0 ||
            spr.getX() > Gdx.graphics.getWidth() || spr.getX() < 0) {
            destroyed = true;
        }
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public boolean checkCollision(Atacable enemigo) {
        // Asumimos que EnemigoBase implementa Atacable y tiene getArea()
        // Si usas interfaces puras, necesitarás un método getArea() en la interfaz o cast
        if (spr.getBoundingRectangle().overlaps(((EnemigoBase)enemigo).getArea())) {
            enemigo.recibirDano(this.dano); // Aplicamos el daño variable
            this.destroyed = true;
            return true;
        }
        return false;
    }

    public boolean isDestroyed() { return destroyed; }
}

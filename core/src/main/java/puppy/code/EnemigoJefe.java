package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnemigoJefe extends EnemigoBase {
    private PantallaJuego juego;
    private FabricaEnemigos fabrica;

    private float tiempoSpawn = 0;
    private float intervaloSpawn = 3f;

    public EnemigoJefe(PantallaJuego juego, FabricaEnemigos fabrica, Texture tNormal, Texture tDebil, int x, int y) {
        super(new MovimientoEstatico(), 50, tNormal, tDebil, x, y, 300);
        this.juego = juego;
        this.fabrica = fabrica;
    }

    // AQUÍ IMPLEMENTAMOS EL PASO 3 DEL TEMPLATE METHOD
    @Override
    public void realizarComportamientoEspecifico() {
        // 1. Rotación visual (Efecto Boss)
        spr.rotate(15 * Gdx.graphics.getDeltaTime());

        // 2. Lógica de Invocación
        tiempoSpawn += Gdx.graphics.getDeltaTime();

        // Si está débil, invoca más rápido (Furia)
        float intervaloActual = debil ? 1.5f : 3f;

        if (tiempoSpawn >= intervaloActual) {
            invocarEsbirros();
            tiempoSpawn = 0;
        }
    }

    private void invocarEsbirros() {
        float spawnX = this.getX() + this.getArea().width / 2;
        float spawnY = this.getY() - 50;

        // El Jefe delega a la fábrica la creación del esbirro
        EnemigoBase esbirro = fabrica.crearEnemigoAleatorio(spawnX, spawnY);
        juego.agregarEnemigo(esbirro);

        // Si está en modo furia (débil), invoca ayuda extra
        if (debil) {
            EnemigoBase refuerzo = fabrica.crearEnemigoAleatorio(spawnX - 50, spawnY - 20);
            juego.agregarEnemigo(refuerzo);
        }
    }
}

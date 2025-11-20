package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class FabricaNivelDificil implements FabricaEnemigos {
    // AGREGA texChicoDebil AQUÍ
    private Texture texNormal, texNormalDebil, texGrande, texGrandeDebil, texChico, texChicoDebil;

    public FabricaNivelDificil(Texture tN, Texture tND, Texture tG, Texture tGD) {
        this.texNormal = tN;
        this.texNormalDebil = tND;
        this.texGrande = tG;
        this.texGrandeDebil = tGD;

        // Reutilizamos las texturas del enemigo normal para el chico
        this.texChico = tN;
        this.texChicoDebil = tND;
    }

    // Versiones más rápidas para niveles avanzados
    @Override
    public EnemigoBase crearEnemigoChico(float x, float y) {
        // Ahora texChicoDebil ya existe y no dará error
        return new EnemigoChico(new MovimientoRebote(500, 500), texChico, texChicoDebil, (int)x, (int)y);
    }

    @Override
    public EnemigoBase crearEnemigoNormal(float x, float y) {
        return new EnemigoNormal(new MovimientoRebote(250, 250), texNormal, texNormalDebil, (int)x, (int)y);
    }

    @Override
    public EnemigoBase crearEnemigoGrande(float x, float y) {
        return new EnemigoGrande(new MovimientoRebote(150, 150), texGrande, texGrandeDebil, (int)x, (int)y);
    }

    // --- LÓGICA ALEATORIA (Usada en Nivel 4, 5 y por el Jefe) ---
    @Override
    public EnemigoBase crearEnemigoAleatorio(float x, float y) {
        Random r = new Random();
        int suerte = r.nextInt(100);

        if (suerte < 50) return crearEnemigoNormal(x, y);
        else if (suerte < 80) return crearEnemigoChico(x, y);
        else return crearEnemigoGrande(x, y);
    }

    // --- JEFE (Usado en Nivel 6) ---
    @Override
    public EnemigoBase crearEnemigoJefe(float x, float y, PantallaJuego juego) {
        // El jefe usará 'this' (esta fábrica difícil) para invocar a sus esbirros
        return new EnemigoJefe(juego, this, texGrande, texGrandeDebil, (int)x, (int)y);
    }
}

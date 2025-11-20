package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class FabricaNivelFacil implements FabricaEnemigos {
    // AGREGADO: texChicoDebil
    private Texture texNormal, texNormalDebil, texGrande, texGrandeDebil, texChico, texChicoDebil;

    public FabricaNivelFacil(Texture tN, Texture tND, Texture tG, Texture tGD) {
        this.texNormal = tN;
        this.texNormalDebil = tND;
        this.texGrande = tG;
        this.texGrandeDebil = tGD;

        // Reusamos las texturas del normal para el chico
        this.texChico = tN;
        this.texChicoDebil = tND; // AGREGADO: Inicialización
    }

    // --- MÉTODOS QUE SÍ SE USAN (Niveles 1, 2, 3) ---
    @Override
    public EnemigoBase crearEnemigoChico(float x, float y) {
        // AGREGADO: Pasamos texChicoDebil
        return new EnemigoChico(new MovimientoRebote(300, 300), texChico, texChicoDebil, (int)x, (int)y);
    }

    @Override
    public EnemigoBase crearEnemigoNormal(float x, float y) {
        return new EnemigoNormal(new MovimientoRebote(150, 150), texNormal, texNormalDebil, (int)x, (int)y);
    }

    @Override
    public EnemigoBase crearEnemigoGrande(float x, float y) {
        return new EnemigoGrande(new MovimientoRebote(80, 80), texGrande, texGrandeDebil, (int)x, (int)y);
    }

    // --- MÉTODOS QUE NO SE DEBEN USAR EN NIVEL FÁCIL ---

    @Override
    public EnemigoBase crearEnemigoAleatorio(float x, float y) {
        // Si por error se llama, devolvemos uno normal para seguridad
        return crearEnemigoNormal(x, y);
    }

    @Override
    public EnemigoBase crearEnemigoJefe(float x, float y, PantallaJuego juego) {
        // En niveles fáciles NO hay jefe. Retornamos null.
        return null;
    }
}

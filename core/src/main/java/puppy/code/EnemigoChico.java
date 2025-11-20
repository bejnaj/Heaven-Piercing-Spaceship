package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class EnemigoChico extends EnemigoBase {

    public EnemigoChico(EstrategiaMovimiento estrategia, Texture tNormal, Texture tDebil, int x, int y) {
        super(estrategia, 5, tNormal, tDebil, x, y, 20);
    }

    @Override
    public void realizarComportamientoEspecifico() {
        // El enemigo chico no tiene comportamiento especial más allá de moverse y recibir daño.
    }
}


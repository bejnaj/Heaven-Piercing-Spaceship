package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class EnemigoNormal extends EnemigoBase {

    public EnemigoNormal(EstrategiaMovimiento estrategia, Texture tNormal, Texture tDebil, int x, int y) {
        super(estrategia, 5, tNormal, tDebil, x, y, 100);
    }

    @Override
    public void realizarComportamientoEspecifico() {
        // El enemigo normal no tiene comportamiento especial más allá de moverse y recibir daño.
    }
}


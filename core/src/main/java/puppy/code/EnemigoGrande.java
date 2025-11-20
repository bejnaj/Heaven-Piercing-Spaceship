package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class EnemigoGrande extends EnemigoBase {

    public EnemigoGrande(EstrategiaMovimiento estrategia, Texture tNormal, Texture tDebil, int x, int y) {
        super(estrategia, 10, tNormal, tDebil, x, y, 50);
    }

    @Override
    public void realizarComportamientoEspecifico() {
        // El enemigo normal no tiene comportamiento especial más allá de moverse y recibir daño.
    }
}
}


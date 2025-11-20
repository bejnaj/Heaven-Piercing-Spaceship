package puppy.code;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MovimientoEstatico implements EstrategiaMovimiento {
    @Override
    public void mover(Sprite spr, float delta) {
        // No hace nada. El jefe es una torre inamovible.
    }
}

package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaDefault extends BalaBase {
    public BalaDefault(float x, float y, float vx, float vy, Texture tx) {
        // Pasamos vx y vy a la estrategia MovimientoRecto
        super(x, y, 16, 32, 1, tx, new MovimientoRecto(vx, vy));
    }
}

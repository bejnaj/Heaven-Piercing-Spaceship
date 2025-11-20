package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaENormal extends BalaBase {
    public BalaENormal(float x, float y, Texture tx) {
        // 15x25 px, Daño 2, Velocidad 450
        super(x, y, 15, 25, 2, tx, new MovimientoRecto(0, 450));
    }
}

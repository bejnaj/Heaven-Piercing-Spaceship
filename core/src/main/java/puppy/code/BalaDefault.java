package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaDefault extends BalaBase {
    public BalaDefault(float x, float y, Texture tx) {
        // 10x20 px, Daño 1, Velocidad 400
        super(x, y, 10, 20, 1, tx, new MovimientoRecto(0, 400));
    }
}

package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaEGrande extends BalaBase {
    public BalaEGrande(float x, float y, Texture tx) {
        // 30x40 px, Daño 5, Velocidad 250
        super(x, y, 30, 40, 5, tx, new MovimientoRecto(0, 250));
    }
}

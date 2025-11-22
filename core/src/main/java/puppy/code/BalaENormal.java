package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaENormal extends BalaBase {
    public BalaENormal(float x, float y, float vx, float vy, Texture tx) {
        // 15x25 px, Daño 2, Velocidad 450
        super(x, y, 25, 45, 2, tx, new MovimientoRecto(vx, vy));
    }
}

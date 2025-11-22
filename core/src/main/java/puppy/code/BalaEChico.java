package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaEChico extends BalaBase {
    public BalaEChico(float x, float y, float vx, float vy, Texture tx) {
        // 8x15 px, Daño 1, Velocidad 700 !!
        super(x, y, 12, 24, 1, tx, new MovimientoRecto(vx, vy));
    }
}

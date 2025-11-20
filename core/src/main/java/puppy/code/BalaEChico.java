package puppy.code;
import com.badlogic.gdx.graphics.Texture;

public class BalaEChico extends BalaBase {
    public BalaEChico(float x, float y, Texture tx) {
        // 8x15 px, Daño 1, Velocidad 700 !!
        super(x, y, 8, 15, 1, tx, new MovimientoRecto(0, 700));
    }
}

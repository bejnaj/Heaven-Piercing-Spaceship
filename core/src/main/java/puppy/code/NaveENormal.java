package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class NaveENormal extends NaveBase {
    public NaveENormal(float x, float y, Texture texturaNave, Sound sonidoHerido, Texture texturaBala, Sound sonidoBala) {
        super(3, texturaNave, texturaBala, sonidoHerido, sonidoBala, x, y);
        this.MAX_SPEED = 500f;
        this.ACCELERATION = 1500f;
        this.FRICTION = 1200f;
        this.shootCooldownMax = 0.25f;
    }

    @Override
    public void actualizarSprite() {
        // lógica visual opcional
    }
}


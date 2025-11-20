package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class NaveENormal extends NaveBase {

    public NaveENormal(float x, float y, Texture texturaNave, Texture texturaNaveDebil, Sound sonidoHerido, Texture texturaBala, Sound sonidoBala) {
        super(3, texturaNave, texturaNaveDebil, texturaBala, sonidoHerido, sonidoBala, x, y);
        this.MAX_SPEED = 600f;         // más rápida
        this.ACCELERATION = 1800f;     // acelera más fuerte
        this.FRICTION = 1000f;         // se desliza más
        this.shootCooldownMax = 0.2f;  // dispara más seguido
        this.spr.setSize(50, 50);      // visualmente más grande
    }

    @Override
    public void actualizarSprite() {
        // Inclinación exagerada al moverse
        if (xVel > 50) {
            spr.setRotation(-15); // Se inclina a la derecha
        } else if (xVel < -50) {
            spr.setRotation(15);  // Se inclina a la izquierda
        } else {
            spr.setRotation(0);
        }
    }
}

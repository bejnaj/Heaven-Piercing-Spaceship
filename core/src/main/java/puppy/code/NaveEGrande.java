package puppy.code;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class NaveEGrande extends NaveBase {
    public NaveEGrande(float x, float y, Texture tNave, Texture tDebil, Sound sHerido, Texture tBala, Sound sBala) {
        super(6, tNave, tDebil, tBala, sHerido, sBala, x, y); // 6 Vidas (Tanque)
        this.MAX_SPEED = 300f;      // Lenta
        this.ACCELERATION = 800f;   // Le cuesta arrancar
        this.shootCooldownMax = 0.8f; // Dispara lento
        this.spr.setSize(70, 70);   // Grande
    }

    @Override
    public BalaBase disparar() {
        float bx = spr.getX() + spr.getWidth()/2 - 15;
        float by = spr.getY() + spr.getHeight();
        shootCooldown = shootCooldownMax;
        // RETORNA SU BALA DESTRUCTIVA
        return new BalaEGrande(bx, by, texturaBala);
    }

    @Override
    public void actualizarSprite() {
        // Efecto de Pulso de Color
        float tiempo = Gdx.graphics.getFrameId() / 60f; // Tiempo basado en frames
        // Oscila el color entre gris claro y blanco
        float color = 0.8f + 0.2f * (float)Math.sin(tiempo * 5);

        spr.setColor(color, color, color, 1f);
    }
}

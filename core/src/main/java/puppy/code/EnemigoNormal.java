package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class EnemigoNormal extends EnemigoBase {
    private Texture texturaNormal;
    private Texture texturaDañado;
    private int vidasMax;

    public EnemigoNormal(Texture texturaNormal, Texture texturaDañado, int x, int y, int size, int xSpeed, int ySpeed) {
        super(5, texturaNormal, x, y, size, xSpeed, ySpeed); // ejemplo: 4 vidas
        this.texturaNormal = texturaNormal;
        this.texturaDañado = texturaDañado;
        this.vidasMax = 5;
        this.debil = false;
    }
    @Override
    public void actualizarSprite() {
        float porcentaje = (float) vidas / vidasMax;

        if (porcentaje <= 0.25f && !debil) {
            spr.setTexture(texturaDañado);
            debil = true;
        } else if (porcentaje > 0.25f && debil) {
            spr.setTexture(texturaNormal);
            debil = false;
        }
    }
}

package puppy.code;

public abstract class EnemigoBase implements Atacable, Actualizable {
    protected int vidas;
    protected String sprite;

    public EnemigoBase(int vidas, String sprite) {
        this.vidas = vidas;
        this.sprite = sprite;
    }

    public EnemigoBase() {
        this.vidas = 1;
        this.sprite = null;
    }

    @Override
    public void recibirDano(int cantidad) {
        vidas -= cantidad;
    }

    @Override
    public boolean estaDestruido() {
        return vidas <= 0;
    }

    @Override
    public abstract void actualizarSprite();
}

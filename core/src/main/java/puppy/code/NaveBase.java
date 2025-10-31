package puppy.code;

public abstract class NaveBase implements Conectable, Atacable, Actualizable {
    protected int vidas;
    protected String sprite;

    public NaveBase(int vidas, String sprite) {
        this.vidas = vidas;
        this.sprite = sprite;
    }

    public NaveBase() {
        this.vidas = 3;
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

    @Override
    public void conectar(Conectable otro) {
        // Lógica por defecto (puede ser sobrescrita)
    }
}

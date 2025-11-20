package puppy.code;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaJuego implements Screen {
    private final SpaceNavigation game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Sound explosionSound;
    private final Music gameMusic;
    private final int ronda, velXEnemigos, velYEnemigos, cantEnemigos;

    private NaveBase nave;
    private ArrayList<EnemigoBase> enemigos = new ArrayList<>();
    private ArrayList<BalaBase> balas = new ArrayList<>(); // Polimorfismo de balas
    private FabricaEnemigos fabrica;

    private Texture fondoGameplay;
    private Texture texturaNaveDefault, texturaNaveDefaultDebil, texturaBalaDefault;
    private Texture texturaNaveENormal, texturaNaveENormalDebil, texturaBalaENormal;
    private Texture texEnemigoGrande, texEnemigoGrandeDebil;
    private Texture texEnemigoNormal, texEnemigoNormalDebil;
    private Sound sonidoHerido, sonidoBala;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int velXEnemigos, int velYEnemigos, int cantEnemigos) {
        this.game = game;
        this.ronda = ronda;
        this.velXEnemigos = velXEnemigos;
        this.velYEnemigos = velYEnemigos;
        this.cantEnemigos = cantEnemigos;
        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);

        // Carga de assets (Simplificado para lectura)
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true); gameMusic.play();
        fondoGameplay = new Texture(Gdx.files.internal("fondoGameplay.png"));
        texturaNaveDefault = new Texture(Gdx.files.internal("NaveDefault.png"));
        texturaNaveDefaultDebil = new Texture(Gdx.files.internal("NaveDefaultDebil.png"));
        texturaBalaDefault = new Texture(Gdx.files.internal("BulletNormal.png"));
        texturaNaveENormal = new Texture(Gdx.files.internal("NaveENormal.png"));
        texturaNaveENormalDebil = new Texture(Gdx.files.internal("NaveENormalDebil.png"));
        texturaBalaENormal = new Texture(Gdx.files.internal("BulletEnemigoNormal.png"));
        sonidoHerido = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        sonidoBala = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));
        texEnemigoGrande = new Texture(Gdx.files.internal("enemigoGrande.png"));
        texEnemigoGrandeDebil = new Texture(Gdx.files.internal("enemigoGrandeDebil.png"));
        texEnemigoNormal = new Texture(Gdx.files.internal("enemigoNormal.png"));
        texEnemigoNormalDebil = new Texture(Gdx.files.internal("enemigoNormalDebil.png"));

        fabrica = new FabricaNivelFacil(texEnemigoNormal, texEnemigoNormalDebil, texEnemigoGrande, texEnemigoGrandeDebil);
        nave = new NaveDefault(Gdx.graphics.getWidth()/2f - 50, 30, texturaNaveDefault, texturaNaveDefaultDebil, sonidoHerido, texturaBalaDefault, sonidoBala);
        nave.setVidas(vidas);
        nave.setJuego(this);
        iniciarNivel();
    }

    private void iniciarNivel() {
        if (ronda <= 3) fabrica = new FabricaNivelFacil(texEnemigoNormal, texEnemigoNormalDebil, texEnemigoGrande, texEnemigoGrandeDebil);
        else fabrica = new FabricaNivelDificil(texEnemigoNormal, texEnemigoNormalDebil, texEnemigoGrande, texEnemigoGrandeDebil);

        if (ronda == 6) {
            enemigos.add(fabrica.crearEnemigoJefe(Gdx.graphics.getWidth()/2f - 50, Gdx.graphics.getHeight() - 150, this));
        } else {
            Random r = new Random();
            for (int i = 0; i < cantEnemigos; i++) {
                float x = r.nextInt(Gdx.graphics.getWidth()-50);
                float y = 50 + r.nextInt(Gdx.graphics.getHeight()-100);
                EnemigoBase e = null;
                if (ronda == 1) e = fabrica.crearEnemigoChico(x, y);
                else if (ronda == 2) e = fabrica.crearEnemigoNormal(x, y);
                else if (ronda == 3) e = fabrica.crearEnemigoGrande(x, y);
                else e = fabrica.crearEnemigoAleatorio(x, y);
                if (e != null) enemigos.add(e);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fondoGameplay, 0, 0, camera.viewportWidth, camera.viewportHeight);
        dibujaEncabezado();

        // Balas
        for (int i = 0; i < balas.size(); i++) {
            BalaBase b = balas.get(i);
            b.update();
            for (int j = 0; j < enemigos.size(); j++) {
                EnemigoBase e = enemigos.get(j);
                if (b.checkCollision(e)) {
                    e.recibirDano(1);
                    if (e.estaDestruido()) {
                        explosionSound.play();
                        enemigos.remove(j); j--;
                        GerentePuntuacion.getInstance().agregarPuntaje(10);
                    }
                    b.setDestroyed(true);
                }
            }
            if (b.isDestroyed()) { balas.remove(i); i--; }
        }

        // Enemigos
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e = enemigos.get(i);
            e.update();
            e.draw(batch);
            for (int j = i+1; j < enemigos.size(); j++) e.checkCollision(enemigos.get(j));

            if (nave.getArea().overlaps(e.getArea())) {
                if (e.getVidas() > 0 && ((float)e.getVidas()/Math.max(1, e.getVidaMaxima())) <= 0.25f) {
                    if (e instanceof EnemigoJefe) {
                        game.setScreen(new PantallaVictoriaSecreta(game));
                        dispose(); return;
                    }
                    if (nave.conectar(e)) { enemigos.remove(i); i--; continue; }
                }
                if (nave.checkCollision(e)) { enemigos.remove(i); i--; }
            }
        }

        // Nave (CORREGIDO: Llamar update y luego draw)
        nave.update();
        nave.draw(batch);

        if (nave.estaDestruido()) {
            game.setScreen(new PantallaGameOver(game));
            dispose();
        }
        if (enemigos.isEmpty()) {
            if (ronda < 6) game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), velXEnemigos + 20, velYEnemigos + 20, cantEnemigos + 3));
            else {
                GerentePuntuacion.getInstance().resetScore();
                game.setScreen(new PantallaMenu(game));
            }
            dispose();
        }
        batch.end();
    }

    public void agregarEnemigo(EnemigoBase e) { enemigos.add(e); }
    public boolean agregarBala(BalaBase b) { return balas.add(b); }
    public void transformarEn(NaveBase n) { this.nave = n; this.nave.setJuego(this); }

    public void dibujaEncabezado() {
        game.getFont().draw(batch, "Score: " + GerentePuntuacion.getInstance().getScoreActual(), Gdx.graphics.getWidth() - 150, 30);
        // ... resto de textos
    }

    // Getters
    public Texture getTexturaNaveENormal() { return texturaNaveENormal; }
    public Texture getTexturaNaveENormalDebil() { return texturaNaveENormalDebil; }
    public Texture getTexturaBalaENormal() { return texturaBalaENormal; }
    public Sound getSonidoHerido() { return sonidoHerido; }
    public Sound getSonidoBala() { return sonidoBala; }

    @Override public void show() {} @Override public void resize(int w, int h) {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { /* ... disposar todo ... */ }
}

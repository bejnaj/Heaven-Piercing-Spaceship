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
    private Texture texturaNaveEChico, texturaNaveEChicoDebil;
    private Texture texturaNaveEGrande, texturaNaveEGrandeDebil;
    private Texture texEnemigoGrande, texEnemigoGrandeDebil;
    private Texture texEnemigoNormal, texEnemigoNormalDebil;
    private Texture texEnemigoChico, texEnemigoChicoDebil;
    private Texture texEnemigoJefe, texEnemigoJefeDebil;
    private Sound sonidoHerido, sonidoBala;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int velXEnemigos, int velYEnemigos, int cantEnemigos) {
        this.game = game;
        this.ronda = ronda;
        this.velXEnemigos = velXEnemigos;
        this.velYEnemigos = velYEnemigos;
        this.cantEnemigos = cantEnemigos;

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        // --- CARGA DE RECURSOS ---
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.1f);

        // MODIFICACIÓN 1: Música distinta para el Jefe Final
        if (ronda == 6) {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("jefe-final.wav"));
            gameMusic.setVolume(0.5f); // Un poco más fuerte para más epicidad
        } else {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
            gameMusic.setVolume(0.3f);
        }

        gameMusic.setLooping(true);
        gameMusic.play();

        fondoGameplay = new Texture(Gdx.files.internal("fondoGameplay.png"));

        // 1. TEXTURAS JUGADOR (Naves y Balas)
        texturaNaveDefault = new Texture(Gdx.files.internal("NaveDefault.png"));
        texturaNaveDefaultDebil = new Texture(Gdx.files.internal("NaveDefaultDebil.png"));
        texturaBalaDefault = new Texture(Gdx.files.internal("BulletNormal.png"));
        // 2. TEXTURAS FUSIONES (Si tienes específicas, cárgalas aquí, si no, reusa)
        texturaNaveENormal = new Texture(Gdx.files.internal("NaveENormal.png"));
        texturaNaveENormalDebil = new Texture(Gdx.files.internal("NaveENormalDebil.png"));
        texturaBalaENormal = new Texture(Gdx.files.internal("BulletEnemigoNormal.png"));
        // 3. TEXTURAS ENEMIGOS
        texEnemigoNormal = new Texture(Gdx.files.internal("enemigoNormal.png"));
        texEnemigoNormalDebil = new Texture(Gdx.files.internal("enemigoNormalDebil.png"));
        texEnemigoGrande = new Texture(Gdx.files.internal("enemigoGrande.png"));
        texEnemigoGrandeDebil = new Texture(Gdx.files.internal("enemigoGrandeDebil.png"));
        texEnemigoChico = new Texture(Gdx.files.internal("enemigoChico.png"));
        texEnemigoChicoDebil = new Texture(Gdx.files.internal("enemigoChicoDebil.png"));
        texEnemigoJefe = new Texture(Gdx.files.internal("enemigoJefe.png"));
        texEnemigoJefeDebil = new Texture(Gdx.files.internal("enemigoJefeDebil.png"));
        texturaNaveEChico = new Texture(Gdx.files.internal("NaveEChico.png"));
        texturaNaveEChicoDebil = new Texture(Gdx.files.internal("NaveEChicoDebil.png"));
        // Cargar texturas de Fusión GRANDE
        texturaNaveEGrande = new Texture(Gdx.files.internal("NaveEGrande.png"));
        texturaNaveEGrandeDebil = new Texture(Gdx.files.internal("NaveEGrandeDebil.png"));

        fabrica = new FabricaNivelFacil(
            texEnemigoNormal, texEnemigoNormalDebil,
            texEnemigoGrande, texEnemigoGrandeDebil,
            texEnemigoChico, texEnemigoChicoDebil,
            texEnemigoJefe, texEnemigoJefeDebil
        );
        nave = new NaveDefault(Gdx.graphics.getWidth()/2f - 50, 30, texturaNaveDefault, texturaNaveDefaultDebil, sonidoHerido, texturaBalaDefault, sonidoBala);
        nave.setVidas(vidas);
        nave.setJuego(this);
        iniciarNivel();
    }

    private void iniciarNivel() {
        if (ronda <= 3){ // Nivel Fácil: Pasamos las 8 texturas (aunque el jefe no se use aquí, el constructor lo pide)
            fabrica = new FabricaNivelFacil(
                texEnemigoNormal, texEnemigoNormalDebil,
                texEnemigoGrande, texEnemigoGrandeDebil,
                texEnemigoChico, texEnemigoChicoDebil,
                texEnemigoJefe, texEnemigoJefeDebil);
        } else {
        // Nivel Difícil: Lo mismo
        fabrica = new FabricaNivelDificil(
            texEnemigoNormal, texEnemigoNormalDebil,
            texEnemigoGrande, texEnemigoGrandeDebil,
            texEnemigoChico, texEnemigoChicoDebil,
            texEnemigoJefe, texEnemigoJefeDebil);
        }

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

        // MODIFICACIÓN 3: Quitamos 'dibujaEncabezado()' de aquí y lo ponemos al FINAL
        // para que se dibuje ENCIMA de todo.

        // --- ACTUALIZAR BALAS ---
        for (int i = 0; i < balas.size(); i++) {
            BalaBase b = balas.get(i);
            b.update();
            b.draw(batch); // ¡No olvides dibujar la bala!
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

        // --- ACTUALIZAR ENEMIGOS ---
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e = enemigos.get(i);
            e.update();
            e.draw(batch);
            for (int j = i+1; j < enemigos.size(); j++) e.checkCollision(enemigos.get(j));

            if (nave.getArea().overlaps(e.getArea())) {
                if (e.getVidas() > 0 && ((float)e.getVidas()/Math.max(1, e.getVidaMaxima())) <= 0.25f) {
                    if (e instanceof EnemigoJefe) {
                        gameMusic.stop(); // Parar música al ganar
                        game.setScreen(new PantallaVictoriaSecreta(game));
                        dispose(); return;
                    }
                    if (nave.conectar(e)) { enemigos.remove(i); i--; continue; }
                }
                if (nave.checkCollision(e)) { enemigos.remove(i); i--; }
            }
        }

        // Nave
        nave.update();
        nave.draw(batch);

        // MODIFICACIÓN 4: Dibujamos el HUD al final para que siempre esté visible
        dibujaEncabezado();

        // --- LÓGICA FIN DE JUEGO ---
        if (nave.estaDestruido()) {
            gameMusic.stop(); // Parar música al perder
            game.setScreen(new PantallaGameOver(game));
            dispose();
        }

        if (enemigos.isEmpty()) {
            gameMusic.stop(); // Parar música al pasar nivel
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
        int puntajeActual = GerentePuntuacion.getInstance().getScoreActual();
        int record = GerentePuntuacion.getInstance().getHighScore();

        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;

        // Configuración de fuente
        game.getFont().getData().setScale(2f);

        // MODIFICACIÓN 2: Dibujar ARRIBA (Y = Height - 20) en lugar de abajo (Y = 30)
        // Así no se tapa con la nave.
        float altoPantalla = Gdx.graphics.getHeight();

        game.getFont().draw(batch, str, 10, altoPantalla - 20);
        game.getFont().draw(batch, "Score: " + puntajeActual, Gdx.graphics.getWidth() - 250, altoPantalla - 20);
        game.getFont().draw(batch, "HighScore: " + record, Gdx.graphics.getWidth() / 2f - 100, altoPantalla - 20);
    }

    // Getters
    public Texture getTexturaNaveENormal() { return texturaNaveENormal; }
    public Texture getTexturaNaveENormalDebil() { return texturaNaveENormalDebil; }
    public Texture getTexturaBalaENormal() { return texturaBalaENormal; }
    public Texture getTexturaNaveEChico() { return texturaNaveEChico; }
    public Texture getTexturaNaveEChicoDebil() { return texturaNaveEChicoDebil; }
    public Texture getTexturaNaveEGrande() { return texturaNaveEGrande; }
    public Texture getTexturaNaveEGrandeDebil() { return texturaNaveEGrandeDebil; }
    public Sound getSonidoHerido() { return sonidoHerido; }
    public Sound getSonidoBala() { return sonidoBala; }

    @Override public void show() {} @Override public void resize(int w, int h) {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { /* ... disposar todo ... */ }
}

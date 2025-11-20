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

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Sound explosionSound;
    private Music gameMusic;

    // Variables de juego
    private int ronda;
    private int velXEnemigos;
    private int velYEnemigos;
    private int cantEnemigos;

    // Entidades
    private NaveBase nave;
    private ArrayList<EnemigoBase> enemigos = new ArrayList<>();
    private ArrayList<Bullet> balas = new ArrayList<>();

    // PATRÓN ABSTRACT FACTORY
    private FabricaEnemigos fabrica;

    // Recursos Graficos y de Audio
    private Texture fondoGameplay;
    private Texture texturaNaveDefault, texturaNaveDefaultDebil, texturaBalaDefault;
    private Texture texturaNaveENormal, texturaNaveENormalDebil, texturaBalaENormal;
    private Texture texEnemigoGrande, texEnemigoGrandeDebil;
    private Texture texEnemigoNormal, texEnemigoNormalDebil;

    private Sound sonidoHerido;
    private Sound sonidoBala;

    // Constructor modificado: Ya no recibe 'score' (Singleton)
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas,
                         int velXEnemigos, int velYEnemigos, int cantEnemigos) {
        this.game = game;
        this.ronda = ronda;
        this.velXEnemigos = velXEnemigos;
        this.velYEnemigos = velYEnemigos;
        this.cantEnemigos = cantEnemigos;

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);

        // --- CARGA DE RECURSOS ---
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.1f);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.3f);
        gameMusic.play();

        fondoGameplay = new Texture(Gdx.files.internal("fondoGameplay.png"));

        // Texturas Nave Jugador
        texturaNaveDefault = new Texture(Gdx.files.internal("NaveDefault.png"));
        texturaNaveDefaultDebil = new Texture(Gdx.files.internal("NaveDefaultDebil.png"));
        texturaBalaDefault = new Texture(Gdx.files.internal("BulletNormal.png"));

        // Texturas Nave Transformada
        texturaNaveENormal = new Texture(Gdx.files.internal("NaveENormal.png"));
        texturaNaveENormalDebil = new Texture(Gdx.files.internal("NaveENormalDebil.png"));
        texturaBalaENormal = new Texture(Gdx.files.internal("BulletEnemigoNormal.png"));

        sonidoHerido = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        sonidoBala = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));

        // Texturas Enemigos
        texEnemigoGrande = new Texture(Gdx.files.internal("enemigoGrande.png"));
        texEnemigoGrandeDebil = new Texture(Gdx.files.internal("enemigoGrandeDebil.png"));
        texEnemigoNormal = new Texture(Gdx.files.internal("enemigoNormal.png"));
        texEnemigoNormalDebil = new Texture(Gdx.files.internal("enemigoNormalDebil.png"));

        // --- INICIALIZAR FÁBRICA ---
        // Pasamos las texturas a la fábrica.
        // Nota: Reutilizamos texEnemigoNormal para el Chico si no tienes un PNG específico "enemigoChico.png"
        fabrica = new FabricaNivel1(
            texEnemigoNormal, texEnemigoNormalDebil, // Normales
            texEnemigoGrande, texEnemigoGrandeDebil, // Grandes
            texEnemigoNormal, texEnemigoNormalDebil  // Chicos (usando sprite normal por ahora)
        );

        // Crear nave inicial
        nave = new NaveDefault(
            Gdx.graphics.getWidth() / 2f - 50, 30,
            texturaNaveDefault, texturaNaveDefaultDebil,
            sonidoHerido, texturaBalaDefault, sonidoBala
        );
        nave.setVidas(vidas);
        nave.setJuego(this);

        // --- SPAWN DE ENEMIGOS SEGÚN RONDA ---
        iniciarNivel();
    }

    private void iniciarNivel() {

        // 1. SELECCIÓN DE FÁBRICA
        if (ronda <= 3) {
            // Niveles 1, 2, 3 -> FÁCIL (Fijos, sin jefe)
            fabrica = new FabricaNivelFacil(texEnemigoNormal, texEnemigoNormalDebil, texEnemigoGrande, texEnemigoGrandeDebil);
        } else {
            // Niveles 4, 5, 6 -> DIFÍCIL (Aleatorios y Jefe)
            fabrica = new FabricaNivelDificil(texEnemigoNormal, texEnemigoNormalDebil, texEnemigoGrande, texEnemigoGrandeDebil);
        }

        Random r = new Random();

        // 2. GENERACIÓN DE ENEMIGOS
        if (ronda == 6) {
            // --- NIVEL 6: EL JEFE FINAL ---
            float bossX = Gdx.graphics.getWidth() / 2f - 50;
            float bossY = Gdx.graphics.getHeight() - 150;

            // La fábrica difícil SÍ sabe crear al jefe
            enemigos.add(fabrica.crearEnemigoJefe(bossX, bossY, this));

        } else {
            // --- NIVELES 1 al 5 ---
            for (int i = 0; i < cantEnemigos; i++) {
                float x = r.nextInt(Gdx.graphics.getWidth() - 50);
                float y = 50 + r.nextInt(Gdx.graphics.getHeight() - 100);
                EnemigoBase nuevoEnemigo = null;

                if (ronda == 1) {
                    // Solo chicos
                    nuevoEnemigo = fabrica.crearEnemigoChico(x, y);
                }
                else if (ronda == 2) {
                    // Solo medianos
                    nuevoEnemigo = fabrica.crearEnemigoNormal(x, y);
                }
                else if (ronda == 3) {
                    // Solo grandes
                    nuevoEnemigo = fabrica.crearEnemigoGrande(x, y);
                }
                else {
                    // Rondas 4 y 5: ALEATORIOS (Usando FabricaNivelDificil)
                    nuevoEnemigo = fabrica.crearEnemigoAleatorio(x, y);
                }

                if (nuevoEnemigo != null) {
                    enemigos.add(nuevoEnemigo);
                }
            }
        }
    }

    // METODO PUBLICO PARA QUE EL JEFE INVOQUE ESBIRROS
    public void agregarEnemigo(EnemigoBase enemigo) {
        this.enemigos.add(enemigo);
    }

    public void dibujaEncabezado() {
        // PATRÓN SINGLETON: Leer puntaje global
        int puntajeActual = GerentePuntuacion.getInstance().getScoreActual();
        int record = GerentePuntuacion.getInstance().getHighScore();

        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score: " + puntajeActual, Gdx.graphics.getWidth() - 150, 30);
        game.getFont().draw(batch, "HighScore: " + record, Gdx.graphics.getWidth() / 2f - 100, 30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondoGameplay, 0, 0, camera.viewportWidth, camera.viewportHeight);
        dibujaEncabezado();

        // --- ACTUALIZAR BALAS ---
        for (int i = 0; i < balas.size(); i++) {
            Bullet b = balas.get(i);
            b.update();
            for (int j = 0; j < enemigos.size(); j++) {
                EnemigoBase e = enemigos.get(j);
                if (b.checkCollision(e)) {
                    e.recibirDano(1);
                    if (e.estaDestruido()) {
                        explosionSound.play();
                        enemigos.remove(j);
                        j--;
                        // PATRÓN SINGLETON: Sumar puntos
                        GerentePuntuacion.getInstance().agregarPuntaje(10);
                    }
                    b.setDestroyed(true);
                }
            }
            if (b.isDestroyed()) {
                balas.remove(i);
                i--;
            }
        }

        // --- ACTUALIZAR ENEMIGOS ---
        // Usamos un for inverso o iterator si vamos a eliminar, pero como aquí
        // solo actualizamos movimiento, el for-each está bien, SALVO si el jefe agrega enemigos
        // al mismo tiempo. Para evitar ConcurrentModificationException, usamos for indexado.
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e = enemigos.get(i);
            e.update(); // Template Method maneja movimiento y visuales
            e.draw(batch);

            // Colisiones Enemigo vs Enemigo
            for (int j = i + 1; j < enemigos.size(); j++) {
                e.checkCollision(enemigos.get(j));
            }

            // Lógica de absorción/choque con Nave
            if (nave.getArea().overlaps(e.getArea())) {
                int vidaActual = e.getVidas();
                int vidaMax = Math.max(1, e.getVidaMaxima());
                float porcentaje = (float)vidaActual / vidaMax;

                // Intentar conectar (Absorber)
                if (vidaActual > 0 && porcentaje <= 0.25f && nave.conectar(e)) {
                    enemigos.remove(i);
                    i--;
                    continue;
                }

                // Choque normal (Daño)
                if (nave.checkCollision(e)) {
                    enemigos.remove(i);
                    i--;
                }
            }
        }

        nave.draw(batch, this);

        // --- LOGICA DE FIN DE JUEGO ---
        if (nave.estaDestruido()) {
            game.setScreen(new PantallaGameOver(game));
            dispose();
        }

        // --- LOGICA DE FIN DE NIVEL / VICTORIA ---
        if (enemigos.isEmpty()) {
            if (ronda < 6) {
                // Pasar al siguiente nivel
                game.setScreen(new PantallaJuego(
                    game,
                    ronda + 1,
                    nave.getVidas(),
                    velXEnemigos + 20,
                    velYEnemigos + 20,
                    cantEnemigos + 3
                ));
            } else {
                // VICTORIA (Terminó la ronda 6)
                // Reiniciamos score para la próxima vez y vamos al menú
                GerentePuntuacion.getInstance().resetScore();
                game.setScreen(new PantallaMenu(game));
            }
            dispose();
        }

        batch.end();
    }

    public boolean agregarBala(Bullet bb) {
        return balas.add(bb);
    }

    public void transformarEn(NaveBase nuevaNave) {
        this.nave = nuevaNave;
        this.nave.setJuego(this);
    }

    // Getters de recursos
    public Texture getTexturaNaveENormal() { return texturaNaveENormal; }
    public Texture getTexturaNaveENormalDebil() { return texturaNaveENormalDebil; }
    public Texture getTexturaBalaENormal() { return texturaBalaENormal; }
    public Sound getSonidoHerido() { return sonidoHerido; }
    public Sound getSonidoBala() { return sonidoBala; }

    @Override public void show() { gameMusic.play(); }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (fondoGameplay != null) fondoGameplay.dispose();
        if (texturaNaveDefault != null) texturaNaveDefault.dispose();
        if (texturaNaveDefaultDebil != null) texturaNaveDefaultDebil.dispose();
        if (texturaBalaDefault != null) texturaBalaDefault.dispose();
        if (texturaNaveENormal != null) texturaNaveENormal.dispose();
        if (texturaNaveENormalDebil != null) texturaNaveENormalDebil.dispose();
        if (texturaBalaENormal != null) texturaBalaENormal.dispose();

        if (texEnemigoGrande != null) texEnemigoGrande.dispose();
        if (texEnemigoGrandeDebil != null) texEnemigoGrandeDebil.dispose();
        if (texEnemigoNormal != null) texEnemigoNormal.dispose();
        if (texEnemigoNormalDebil != null) texEnemigoNormalDebil.dispose();

        if (explosionSound != null) explosionSound.dispose();
        if (sonidoHerido != null) sonidoHerido.dispose();
        if (sonidoBala != null) sonidoBala.dispose();
        if (gameMusic != null) gameMusic.dispose();
    }
}

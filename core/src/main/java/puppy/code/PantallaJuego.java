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
    private int ronda;
    private int velXEnemigos;
    private int velYEnemigos;
    private int cantEnemigos;

    private NaveBase nave;
    private ArrayList<EnemigoBase> enemigos = new ArrayList<>();
    private ArrayList<Bullet> balas = new ArrayList<>();

    // Fondo de gameplay
    private Texture fondoGameplay;

    // Texturas y sonidos compartidos (incluyendo versiones débiles)
    private Texture texturaNaveDefault;
    private Texture texturaNaveDefaultDebil;
    private Texture texturaBalaDefault;

    private Texture texturaNaveENormal;
    private Texture texturaNaveENormalDebil;
    private Texture texturaBalaENormal;

    private Sound sonidoHerido;
    private Sound sonidoBala;

    // Texturas de enemigos
    private Texture texEnemigoGrande;
    private Texture texEnemigoGrandeDebil;
    private Texture texEnemigoNormal;
    private Texture texEnemigoNormalDebil;

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

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.1f);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.3f);
        gameMusic.play();

        // Cargar fondo de gameplay (archivo en assets: "fondoGameplay.png")
        fondoGameplay = new Texture(Gdx.files.internal("fondoGameplay.png"));

        // Cargar texturas y sonidos una vez (incluyendo versiones débiles)
        texturaNaveDefault = new Texture(Gdx.files.internal("NaveDefault.png"));
        texturaNaveDefaultDebil = new Texture(Gdx.files.internal("NaveDefaultDebil.png"));
        texturaBalaDefault = new Texture(Gdx.files.internal("BulletNormal.png"));

        texturaNaveENormal = new Texture(Gdx.files.internal("NaveENormal.png"));
        texturaNaveENormalDebil = new Texture(Gdx.files.internal("NaveENormalDebil.png"));
        texturaBalaENormal = new Texture(Gdx.files.internal("BulletEnemigoNormal.png"));

        sonidoHerido = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        sonidoBala = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));

        // Cargar texturas de enemigos
        texEnemigoGrande = new Texture(Gdx.files.internal("enemigoGrande.png"));
        texEnemigoGrandeDebil = new Texture(Gdx.files.internal("enemigoGrandeDebil.png"));
        texEnemigoNormal = new Texture(Gdx.files.internal("enemigoNormal.png"));
        texEnemigoNormalDebil = new Texture(Gdx.files.internal("enemigoNormalDebil.png"));

        // Crear nave inicial (NaveDefault recibe su textura normal y su versión débil)
        nave = new NaveDefault(
            Gdx.graphics.getWidth() / 2f - 50, 30,
            texturaNaveDefault, texturaNaveDefaultDebil,
            sonidoHerido, texturaBalaDefault, sonidoBala
        );
        nave.setVidas(vidas);
        nave.setJuego(this);

        // Crear enemigos usando las texturas cargadas
        Random r = new Random();
        for (int i = 0; i < cantEnemigos; i++) {
            enemigos.add(new EnemigoGrande(
                texEnemigoGrande, texEnemigoGrandeDebil,
                r.nextInt(Gdx.graphics.getWidth()),
                50 + r.nextInt(Gdx.graphics.getHeight() - 50),
                60 + r.nextInt(10),
                velXEnemigos + r.nextInt(1),
                velYEnemigos + r.nextInt(1)
            ));
            enemigos.add(new EnemigoNormal(
                texEnemigoNormal, texEnemigoNormalDebil,
                r.nextInt(Gdx.graphics.getWidth()),
                50 + r.nextInt(Gdx.graphics.getHeight() - 50),
                40 + r.nextInt(10),
                velXEnemigos + r.nextInt(1),
                velYEnemigos + r.nextInt(1)
            ));
        }
    }

    public void dibujaEncabezado() {
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

        // Dibujar fondo estirado al viewport
        batch.draw(fondoGameplay, 0, 0, camera.viewportWidth, camera.viewportHeight);

        dibujaEncabezado();

        // Actualizar y procesar balas
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
                        // SUMAR al Singleton
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

        // Actualizar movimiento de enemigos
        for (EnemigoBase e : enemigos) {
            e.update();
        }

        // Colisiones entre enemigos
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e1 = enemigos.get(i);
            for (int j = i + 1; j < enemigos.size(); j++) {
                EnemigoBase e2 = enemigos.get(j);
                e1.checkCollision(e2);
            }
        }

        // Dibujar balas
        for (Bullet b : balas) {
            b.draw(batch);
        }

        // Dibujar y actualizar la nave
        nave.draw(batch, this);

        // Dibujar enemigos y gestionar interacción con la nave
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e = enemigos.get(i);

            // Actualizar estado visual antes de dibujar (para que la textura débil se aplique)
            e.update();
            e.actualizarSprite();

            // Dibujar enemigo
            e.draw(batch);

            // Validar vida y porcentaje
            int vidaActual = e.getVidas();
            int vidaMaxima = Math.max(1, e.getVidaMaxima());
            float porcentajeEnemigo = (float) vidaActual / vidaMaxima;

            // Intentar fusión solo si:
            // - el enemigo está vivo
            // - está debilitado (<=25%)
            // - hay overlap físico entre nave y enemigo
            if (vidaActual > 0
                && porcentajeEnemigo <= 0.25f
                && nave.getArea().overlaps(e.getArea())) {

                if (nave.conectar(e)) {
                    // se transformó correctamente: eliminar enemigo y continuar
                    enemigos.remove(i);
                    i--;
                    continue;
                }
            }

            // Si no se fusionó, procesar colisión normal (solo si hay overlap)
            if (nave.getArea().overlaps(e.getArea())) {
                if (nave.checkCollision(e)) {
                    enemigos.remove(i);
                    i--;
                }
            }
        }

        // Game over
        if (nave.estaDestruido()) {
            game.setScreen(new PantallaGameOver(game));
            dispose();
        }

        // Nivel completado
        if (enemigos.isEmpty()) {
            // Nota: En el new PantallaJuego, pasa 0 o quita el parámetro del constructor
            game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), velXEnemigos + 1, velYEnemigos + 1, cantEnemigos + 2));
            dispose();
        }

        batch.end();
    }

    public boolean agregarBala(Bullet bb) {
        return balas.add(bb);
    }

    public void transformarEn(NaveBase nuevaNave) {
        // conservar posición y vidas si es necesario ya se asignan en el constructor de la nueva nave
        this.nave = nuevaNave;
        this.nave.setJuego(this);
    }

    // Getters para que NaveBase / subclases tomen texturas/sonidos sin recargar archivos
    public Texture getTexturaNaveENormal() {
        return texturaNaveENormal;
    }

    public Texture getTexturaNaveENormalDebil() {
        return texturaNaveENormalDebil;
    }

    public Texture getTexturaBalaENormal() {
        return texturaBalaENormal;
    }

    public Sound getSonidoHerido() {
        return sonidoHerido;
    }

    public Sound getSonidoBala() {
        return sonidoBala;
    }

    @Override public void show() { gameMusic.play(); }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        // disposar texturas y sonidos que cargaste aquí
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

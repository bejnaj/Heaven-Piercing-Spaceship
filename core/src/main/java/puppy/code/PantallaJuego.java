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
    private int score;
    private int ronda;
    private int velXEnemigos;
    private int velYEnemigos;
    private int cantEnemigos;

    private NaveDefault nave;
    private ArrayList<EnemigoBase> enemigos = new ArrayList<>();
    private ArrayList<Bullet> balas = new ArrayList<>();

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
                         int velXEnemigos, int velYEnemigos, int cantEnemigos) {
        this.game = game;
        this.ronda = ronda;
        this.score = score;
        this.velXEnemigos = velXEnemigos;
        this.velYEnemigos = velYEnemigos;
        this.cantEnemigos = cantEnemigos;

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 640);

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.1f);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.3f);
        gameMusic.play();

        nave = new NaveDefault(
            Gdx.graphics.getWidth() / 2f - 50, 30,
            new Texture(Gdx.files.internal("MainShip3.png")),
            Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")),
            new Texture(Gdx.files.internal("Rocket2.png")),
            Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"))
        );
        nave.setVidas(vidas);

        // Crear enemigos
        Random r = new Random();
        for (int i = 0; i < cantEnemigos; i++) {
            EnemigoGrande enemigo = new EnemigoGrande(
                new Texture(Gdx.files.internal("enemigoGrande.png")),
                new Texture(Gdx.files.internal("enemigoGrandeDebil.png")),
                r.nextInt(Gdx.graphics.getWidth()),
                50 + r.nextInt(Gdx.graphics.getHeight() - 50),
                60 + r.nextInt(10),
                velXEnemigos + r.nextInt(1),
                velYEnemigos + r.nextInt(1)
            );
            enemigos.add(enemigo);

            // Otros tipos posibles:
            // enemigos.add(new EnemigoMediano(...));
            // enemigos.add(new EnemigoPequeno(...));
            // enemigos.add(new EnemigoExplosivo(...));
        }
    }

    public void dibujaEncabezado() {
        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score: " + score, Gdx.graphics.getWidth() - 150, 30);
        game.getFont().draw(batch, "HighScore: " + game.getHighScore(), Gdx.graphics.getWidth() / 2f - 100, 30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        dibujaEncabezado();

        if (!nave.estaHerido()) {
            // Actualizar balas y colisión con enemigos
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
                            score += 10;
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
        }

        // Dibujar balas
        for (Bullet b : balas) {
            b.draw(batch);
        }

        // Dibujar nave
        nave.draw(batch, this);

        // Dibujar enemigos y colisión con nave
        for (int i = 0; i < enemigos.size(); i++) {
            EnemigoBase e = enemigos.get(i);
            e.draw(batch);
            e.update();
            e.actualizarSprite();
            if (nave.checkCollision(e)) {
                enemigos.remove(i);
                i--;
            }
        }

        // Game over
        if (nave.estaDestruido()) {
            if (score > game.getHighScore()) {
                game.setHighScore(score);
            }
            game.setScreen(new PantallaGameOver(game));
            dispose();
        }

        // Nivel completado
        if (enemigos.size() == 0) {
            game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), score,
                velXEnemigos + 3, velYEnemigos + 3, cantEnemigos + 10));
            dispose();
        }

        batch.end();
    }

    public boolean agregarBala(Bullet bb) {
        return balas.add(bb);
    }

    @Override
    public void show() {
        gameMusic.play();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        explosionSound.dispose();
        gameMusic.dispose();
    }
}

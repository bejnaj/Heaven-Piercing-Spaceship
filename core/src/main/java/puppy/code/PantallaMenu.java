package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaMenu implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private Texture fondo; // portada.png

    public PantallaMenu(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);

        // Cargar la imagen de fondo (portada.png) desde assets
        fondo = new Texture(Gdx.files.internal("portada.png"));
    }

    @Override
    public void render(float delta) {
        // Limpia pantalla (el color no importa porque dibujamos la textura)
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        // Dibujar fondo estirado para cubrir todo el viewport actual
        game.getBatch().draw(fondo, 0, 0, camera.viewportWidth, camera.viewportHeight);

        // Texto encima del fondo
        game.getFont().draw(game.getBatch(), "Presiona en cualquier lado o presiona cualquier tecla para comenzar ...", 120, 70);
        game.getBatch().end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            Screen ss = new PantallaJuego(game, 1, 10, 0, 1, 1, 6);
            ss.resize((int) camera.viewportWidth, (int) camera.viewportHeight);
            game.setScreen(ss);
            dispose();
        }
    }

    @Override
    public void show() {
        // para reporudir música de fondo opciones y boton de jugar
    }

    @Override
    public void resize(int width, int height) {
        // Actualizar cámara para el nuevo tamaño y mantener la proyección
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (fondo != null) fondo.dispose();
    }
}

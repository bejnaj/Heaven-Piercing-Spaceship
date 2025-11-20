package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaVictoriaSecreta implements Screen {
    private SpaceNavigation game;
    private OrthographicCamera camera;
    private Texture imagenVictoria;

    public PantallaVictoriaSecreta(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
        // Asegúrate de tener este archivo en assets
        imagenVictoria = new Texture(Gdx.files.internal("victoriaSecreta.png"));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        game.getBatch().draw(imagenVictoria, 0, 0, 1200, 800);
        game.getFont().getData().setScale(2);
        game.getFont().draw(game.getBatch(), "¡HAS FUSIONADO EL NÚCLEO DEL JEFE!", 300, 100);
        game.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched()) {
            GerentePuntuacion.getInstance().resetScore();
            game.setScreen(new PantallaMenu(game));
            dispose();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { imagenVictoria.dispose(); }
}

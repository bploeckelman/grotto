package zendo.games.grotto.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.eskalon.commons.screen.ManagedScreen;
import zendo.games.grotto.Assets;
import zendo.games.grotto.Game;

public abstract class BaseScreen extends ManagedScreen implements Disposable {

    private static final String TAG = BaseScreen.class.getSimpleName();

    public final Game game;
    public final Assets assets;
    public final Engine engine;
    public final SpriteBatch batch;
    public final TweenManager tween;
    public final Vector3 pointerPos;

    public Camera worldCamera;
    public OrthographicCamera windowCamera;

    public BaseScreen() {
        this.game = Game.instance;
        this.assets = game.assets;
        this.engine = game.engine;
        this.batch = game.assets.batch;
        this.tween = game.tween;
        this.pointerPos = new Vector3();
        this.windowCamera = new OrthographicCamera();
        this.windowCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.windowCamera.update();
    }

    @Override
    protected void create() {
        var camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        this.worldCamera = camera;
    }

    @Override
    public void hide() {

    }

    public void update(float delta) {
        windowCamera.update();
        worldCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        windowCamera.setToOrtho(false, width, height);
        windowCamera.update();
    }

    @Override
    public void dispose() {}

}

package zendo.games.grotto.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.eskalon.commons.screen.ManagedScreen;
import zendo.games.grotto.Assets;
import zendo.games.grotto.Config;
import zendo.games.grotto.Game;
import zendo.games.grotto.scene.Scene;

public abstract class BaseScreen extends ManagedScreen implements Disposable {

    private static final String TAG = BaseScreen.class.getSimpleName();

    public final Game game;
    public final Assets assets;
    public final Engine engine;
    public final SpriteBatch batch;
    public final TweenManager tween;
    public final Vector3 pointerPos;

    public Scene scene;
    public OrthographicCamera worldCamera;
    public final OrthographicCamera windowCamera;

    public BaseScreen() {
        this.game = Game.instance;
        this.assets = game.assets;
        this.engine = game.engine;
        this.batch = game.assets.batch;
        this.tween = game.tween;
        this.windowCamera = game.windowCamera;
        this.pointerPos = new Vector3();
    }

    @Override
    protected void create() {
        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        this.worldCamera.update();
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

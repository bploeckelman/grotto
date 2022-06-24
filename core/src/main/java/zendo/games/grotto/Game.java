package zendo.games.grotto;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screen.transition.impl.PushTransition;
import de.eskalon.commons.screen.transition.impl.SlidingDirection;
import de.eskalon.commons.utils.BasicInputMultiplexer;
import zendo.games.grotto.scene.systems.AnimationSystem;
import zendo.games.grotto.scene.systems.RenderSystem;
import zendo.games.grotto.screens.BaseScreen;
import zendo.games.grotto.screens.MapScreen;
import zendo.games.grotto.screens.TitleScreen;
import zendo.games.grotto.utils.Time;
import zendo.games.grotto.utils.accessors.*;

public class Game extends ManagedGame<BaseScreen, ScreenTransition> {

	public static Game instance;

	public Assets assets;
	public Engine engine;
	public TweenManager tween;

	public Game() {
		Game.instance = this;
	}

	@Override
	public void create() {
		Time.init();
		VisUI.load();

		assets = new Assets();

		engine = new Engine();
		engine.addSystem(new RenderSystem());
		engine.addSystem(new AnimationSystem());

		tween = new TweenManager();
		Tween.setWaypointsLimit(4);
		Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(Color.class, new ColorAccessor());
		Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
		Tween.registerAccessor(Vector2.class, new Vector2Accessor());
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());

		var inputMux = new BasicInputMultiplexer();
		screenManager.initialize(inputMux, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		screenManager.addScreen("title", new TitleScreen());
		screenManager.addScreen("map", new MapScreen());
		screenManager.addScreenTransition("blend", new BlendingTransition(assets.batch, 1f));
		screenManager.addScreenTransition("push", new PushTransition(assets.batch, SlidingDirection.DOWN, 0.5f));

		screenManager.pushScreen("title", "blend");
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		Time.update();

		// TODO - need a way to separate 'pausable update' from 'always update' on entity components
		engine.update(Time.delta);

		// handle a pause
		{
			if (Time.pause_timer > 0) {
				Time.pause_timer -= Time.delta;
				if (Time.pause_timer <= -0.0001f) {
					Time.delta = -Time.pause_timer;
				} else {
					// skip updates if we're paused
					return;
				}
			}
			Time.millis += Time.delta;
			Time.previous_elapsed = Time.elapsed_millis();
		}

		screenManager.render(Time.delta);
	}

	@Override
	public void dispose() {
		screenManager.getScreens().forEach(BaseScreen::dispose);
		assets.dispose();
	}

}
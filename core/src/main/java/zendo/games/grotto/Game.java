package zendo.games.grotto;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screen.transition.impl.PushTransition;
import de.eskalon.commons.screen.transition.impl.SlidingDirection;
import de.eskalon.commons.utils.BasicInputMultiplexer;
import zendo.games.grotto.scene.components.Families;
import zendo.games.grotto.scene.systems.AnimationSystem;
import zendo.games.grotto.scene.systems.MovementSystem;
import zendo.games.grotto.scene.systems.RenderSystem;
import zendo.games.grotto.screens.BaseScreen;
import zendo.games.grotto.screens.MapScreen;
import zendo.games.grotto.screens.TitleScreen;
import zendo.games.grotto.utils.Time;
import zendo.games.grotto.utils.accessors.*;

import static com.badlogic.gdx.Input.Keys;

public class Game extends ManagedGame<BaseScreen, ScreenTransition> {

	public static Game instance;

	public Assets assets;
	public Engine engine;
	public TweenManager tween;
	public NestableFrameBuffer frameBuffer;
	public TextureRegion frameBufferRegion;
	public OrthographicCamera windowCamera;

	public Game() {
		Game.instance = this;
	}

	@Override
	public void create() {
		Time.init();
		VisUI.load();

		assets = new Assets();

		engine = new Engine();
		{
			var animationSystem = new AnimationSystem();
			engine.addEntityListener(Families.animators, animationSystem);
			engine.addSystem(animationSystem);

			var movementSystem = new MovementSystem();
			engine.addEntityListener(Families.movers, movementSystem);
			engine.addSystem(movementSystem);

			var renderSystem = new RenderSystem();
			engine.addEntityListener(Families.renderSystem, renderSystem);
			engine.addSystem(renderSystem);
		}

		tween = new TweenManager();
		Tween.setWaypointsLimit(4);
		Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(Color.class, new ColorAccessor());
		Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
		Tween.registerAccessor(Vector2.class, new Vector2Accessor());
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());

		var format = Pixmap.Format.RGBA8888;
		var width = Config.Screen.framebuffer_width;
		var height = Config.Screen.framebuffer_height;
		var hasDepth = false;
		frameBuffer = new NestableFrameBuffer(format, width, height, hasDepth);
		var frameBufferTexture = frameBuffer.getColorBufferTexture();
		frameBufferTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		frameBufferRegion = new TextureRegion(frameBufferTexture);
		frameBufferRegion.flip(false, true);

		windowCamera = new OrthographicCamera();
		windowCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
		windowCamera.update();

		var inputMux = new BasicInputMultiplexer();
		Gdx.input.setInputProcessor(inputMux);

		screenManager.initialize(inputMux, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		screenManager.addScreen("title", new TitleScreen());
		screenManager.addScreen("map", new MapScreen());
		screenManager.addScreenTransition("blend", new BlendingTransition(assets.batch, 1f));
		screenManager.addScreenTransition("push", new PushTransition(assets.batch, SlidingDirection.UP, 0.25f));

		screenManager.pushScreen("title", "blend");
	}

	public void update(float delta) {
		// handle top level input
		{
			if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
				Gdx.app.exit();
			}
			if (Gdx.input.isKeyJustPressed(Keys.F1)) Config.Debug.general           = !Config.Debug.general;
			if (Gdx.input.isKeyJustPressed(Keys.F2)) Config.Debug.draw_colliders    = !Config.Debug.draw_colliders;
			if (Gdx.input.isKeyJustPressed(Keys.F3)) Config.Debug.draw_anim_bounds  = !Config.Debug.draw_anim_bounds;
		}

		// update time
		{
			Time.update();
		}

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

		// update systems
		{
			// TODO - need a way to separate 'pausable update' from 'always update' on entity components
			engine.update(Time.delta);
		}
	}

	@Override
	public void render() {
		update(Time.delta);
		screenManager.render(Time.delta);
	}

	@Override
	public void dispose() {
		screenManager.getScreens().forEach(BaseScreen::dispose);
		frameBuffer.dispose();
		assets.dispose();
	}

}
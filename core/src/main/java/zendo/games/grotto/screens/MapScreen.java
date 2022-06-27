package zendo.games.grotto.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import zendo.games.grotto.Config;
import zendo.games.grotto.Game;
import zendo.games.grotto.scene.Scene;
import zendo.games.grotto.scene.components.CameraControllerComponent;
import zendo.games.grotto.scene.components.Collider;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.components.Mover;
import zendo.games.grotto.scene.factories.EntityFactory;
import zendo.games.grotto.scene.systems.AnimationSystem;
import zendo.games.grotto.scene.systems.RenderSystem;
import zendo.games.grotto.utils.Calc;
import zendo.games.grotto.utils.Point;

import static com.badlogic.gdx.Input.Buttons;
import static com.badlogic.gdx.Input.Keys;

public class MapScreen extends BaseScreen {

    private static final String TAG = MapScreen.class.getSimpleName();

    RenderSystem renderSystem;
    AnimationSystem animationSystem;

    Entity player;
    Entity map;
    CameraControllerComponent cameraController;

    static class UI {
        static Stage stage;

        static VisLabel fpsLabel;
        static VisLabel playerPosLabel;
    }

    @Override
    protected void create() {
        super.create();

        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.Screen.framebuffer_width, Config.Screen.framebuffer_height);
        this.worldCamera.update();

        this.scene = new Scene(engine);
        this.renderSystem = engine.getSystem(RenderSystem.class);
        this.animationSystem = engine.getSystem(AnimationSystem.class);

        var width = Config.Screen.framebuffer_width;
        var height = Config.Screen.framebuffer_height;
        this.map = EntityFactory.createMap(engine, width, height);

        this.player = EntityFactory.createPlayer(engine, Point.at(10, 10));

        var viewport = new ScreenViewport(windowCamera);
        UI.stage = new Stage(viewport, batch);
        createUserInterfaceElements();

        addInputProcessor(UI.stage);
        addInputProcessor(input);
    }

    @Override
    public void dispose() {
        if (UI.stage != null) {
            UI.stage.dispose();
        }
        super.dispose();
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
        updateUserInterfaceElements();

        var speed = 200f;
        var moveAmount = speed * delta;
        var mover = player.getComponent(Mover.class);
        if      (Gdx.input.isKeyPressed(Keys.LEFT))  mover.speed.x -= moveAmount;
        else if (Gdx.input.isKeyPressed(Keys.RIGHT)) mover.speed.x += moveAmount;

        super.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        var frameBuffer = Game.instance.frameBuffer;
        frameBuffer.begin();
        {
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

            var tilemap = Mappers.tilemaps.get(map);
            if (tilemap != null) {
                batch.setProjectionMatrix(worldCamera.combined);
                batch.begin();
                tilemap.render(batch, Point.zero());
                batch.end();
            }

            renderSystem.render(worldCamera, batch, assets.shapes);
            animationSystem.render(worldCamera, batch);

            if (Config.Debug.general) {
                batch.setProjectionMatrix(worldCamera.combined);
                batch.begin();
                animationSystem.render(assets.shapes);
                batch.end();
            }
        }
        frameBuffer.end();

        var batch = assets.batch;
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            ScreenUtils.clear(Color.BLACK);

            var frameBufferRegion = Game.instance.frameBufferRegion;
            batch.draw(frameBufferRegion, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        }
        batch.end();

        UI.stage.draw();
    }

    private void createUserInterfaceElements() {
        UI.fpsLabel = new VisLabel("0 fps");
        UI.playerPosLabel = new VisLabel("(0, 0)");

        var testButton = new VisTextButton("test");
        testButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "clicked");
            }
        });

        var table = new VisTable();
        table.padLeft(10f);
        table.left().top();
        table.defaults().align(Align.left);
        table.add(UI.fpsLabel).expandX().row();
        table.add(UI.playerPosLabel).expandX().row();
        table.add(testButton).row();
        table.setFillParent(true);

        UI.stage.addActor(table);
    }

    private void updateUserInterfaceElements() {
        var position = Mappers.positions.get(player).position();
        UI.fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " fps");
        UI.playerPosLabel.setText(String.format("(%.1f, %.1f)", position.x, position.y));
    }

    private final InputAdapter input = new InputAdapter() {

        boolean leftMouseDown = false;
        boolean rightMouseDown = false;
        boolean middleMouseDown = false;

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            switch (button) {
                case Buttons.LEFT -> leftMouseDown = true;
                case Buttons.RIGHT -> rightMouseDown = true;
                case Buttons.MIDDLE -> middleMouseDown = true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            switch (button) {
                case Buttons.LEFT -> leftMouseDown = false;
                case Buttons.RIGHT -> rightMouseDown = false;
                case Buttons.MIDDLE -> middleMouseDown = false;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (middleMouseDown) {
                worldCamera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                worldCamera.update();
            } else {
                worldCamera.unproject(pointerPos.set(screenX, screenY, 0));
                var tilemap = Mappers.tilemaps.get(map);
                if (tilemap != null) {
                    var x = (int) Calc.floor(pointerPos.x) / tilemap.tileSize();
                    var y = (int) Calc.floor(pointerPos.y) / tilemap.tileSize();
                    var region = (TextureRegion) null;
                    if (leftMouseDown) {
                        region = assets.pixelRegion;
                    }
                    tilemap.setCell(x, y, region);

                    var collider = Mappers.colliders.get(map);
                    if (collider != null && collider.shape() == Collider.Shape.grid) {
                        var cellFilled = (region != null);
                        collider.setCell(x, y, cellFilled);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            // TODO - highlight hovered cell in tilemap
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (amountY != 0) {
                var zoomSpeed = 0.5f;
                var sign = Calc.sign(amountY);
                worldCamera.zoom = Calc.eerp(worldCamera.zoom, worldCamera.zoom + sign * 0.1f, zoomSpeed);
                worldCamera.zoom = Calc.clampf(worldCamera.zoom, 0.1f, 8f);
                return true;
            }
            return false;
        }
    };

}

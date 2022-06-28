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
import zendo.games.grotto.scene.components.Collider;
import zendo.games.grotto.scene.components.Mappers;
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

    private boolean isJumping = false;

    static class UI {
        static Stage stage;

        static VisLabel fpsLabel;
        static VisLabel playerPosLabel;
        static VisLabel playerSpeedLabel;
        static VisLabel playerColliderLabel;
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

        // place a floor and walls
        var tilemap = Mappers.tilemaps.get(map);
        tilemap.setCells(0, 0, tilemap.cols(), 1, assets.pixelRegion);
        tilemap.setCells(0, 0, 1, tilemap.rows(), assets.pixelRegion);
        tilemap.setCells(tilemap.cols() - 1, 0, 1, tilemap.rows(), assets.pixelRegion);
        var collider = Mappers.colliders.get(map);
        collider.setCells(0, 0, tilemap.cols(), 1, true);
        collider.setCells(0, 0, 1, tilemap.rows(), true);
        collider.setCells(tilemap.cols() - 1, 0, 1, tilemap.rows(), true);

        this.player = EntityFactory.createPlayer(engine, Point.at(10, 20));
        var mover = Mappers.movers.get(player);
        mover.speed.set(100, 200);

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

        var leftPressed  = Gdx.input.isKeyPressed(Keys.LEFT)  || Gdx.input.isKeyPressed(Keys.A);
        var rightPressed = Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D);
        var spacePressed = Gdx.input.isKeyPressed(Keys.SPACE);

        var mover = Mappers.movers.get(player);

        // horizontal movement
        {
            var inputDir = leftPressed ? -1 : rightPressed ? 1 : 0;

            var acceleration_ground = 700f;
            mover.speed.x += inputDir * acceleration_ground * delta;

            var max_speed = 70;
            if (Calc.abs(mover.speed.x) > max_speed) {
                var facing = Calc.sign(mover.speed.x);
                mover.speed.x = Calc.approach(mover.speed.x, facing * max_speed, 2000 * delta);
            }
        }

        // vertical movement
        {
            if (spacePressed && !isJumping) {
                isJumping = true;

                var jump_force = 150;
                mover.speed.y = jump_force;

                // squash and stretch
                var anim = Mappers.animators.get(player);
                anim.scale.set(0.8f, 1.6f);
            }
            if (isJumping && mover.isOnGround()) {
                isJumping = false;
            }
        }

        super.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        var frameBuffer = Game.instance.frameBuffer;
        frameBuffer.begin();
        {
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            renderSystem.render(worldCamera, batch, assets.shapes);
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
        var textColor = Color.FOREST.cpy();
        UI.fpsLabel = new VisLabel("0 fps");
        UI.fpsLabel.setColor(textColor);

        UI.playerPosLabel = new VisLabel("(0, 0)");
        UI.playerPosLabel.setColor(textColor);

        UI.playerSpeedLabel = new VisLabel("(0, 0)");
        UI.playerSpeedLabel.setColor(textColor);

        UI.playerColliderLabel = new VisLabel("[0, 0 : 0, 0]");
        UI.playerColliderLabel.setColor(textColor);

        var testButton = new VisTextButton("test");
        testButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "clicked");
            }
        });

        var table = new VisTable();
        table.padLeft(40f);
        table.left().top();
        table.defaults().align(Align.left);
        table.add(UI.fpsLabel).expandX().row();
        table.add(UI.playerPosLabel).expandX().row();
        table.add(UI.playerSpeedLabel).expandX().row();
        table.add(UI.playerColliderLabel).expandX().row();
        table.add(testButton).row();
        table.setFillParent(true);

        UI.stage.addActor(table);
    }

    private void updateUserInterfaceElements() {
        UI.fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " fps");

        var position = Mappers.positions.get(player).position();
        UI.playerPosLabel.setText(String.format("(%.1f, %.1f)", position.x, position.y));

        var mover = Mappers.movers.get(player);
        UI.playerSpeedLabel.setText(String.format("(%.1f, %.1f)", mover.speed.x, mover.speed.y));

        var collider = Mappers.colliders.get(player).rect();
        UI.playerColliderLabel.setText(String.format("[%d, %d : %d, %d]", collider.x, collider.y, collider.w, collider.h));
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

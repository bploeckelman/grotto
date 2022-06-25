package zendo.games.grotto.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import zendo.games.grotto.Config;
import zendo.games.grotto.scene.Scene;
import zendo.games.grotto.scene.components.CameraControllerComponent;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.components.PositionComponent;
import zendo.games.grotto.scene.factories.EntityFactory;
import zendo.games.grotto.scene.systems.AnimationSystem;
import zendo.games.grotto.scene.systems.RenderSystem;
import zendo.games.grotto.utils.Point;

import static com.badlogic.gdx.Input.Keys;

public class MapScreen extends BaseScreen {

    Scene scene;
    RenderSystem renderSystem;
    AnimationSystem animationSystem;

    Entity player;
    CameraControllerComponent cameraController;


    @Override
    protected void create() {
        super.create();
        this.scene = new Scene(engine);
        this.renderSystem = engine.getSystem(RenderSystem.class);
        this.animationSystem = engine.getSystem(AnimationSystem.class);

        var width = Gdx.graphics.getWidth() / 2;
        var height = Gdx.graphics.getHeight() / 2;
        var map = EntityFactory.createMap(engine, width, height);
        var shape = Mappers.shapes.get(map);
        if (shape.shape() instanceof Rectangle rect) {
            rect.setPosition(width - width / 2f, height - height / 2f);
        }

        this.player = EntityFactory.createPlayer(engine, Point.at(width, height));

        if (worldCamera instanceof OrthographicCamera camera) {
            var cameraEntity = engine.createEntity();
            cameraController = new CameraControllerComponent(camera, tween);
            cameraEntity.add(cameraController);
        }
    }

    @Override
    public void update(float delta) {
        scene.update(delta);

        var speed = 200f;
        var moveAmount = speed * delta;
        var playerPos = player.getComponent(PositionComponent.class).position();
        if      (Gdx.input.isKeyPressed(Keys.LEFT))  playerPos.x -= moveAmount;
        else if (Gdx.input.isKeyPressed(Keys.RIGHT)) playerPos.x += moveAmount;
        if      (Gdx.input.isKeyPressed(Keys.UP))    playerPos.y += moveAmount;
        else if (Gdx.input.isKeyPressed(Keys.DOWN))  playerPos.y -= moveAmount;

        cameraController.target(playerPos.x, playerPos.y);
        cameraController.update(delta);

        super.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        renderSystem.render(worldCamera, batch, assets.shapes);
        animationSystem.render(worldCamera, batch);

        if (Config.Debug.general) {
            animationSystem.render(assets.shapes);
        }
    }

}

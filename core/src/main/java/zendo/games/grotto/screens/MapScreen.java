package zendo.games.grotto.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import zendo.games.grotto.Config;
import zendo.games.grotto.scene.Scene;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.factories.EntityFactory;
import zendo.games.grotto.scene.systems.AnimationSystem;
import zendo.games.grotto.scene.systems.RenderSystem;
import zendo.games.grotto.utils.Point;

public class MapScreen extends BaseScreen {

    Scene scene;
    RenderSystem renderSystem;
    AnimationSystem animationSystem;

    Entity player;

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
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
        animationSystem.update(delta);
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

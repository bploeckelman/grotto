package zendo.games.grotto.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import zendo.games.grotto.scene.Scene;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.factories.EntityFactory;
import zendo.games.grotto.scene.systems.RenderSystem;

public class MapScreen extends BaseScreen {

    Scene scene;
    RenderSystem renderSystem;

    @Override
    protected void create() {
        super.create();
        this.scene = new Scene(engine);
        this.renderSystem = engine.getSystem(RenderSystem.class);

        var width = Gdx.graphics.getWidth() / 2;
        var height = Gdx.graphics.getHeight() / 2;
        var map = EntityFactory.createMap(engine, width, height);
        var shape = Mappers.shapes.get(map);
        if (shape.shape() instanceof Rectangle rect) {
            rect.setPosition(width - width / 2f, height - height / 2f);
        }
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
        super.update(delta);
    }

    @Override
    public void render(float delta) {
        renderSystem.render(worldCamera, batch, assets.shapes);
    }

}

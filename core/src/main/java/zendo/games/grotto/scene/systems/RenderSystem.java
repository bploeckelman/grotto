package zendo.games.grotto.scene.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.scene.components.Families;
import zendo.games.grotto.scene.components.Mappers;

public class RenderSystem extends EntitySystem {

    public void render(Camera camera, SpriteBatch batch, ShapeDrawer shapeDrawer) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            // draw shape components
            var entities = getEngine().getEntitiesFor(Families.shapes);
            for (var entity : entities) {
                var component = Mappers.shapes.get(entity);
                component.render(shapeDrawer, Color.WHITE);
            }

            // draw sprite components
            // ...
        }
        batch.end();
    }

}

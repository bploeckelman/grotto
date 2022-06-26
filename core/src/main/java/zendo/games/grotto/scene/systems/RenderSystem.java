package zendo.games.grotto.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectSet;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.components.RenderableComponent;
import zendo.games.grotto.scene.components.ShapeComponent;

public class RenderSystem extends EntitySystem implements EntityListener {

    private final ObjectSet<ShapeComponent> shapeComponents = new ObjectSet<>();
    private final ObjectSet<RenderableComponent> renderableComponents = new ObjectSet<>();

    @Override
    public void entityAdded(Entity entity) {
        var shape = Mappers.shapes.get(entity);
        if (shape != null) {
            shapeComponents.add(shape);
        }

        var renderable = Mappers.renderables.get(entity);
        if (renderable != null) {
            renderableComponents.add(renderable);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        var shape = Mappers.shapes.get(entity);
        if (shape != null) {
            shapeComponents.remove(shape);
        }

        var renderable = Mappers.renderables.get(entity);
        if (renderable != null) {
            renderableComponents.remove(renderable);
        }
    }

    public void render(Camera camera, SpriteBatch batch, ShapeDrawer shapeDrawer) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            for (var shape : shapeComponents) {
                shape.render(shapeDrawer, Color.WHITE);
            }

            // draw sprite components
            for (var renderable : renderableComponents) {
                var bounds = renderable.bounds();
                batch.draw(renderable.region(), bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
        batch.end();
    }

}

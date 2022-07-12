package zendo.games.grotto.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectSet;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zendo.games.grotto.Config;
import zendo.games.grotto.scene.components.*;
import zendo.games.grotto.utils.Point;

public class RenderSystem extends EntitySystem implements EntityListener {

    private final ObjectSet<Shape> shapes = new ObjectSet<>();
    private final ObjectSet<TextureComponent> textures = new ObjectSet<>();
    private final ObjectSet<Collider> colliders = new ObjectSet<>();
    private final ObjectSet<Tilemap> tilemaps = new ObjectSet<>();
    private final ObjectSet<Animator> animators = new ObjectSet<>();

    @Override
    public void entityAdded(Entity entity) {
        var shape = Mappers.shapes.get(entity);
        if (shape != null) {
            shapes.add(shape);
        }

        var renderable = Mappers.textures.get(entity);
        if (renderable != null) {
            textures.add(renderable);
        }

        var collider = Mappers.colliders.get(entity);
        if (collider != null) {
            colliders.add(collider);
        }

        var tilemap = Mappers.tilemaps.get(entity);
        if (tilemap != null) {
            tilemaps.add(tilemap);
        }

        var animator = Mappers.animators.get(entity);
        if (animator != null) {
            animators.add(animator);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        var shape = Mappers.shapes.get(entity);
        if (shape != null) {
            shapes.remove(shape);
        }

        var renderable = Mappers.textures.get(entity);
        if (renderable != null) {
            textures.remove(renderable);
        }

        var collider = Mappers.colliders.get(entity);
        if (collider != null) {
            colliders.remove(collider);
        }

        var tilemap = Mappers.tilemaps.get(entity);
        if (tilemap != null) {
            tilemaps.remove(tilemap);
        }

        var animator = Mappers.animators.get(entity);
        if (animator != null) {
            animators.remove(animator);
        }
    }

    public void render(Camera camera, SpriteBatch batch, ShapeDrawer shapeDrawer) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            int extents = 1000;
            int stepSize = 8;
            boolean flip = false;
            for (int y = -extents; y < extents; y += stepSize) {
                for (int x = -extents; x < extents; x += stepSize) {
                    shapeDrawer.filledRectangle(
                            x, y, stepSize, stepSize,
                            flip ? Color.DARK_GRAY : Color.GRAY);
                    flip = !flip;
                }
                flip = !flip;
            }

            for (var tilemap : tilemaps) {
                tilemap.render(batch, Point.Zero);
            }

            for (var shape : shapes) {
                shape.render(shapeDrawer, Color.WHITE);
            }

            for (var renderable : textures) {
                var bounds = renderable.bounds();
                batch.draw(renderable.region(), bounds.x, bounds.y, bounds.width, bounds.height);
            }

            for (var animator : animators) {
                animator.render(batch);
            }

            if (Config.Debug.draw_anim_bounds || Config.Debug.general) {
                for (var animator : animators) {
                    animator.render(shapeDrawer);
                }
            }

            if (Config.Debug.draw_colliders || Config.Debug.general) {
                for (var collider : colliders) {
                    collider.render(shapeDrawer);
                }
            }
        }
        batch.end();
    }

}

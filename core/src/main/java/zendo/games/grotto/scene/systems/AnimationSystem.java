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
import zendo.games.grotto.scene.components.Animator;
import zendo.games.grotto.scene.components.Families;
import zendo.games.grotto.scene.components.Mappers;

public class AnimationSystem extends EntitySystem implements EntityListener {

    private final ObjectSet<Animator> animators = new ObjectSet<>();

    @Override
    public void entityAdded(Entity entity) {
        var animator = Mappers.animators.get(entity);
        if (animator != null) {
            animators.add(animator);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        var animator = Mappers.animators.get(entity);
        if (animator != null) {
            animators.remove(animator);
        }
    }

    @Override
    public void update(float delta) {
        for (var animator : animators) {
            animator.update(delta);
        }
    }

    public void render(Camera camera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            var entities = getEngine().getEntitiesFor(Families.animators);
            for (var entity : entities) {
                var animator = Mappers.animators.get(entity);
                var position = Mappers.positions.get(entity);
                if (!animator.inValidState()) continue;

                var sprite = animator.sprite();
                var anim = sprite.animations.get(animator.animationIndex());
                var frame = anim.frames.get(animator.frameIndex());

                batch.setColor(animator.tint());
                batch.draw(frame.image,
                        position.x() - animator.sprite().origin.x,
                        position.y() - animator.sprite().origin.y,
                        sprite.origin.x,
                        sprite.origin.y,
                        frame.image.getRegionWidth(),
                        frame.image.getRegionHeight(),
                        animator.scale.x, animator.scale.y,
                        animator.rotation
                );
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
        batch.end();
    }

    public void render(ShapeDrawer shapes) {
        var entities = getEngine().getEntitiesFor(Families.animators);
        for (var entity : entities) {
            var animator = Mappers.animators.get(entity);
            var position = Mappers.positions.get(entity);
            if (!animator.inValidState()) continue;

            var sprite = animator.sprite();
            var anim = sprite.animations.get(animator.animationIndex());
            var frame = anim.frames.get(animator.frameIndex());

            // draw entity position
            var radius = 1f;
            shapes.filledCircle(position.position(), radius, Color.MAGENTA);

            if (!Config.Debug.draw_anim_bounds) continue;

            // draw anim image bounds
            var lineWidth = 1f;
            var x = position.x() - sprite.origin.x;
            var y = position.y() - sprite.origin.y;
            var w = frame.image.getRegionWidth();
            var h = frame.image.getRegionHeight();
            shapes.rectangle(x, y, w, h, Color.YELLOW, lineWidth);
        }
    }

}

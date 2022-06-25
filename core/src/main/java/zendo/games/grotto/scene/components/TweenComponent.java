package zendo.games.grotto.scene.components;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

public record TweenComponent(Tween tween) implements Component, EntityListener {
    @Override
    public void entityAdded(Entity entity) {
        // nothing to do here
    }

    // TODO - not sure this is necessary, but if components are pooled than the tween
    //  wouldn't get killed when the entity or component get destroyed and whatever new
    //  entity reuses the old entity's object would continue tweening.
    //  Might need to move this into a system of some sort instead to use component added/removed hooks
    @Override
    public void entityRemoved(Entity entity) {
        var component = entity.getComponent(TweenComponent.class);
        if (component.tween != null) {
            component.tween.kill();
        }

    }
}

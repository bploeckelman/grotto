package zendo.games.grotto.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectSet;
import zendo.games.grotto.scene.components.Mappers;

public class UpdateSystem extends EntitySystem implements EntityListener {

    private final ObjectSet<Entity> entities = new ObjectSet<>();

    @Override
    public void entityAdded(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public void update(float delta) {
        for (var entity : entities) {
            var player  = Mappers.players.get(entity);
            if (player != null) {
                player.update(delta);
            }
        }
    }
}

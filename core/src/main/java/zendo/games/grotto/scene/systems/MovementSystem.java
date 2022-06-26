package zendo.games.grotto.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectSet;
import zendo.games.grotto.scene.components.Mappers;
import zendo.games.grotto.scene.components.MoverComponent;

public class MovementSystem extends EntitySystem implements EntityListener {

    private final ObjectSet<MoverComponent> movers = new ObjectSet<>();

    @Override
    public void entityAdded(Entity entity) {
        var mover = Mappers.movers.get(entity);
        if (mover != null) {
            movers.add(mover);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        var mover = Mappers.movers.get(entity);
        if (mover != null) {
            movers.remove(mover);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (var mover : movers) {
            mover.update(deltaTime);
        }
    }

}

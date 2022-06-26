package zendo.games.grotto.scene.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import zendo.games.grotto.scene.components.*;
import zendo.games.grotto.utils.Point;

public class EntityFactory {

    public static Entity createMap(Engine engine, int width, int height) {
        var entity = engine.createEntity();
        {
            var name = new NameComponent("map");
            var map = new MapComponent();
            var bounds = new BoundsComponent(width, height);

            var recti = bounds.rect();
            var rect = new Rectangle(recti.x, recti.y, recti.w, recti.h);
            var shape = new ShapeComponent(rect);

            entity.add(name);
            entity.add(map);
            entity.add(bounds);
            entity.add(shape);

            engine.addEntity(entity);
        }
        return entity;
    }

    public static Entity createPlayer(Engine engine, Point playerPosition) {
        var entity = engine.createEntity();
        {
            var name = new NameComponent("player");
            var position = new PositionComponent(playerPosition);
            var animator = new AnimatorComponent("hero", "idle");
            var mover = new MoverComponent(position);

            // TODO - collider

            entity.add(name);
            entity.add(position);
            entity.add(mover);
            entity.add(animator);

            engine.addEntity(entity);
        }
        return entity;
    }

}

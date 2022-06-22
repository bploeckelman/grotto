package zendo.games.grotto.scene.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import zendo.games.grotto.scene.components.NameComponent;
import zendo.games.grotto.scene.components.RectiComponent;
import zendo.games.grotto.scene.components.ShapeComponent;

public class EntityFactory {

    public static Entity createMap(Engine engine, int width, int height) {
        var entity = engine.createEntity();
        {
            var name = new NameComponent("map");
            var bounds = new RectiComponent(width, height);

            var recti = bounds.rect();
            var rect = new Rectangle(recti.x, recti.y, recti.w, recti.h);
            var shape = new ShapeComponent(rect);

            entity.add(name);
            entity.add(bounds);
            entity.add(shape);

            engine.addEntity(entity);
        }
        return entity;
    }

}

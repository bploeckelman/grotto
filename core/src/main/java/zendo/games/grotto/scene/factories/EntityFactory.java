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
            var name = new Name("map");
            var map = new Map();
            var bounds = new Boundary(width, height);

            var tileSize = 8;
            var cols = width / tileSize;
            var rows = height / tileSize;
            var tilemap = new Tilemap(tileSize, cols, rows);
            var collider = Collider.makeGrid(entity, tileSize, cols, rows);
            collider.mask = Collider.Mask.solid;

            var recti = bounds.rect();
            var rect = new Rectangle(recti.x, recti.y, recti.w, recti.h);
            var shape = new Shape(rect);

            entity.add(name);
            entity.add(map);
            entity.add(tilemap);
            entity.add(collider);
            entity.add(bounds);
            entity.add(shape);

            engine.addEntity(entity);
        }
        return entity;
    }

    public static Entity createPlayer(Engine engine, Point playerPosition) {
        var entity = engine.createEntity();
        {
            var name = new Name("player");
            var position = new Position(playerPosition);
            var animator = new Animator("hero", "idle");
            var mover = new Mover(position);

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

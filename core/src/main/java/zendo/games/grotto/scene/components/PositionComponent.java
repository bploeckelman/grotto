package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import zendo.games.grotto.utils.Point;

public record PositionComponent(Vector2 position) implements Component {
    public PositionComponent() {
        this(Vector2.Zero.cpy());
    }
    public PositionComponent(Point point) {
        this(point.x, point.y);
    }
    public PositionComponent(float x, float y) {
        this(new Vector2(x, y));
    }
    public float x() {
        return position.x;
    }
    public float y() {
        return position.y;
    }
    public void x(float x) {
        position.x = x;
    }
    public void y(float y) {
        position.y = y;
    }
}

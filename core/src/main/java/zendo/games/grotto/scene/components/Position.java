package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import zendo.games.grotto.utils.Point;

public record Position(Vector2 value) implements Component {
    public Position() {
        this(Vector2.Zero.cpy());
    }
    public Position(Point point) {
        this(point.x, point.y);
    }
    public Position(float x, float y) {
        this(new Vector2(x, y));
    }
    public float x() {
        return value.x;
    }
    public float y() {
        return value.y;
    }
    public void x(float x) {
        value.x = x;
    }
    public void y(float y) {
        value.y = y;
    }
}

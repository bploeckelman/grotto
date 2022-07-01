package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import zendo.games.grotto.utils.Calc;
import zendo.games.grotto.utils.Point;

public class Mover implements Component {

    public float gravity;
    public float friction;
    public Vector2 speed;
    public Position position;
    public Collider collider;

    private final Vector2 remainder;

    public Mover(Position position) {
        this.position = position;
        this.speed = new Vector2();
        this.remainder = new Vector2();
    }

    public void update(float delta) {
        if (friction > 0 && isOnGround()) {
            speed.x = Calc.approach(speed.x, 0, friction * delta);
        }

        if (gravity != 0 && !isOnGround()) {
            speed.y += gravity * delta;
        }

        var totalMoveX = remainder.x + speed.x * delta;
        var totalMoveY = remainder.y + speed.y * delta;

        var intMoveX = (int) totalMoveX;
        var intMoveY = (int) totalMoveY;

        remainder.x = totalMoveX - intMoveX;
        remainder.y = totalMoveY - intMoveY;

        moveX(intMoveX);
        moveY(intMoveY);
    }

    public boolean moveX(int amount) {
        if (collider == null) {
            position.value().x += amount;
        } else {
            var sign = Calc.sign(amount);

            while (amount != 0) {
                var offset = Point.pool.obtain().set(sign, 0);
                var isSolid = collider.check(Collider.Mask.solid, offset);
                Point.pool.free(offset);

                if (isSolid) {
                    stopX();
                    return true;
                }

                amount -= sign;
                position.value().x += sign;
            }
        }

        return false;
    }

    public boolean moveY(int amount) {
        if (collider == null) {
            position.value().y += amount;
        } else {
            var sign = Calc.sign(amount);

            while (amount != 0) {
                var offset = Point.pool.obtain().set(0, sign);
                var isSolid = collider.check(Collider.Mask.solid, offset);
                Point.pool.free(offset);

                if (isSolid) {
                    stopY();
                    return true;
                }

                amount -= sign;
                position.value().y += sign;
            }
        }

        return false;
    }

    public void stop() {
        stopX();
        stopY();
    }

    public void stopX() {
        speed.x = 0f;
        remainder.x = 0f;
    }

    public void stopY() {
        speed.y = 0f;
        remainder.y = 0f;
    }

    public boolean isOnGround() {
        return onGround(-1);
    }

    public boolean onGround(int dist) {
        if (collider == null) {
            return false;
        }

        var offset = Point.pool.obtain().set(0, dist);
        var hitSolid = collider.check(Collider.Mask.solid, offset);
        Point.pool.free(offset);

        return hitSolid;
    }

}

package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import zendo.games.grotto.utils.Calc;

public class MoverComponent implements Component {

    public float gravity;
    public float friction;
    public Vector2 speed;
    public PositionComponent position;

    private final Vector2 remainder;

    public MoverComponent(PositionComponent position) {
        this.position = position;
        this.speed = new Vector2();
        this.remainder = new Vector2();
    }

    public boolean isOnGround() {
        return (position.y() == 0);
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
        var sign = Calc.sign(amount);
        while (amount != 0) {
            // TODO - check for collision with other colliders in movement direction
            if (sign < 0 || sign > 180) { // TEMP
                stopX();
                return true;
            }

            amount -= sign;
            position.position().x += sign;
        }

        return false;
    }

    public boolean moveY(int amount) {
        var sign = Calc.sign(amount);
        while (amount != 0) {
            // TODO - check for collision with other colliders in movement direction
            if (isOnGround()) {
                stopY();
                return true;
            }

            amount -= sign;
            position.position().y += sign;
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

}

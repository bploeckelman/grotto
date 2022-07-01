package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import zendo.games.grotto.utils.Calc;
import zendo.games.grotto.utils.VectorPool;

import static com.badlogic.gdx.Input.Keys;

public class Player implements Component {

    private final Entity entity;
    private final Vector2 groundedPosition;

    private final float jump_force = 180f;
    private final float accel_ground = 700f;
    private final float max_speed_ground = 80f;
    private final float gravity_normal = -400f;
    private final float gravity_peak = -100f;
    private final float maxfall_speed = -180f;
    private final float friction_ground = 450f;

    private int facing = 1;

    private float airTimer = 0;
    private float variableJumpTimer = 0;

    private boolean grounded = false;
    private boolean jumping = false;


    public Player(Entity entity) {
        this.entity = entity;
        this.groundedPosition = new Vector2();
    }

    public void update(float delta) {
        // get input
        var input = Gdx.input;
        var isLeftPressed  = input.isKeyPressed(Keys.LEFT);
        var isRightPressed = input.isKeyPressed(Keys.RIGHT);
        var isDownPressed  = input.isKeyPressed(Keys.DOWN);
        var isUpPressed    = input.isKeyPressed(Keys.UP);
        var isAPressed     = input.isKeyPressed(Keys.A);
        var isDPressed     = input.isKeyPressed(Keys.D);
        var isSPressed     = input.isKeyPressed(Keys.S);
        var isWPressed     = input.isKeyPressed(Keys.W);
        var isSpacePressed = input.isKeyPressed(Keys.SPACE);

        var isJumpPressed = isSpacePressed;
        var isMoveLeftPressed  = isLeftPressed  || isAPressed;
        var isMoveRightPressed = isRightPressed || isDPressed;
        var isMoveDownPressed  = isDownPressed  || isSPressed;
        var isMoveUpPressed    = isUpPressed    || isWPressed;

        // get components
        var position = Mappers.positions.get(entity);
        var animator = Mappers.animators.get(entity);
        var mover = Mappers.movers.get(entity);

        // update movement input
        var moveDirX = 0;
        var moveDirY = 0;
        {
            var sign = VectorPool.int2.obtain();
            if      (isMoveLeftPressed)  sign.x = -1;
            else if (isMoveRightPressed) sign.x =  1;
            if      (isMoveDownPressed)  sign.y = -1;
            else if (isMoveUpPressed)    sign.y =  1;
            moveDirX = sign.x;
            moveDirY = sign.y;
            VectorPool.int2.free(sign);
        }

        // update grounded state
        {
            var wasGrounded = grounded;
            grounded = mover.isOnGround();

            if (grounded) {
                // reset air timer
                airTimer = 0;

                if (!wasGrounded) {
                    // just landed, squash and stretch
                    animator.scale.set(1.4f, 0.6f);
                }

                groundedPosition.set(position.value());
            } else {
                // not touching ground, increase air timer
                airTimer += delta;
            }
        }

        // update falling speed
        {
            if (!grounded) {
                var gravity = gravity_normal;

                // slow at peak of jump
                var peakJumpThreshold = 12;
                if (isJumpPressed && Calc.abs(mover.speed.y) < peakJumpThreshold) {
                    gravity = gravity_peak;
                }

                // apply gravity
                mover.speed.y += gravity * delta;
            }

            // limit fall speed
            {
                var maxfall = maxfall_speed;

                if (mover.speed.y < maxfall) {
                    mover.speed.y = maxfall;
                }
            }
        }

        // update jumping
        {
            if (!jumping && isJumpPressed) {
                jumping = true;
                mover.speed.y = jump_force;
                animator.scale.set(0.8f, 1.6f);
            }
            if (jumping && mover.isOnGround()) {
                jumping = false;
            }
        }

        // update variable jumping
        {
            if (variableJumpTimer > 0) {
                variableJumpTimer -= delta;

                mover.speed.y = jump_force;
                if (!isJumpPressed) {
                    variableJumpTimer = 0;
                }
            }
        }

        // lerp scale back to normal
        {
            var sx = Calc.approach(Calc.abs(animator.scale.x), 1f, 4 * delta);
            var sy = Calc.approach(Calc.abs(animator.scale.y), 1f, 4 * delta);
            animator.scale.set(facing * sx, sy);
        }

        // horizontal movement
        {
            var inputDir = moveDirX;
            mover.speed.x += inputDir * accel_ground * delta;

            // limit max speed
            if (Calc.abs(mover.speed.x) > max_speed_ground) {
                mover.speed.x = Calc.approach(mover.speed.x, facing * max_speed_ground, 2000 * delta);
            }

            // apply friction
            if (inputDir == 0) {
                mover.speed.x = Calc.approach(mover.speed.x, 0, friction_ground * delta);
            }

            // update facing
            if (inputDir != 0) {
                facing = (int) Calc.sign(mover.speed.x);
            }
        }

        // set animation
        {
            if (grounded) {
                var isMovingHoriz = Calc.abs(mover.speed.x) > 4;
                if (isMovingHoriz) {
                    animator.play("run");
                } else {
                    animator.play("idle");
                }
            } else {
                if (mover.speed.y > 10) {
                    animator.play("jump");
                } else {
                    animator.play("fall");
                }
            }
        }
    }

}

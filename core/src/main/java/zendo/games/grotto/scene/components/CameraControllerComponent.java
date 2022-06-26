package zendo.games.grotto.scene.components;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import zendo.games.grotto.utils.Calc;

public class CameraControllerComponent implements Component {

    public OrthographicCamera camera;

    private final Vector3 target;
    private final Vector2 dist;

    private TweenManager tween;
    private TweenComponent transition;

    public CameraControllerComponent(OrthographicCamera camera, TweenManager tween) {
        this.camera = camera;
        this.target = Vector3.Zero.cpy();
        this.dist = Vector2.Zero.cpy();
        this.transition = null;
    }

    public Vector3 target() {
        return target;
    }

    public void target(float x, float y) {
        target.x = x;
        target.y = y;
    }

    public void update(float dt) {
        var targetPoint = Vector2.Zero.cpy();

        // update and constrain camera bounds if not currently transitioning,
        // otherwise transition tween will automatically update target until complete
        if (transition == null) {
            // get camera edges
            var cameraHorzEdge = (int) (camera.viewportWidth / 2f);
            var cameraVertEdge = (int) (camera.viewportHeight / 2f);

            // approach target with a speed based on distance
            var speed = 1f;
            dist.x = targetPoint.x - target.x;
            dist.y = targetPoint.y - target.y;
            var absDx = Calc.abs(dist.x);
            var absDy = Calc.abs(dist.y);
            var scaleX = (absDx > 80) ? 160 : 80f;
            var scaleY = (absDy > 20) ? 200 : 100f;
            target.x = Calc.approach(target.x, targetPoint.x, scaleX * speed * dt);
            target.y = Calc.approach(target.y, targetPoint.y, scaleY * speed * dt);

//            // lookup the room that the target is currently in (if any)
//            var room = worldMap.room(targetPoint);
//            if (room != null) {
//                // start a transition between rooms if we need to
//                // TODO: pause enemies in source and dest rooms during transition
//                if (lastRoom != null && lastRoom != room) {
//                    // find the bounds for both room and lastRoom;
//                    //  clamp two sets of coords so they are in bounds for both rooms
//                    //  then create a transition tween to move from lastRoom target to room target
//                    //  once complete, set lastRoom = room and destroy the transition component
//                    float lastTargetX, lastTargetY;
//                    float nextTargetX, nextTargetY;
//
//                    var lastBounds = worldMap.getRoomBounds(lastRoom);
//                    var nextBounds = worldMap.getRoomBounds(room);
//                    if (lastBounds != null && nextBounds != null) {
//                        // clamp the camera to within the last room's bounds
//                        var lastLeft   = lastBounds.x + cameraHorzEdge;
//                        var lastBottom = lastBounds.y + cameraVertEdge;
//                        var lastRight  = lastBounds.x + lastBounds.w - cameraHorzEdge;
//                        var lastTop    = lastBounds.y + lastBounds.h - cameraVertEdge;
//                        lastTargetX = MathUtils.clamp(camera.position.x, lastLeft, lastRight);
//                        lastTargetY = MathUtils.clamp(camera.position.y, lastBottom, lastTop);
//
//                        // clamp the camera to within the next room's bounds
//                        var nextLeft   = nextBounds.x + cameraHorzEdge;
//                        var nextBottom = nextBounds.y + cameraVertEdge;
//                        var nextRight  = nextBounds.x + nextBounds.w - cameraHorzEdge;
//                        var nextTop    = nextBounds.y + nextBounds.h - cameraVertEdge;
//                        nextTargetX = MathUtils.clamp(target.x, nextLeft, nextRight);
//                        nextTargetY = MathUtils.clamp(target.y, nextBottom, nextTop);
//
//                        // pause the player for the duration of the transition
//                        var player = world().first(Player.class);
//                        player.entity().active = false;
//
//                        // set the transition's starting point
//                        target.set(lastTargetX, lastTargetY, 0);
//
//                        // create a transition tween to move from last to next target
//                        // TODO: could probably add a self-destruct into the TweenComponent, or a generic onComplete callback where we can self.destroy()
//                        float duration = 1.66f;
//                        transition = entity.add(new TweenComponent(
//                                Tween.to(target, Vector3Accessor.XY, duration)
//                                        .target(nextTargetX, nextTargetY)
//                                        .setCallback((type, source) -> {
//                                            // restart the player
//                                            player.entity().active = true;
//                                            // update room reference
//                                            lastRoom = room;
//                                            // kill the transition component
//                                            transition.destroy();
//                                            transition = null;
//                                        })
//                                        .start(tween)
//                        ), TweenComponent.class);
//                    }
//                } else {
//                    // keep the camera inside the room's bounds
//                    var map = Game.instance.engine.getEntitiesFor(Families.maps).first();
//                    var boundsComponent = map.getComponent(BoundsComponent.class);
//                    if (boundsComponent != null) {
//                        var bounds = boundsComponent.rect();
//                        // clamp the camera to within the current room's bounds
//                        var left   = bounds.x + cameraHorzEdge;
//                        var bottom = bounds.y + cameraVertEdge;
//                        var right  = bounds.x + bounds.w - cameraHorzEdge;
//                        var top    = bounds.y + bounds.h - cameraVertEdge;
//                        target.x = MathUtils.clamp(target.x, left, right);
//                        target.y = MathUtils.clamp(target.y, bottom, top);
//                    }
//                }
//            }
        }

        // move the camera to the currently calculated target position
        camera.position.set((int) target.x, (int) target.y, 0);
        camera.update();
    }

}

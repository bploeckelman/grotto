package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Family;

public class Families {
    // one off families
    public static Family names = Family.one(Name.class).get();
    public static Family maps = Family.one(Map.class).get();
    public static Family boundaries = Family.one(Boundary.class).get();
    public static Family movers = Family.one(Mover.class).get();
    public static Family shapes = Family.one(Shape.class).get();
    public static Family textures = Family.one(TextureComponent.class).get();
    public static Family animators = Family.one(Animator.class).get();
    public static Family colliders = Family.one(Collider.class).get();

    // system families
    public static Family renderSystem = Family.one(
            TextureComponent.class,
            Collider.class,
            Tilemap.class,
            Animator.class,
            Shape.class
    ).get();
}

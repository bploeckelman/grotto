package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Family;

public class Families {
    // one off families
    public static Family names = Family.one(NameComponent.class).get();
    public static Family maps = Family.one(MapComponent.class).get();
    public static Family bounds = Family.one(BoundsComponent.class).get();
    public static Family movers = Family.one(MoverComponent.class).get();
    public static Family shapes = Family.one(ShapeComponent.class).get();
    public static Family renderables = Family.one(RenderableComponent.class).get();
    public static Family animators = Family.one(AnimatorComponent.class).get();

    // system families
    public static Family renderSystem = Family.one(ShapeComponent.class, RenderableComponent.class).get();
}

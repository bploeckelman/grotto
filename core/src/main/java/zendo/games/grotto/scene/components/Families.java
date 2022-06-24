package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Family;

public class Families {
    public static Family names = Family.one(NameComponent.class).get();
    public static Family rectis = Family.one(RectiComponent.class).get();
    public static Family shapes = Family.one(ShapeComponent.class).get();
    public static Family animators = Family.one(AnimatorComponent.class).get();
}

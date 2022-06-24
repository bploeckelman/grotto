package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {
    public static ComponentMapper<NameComponent> names = ComponentMapper.getFor(NameComponent.class);
    public static ComponentMapper<RectiComponent> rectis = ComponentMapper.getFor(RectiComponent.class);
    public static ComponentMapper<ShapeComponent> shapes = ComponentMapper.getFor(ShapeComponent.class);
    public static ComponentMapper<AnimatorComponent> animators = ComponentMapper.getFor(AnimatorComponent.class);
}

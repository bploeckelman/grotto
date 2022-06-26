package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {
    public static ComponentMapper<NameComponent> names = ComponentMapper.getFor(NameComponent.class);
    public static ComponentMapper<MapComponent> maps = ComponentMapper.getFor(MapComponent.class);
    public static ComponentMapper<BoundsComponent> bounds = ComponentMapper.getFor(BoundsComponent.class);
    public static ComponentMapper<MoverComponent> movers = ComponentMapper.getFor(MoverComponent.class);
    public static ComponentMapper<ShapeComponent> shapes = ComponentMapper.getFor(ShapeComponent.class);
    public static ComponentMapper<AnimatorComponent> animators = ComponentMapper.getFor(AnimatorComponent.class);
    public static ComponentMapper<RenderableComponent> renderables = ComponentMapper.getFor(RenderableComponent.class);
    public static ComponentMapper<PositionComponent> positions = ComponentMapper.getFor(PositionComponent.class);
}

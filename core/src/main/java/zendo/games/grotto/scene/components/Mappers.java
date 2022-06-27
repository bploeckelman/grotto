package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {
    public static ComponentMapper<Name> names = ComponentMapper.getFor(Name.class);
    public static ComponentMapper<Map> maps = ComponentMapper.getFor(Map.class);
    public static ComponentMapper<Boundary> boundaries = ComponentMapper.getFor(Boundary.class);
    public static ComponentMapper<Mover> movers = ComponentMapper.getFor(Mover.class);
    public static ComponentMapper<Shape> shapes = ComponentMapper.getFor(Shape.class);
    public static ComponentMapper<Animator> animators = ComponentMapper.getFor(Animator.class);
    public static ComponentMapper<TextureComponent> textures = ComponentMapper.getFor(TextureComponent.class);
    public static ComponentMapper<Position> positions = ComponentMapper.getFor(Position.class);
    public static ComponentMapper<Tilemap> tilemaps = ComponentMapper.getFor(Tilemap.class);
    public static ComponentMapper<Collider> colliders = ComponentMapper.getFor(Collider.class);
}

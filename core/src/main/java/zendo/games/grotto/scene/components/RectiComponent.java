package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import zendo.games.grotto.utils.RectI;

public record RectiComponent(RectI rect) implements Component {
    public RectiComponent(int x, int y, int w, int h) {
        this(new RectI(x, y, w, h));
    }
    public RectiComponent(int w, int h) {
        this(new RectI(0, 0, w, h));
    }
}

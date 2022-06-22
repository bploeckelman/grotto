package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import space.earlygrey.shapedrawer.ShapeDrawer;

public record ShapeComponent(Shape2D shape) implements Component {

    public void render(ShapeDrawer drawer, Color color) {
        // TODO - pattern matching on class is a jdk 17 (preview) feature, use that when we upgrade jdks
        //  [switch (shape) { case Rectangle r -> drawer.rectangle(r, color) ...]

        if (shape instanceof Rectangle r) {
            drawer.rectangle(r, color, 2);
        }
        // TODO - add other handlers
        else {
            Gdx.app.error(ShapeComponent.class.getSimpleName(), "Unhandled Shape2D type: " + shape.getClass().getSimpleName());
        }
    }

}

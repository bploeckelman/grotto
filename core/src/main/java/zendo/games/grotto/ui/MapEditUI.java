package zendo.games.grotto.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class MapEditUI extends VisWindow {

    private boolean isShown;
    private VisLabel testLabel;

    private final Rectangle visibleBounds = new Rectangle();
    private final Rectangle hiddenBounds = new Rectangle();
    private MoveToAction showAction = new MoveToAction();
    private MoveToAction hideAction = new MoveToAction();

    public MapEditUI(OrthographicCamera camera) {
        super("Map Editor");

        var percentWidth = 0.2f;
        var percentHeight = 1f;
        visibleBounds.set(0f, 0f, percentWidth * camera.viewportWidth, percentHeight * camera.viewportHeight);
        hiddenBounds.set(visibleBounds);
        hiddenBounds.x -= visibleBounds.width;

        // set initial state
        isShown = false;
        var bounds = hiddenBounds;
        setSize(bounds.width, bounds.height);
        setPosition(bounds.x, bounds.y);

        // setup show/hide actions
        showAction.setDuration(0.075f);
        showAction.setPosition(visibleBounds.x, visibleBounds.y);
        showAction.setInterpolation(Interpolation.exp5In);

        hideAction.setDuration(0.05f);
        hideAction.setPosition(hiddenBounds.x, hiddenBounds.y);
        hideAction.setInterpolation(Interpolation.exp5In);

        align(Align.top | Align.center);
        pad(10f);

        setModal(false);
        setMovable(false);
        setKeepWithinStage(false);

        testLabel = new VisLabel("Label");
        add(testLabel);
    }

    public void toggle() {
        if (isShown) {
            hide();
        } else {
            show();
        }
    }

    public void hide() {
        if (!isShown) return;
        isShown = false;
        hideAction.reset();
        addAction(hideAction);
    }

    public void show() {
        if (isShown) return;
        isShown = true;
        showAction.reset();
        addAction(showAction);
    }

}

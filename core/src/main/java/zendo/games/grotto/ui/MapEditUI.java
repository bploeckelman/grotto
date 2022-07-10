package zendo.games.grotto.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import zendo.games.grotto.Game;
import zendo.games.grotto.utils.Calc;

import static com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

public class MapEditUI extends VisWindow {

    private boolean isShown;
    private VisImageButton activeTileButton;

    private final VisScrollPane tileScrollPane;

    private final Rectangle visibleBounds = new Rectangle();
    private final Rectangle hiddenBounds = new Rectangle();
    private final MoveToAction showAction = new MoveToAction();
    private final MoveToAction hideAction = new MoveToAction();

    public MapEditUI(OrthographicCamera camera) {
        super("Map Editor");

        var percentWidth = 0.2f;
        var percentHeight = 1f;
        visibleBounds.set(0f, 0f,
                percentWidth * camera.viewportWidth,
                percentHeight * camera.viewportHeight);
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

        align(Align.topLeft);
        padTop(20f);

        setModal(false);
        setMovable(false);
        setKeepWithinStage(false);

        var iconSize = 50f;

        var assets = Game.instance.assets;
        var tileScrollTable = new VisTable();
        tileScrollTable.padTop(iconSize).padBottom(10f);
        {
            // radio button style, only 1 checked at a time
            // TODO - being able to multi-select would probably be useful
            var group = new ButtonGroup<VisImageButton>();
            group.setMinCheckCount(0);
            group.setMaxCheckCount(1);

            var first = true;
            var numButtonsInRow = 0;
            var numButtonsPerRow = Calc.floor(visibleBounds.width / iconSize) - 1;
            for (var iconRegion : assets.atlas.getRegions()) {
                var style = getCustomImageButtonStyle(iconRegion, iconSize);
                var button = new VisImageButton(style);
//                button.setUserObject(...); // TODO - use this for serde?

                final var thisButton = button;
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        activeTileButton = thisButton;
                    }
                });

                // start with one button checked
                if (first) {
                    first = false;
                    button.setChecked(true);
                    activeTileButton = button;
                }

                group.add(button);

                // move to a new row if appropriate
                if (numButtonsInRow >= numButtonsPerRow) {
                    tileScrollTable.row();
                    numButtonsInRow = 0;
                }

                tileScrollTable.add(button);
                numButtonsInRow++;
            }
        }
        tileScrollPane = new VisScrollPane(tileScrollTable);
        tileScrollPane.setFillParent(true);
        tileScrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(tileScrollPane);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStage().setScrollFocus(null);
            }
        });
        this.add(tileScrollPane).padTop(10f).padBottom(10f);
    }

    private static Drawable defaultTileDrawable;
    public Drawable getActiveTileDrawable() {
        if (defaultTileDrawable == null) {
            defaultTileDrawable = new TextureRegionDrawable(Game.instance.assets.pixelRegion);
        }
        return (activeTileButton == null) ? defaultTileDrawable : activeTileButton.getImage().getDrawable();
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

    private VisImageButtonStyle getCustomImageButtonStyle(TextureRegion region, float size) {
        var originalStyle = VisUI.getSkin().get(VisImageButtonStyle.class);
        var drawable = new TextureRegionDrawable(region);
        var checkedBackgroundDrawable = new TextureRegionDrawable(Game.instance.assets.pixelRegion).tint(Color.GRAY);

        var style = new VisImageButtonStyle(originalStyle);
        style.checked = checkedBackgroundDrawable;
        style.imageUp = drawable;
        style.imageDown = drawable;
        style.imageDisabled = drawable;
        style.imageOver = drawable;
        style.imageChecked = drawable;
        style.imageCheckedOver = drawable;

        // force images to be a fixed size
        style.checked.setMinWidth(size);
        style.checked.setMinHeight(size);
        style.imageUp.setMinWidth(size);
        style.imageUp.setMinHeight(size);
        style.imageDown.setMinWidth(size);
        style.imageDown.setMinHeight(size);
        style.imageDisabled.setMinWidth(size);
        style.imageDisabled.setMinHeight(size);
        style.imageOver.setMinWidth(size);
        style.imageOver.setMinHeight(size);
        style.imageChecked.setMinWidth(size);
        style.imageChecked.setMinHeight(size);
        style.imageCheckedOver.setMinWidth(size);
        style.imageCheckedOver.setMinHeight(size);

        return style;
    }

}

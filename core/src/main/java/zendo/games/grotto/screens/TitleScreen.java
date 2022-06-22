package zendo.games.grotto.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import zendo.games.grotto.Game;
import zendo.games.grotto.utils.Calc;
import zendo.games.grotto.utils.Time;

public class TitleScreen extends BaseScreen {

    private TextureRegion logo;
    private BitmapFont font;
    private GlyphLayout layout;
    private float playPromptAlpha;
    private boolean drawPlayPrompt;

    @Override
    protected void create() {
        super.create();

        logo = assets.atlas.findRegion("libgdx");
        font = assets.font;
        layout = assets.layout;

        playPromptAlpha = 0f;
        drawPlayPrompt = false;
        Time.do_after_delay(1.5f, (params) -> drawPlayPrompt = true);
    }

    @Override
    public void hide() {
        drawPlayPrompt = false;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.justTouched()) {
            Game.instance.getScreenManager().pushScreen("map", "push");
        }

        if (drawPlayPrompt) {
            playPromptAlpha = Calc.min(playPromptAlpha + delta, 1f);
        }

        super.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(logo,
                0.5f * (Gdx.graphics.getWidth()  - logo.getRegionWidth()),
                0.5f * (Gdx.graphics.getHeight() - logo.getRegionHeight()));
        if (drawPlayPrompt) {
            var color = Color.LIGHT_GRAY.cpy();
            color.a = playPromptAlpha;

            layout.setText(font, "Click to continue...", color, Gdx.graphics.getWidth(), Align.center, false);
            font.draw(batch, layout, 0f, Gdx.graphics.getHeight() / 3f + layout.height / 2f);
        }
        batch.end();
    }
}

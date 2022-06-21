package zendo.games.grotto.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends BaseScreen {

    private TextureRegion logo;

    @Override
    protected void create() {
        logo = assets.atlas.findRegion("libgdx");
    }

    @Override
    public void hide() {

    }

    @Override
    public void update(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.begin();
        batch.draw(logo,
                0.5f * (Gdx.graphics.getWidth()  - logo.getRegionWidth()),
                0.5f * (Gdx.graphics.getHeight() - logo.getRegionHeight()));
        batch.end();
    }
}

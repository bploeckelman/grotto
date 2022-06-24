package zendo.games.grotto.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import zendo.games.grotto.sprites.Content;
import zendo.games.grotto.sprites.Sprite;
import zendo.games.grotto.utils.Calc;

public class AnimatorComponent implements Component {

    public enum LoopMode { none, loop }

    public Vector2 scale;
    public LoopMode mode;
    public float rotation;
    public float speed;

    private Color tint;
    private Sprite sprite;
    private int animationIndex;
    private int frameIndex;
    private float frameCounter;

    public AnimatorComponent(String spriteName) {
        scale = new Vector2(1f, 1f);
        tint = Color.WHITE.cpy();
        mode = LoopMode.loop;
        speed = 1;
        sprite = Content.findSprite(spriteName);
    }

    public AnimatorComponent(String spriteName, String animationName) {
        this(spriteName);
        play(animationName);
    }

    public void update(float delta) {
        if (!inValidState()) return;

        var anim = sprite.animations.get(animationIndex);
        var frame = anim.frames.get(frameIndex);

        // increment frame counter
        frameCounter += speed * delta;

        // move to next frame after duration
        while (frameCounter >= frame.duration) {
            // reset frame counter
            frameCounter -= frame.duration;

            // increment frame, adjust based on loop mode
            frameIndex++;
            switch (mode) {
                case none -> frameIndex = Calc.clampInt(frameIndex, 0, animation().frames.size() - 1);
                case loop -> {
                    if (frameIndex >= anim.frames.size()) {
                        frameIndex = 0;
                    }
                }
            }
        }
    }

    public void render(ShapeRenderer shapes) {
    }

    public Sprite sprite() {
        return sprite;
    }

    public float duration() {
        return (animation() != null) ? animation().duration() : 0f;
    }

    public Sprite.Anim animation() {
        if (sprite != null && animationIndex >= 0 && animationIndex < sprite.animations.size()) {
            return sprite.animations.get(animationIndex);
        }
        return null;
    }

    public int animationIndex() {
        return animationIndex;
    }

    public int frameIndex() {
        return frameIndex;
    }

    public Sprite.Frame frame() {
        return animation().frames.get(frameIndex);
    }

    public Color tint() {
        return tint;
    }

    public float getAlpha() {
        return tint.a;
    }

    public void setAlpha(float a) {
        tint.a = a;
    }

    public void setRGB(float r, float g, float b) {
        tint.set(r, g, b, tint.a);
    }

    public void setColor(float r, float g, float b, float a) {
        tint.set(r, g, b, a);
    }

    public AnimatorComponent play(String animation) {
        return play(animation, false);
    }

    public AnimatorComponent play(String animation, boolean restart) {
        if (sprite == null) {
            throw new GdxRuntimeException("No Sprite assigned to animator");
        }

        for (int i = 0; i < sprite.animations.size(); i++) {
            if (sprite.animations.get(i).name.equals(animation)) {
                if (animationIndex != i || restart) {
                    animationIndex = i;
                    frameIndex = 0;
                    frameCounter = 0;
                }
                break;
            }
        }
        return this;
    }

    public boolean inValidState() {
        return (sprite != null
            && animationIndex >= 0
            && animationIndex < sprite.animations.size()
            && frameIndex >= 0
            && frameIndex < sprite.animations.get(animationIndex).frames.size()
        );
    }

}

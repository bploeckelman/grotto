package zendo.games.grotto;

public class Config {
    public static class Debug {
        public static final boolean shaders = false;

        public static boolean general = false;
        public static boolean draw_anim_bounds = false;
        public static boolean draw_colliders = false;
    }
    public static class Screen {
        public static final int window_width = 1280;
        public static final int window_height = 720;
        public static final int framebuffer_width = 320;
        public static final int framebuffer_height = 180;
    }
}

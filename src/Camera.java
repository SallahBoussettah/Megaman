public class Camera {
    private float x;
    private float y;
    private int levelWidth;
    private int levelHeight;
    private float targetX;
    private float targetY;
    private float smoothFactor = 0.1f; // Lower for smoother camera, higher for more responsive
    
    public Camera(float x, float y, int levelWidth, int levelHeight) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }
    
    public void update(Player player) {
        // Calculate target position (center the camera on the player)
        targetX = player.x - Game.GAME_WIDTH / 2 + player.width / 2;
        targetY = player.y - Game.GAME_HEIGHT / 2 + player.height / 2;
        
        // Smoothly move camera towards target (lerp)
        x += (targetX - x) * smoothFactor;
        y += (targetY - y) * smoothFactor;
        
        // Apply camera bounds
        if (x < 0) {
            x = 0;
        } else if (x > levelWidth - Game.GAME_WIDTH) {
            x = levelWidth - Game.GAME_WIDTH;
        }
        
        if (y < 0) {
            y = 0;
        } else if (y > levelHeight - Game.GAME_HEIGHT) {
            y = levelHeight - Game.GAME_HEIGHT;
        }
    }
    
    public int getX() {
        return (int)x; // Cast to int to avoid subpixel rendering issues
    }
    
    public int getY() {
        return (int)y; // Cast to int to avoid subpixel rendering issues
    }
} 
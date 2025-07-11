import java.util.ArrayList;
import java.util.List;

public class Metall extends GameObject {
    // Metall constants
    public static final int METALL_WIDTH = 36;
    public static final int METALL_HEIGHT = 30;
    
    // Metall state
    public String direction;
    public boolean jumping;
    public int health;
    public List<Bullet> bullets;
    private long lastFired;
    
    public Metall(int x, int y) {
        super(x, y, METALL_WIDTH, METALL_HEIGHT);
        this.direction = "left";
        this.jumping = false;
        this.health = 1;
        this.bullets = new ArrayList<>();
        this.lastFired = System.currentTimeMillis();
        this.image = ResourceManager.metallImageLeft;
    }
    
    public void updateImage() {
        if (direction.equals("right")) {
            image = ResourceManager.metallImageRight;
        } else {
            image = ResourceManager.metallImageLeft;
        }
    }
    
    public void setShooting(Player player) {
        if (Math.abs(x - player.x) <= Game.TILE_SIZE * 4) {
            long now = System.currentTimeMillis();
            if (now - lastFired > 1000) {
                lastFired = now;
                bullets.add(new Bullet(this, -Bullet.BULLET_VELOCITY_Y));
                bullets.add(new Bullet(this, 0));
                bullets.add(new Bullet(this, Bullet.BULLET_VELOCITY_Y));
            }
        }
    }
    
    // Metall bullet class
    public static class Bullet extends GameObject {
        public static final int BULLET_WIDTH = 12;
        public static final int BULLET_HEIGHT = 12;
        public static final int BULLET_VELOCITY_X = 2;
        public static final int BULLET_VELOCITY_Y = 2;
        
        public Bullet(Metall metall, double velocityY) {
            super(0, 0, BULLET_WIDTH, BULLET_HEIGHT);
            if (metall.direction.equals("left")) {
                x = metall.x;
                y = metall.y + Game.TILE_SIZE / 2;
                velocityX = -BULLET_VELOCITY_X;
            } else {
                x = metall.x + metall.width;
                y = metall.y + Game.TILE_SIZE / 2;
                velocityX = BULLET_VELOCITY_X;
            }
            this.velocityY = velocityY;
            this.image = ResourceManager.metallBulletImage;
        }
    }
} 
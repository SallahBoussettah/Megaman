import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    // Player constants
    public static final int PLAYER_WIDTH = 42;
    public static final int PLAYER_HEIGHT = 48;
    public static final int PLAYER_JUMP_WIDTH = 52;
    public static final int PLAYER_JUMP_HEIGHT = 60;
    public static final int PLAYER_SHOOT_WIDTH = 62;
    public static final int PLAYER_JUMP_SHOOT_WIDTH = 58;
    public static final int PLAYER_VELOCITY_X = 5;
    public static final int PLAYER_VELOCITY_Y = -11;
    public static final int HEALTH_WIDTH = 16;
    public static final int HEALTH_HEIGHT = 4;
    
    // Player state
    public String direction;
    public boolean jumping;
    public boolean invincible;
    public boolean shooting;
    public int health;
    public int maxHealth;
    public List<Bullet> bullets;
    
    public Player(int x, int y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        this.direction = "right";
        this.jumping = false;
        this.invincible = false;
        this.shooting = false;
        this.maxHealth = 28;
        this.health = maxHealth;
        this.bullets = new ArrayList<>();
        this.image = ResourceManager.playerImageRight;
    }
    
    public void updateImage() {
        if (jumping && shooting) {
            if (direction.equals("right")) {
                image = ResourceManager.playerImageJumpShootRight;
                width = PLAYER_JUMP_SHOOT_WIDTH;
                height = PLAYER_JUMP_HEIGHT;
            } else {
                image = ResourceManager.playerImageJumpShootLeft;
                width = PLAYER_JUMP_SHOOT_WIDTH;
                height = PLAYER_JUMP_HEIGHT;
            }
        } else if (shooting) {
            if (direction.equals("right")) {
                image = ResourceManager.playerImageShootRight;
                width = PLAYER_SHOOT_WIDTH;
                height = PLAYER_HEIGHT;
            } else {
                image = ResourceManager.playerImageShootLeft;
                width = PLAYER_SHOOT_WIDTH;
                height = PLAYER_HEIGHT;
            }
        } else if (jumping) {
            if (direction.equals("right")) {
                image = ResourceManager.playerImageJumpRight;
                width = PLAYER_JUMP_WIDTH;
                height = PLAYER_JUMP_HEIGHT;
            } else {
                image = ResourceManager.playerImageJumpLeft;
                width = PLAYER_JUMP_WIDTH;
                height = PLAYER_JUMP_HEIGHT;
            }
        } else {
            if (direction.equals("right")) {
                image = ResourceManager.playerImageRight;
                width = PLAYER_WIDTH;
                height = PLAYER_HEIGHT;
            } else {
                image = ResourceManager.playerImageLeft;
                width = PLAYER_WIDTH;
                height = PLAYER_HEIGHT;
            }
        }
    }
    
    // Player bullet class
    public static class Bullet extends GameObject {
        public static final int BULLET_WIDTH = 16;
        public static final int BULLET_HEIGHT = 12;
        public static final int BULLET_VELOCITY_X = 8;
        
        public Bullet(Player player) {
            super(0, 0, BULLET_WIDTH, BULLET_HEIGHT);
            if (player.direction.equals("left")) {
                x = player.x;
                y = player.y + Game.TILE_SIZE / 2;
                velocityX = -BULLET_VELOCITY_X;
            } else {
                x = player.x + player.width;
                y = player.y + Game.TILE_SIZE / 2;
                velocityX = BULLET_VELOCITY_X;
            }
            this.image = ResourceManager.playerBulletImage;
        }
    }
} 
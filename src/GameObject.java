import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject extends Rectangle {
    protected double velocityX;
    protected double velocityY;
    protected BufferedImage image;
    
    public GameObject(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    // Override Rectangle's contains and intersects methods to work with our floating-point coordinates
    @Override
    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width && 
               y >= this.y && y < this.y + this.height;
    }
    
    @Override
    public boolean intersects(Rectangle r) {
        return r.x + r.width > this.x && 
               r.x < this.x + this.width && 
               r.y + r.height > this.y && 
               r.y < this.y + this.height;
    }
}
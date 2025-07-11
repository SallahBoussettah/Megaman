import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ResourceManager {
    // Player images
    public static BufferedImage playerImageRight;
    public static BufferedImage playerImageLeft;
    public static BufferedImage playerImageJumpRight;
    public static BufferedImage playerImageJumpLeft;
    public static BufferedImage playerImageShootRight;
    public static BufferedImage playerImageShootLeft;
    public static BufferedImage playerImageJumpShootRight;
    public static BufferedImage playerImageJumpShootLeft;
    public static BufferedImage playerBulletImage;
    
    // Enemy images
    public static BufferedImage metallImageRight;
    public static BufferedImage metallImageLeft;
    public static BufferedImage metallBulletImage;
    
    // Tile images
    public static BufferedImage floorTileImage;
    
    // Other images
    public static BufferedImage backgroundImage;
    public static BufferedImage healthImage;
    
    // Base path for resources
    private static String basePath = "";
    
    // Load all resources
    public static void loadResources() {
        try {
            // Try to determine the correct image path
            findImagePath();
            
            // Load player images
            playerImageRight = loadImage("megaman-right.png", Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT);
            playerImageLeft = loadImage("megaman-left.png", Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT);
            playerImageJumpRight = loadImage("megaman-right-jump.png", Player.PLAYER_JUMP_WIDTH, Player.PLAYER_JUMP_HEIGHT);
            playerImageJumpLeft = loadImage("megaman-left-jump.png", Player.PLAYER_JUMP_WIDTH, Player.PLAYER_JUMP_HEIGHT);
            playerImageShootRight = loadImage("megaman-right-shoot.png", Player.PLAYER_SHOOT_WIDTH, Player.PLAYER_HEIGHT);
            playerImageShootLeft = loadImage("megaman-left-shoot.png", Player.PLAYER_SHOOT_WIDTH, Player.PLAYER_HEIGHT);
            playerImageJumpShootRight = loadImage("megaman-right-jump-shoot.png", Player.PLAYER_JUMP_SHOOT_WIDTH, Player.PLAYER_JUMP_HEIGHT);
            playerImageJumpShootLeft = loadImage("megaman-left-jump-shoot.png", Player.PLAYER_JUMP_SHOOT_WIDTH, Player.PLAYER_JUMP_HEIGHT);
            playerBulletImage = loadImage("bullet.png", Player.Bullet.BULLET_WIDTH, Player.Bullet.BULLET_HEIGHT);
            
            // Load enemy images
            metallImageRight = loadImage("metall-right.png", Metall.METALL_WIDTH, Metall.METALL_HEIGHT);
            metallImageLeft = loadImage("metall-left.png", Metall.METALL_WIDTH, Metall.METALL_HEIGHT);
            metallBulletImage = loadImage("metall-bullet.png", Metall.Bullet.BULLET_WIDTH, Metall.Bullet.BULLET_HEIGHT);
            
            // Load tile images
            floorTileImage = loadImage("floor-tile.png", Game.TILE_SIZE, Game.TILE_SIZE);
            
            // Load other images
            backgroundImage = loadImage("background.png");
            healthImage = loadImage("health.png", Player.HEALTH_WIDTH, Player.HEALTH_HEIGHT);
            
        } catch (IOException e) {
            System.err.println("Error loading resources: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Try to find the correct image path
    private static void findImagePath() {
        // Try different possible paths
        String[] possiblePaths = {
            "images/",
            "JavaPy/images/",
            "../images/"
        };
        
        for (String path : possiblePaths) {
            File testFile = new File(path + "megaman-right.png");
            if (testFile.exists()) {
                basePath = path;
                System.out.println("Found images at: " + Paths.get(path).toAbsolutePath());
                return;
            }
        }
        
        System.err.println("Warning: Could not find images directory. Tried:");
        for (String path : possiblePaths) {
            System.err.println("  - " + Paths.get(path).toAbsolutePath());
        }
        System.err.println("Will try default path 'images/'");
        basePath = "images/";
    }
    
    // Load an image without scaling
    private static BufferedImage loadImage(String fileName) throws IOException {
        File file = new File(basePath + fileName);
        if (!file.exists()) {
            System.err.println("Image file not found: " + file.getAbsolutePath());
        }
        return ImageIO.read(file);
    }
    
    // Load an image with scaling
    private static BufferedImage loadImage(String fileName, int width, int height) throws IOException {
        BufferedImage originalImage = loadImage(fileName);
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.getGraphics().drawImage(originalImage, 0, 0, width, height, null);
        return scaledImage;
    }
} 
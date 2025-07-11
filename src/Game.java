import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game extends Canvas implements Runnable {
    // Game constants
    public static final int GAME_WIDTH = 512;
    public static final int GAME_HEIGHT = 512;
    public static final int TILE_SIZE = 32;
    public static final String TITLE = "Megaman Clone";
    
    // Level dimensions (much wider than screen)
    private static final int LEVEL_WIDTH = 1600; // 50 tiles wide
    private static final int LEVEL_HEIGHT = 512; // Same as game height
    
    // Game variables
    private boolean running = false;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics2D g;
    private KeyInput keyInput;
    
    // Game objects
    private Player player;
    private List<Metall> metalls;
    private List<Tile> tiles;
    private Camera camera;
    private Level level;
    
    // Timer for invincibility and shooting
    private long invincibleEndTime = 0;
    private long shootingEndTime = 0;
    
    public Game() {
        // Set up the window
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        
        // Create JFrame
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Initialize game
        init();
    }
    
    private void init() {
        // Load resources
        ResourceManager.loadResources();
        
        // Create key input handler
        keyInput = new KeyInput();
        addKeyListener(keyInput);
        
        // Initialize game objects
        player = new Player(TILE_SIZE * 2, GAME_HEIGHT / 2);
        metalls = new ArrayList<>();
        tiles = new ArrayList<>();
        
        // Create level and camera
        level = new Level(LEVEL_WIDTH, LEVEL_HEIGHT);
        level.createLevel(player);
        tiles = level.getTiles();
        metalls = level.getMetalls();
        
        camera = new Camera(0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
        
        // Set focus to the game canvas
        requestFocus();
    }
    
    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        // Game loop variables
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        // Game loop
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while (delta >= 1) {
                tick();
                delta--;
            }
            
            render();
            frames++;
            
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }
    
    private void tick() {
        // Check timers
        long currentTime = System.currentTimeMillis();
        if (player.invincible && currentTime > invincibleEndTime) {
            player.invincible = false;
        }
        
        if (player.shooting && currentTime > shootingEndTime) {
            player.shooting = false;
        }
        
        // Handle input
        if (keyInput.isKeyDown(KeyEvent.VK_UP) || keyInput.isKeyDown(KeyEvent.VK_W)) {
            if (!player.jumping) {
                player.velocityY = Player.PLAYER_VELOCITY_Y;
                player.jumping = true;
            }
        }
        
        if (keyInput.isKeyDown(KeyEvent.VK_LEFT) || keyInput.isKeyDown(KeyEvent.VK_A)) {
            player.velocityX = -Player.PLAYER_VELOCITY_X;
            player.direction = "left";
        }
        
        if (keyInput.isKeyDown(KeyEvent.VK_RIGHT) || keyInput.isKeyDown(KeyEvent.VK_D)) {
            player.velocityX = Player.PLAYER_VELOCITY_X;
            player.direction = "right";
        }
        
        if (keyInput.isKeyDown(KeyEvent.VK_X) || keyInput.isKeyDown(KeyEvent.VK_SPACE)) {
            setShooting();
        }
        
        // Move objects
        move();
        
        // Update camera
        camera.update(player);
    }
    
    private void setShooting() {
        if (!player.shooting) {
            player.shooting = true;
            player.bullets.add(new Player.Bullet(player));
            shootingEndTime = System.currentTimeMillis() + 250;
        }
    }
    
    private void setInvincible(int milliseconds) {
        player.invincible = true;
        invincibleEndTime = System.currentTimeMillis() + milliseconds;
    }
    
    private void move() {
        // X movement for player
        if (player.direction.equals("left") && player.velocityX < 0) {
            player.velocityX += Physics.FRICTION;
            if (Math.abs(player.velocityX) < 0.1) player.velocityX = 0;
        } else if (player.direction.equals("right") && player.velocityX > 0) {
            player.velocityX -= Physics.FRICTION;
            if (Math.abs(player.velocityX) < 0.1) player.velocityX = 0;
        } else {
            player.velocityX = 0;
        }
        
        // Move X first, then check collision
        player.x += player.velocityX;
        
        // Level boundaries for player
        if (player.x < 0) {
            player.x = 0;
        } else if (player.x + player.width > LEVEL_WIDTH) {
            player.x = LEVEL_WIDTH - player.width;
        }
        
        // Check X collision with tiles
        checkTileCollisionX(player);
        
        // Y movement for player
        player.velocityY += Physics.GRAVITY;
        
        // Terminal velocity to prevent falling too fast
        if (player.velocityY > 15) {
            player.velocityY = 15;
        }
        
        // Move Y, then check collision
        player.y += player.velocityY;
        
        // Bottom level boundary
        if (player.y + player.height > LEVEL_HEIGHT) {
            player.y = LEVEL_HEIGHT - player.height;
            player.jumping = false;
            player.velocityY = 0;
        }
        
        // Check Y collision with tiles
        checkTileCollisionY(player);
        
        // Check if player is stuck and try to resolve
        checkAndResolveStuck(player);
        
        // Handle player bullets
        Iterator<Player.Bullet> bulletIterator = player.bullets.iterator();
        while (bulletIterator.hasNext()) {
            Player.Bullet bullet = bulletIterator.next();
            bullet.x += bullet.velocityX;
            
            // Check collision with tiles
            boolean bulletHitTile = false;
            for (Tile tile : tiles) {
                if (bullet.intersects(tile)) {
                    bulletHitTile = true;
                    break;
                }
            }
            
            // Check collision with enemies
            Iterator<Metall> metallIterator = metalls.iterator();
            boolean bulletUsed = false;
            
            while (metallIterator.hasNext() && !bulletUsed) {
                Metall metall = metallIterator.next();
                if (metall.health > 0 && bullet.intersects(metall)) {
                    metall.health--;
                    bulletUsed = true;
                    if (metall.health <= 0) {
                        metallIterator.remove();
                    }
                }
            }
            
            // Remove bullet if used, hit tile, or out of bounds
            if (bulletUsed || bulletHitTile || bullet.x + bullet.width < 0 || bullet.x > LEVEL_WIDTH) {
                bulletIterator.remove();
            }
        }
        
        // Handle enemies
        for (Metall metall : metalls) {
            // Enemy direction based on player position
            if (player.x < metall.x) {
                metall.direction = "left";
            } else {
                metall.direction = "right";
            }
            
            // Enemy Y movement
            metall.velocityY += Physics.GRAVITY;
            metall.y += metall.velocityY;
            checkTileCollisionY(metall);
            
            // Check collision with player
            if (!player.invincible && player.intersects(metall)) {
                player.health--;
                setInvincible(1000);
            }
            
            // Enemy shooting - only if on screen
            if (isOnScreen(metall)) {
                metall.setShooting(player);
            }
            
            // Move enemy bullets
            Iterator<Metall.Bullet> enemyBulletIterator = metall.bullets.iterator();
            while (enemyBulletIterator.hasNext()) {
                Metall.Bullet bullet = enemyBulletIterator.next();
                bullet.x += bullet.velocityX;
                bullet.y += bullet.velocityY;
                
                // Check collision with tiles
                boolean bulletHitTile = false;
                for (Tile tile : tiles) {
                    if (bullet.intersects(tile)) {
                        bulletHitTile = true;
                        break;
                    }
                }
                
                // Check collision with player
                if (!player.invincible && player.intersects(bullet)) {
                    player.health -= 2;
                    setInvincible(1000);
                    enemyBulletIterator.remove();
                }
                // Remove bullet if hit tile or out of bounds
                else if (bulletHitTile || bullet.x + bullet.width < 0 || bullet.x > LEVEL_WIDTH) {
                    enemyBulletIterator.remove();
                }
            }
        }
        
        // Check if player is dead
        if (player.health <= 0) {
            // Reset player position and health
            player.x = TILE_SIZE * 2;
            player.y = GAME_HEIGHT / 2;
            player.health = player.maxHealth;
        }
    }
    
    private boolean isOnScreen(GameObject obj) {
        return obj.x + obj.width > camera.getX() && 
               obj.x < camera.getX() + GAME_WIDTH &&
               obj.y + obj.height > camera.getY() && 
               obj.y < camera.getY() + GAME_HEIGHT;
    }
    
    private void checkTileCollisionX(GameObject character) {
        // Create a slightly expanded collision box for more forgiving collisions
        Rectangle predictedPosition = new Rectangle(
            (int)(character.x + character.velocityX), 
            (int)character.y, 
            character.width, 
            character.height
        );
        
        for (Tile tile : tiles) {
            if (predictedPosition.intersects(tile)) {
                if (character.velocityX < 0) { // going left
                    character.x = tile.x + tile.width; // right side of tile
                } else if (character.velocityX > 0) { // going right
                    character.x = tile.x - character.width; // left side of tile
                }
                character.velocityX = 0;
                break;
            }
        }
    }
    
    private void checkTileCollisionY(GameObject character) {
        // Create a slightly expanded collision box for more forgiving collisions
        Rectangle predictedPosition = new Rectangle(
            (int)character.x, 
            (int)(character.y + character.velocityY), 
            character.width, 
            character.height
        );
        
        // Check for ceiling collision first (prevents getting stuck in ceiling)
        if (character.velocityY < 0) {
            for (Tile tile : tiles) {
                if (predictedPosition.intersects(tile)) {
                    character.y = tile.y + tile.height; // bottom of tile
                    character.velocityY = 0;
                    break;
                }
            }
        }
        // Then check for floor collision
        else if (character.velocityY > 0) {
            boolean collision = false;
            float closestTileY = Float.MAX_VALUE;
            
            // Find the closest tile below
            for (Tile tile : tiles) {
                if (predictedPosition.intersects(tile)) {
                    collision = true;
                    // Find the closest tile (in case of multiple collisions)
                    if (tile.y < closestTileY) {
                        closestTileY = tile.y;
                    }
                }
            }
            
            if (collision) {
                character.y = (int)(closestTileY - character.height); // top of closest tile
                character.velocityY = 0;
                if (character instanceof Player) {
                    ((Player) character).jumping = false;
                } else if (character instanceof Metall) {
                    ((Metall) character).jumping = false;
                }
            }
        }
    }
    
    // Helper method to check if the player is stuck and try to resolve it
    private void checkAndResolveStuck(GameObject character) {
        // Check if character is intersecting with any tiles
        for (Tile tile : tiles) {
            if (character.intersects(tile)) {
                // Try to move character out of the tile
                int leftDist = (int)(character.x + character.width - tile.x);
                int rightDist = (int)(tile.x + tile.width - character.x);
                int topDist = (int)(character.y + character.height - tile.y);
                int bottomDist = (int)(tile.y + tile.height - character.y);
                
                // Find smallest distance to move
                int minDist = Math.min(Math.min(leftDist, rightDist), Math.min(topDist, bottomDist));
                
                if (minDist == leftDist) {
                    character.x = tile.x - character.width - 1;
                } else if (minDist == rightDist) {
                    character.x = tile.x + tile.width + 1;
                } else if (minDist == topDist) {
                    character.y = tile.y - character.height - 1;
                    character.velocityY = 0;
                    if (character instanceof Player) {
                        ((Player) character).jumping = false;
                    }
                } else if (minDist == bottomDist) {
                    character.y = tile.y + tile.height + 1;
                    character.velocityY = 0;
                }
                
                break;
            }
        }
    }
    
    private void render() {
        // Create buffer strategy if not already created
        if (bs == null) {
            createBufferStrategy(3);
            bs = getBufferStrategy();
            return;
        }
        
        g = (Graphics2D) bs.getDrawGraphics();
        
        // Clear screen
        g.setColor(new Color(20, 18, 167));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Apply camera transformation
        g.translate(-camera.getX(), -camera.getY());
        
        // Draw background (tiled to fit level width)
        for (int x = 0; x < LEVEL_WIDTH; x += ResourceManager.backgroundImage.getWidth()) {
            g.drawImage(ResourceManager.backgroundImage, x, 80, this);
        }
        
        // Draw tiles
        for (Tile tile : tiles) {
            // Only draw tiles that are visible on screen
            if (isOnScreen(tile)) {
                g.drawImage(ResourceManager.floorTileImage, tile.x, tile.y, this);
            }
        }
        
        // Draw player
        player.updateImage();
        g.drawImage(player.image, player.x, player.y, this);
        
        // Draw player bullets
        for (Player.Bullet bullet : player.bullets) {
            if (isOnScreen(bullet)) {
                g.drawImage(ResourceManager.playerBulletImage, bullet.x, bullet.y, this);
            }
        }
        
        // Draw enemies
        for (Metall metall : metalls) {
            if (isOnScreen(metall)) {
                metall.updateImage();
                g.drawImage(metall.image, metall.x, metall.y, this);
                
                // Draw enemy bullets
                for (Metall.Bullet bullet : metall.bullets) {
                    if (isOnScreen(bullet)) {
                        g.drawImage(ResourceManager.metallBulletImage, bullet.x, bullet.y, this);
                    }
                }
            }
        }
        
        // Reset transformation for UI elements
        g.translate(camera.getX(), camera.getY());
        
        // Draw health bar (fixed on screen)
        g.setColor(Color.BLACK);
        g.fillRect(TILE_SIZE, TILE_SIZE, Player.HEALTH_WIDTH, Player.HEALTH_HEIGHT * player.maxHealth);
        
        for (int i = player.maxHealth - player.health; i < player.maxHealth; i++) {
            g.drawImage(ResourceManager.healthImage, TILE_SIZE, TILE_SIZE + i * Player.HEALTH_HEIGHT, this);
        }
        
        // Dispose graphics and show buffer
        g.dispose();
        bs.show();
    }
    
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
    
    // Key input handler
    private class KeyInput extends KeyAdapter {
        private boolean[] keys = new boolean[256];
        
        @Override
        public void keyPressed(KeyEvent e) {
            keys[e.getKeyCode()] = true;
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            keys[e.getKeyCode()] = false;
        }
        
        public boolean isKeyDown(int keyCode) {
            return keys[keyCode];
        }
    }
}
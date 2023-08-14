import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class SpaceShooterGame extends PApplet {
    private static final int w = 1000;
    private static final int h = 563;
    private static final int player_Size = 100;
    private static final int enemy_Size = 60;
    private static final int bullet_Size = 30;
    private static final int star_Size = 40;
    private static final int min_stone_size = 60;
    private static final int max_stone_size = 120;

    private PImage spaceImage;
    private PImage playerImage;
    private PImage enemyImage;
    private PImage bulletImage;
    private PImage starImage;
    private PImage stoneImage;

    private PVector playerPosition;
    private PVector playerSpeed;
    private PVector[] enemies;
    private PVector[] bullet;
    private PVector star;
    private Stone[] stones;
    private int score;
    private int health;

    private boolean gameOver;

    public void settings() {
        size(w, h);
    }

    public void setup() {
        spaceImage = loadImage("image/space.jpg");
        playerImage = loadImage("image/spaceplane.png");
        enemyImage = loadImage("image/enemy.png");
        bulletImage = loadImage("image/bullet.png");
        starImage = loadImage("image/star.png");
        stoneImage = loadImage("image/stone.png");



        playerPosition = new PVector(w / 2, h - 100);
        playerSpeed = new PVector(0, 0);
        enemies = new PVector[5];
        bullet = new PVector[5];
        star = new PVector(-star_Size, -star_Size);
        stones = new Stone[5];
        score = 0;
        health = 100;
        gameOver = false;

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new PVector(random(w), random(100, h / 2));
        }

        for (int i = 0; i < stones.length; i++) {
            float stoneSize = random(min_stone_size, max_stone_size);
            stones[i] = new Stone(random(w), random(100, h / 2), stoneSize);
        }
    }

    public void draw() {
        background(spaceImage);
        if (!gameOver) {
            movePlayer();
            movebullet();
            moveEnemies();
            moveStones();
            checkCollisions();
            drawPlayer();
            drawbullet();
            drawEnemies();
            drawStones();
            drawstar();
            displayScore();
            displayHealth();
            spawnStar();
        } else {
            gameOver();
        }
    }

    public void keyPressed() {
        if (!gameOver) {
            if (keyCode == LEFT || key == 'A') {
                playerSpeed.x = -5;
            } else if (keyCode == RIGHT || key == 'D') {
                playerSpeed.x = 5;
            } else if (keyCode == UP || key == 'W') {
                playerSpeed.y = -5;
            } else if (keyCode == DOWN || key == 'S') {
                playerSpeed.y = 5;
            } else if (keyCode == ' ') {
                shoot();
            }
        }
    }

    public void keyReleased() {
        if (!gameOver) {
            if (keyCode == LEFT || key == 'A' || keyCode == RIGHT || key == 'D') {
                playerSpeed.x = 0;
            } else if (keyCode == UP || key == 'W' || keyCode == DOWN || key == 'S') {
                playerSpeed.y = 0;
            }
        }
    }

    private void movePlayer() {
        playerPosition.add(playerSpeed);
        playerPosition.x = constrain(playerPosition.x, 0, w - player_Size);
        playerPosition.y = constrain(playerPosition.y, 0, h - player_Size);
    }

    private void movebullet() {
        for (int i = 0; i < bullet.length; i++) {
            PVector bullets = bullet[i];
            if (bullets != null) {
                bullets.y -= 10;
                if (bullets.y < 0) {
                    bullet[i] = null;
                }
            }
        }
    }

    private void moveEnemies() {
        for (int i = 0; i < enemies.length; i++) {
            PVector enemy = enemies[i];
            enemy.y += 3;
            if (enemy.y > h) {
                enemy.x = random(w);
                enemy.y = -enemy_Size;
            }
        }
    }

    private void moveStones() {
        for (int i = 0; i < stones.length; i++) {
            Stone stone = stones[i];
            stone.y += 3;
            if (stone.y > h) {
                stone.x = random(w);
                stone.y = -stone.size;
                stone.size = random(min_stone_size, max_stone_size);
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < enemies.length; i++) {
            PVector enemy = enemies[i];
            if (enemy.y + enemy_Size >= playerPosition.y && enemy.y <= playerPosition.y + player_Size &&
                    enemy.x + enemy_Size >= playerPosition.x && enemy.x <= playerPosition.x + player_Size) {
                health -= 10;
                enemy.x = random(w);
                enemy.y = -enemy_Size;
            }
        }

        for (int j = 0; j < bullet.length; j++) {
            PVector bullets = bullet[j];

            if (bullets != null && bullets.y <= star.y + star_Size &&
                    bullets.y + bullet_Size >= star.y &&
                    bullets.x + bullet_Size >= star.x &&
                    bullets.x <= star.x + star_Size) {
                health += 20;
                bullet[j] = null;
                star.x = -star_Size;
                star.y = -star_Size;
            }

            for (int i = 0; i < enemies.length; i++) {
                PVector enemy = enemies[i];
                if (bullets != null && bullets.y <= enemy.y + enemy_Size &&
                        bullets.y + bullet_Size >= enemy.y &&
                        bullets.x + bullet_Size >= enemy.x &&
                        bullets.x <= enemy.x + enemy_Size) {
                    score += 10;
                    bullet[j] = null;
                    enemy.x = random(w);
                    enemy.y = -enemy_Size;
                }
            }

            for (int i = 0; i < stones.length; i++) {
                Stone stone = stones[i];
                if (bullets != null && bullets.y <= stone.y + stone.size &&
                        bullets.y + bullet_Size >= stone.y &&
                        bullets.x + bullet_Size >= stone.x &&
                        bullets.x <= stone.x + stone.size) {
                    bullet[j] = null;
                    stone.size -= 10;
                    score +=20;
                    if (stone.size <= min_stone_size) {
                        stone.x = random(w);
                        stone.y = -stone.size;
                        stone.size = random(min_stone_size, max_stone_size);
                    }
                }
            }
        }

        if (playerPosition.y <= star.y + star_Size &&
                playerPosition.y + player_Size >= star.y &&
                playerPosition.x + player_Size >= star.x &&
                playerPosition.x <= star.x + star_Size) {
            health += 20;
            star.x = -star_Size;
            star.y = -star_Size;
        }

        for (int i = 0; i < enemies.length; i++) {
            PVector enemy = enemies[i];
            if (enemy.y + enemy_Size >= playerPosition.y && enemy.y <= playerPosition.y + player_Size &&
                    enemy.x + enemy_Size >= playerPosition.x && enemy.x <= playerPosition.x + player_Size) {
                health -= 20;
                enemy.x = random(w);
                enemy.y = -enemy_Size;
            }
        }

        for (int i = 0; i < stones.length; i++) {
            Stone stone = stones[i];
            if (playerPosition.y + player_Size >= stone.y && playerPosition.y <= stone.y + stone.size &&
                    playerPosition.x + player_Size >= stone.x && playerPosition.x <= stone.x + stone.size) {
                health -= 10;
                stone.x = random(w);
                stone.y = -stone.size;
                stone.size = random(min_stone_size, max_stone_size);
            }
        }

        if (health <= 0) {
            gameOver = true;
        }
    }

    private void drawPlayer() {
        image(playerImage, playerPosition.x, playerPosition.y, player_Size, player_Size);
    }

    private void drawbullet() {
        for (PVector bullets : bullet) {
            if (bullets != null) {
                image(bulletImage, bullets.x, bullets.y, bullet_Size, bullet_Size);
            }
        }
    }

    private void drawEnemies() {
        for (PVector enemy : enemies) {
            image(enemyImage, enemy.x, enemy.y, enemy_Size, enemy_Size);
        }
    }

    private void drawStones() {
        for (Stone stone : stones) {
            image(stoneImage, stone.x, stone.y, stone.size, stone.size);
        }
    }

    private void drawstar() {
        image(starImage, star.x, star.y, star_Size, star_Size);
    }

    private void displayScore() {
        fill(255);
        textSize(24);
        text("Score: " + score, 20, 30);
    }

    private void displayHealth() {
        fill(255);
        textSize(24);
        text("Health: " + health, 20, 60);
    }

    private void spawnStar() {
        if (random(100) < 0.1 && star.y == -star_Size) {
            star.x = random(w - star_Size);
            star.y = random(h / 2);
        }
    }

    private void shoot() {
        for (int i = 0; i < bullet.length; i++) {
            if (bullet[i] == null) {
                bullet[i] = new PVector(playerPosition.x + (player_Size / 2) - (bullet_Size / 2), playerPosition.y);
                break;
            }
        }
    }

    private void gameOver() {
        background(0);
        fill(255);
        textSize(40);
        textAlign(CENTER, CENTER);
        text("Game Over", w / 2, h / 2);
        textSize(24);
        text("Score: " + score, w / 2, h / 2 + 50);
    }

    class Stone {
        float x;
        float y;
        float size;

        Stone(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }

    public static void main(String[] args) {
        PApplet.main("SpaceDefender");
    }
}


package eol.render;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SpriteManager {
    private static String path = "/assets/sprites/";
    private static Map<String, BufferedImage> sprites = new HashMap<>();
    private boolean loaded = false;

    private static SpriteManager instance;

    private SpriteManager() {
    }

    public static synchronized SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    public void loadSprite(String id, String fileName) {
        try (InputStream in = getClass().getResourceAsStream(path + fileName)) {
            if (in == null) {
                System.out.println("Sprite not found: " + fileName);
                return;
            }
            BufferedImage img = ImageIO.read(in);
            sprites.put(id, img);
            System.out.println("Loaded sprite: " + id);
        } catch (Exception e) {
            System.out.println("Failed to load sprite: " + fileName);
            e.printStackTrace();
        }
    }

    public BufferedImage getSprite(String id) {
        return sprites.get(id);
    }

    public void loadAllSprites() {

        for (int i = 0; i < 8; i++) {
            loadSprite("boss_idle_" + i, "boss_idle_" + i + ".png");
        }

        loadSprite("knight_sword_idle_0", "knight_sword_idle_0.png");
        loadSprite("knight_greatsword_idle_0", "knight_greatsword_idle_0.png");
        loadSprite("knight_dagger_idle_0", "knight_dagger_idle_0.png");
        loadSprite("knight_plasma_idle_0", "knight_plasma_idle_0.png");
        loadSprite("knight_cannon_idle_0", "knight_cannon_idle_0.png");
        
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_sword_walk_" + i, "knight_sword_walk_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_greatsword_walk_" + i, "knight_greatsword_walk_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_dagger_walk_" + i, "knight_dagger_walk_" + i + ".png");
        }
        for (int i = 0; i < 2; i++) {
            loadSprite("knight_plasma_walk_" + i, "knight_plasma_walk_" + i + ".png");
        }
        for (int i = 0; i < 2; i++) {
            loadSprite("knight_cannon_walk_" + i, "knight_cannon_walk_" + i + ".png");
        }


        for (int i = 0; i < 3; i++) {
            loadSprite("knight_sword_jump_" + i, "knight_sword_jump_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_greatsword_jump_" + i, "knight_greatsword_jump_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_dagger_jump_" + i, "knight_dagger_jump_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_plasma_jump_" + i, "knight_plasma_jump_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_cannon_jump_" + i, "knight_cannon_jump_" + i + ".png");
        }


        for (int i = 0; i < 8; i++) {
            loadSprite("knight_sword_attack_" + i, "knight_sword_attack_" + i + ".png");
        }
        for (int i = 0; i < 10; i++) {
            loadSprite("knight_greatsword_attack_" + i, "knight_greatsword_attack_" + i + ".png");
        }
        for (int i = 0; i < 7; i++) {
            loadSprite("knight_dagger_attack_" + i, "knight_dagger_attack_" + i + ".png");
        }
        for (int i = 0; i < 8; i++) {
            loadSprite("knight_plasma_attack_" + i, "knight_plasma_attack_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("knight_cannon_attack_" + i, "knight_cannon_attack_" + i + ".png");
        }

        loadSprite("mage_idle_0", "mage_idle_0.png");
        for (int i = 0; i < 3; i++) {
            loadSprite("mage_walk_" + i, "mage_walk_" + i + ".png");
        }
        for (int i = 0; i < 3; i++) {
            loadSprite("mage_jump_" + i, "mage_jump_" + i + ".png");
        }
        for (int i = 0; i < 2; i++) {
            loadSprite("mage_attack_" + i, "mage_attack_" + i + ".png");
        }
        for (int i = 0; i < 6; i++) {
            loadSprite("projectile_" + i, "projectile_" + i + ".png");
        }

        for (int i = 0; i < 6; i++) {
            loadSprite("black_flame_projectile_" + i, "black_flame_projectile_" + i + ".png");
        }
        for (int i = 0; i < 6; i++) {
            loadSprite("dust_projectile_" + i, "dust_projectile_" + i + ".png");
        }

        loadSprite("zombie_basic", "zombie_basic.png");
        loadSprite("zombie_ranged", "zombie_ranged.png");

        for (int i = 1; i <= 11; i++) {
            loadSprite("item_" + i, "item_" + i + ".png");
        }

        for (int i = 0; i < 5; i++) {
            loadSprite("background_" + i, "background_" + i + ".png");
        }

        loadSprite("healer_ally", "healer_ally.png");
        loadSprite("offense_ally", "offense_ally.png");
        loadSprite("defense_ally", "defense_ally.png");

        // Add more as needed
        loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public BufferedImage[] getPlayerIdle(String playerType, String weapon) {
        BufferedImage[] frames = new BufferedImage[1];
        if (playerType.equals("melee")) {
            if (weapon.equals("starter_sword")) {
                frames[0] = getSprite("knight_sword_idle_0");
            } else if (weapon.equals("greatsword")) {
                frames[0] = getSprite("knight_greatsword_idle_0");
            } else if (weapon.equals("dagger")) {
                frames[0] = getSprite("knight_dagger_idle_0");
            } else if (weapon.equals("plasma_sword")) {
                frames[0] = getSprite("knight_plasma_idle_0");
            } else {
                frames[0] = getSprite("knight_cannon_idle_0");
            }
        } else if (playerType.equals("ranged")) {
            frames = new BufferedImage[1];
            frames[0] = getSprite("mage_idle_0");
        } else {
            return getPlayerIdle("melee", "starter_sword");
        }
        return frames;
    }

    public BufferedImage[] getPlayerWalk(String playerType, String weapon) {
        if (playerType.equals("melee")) {
            if (weapon.equals("starter_sword")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_sword_walk_" + i);
                }
                return frames;
            } else if (weapon.equals("greatsword")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_greatsword_walk_" + i);
                }
                return frames;
            } else if (weapon.equals("dagger")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_dagger_walk_" + i);
                }
                return frames;
            } else if (weapon.equals("plasma_sword")) {
                BufferedImage[] frames = new BufferedImage[2];
                for (int i = 0; i < 2; i++) {
                    frames[i] = getSprite("knight_plasma_walk_" + i);
                }
                return frames;
            } else {
                BufferedImage[] frames = new BufferedImage[2];
                for (int i = 0; i < 2; i++) {
                    frames[i] = getSprite("knight_cannon_walk_" + i);
                }
                return frames;
            }
        } else if (playerType.equals("ranged")) {
            BufferedImage[] frames = new BufferedImage[3];
            for (int i = 0; i < 3; i++) {
                frames[i] = getSprite("mage_walk_" + i);
            }
            return frames;
        }
        return getPlayerWalk("melee", "starter_sword");
    }

    public BufferedImage[] getPlayerAttack(String playerType, String weapon) {
        if (playerType.equals("melee")) {
            if (weapon.equals("starter_sword")) {
                BufferedImage[] frames = new BufferedImage[8];
                for (int i = 0; i < 8; i++) {
                    frames[i] = getSprite("knight_sword_attack_" + i);
                }
                return frames;
            } else if (weapon.equals("greatsword")) {
                BufferedImage[] frames = new BufferedImage[10];
                for (int i = 0; i < 10; i++) {
                    frames[i] = getSprite("knight_greatsword_attack_" + i);
                }
                return frames;
            } else if (weapon.equals("dagger")) {
                BufferedImage[] frames = new BufferedImage[7];
                for (int i = 0; i < 7; i++) {
                    frames[i] = getSprite("knight_dagger_attack_" + i);
                }
                return frames;
            } else if (weapon.equals("plasma_sword")) {
                BufferedImage[] frames = new BufferedImage[8];
                for (int i = 0; i < 8; i++) {
                    frames[i] = getSprite("knight_plasma_attack_" + i);
                }
                return frames;
            } else {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_cannon_attack_" + i);
                }
                return frames;
            }
        } else if (playerType.equals("ranged")) {
            BufferedImage[] frames = new BufferedImage[2];
            for (int i = 0; i < 2; i++) {
                frames[i] = getSprite("mage_attack_" + i);
            }
            return frames;
        }
        return getPlayerAttack("melee", "starter_sword");
    }

    public BufferedImage[] getPlayerJump(String playerType, String weapon) {
        if (playerType.equals("melee")) {
            if (weapon.equals("starter_sword")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_sword_jump_" + i);
                }
                return frames;
            } else if (weapon.equals("greatsword")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_greatsword_jump_" + i);
                }
                return frames;
            } else if (weapon.equals("dagger")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_dagger_jump_" + i);
                }
                return frames;
            } else if (weapon.equals("plasma_sword")) {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_plasma_jump_" + i);
                }
                return frames;
            } else {
                BufferedImage[] frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = getSprite("knight_cannon_jump_" + i);
                }
                return frames;
            }
        } else if (playerType.equals("ranged")) {
           BufferedImage[] frames = new BufferedImage[3];
            for (int i = 0; i < 3; i++) {
                frames[i] = getSprite("mage_jump_" + i);
            }
            return frames; 
        }
        return getPlayerJump("melee", "starter_sword");
    }
    

}
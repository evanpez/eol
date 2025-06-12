package eol.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eol.audio.AudioManager;
import eol.engine.InputHandler;
import eol.entities.Player;
import eol.items.Item;
import eol.logic.LootManager;
import eol.render.SpriteManager;
import eol.weapons.Weapon;

public class ItemPanel {
    private boolean visible;
    private List<Item> items;
    private int selectedIndex;
    private Player player;
    private Font font;
    private Font labelFont;
    private float timer = 0f;

    public ItemPanel(Player player) {
        this.visible = false;
        this.items = new ArrayList<>();
        this.selectedIndex = 0;
        this.player = player;
        this.font = new Font("Martian Mono", Font.BOLD, 32);
        this.labelFont = new Font("Martian Mono", Font.PLAIN, 24);
    }

    public void showItems(List<Item> itemSet) {
        AudioManager.getInstance().playSound("newWave");
        items.clear();
        items.addAll(itemSet);
        selectedIndex = 0;
        visible = true;
        timer = 0f;
    }

    public void update(InputHandler inputHandler, float deltaTime, LootManager lootManager) {
        if (!visible) return;
        timer += deltaTime;

        if (inputHandler.isKeyPressed(KeyEvent.VK_LEFT) || inputHandler.isKeyPressed(KeyEvent.VK_A)) {
            AudioManager.getInstance().playSound("menuSound");
            selectedIndex = (selectedIndex + items.size() - 1) % items.size();
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_RIGHT) || inputHandler.isKeyPressed(KeyEvent.VK_D)) {
            AudioManager.getInstance().playSound("menuSound");
            selectedIndex = (selectedIndex + 1) % items.size();
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_X) && timer > 1.5f) {
            AudioManager.getInstance().playSound("menuSound");
            Item selectedItem = items.get(selectedIndex);
            selectedItem.applyStats(player, lootManager);
            lootManager.updatePool(selectedItem);
            visible = false;
        }
        inputHandler.clearKeysPressed();
    }

    public void render(Graphics2D g) {
        if (!visible) return;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, 800, 600);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            int x = 100 + i * 170;
            int y = 100;

            if (i == selectedIndex) {
                g.setColor(Color.WHITE);
                g.drawRect(x - 5, y - 5, 106, 106); //74, 74
            }

            BufferedImage sprite = SpriteManager.getInstance().getSprite("item_" + item.getId());
            g.drawImage(sprite, x, y, 96, 96, null); //64, 64
        }
        
        Item selectedItem = items.get(selectedIndex);
        String rarity = selectedItem.getRarity();
        switch (rarity) {
            case "Common":
                g.setColor(Color.WHITE);
                break;
            case "Rare":
                g.setColor(new Color(0, 150, 255, 255));
                break;
            case "Epic":
                g.setColor(Color.MAGENTA);
                break;
            case "Legendary":
                g.setColor(Color.ORANGE);
                break;
            case "Mythic":
                g.setColor(Color.CYAN);
                break;
        }
        drawCenteredString(g, selectedItem.getName(), 400, 260, font);
        drawCenteredString(g, selectedItem.getRarity(), 400, 300, font);
        List<String> statLabels = selectedItem.getStatLabels();
        Iterator<String> it = statLabels.iterator();
        int y = 340;
        g.setColor(Color.WHITE);
        while (it.hasNext()) {
            drawCenteredString(g, it.next(), 400, y, labelFont);
            y += 40;
        }
        drawCenteredString(g, "PRESS X TO CONFIRM", 400, 575, font);
    }

    public boolean isVisible() {
        return visible;
    }

    public void hide() {
        visible = false;
    }

    public static void drawCenteredString(Graphics2D g, String text, int centerX, int centerY, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = centerX - metrics.stringWidth(text) / 2;
        int y = centerY - ((metrics.getHeight() - metrics.getAscent()));
        g.setFont(font);
        g.drawString(text, x, y);
    }


}
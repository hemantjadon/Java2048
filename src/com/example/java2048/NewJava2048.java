package com.example.java2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Created by hemantjadon on 23/04/16.
 */

public class NewJava2048 extends JPanel {
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 64;
    private static final int TILES_MARGIN = 16;

    private Tile[][] Tiles;
    private boolean win = false;
    private boolean lose = false;
    private int score = 0;
    private int TARGET = 2048;

    //Constructor
    public NewJava2048() {
        //Adding Keypress Listener
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resetGame();
                }

                if (!win && !lose) {
                    int[][] orignalTiles = new int[4][4];
                    for (int i=0 ; i<4 ; i++){
                        for (int j=0 ; j<4 ; j++){
                            orignalTiles[i][j] = Tiles[i][j].value;
                        }
                    }

                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            break;
                        case KeyEvent.VK_UP:
                            moveUp();
                            break;
                    }

                    int[][] newTiles = new int[4][4];
                    for (int i=0 ; i<4 ; i++){
                        for (int j=0 ; j<4 ; j++){
                            newTiles[i][j] = Tiles[i][j].value;
                        }
                    }

                    if (!compare(orignalTiles,newTiles)){
                        addTile();
                    }
                }

                if (!win && !canMove()) {
                    lose = true;
                }

                repaint();
            }
        });

        this.resetGame();
    }

    public void resetGame(){
        win = false;
        lose = false;
        score = 0;
        Tiles = new Tile[4][4];

        //Initialization of tiles objects
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Tiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();
    }

    private void moveDown(){
        Tile[] list = new Tile[4];
        for(int i=0 ; i<4 ; i++){
            for (int j=0 ; j<4 ; j++){
                list[j] = Tiles[i][j];
            }
            collapse(list);
        }
    }

    private void moveUp(){
        Tile[] list = new Tile[4];
        for(int i=0 ; i<4 ; i++){
            for (int j=3,p=0 ; j>=0 ; j--,p++){
                list[p] = Tiles[i][j];
            }
            collapse(list);
        }
    }

    private void moveLeft(){
        Tile[] list = new Tile[4];
        for(int i=0 ; i<4 ; i++){
            for (int j=3,p=0 ; j>=0 ; j--,p++){
                list[p] = Tiles[j][i];
            }
            collapse(list);
        }
    }

    private void moveRight(){
        Tile[] list = new Tile[4];
        for(int i=0 ; i<4 ; i++){
            for (int j=0 ; j<4 ; j++){
                list[j] = Tiles[j][i];
            }
            collapse(list);
        }
    }


    private void collapse(Tile[] list){
        Stack<Integer> st = new  Stack<Integer>();
        int[]  originalLine = new int[4];
        for (int i=0 ; i<4 ; i++){
            if(!list[i].isEmpty()) {
                st.push(new Integer(list[i].value));
                originalLine[i] = list[i].value;
                list[i].value = 0;
            }
        }
        int index = 3;
        while (!st.isEmpty()){
            int num = st.pop();
            if(list[index].value == 0){
                list[index].value = num;
            }
            else if (list[index].value == num) {
                list[index].value *= 2;
                score += num*2;
                if(list[index].value == TARGET){
                    win = true;
                }
            }
            else {
                index--;
                list[index].value = num;
            }
        }
    }

    private  boolean compare(int[][] orignalTiles , int[][] newTiles){
        boolean flag = true;
        for (int i=0 ; i<orignalTiles.length ; i++){
            for (int j=0 ; j<orignalTiles.length ; j++) {
                if (orignalTiles[i][j] != newTiles[i][j]) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    private boolean canMove() {
        if (!isFull()) {
            return true;
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Tile t = Tiles[x][y];
                if ((x < 3 && t.value == Tiles[x + 1][y].value)
                        || ((y < 3) && t.value == Tiles[x][y + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addTile() {
        java.util.List<Tile> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Tile emptyTile = list.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> availableSpace() {
        final List<Tile> list = new ArrayList<Tile>(16);
        for (int i=0 ; i<4 ; i++){
            for (int j=0 ; j<4 ; j++){
                if (this.Tiles[i][j].isEmpty()){
                    list.add(Tiles[i][j]);
                }
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }




    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                drawTile(g , Tiles[i][j] , i , j);
            }
        }
    }

    private void drawTile(Graphics g2 , Tile tile , int i , int j){
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        int value = tile.getValue();
        int xOffset = offsetCoordinates(i);
        int yOffset = offsetCoordinates(j);

        g.setColor(tile.getBackground());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);

        g.setColor(tile.getForeground());
        int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0) {
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
        }

        if (win || lose) {
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(78, 139, 202));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
            if (win) {
                g.drawString("You won!", 68, 150);
            }
            if (lose) {
                g.drawString("Game over!", 50, 130);
                g.drawString("You lose!", 64, 200);
            }
            if (win || lose) {
                g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
                g.setColor(new Color(128, 128, 128, 128));
                g.drawString("Press ESC to play again", 80, getHeight() - 40);
            }
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        g.drawString("Score: " + score, 200, 365);
    }

    private static int offsetCoordinates(int arg){
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }




    static class Tile{
        private int value;

        public Tile(){
            this.value = 0;
        }
        public Tile(int n){
            this.value = n;
        }

        public int getValue(){
            return this.value;
        }

        public boolean isEmpty(){
            return this.value == 0;
        }

        public Color getForeground(){
            if(this.value < 16){
                return new Color(0x776e65);
            }
            else {
                return new Color(0xf9f6f2);
            }
        }

        public Color getBackground(){
            switch (this.value){
                case 2:    return new Color(0xeee4da);
                case 4:    return new Color(0xede0c8);
                case 8:    return new Color(0xf2b179);
                case 16:   return new Color(0xf59563);
                case 32:   return new Color(0xf67c5f);
                case 64:   return new Color(0xf65e3b);
                case 128:  return new Color(0xedcf72);
                case 256:  return new Color(0xedcc61);
                case 512:  return new Color(0xedc850);
                case 1024: return new Color(0xedc53f);
                case 2048: return new Color(0xedc22e);
                default: return new Color(0xcdc1b4);
            }
        }
    }



    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("New 2048 Game");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(340, 400);
        game.setResizable(true);

        game.add(new NewJava2048());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
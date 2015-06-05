package com.base;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


//Keyboard class acts as a key listener for the program, re used code from an earlier project
public class Keyboard implements KeyListener{
    public boolean[] keys = new boolean[200];

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e)
    {
        this.keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e)
    {
        this.keys[e.getKeyCode()] = false;
    }
}

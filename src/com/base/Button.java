package com.base;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by MoloHunt on 05/06/15.
 */
public class Button implements ChangeListener {

    public boolean down;

    @Override
    public void stateChanged(ChangeEvent e) {
        JButton source = (JButton)e.getSource();
        down = source.getModel().isPressed();
    }
}

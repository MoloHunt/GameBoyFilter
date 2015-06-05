package com.base;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//Button class works as a change listener for various buttons in the program
//Very simple implements ChangeListener and then handles the button press
public class Button implements ChangeListener {

    //boolean accessibe by main class
    public boolean down;

    //when this function is fired it sets down to the value of the button (pushed or not)
    @Override
    public void stateChanged(ChangeEvent e) {
        JButton source = (JButton)e.getSource();
        down = source.getModel().isPressed();
    }
}

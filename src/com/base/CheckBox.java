package com.base;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//CheckBox works the same as Button and uses change listener to allow access to GUI elements
//In this case it handles the check box
//That requires an initial value to be set
public class CheckBox implements ChangeListener {

    public boolean checked;

    //Initial value is set in constructor
    public CheckBox(boolean val){
        checked = val;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JCheckBox source = (JCheckBox)e.getSource();
        checked = source.isSelected();
    }
}

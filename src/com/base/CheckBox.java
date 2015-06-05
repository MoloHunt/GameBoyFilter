package com.base;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by MoloHunt on 05/06/15.
 */
public class CheckBox implements ChangeListener {

    public boolean checked;

    public CheckBox(boolean val){
        checked = val;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JCheckBox source = (JCheckBox)e.getSource();
        checked = source.isSelected();
    }
}

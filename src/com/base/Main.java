package com.base;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends Canvas implements ChangeListener{

    private JFileChooser fileChoose;
    private BufferedImage after;

    private int width;
    private int height;
    private JFrame frame;
    private BufferStrategy bs;
    private Keyboard key;

    private int pixScale = 0;
    private boolean gray;

    private int minScale = 0;
    private int maxScale = 4;
    private JSlider pixelScaler;

    private JCheckBox grayButton;
    private CheckBox check;

    private JButton saveButton;
    private Button save;

    private JButton loadButton;
    private Button load;

    public static void main(String[] args) {
       new Main();
    }

    public Main(){
        String userDir = System.getProperty("user.home");
        fileChoose = new JFileChooser(userDir + "/Desktop");

        BufferedImage before = LoadImage();

        Filter filter = new Filter(before, 0, false);

        BufferedImage after = filter.ConvertImageScalePixel();


        while(true){
            filter.setGray(check.checked);
            filter.setScale(pixScale);

            if (key.keys[KeyEvent.VK_S] || save.down){
                SaveImage(before);
            }
            if (key.keys[KeyEvent.VK_L] || load.down){
                frame.setVisible(false);
                before = LoadImage();
                filter.setBefore(before);
                after = filter.ConvertImageScalePixel();
                filter.setRender(true);
                InitFrame();
            }

            if(filter.needRender){
                after = filter.ConvertImageScalePixel();
                renderImages(before, after);
            }
        }
    }

    private void InitFrame()
    {
        Dimension size = new Dimension(this.width, this.height);
        setSize(size);
        setPreferredSize(size);

        key = new Keyboard();
        addKeyListener(this.key);

        pixelScaler = new JSlider(JSlider.HORIZONTAL, minScale, maxScale, pixScale);
        pixelScaler.setMajorTickSpacing(2);
        pixelScaler.setPaintTicks(true);
        pixelScaler.setPaintLabels(true);
        pixelScaler.addChangeListener(this);
        pixelScaler.setSnapToTicks(true);

        grayButton = new JCheckBox("Gray Scale", false);
        grayButton.addChangeListener(check = new CheckBox(false));

        saveButton = new JButton("Save Image");
        saveButton.addChangeListener(save = new Button());

        loadButton = new JButton("Load Image");
        loadButton.addChangeListener(load = new Button());

        frame.setLayout(new BorderLayout());
        JPanel pan = new JPanel(new BorderLayout());
        pan.add(pixelScaler, BorderLayout.NORTH);
        pan.add(grayButton, BorderLayout.WEST);
        pan.add(loadButton, BorderLayout.EAST);
        pan.add(saveButton, BorderLayout.CENTER);
        frame.add(pan, BorderLayout.NORTH);
        frame.add(this, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(3);
        frame.setResizable(false);
        frame.setVisible(true);

        if(getBufferStrategy() == null){
            createBufferStrategy(2);
        }
    }

    private void renderImages(BufferedImage before, BufferedImage after){
        bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(2);
        }
        Graphics g = bs.getDrawGraphics();

        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(before, 0, 0, null);
        g.drawImage(after, before.getWidth(), 0, null);

        g.dispose();
        this.bs.show();
    }

    private BufferedImage LoadImage(){

        BufferedImage before = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        boolean fileChosen = false;
        while (!fileChosen) {
            if (fileChoose.showOpenDialog(this) == 0)
            {
                File selectedFile = fileChoose.getSelectedFile();
                try
                {
                    before = ImageIO.read(selectedFile);
                    fileChosen = true;
                }
                catch (IOException e)
                {
                    fileChosen = false;
                }
            }
        }
        width = (before.getWidth() * 2);
        height = before.getHeight();

        frame = new JFrame("GameBoy Palette Swapper");
        InitFrame();
        return before;
    }

    private void SaveImage(BufferedImage before){
            String userDir = System.getProperty("user.home");
            JFileChooser fc = new JFileChooser(userDir + "/Desktop");
            if (fc.showSaveDialog(fc) == 0)
            {
                File file = fc.getSelectedFile();
                Container c = frame.getContentPane();
                BufferedImage im = new BufferedImage(before.getWidth(), before.getHeight(), 2);
                Graphics g = im.createGraphics();
                g.drawImage(after, 0, 0, null);
                try
                {
                    ImageIO.write(im, "PNG", file);
                    System.exit(0);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if(!source.getValueIsAdjusting()){
            pixScale = (int)source.getValue();
        }
    }
}

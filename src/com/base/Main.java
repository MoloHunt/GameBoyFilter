package com.base;

import com.sun.codemodel.internal.JOp;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends Canvas implements ChangeListener{

    private JFileChooser fileChoose;
    private BufferedImage before;
    private BufferedImage after;
    private int width;
    private int height;
    private JFrame frame;
    private BufferStrategy bs;
    private Keyboard key;
    private Color[] palette;

    private int pixScale;
    private boolean gray;

    private int minScale = 0;
    private int maxScale = 4;
    private JSlider pixelScaler;

    private JCheckBox grayButton;
    private CheckBox check;

    private JButton saveButton;
    private Button save;

    public static void main(String[] args) {
       new Main();
    }

    public Main(){
        String userDir = System.getProperty("user.home");
        fileChoose = new JFileChooser(userDir + "/Desktop");

        this.palette = new Color[5];
        this.palette[0] = new Color(255, 255, 255);
        this.palette[1] = new Color(156, 189, 15);
        this.palette[2] = new Color(140, 173, 15);
        this.palette[3] = new Color(48, 98, 48);
        this.palette[4] = new Color(15, 56, 15);

        LoadImage();

        ConvertImageScalePixel(1, false);

        frame = new JFrame("GameBoy Palette Swapper");

        InitFrame();
        while(true){
            boolean oldGray = gray;
            gray = check.checked;
            if(gray != oldGray){
                ConvertImageScalePixel(pixScale, gray);
            }
            RenderImages();
            if (key.keys[KeyEvent.VK_S] || save.down){
                SaveImage();
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

        pixelScaler = new JSlider(JSlider.HORIZONTAL, minScale, maxScale, minScale);
        pixelScaler.setMajorTickSpacing(2);
        pixelScaler.setPaintTicks(true);
        pixelScaler.setPaintLabels(true);
        pixelScaler.addChangeListener(this);
        pixelScaler.setSnapToTicks(true);

        grayButton = new JCheckBox("Gray Scale", false);
        grayButton.addChangeListener(check = new CheckBox(false));

        saveButton = new JButton("Save Image");
        saveButton.addChangeListener(save = new Button());


        frame.setLayout(new BorderLayout());
        JPanel pan = new JPanel(new BorderLayout());
        pan.add(pixelScaler, BorderLayout.NORTH);
        pan.add(grayButton, BorderLayout.WEST);
        pan.add(saveButton, BorderLayout.EAST);
        frame.add(pan, BorderLayout.NORTH);
        frame.add(this, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(3);
        frame.setResizable(false);
        frame.setVisible(true);

        createBufferStrategy(2);
    }

    private void RenderImages()
    {
        bs = getBufferStrategy();
        Graphics g = bs.getDrawGraphics();

        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(before, 0, 0, null);
        g.drawImage(after, before.getWidth(), 0, null);

        g.dispose();
        this.bs.show();
    }


    private void ConvertImageScalePixel(int scale, boolean gray){

        if(scale == 0){
            scale = 1;
        }

        System.out.println(before.getWidth() % scale + " " + before.getHeight() % scale);
        if(before.getWidth() % scale != 0 || before.getWidth() % scale != 0){
            before = ScaleImage(scale);
        }

        after = new BufferedImage(before.getWidth(), before.getHeight(), before.getType());
        int[] avgLookUpTable = new int[766];
        for (int i = 0; i < 766; i++) {
            avgLookUpTable[i] = (i / 3);
        }
        for (int x = 0; x < before.getWidth() - scale; x += scale) {
            for (int y = 0; y < before.getHeight() - scale; y += scale) {

                int red = 0, blue = 0, green = 0;

                for (int x2 = 0; x2 < scale; x2++) {
                    for (int y2 = 0; y2 < scale; y2++) {
                        red += new Color(before.getRGB(x + x2, y + y2)).getRed();
                        green += new Color(before.getRGB(x + x2, y + y2)).getGreen();
                        blue += new Color(before.getRGB(x + x2, y + y2)).getBlue();
                    }
                }

                int newPixel = 0;

                if(!gray) {
                    newPixel = (red + green + blue) / (scale * scale);
                    newPixel = avgLookUpTable[newPixel];
                    if ((newPixel >= 0) && (newPixel < 51)) {
                        newPixel = palette[4].getRGB();
                    } else if ((newPixel >= 51) && (newPixel < 102)) {
                        newPixel = palette[3].getRGB();
                    } else if ((newPixel >= 102) && (newPixel < 153)) {
                        newPixel = palette[2].getRGB();
                    } else if ((newPixel >= 153) && (newPixel < 204)) {
                        newPixel = palette[1].getRGB();
                    } else {
                        newPixel = palette[0].getRGB();
                    }
                }else{
                    newPixel = (red + green + blue) / (scale * scale);
                    newPixel = avgLookUpTable[newPixel];
                    if ((newPixel >= 0) && (newPixel < 51)) {
                        newPixel = new Color(39, 39, 39).getRGB();
                    } else if ((newPixel >= 51) && (newPixel < 102)) {
                        newPixel = new Color(77, 77, 77).getRGB();
                    } else if ((newPixel >= 102) && (newPixel < 153)) {
                        newPixel = new Color(145, 145, 145).getRGB();
                    } else if ((newPixel >= 153) && (newPixel < 204)) {
                        newPixel = new Color(159, 159, 159).getRGB();
                    } else {
                        newPixel = new Color(255, 255, 255).getRGB();
                    }
                }

                for (int x2 = 0; x2 < scale; x2++) {
                    for (int y2 = 0; y2 < scale; y2++) {
                        after.setRGB(x + x2, y + y2, newPixel);
                    }
                }
            }
        }
    }

    private BufferedImage ScaleImage(int scale) {
        int widthCrop = before.getWidth() % scale;
        int heightCrop = before.getHeight() % scale;

        BufferedImage cropped = before.getSubimage(0, 0, before.getWidth() - widthCrop, before.getHeight() - heightCrop);

        return cropped;
    }

    private void LoadImage()
    {
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
        this.width = (this.before.getWidth() * 2);
        this.height = this.before.getHeight();
    }

    private void SaveImage(){
            String userDir = System.getProperty("user.home");
            JFileChooser fc = new JFileChooser(userDir + "/Desktop");
            if (fc.showSaveDialog(fc) == 0)
            {
                File file = fc.getSelectedFile();
                Container c = this.frame.getContentPane();
                BufferedImage im = new BufferedImage(this.before.getWidth(), this.before.getHeight(), 2);
                Graphics g = im.createGraphics();
                g.drawImage(this.after, 0, 0, null);
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

        ConvertImageScalePixel(pixScale, gray);
    }
}

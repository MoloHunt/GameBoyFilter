package com.base;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main
        extends Canvas
{
    private JFileChooser fileChoose = new JFileChooser();
    private BufferedImage before;
    private BufferedImage after;
    private int width;
    private int height;
    private JFrame frame;
    private BufferStrategy bs;
    private Keyboard key;
    private Color[] palette;

    public static void main(String[] args)
    {
        new Main();
    }

    public Main()
    {
        this.palette = new Color[5];
        this.palette[0] = new Color(255, 255, 255);
        this.palette[1] = new Color(156, 189, 15);
        this.palette[2] = new Color(140, 173, 15);
        this.palette[3] = new Color(48, 98, 48);
        this.palette[4] = new Color(15, 56, 15);

        LoadImage();
        ConvertImage();
        InitFrame();
        for (;;)
        {
            RenderImages();
            if (key.keys[KeyEvent.VK_S])
            {
                SaveImage();
                System.exit(0);
            }
        }
    }

    private void InitFrame()
    {
        Dimension size = new Dimension(this.width, this.height);
        setSize(size);
        setPreferredSize(size);

        this.key = new Keyboard();
        addKeyListener(this.key);

        this.frame = new JFrame("GameBoy Palette Swapper");
        this.frame.add(this);
        this.frame.pack();
        this.frame.setDefaultCloseOperation(3);
        this.frame.setResizable(false);
        this.frame.setVisible(true);

        createBufferStrategy(2);
    }

    private void RenderImages()
    {
        this.bs = getBufferStrategy();
        Graphics g = this.bs.getDrawGraphics();

        g.drawImage(this.before, 0, 0, null);
        g.drawImage(this.after, this.before.getWidth(), 0, null);

        g.dispose();
        this.bs.show();
    }

    private void ConvertImage()
    {
        this.after = new BufferedImage(this.before.getWidth(), this.before.getHeight(), this.before.getType());
        int[] avgLookUpTable = new int[766];
        for (int i = 0; i < 766; i++) {
            avgLookUpTable[i] = (i / 3);
        }
        for (int x = 0; x < this.before.getWidth(); x++) {
            for (int y = 0; y < this.before.getHeight(); y++)
            {
                int red = new Color(this.before.getRGB(x, y)).getRed();
                int green = new Color(this.before.getRGB(x, y)).getGreen();
                int blue = new Color(this.before.getRGB(x, y)).getBlue();

                int newPixel = red + green + blue;
                newPixel = avgLookUpTable[newPixel];
                if ((newPixel >= 0) && (newPixel < 51)) {
                    newPixel = this.palette[4].getRGB();
                } else if ((newPixel >= 51) && (newPixel < 102)) {
                    newPixel = this.palette[3].getRGB();
                } else if ((newPixel >= 102) && (newPixel < 153)) {
                    newPixel = this.palette[2].getRGB();
                } else if ((newPixel >= 153) && (newPixel < 204)) {
                    newPixel = this.palette[1].getRGB();
                } else {
                    newPixel = this.palette[0].getRGB();
                }
                this.after.setRGB(x, y, newPixel);
            }
        }
    }

    private void LoadImage()
    {
        boolean fileChosen = false;
        while (!fileChosen) {
            if (this.fileChoose.showOpenDialog(this) == 0)
            {
                File selectedFile = this.fileChoose.getSelectedFile();
                try
                {
                    this.before = ImageIO.read(selectedFile);
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

    private void SaveImage()
    {
        int reply = JOptionPane.showConfirmDialog(null, "Would you like to save?", "Save Image?", 0);
        if (reply == 0)
        {
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
                catch (IOException e)
                {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
        else
        {
            System.exit(0);
        }
    }
}

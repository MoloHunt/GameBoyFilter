package com.base;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by MoloHunt on 06/06/15.
 */
public class Filter {

    private Color[] palette;
    private BufferedImage before;

    public boolean needRender;

    private boolean gray;
    private int scale;

    public Filter(BufferedImage before, int scale, boolean gray){

        this.before = before;
        this.scale = scale;
        this.gray = gray;

        palette = new Color[5];
        palette[0] = new Color(255, 255, 255);
        palette[1] = new Color(156, 189, 15);
        palette[2] = new Color(140, 173, 15);
        palette[3] = new Color(48, 98, 48);
        palette[4] = new Color(15, 56, 15);
    }

    public BufferedImage ConvertImageScalePixel() {

        BufferedImage after;

        if (scale == 0) {
            scale = 1;
        }

        if (before.getWidth() % scale != 0 || before.getWidth() % scale != 0) {
            before = ScaleImage(before, scale);
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

                int newPixel = (red + green + blue) / (scale * scale);
                newPixel = avgLookUpTable[newPixel];

                if (!gray) {
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
                } else {
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

        setRender(true);
        return after;
    }

    private BufferedImage ScaleImage(BufferedImage before, int scale) {
        int widthCrop = before.getWidth() % scale;
        int heightCrop = before.getHeight() % scale;

        BufferedImage cropped = before.getSubimage(0, 0, before.getWidth() - widthCrop, before.getHeight() - heightCrop);

        return cropped;
    }

    public void setRender(boolean render) {
        needRender = render;
    }

    public void setGray(boolean checked) {
        boolean oldGray = gray;
        gray = checked;
        if(gray != oldGray){
            setRender(true);
        }
    }

    public void setScale(int pixScale) {
        int oldScale = scale;
        scale = pixScale;
        if(oldScale != scale){
            setRender(true);
        }
    }

    public void setBefore(BufferedImage before) {
        this.before = before;
    }
}

package ZKGPBTAI.gui;

/**
 * Created by Jonatan on 30-Nov-15.
 */


import ZKGPBTAI.Main;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.*;


public class DebugView extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Main ai;
    BufferedImage losImage;
    BufferedImage threatImage;
    BufferedImage mapTexture;
    BufferedImage backbuffer;
    Graphics2D bufferGraphics;
    int mapWidth;
    int mapHeight;
    private BufferedImage graphImage;
    DebugComponent dc;

    public DebugView(Main parent) {
        super();
        this.ai = parent;
        this.setVisible(true);
        this.setTitle("ZKGPBTAI");

        this.mapWidth  =  parent.getCallback().getMap().getWidth();
        this.mapHeight = parent.getCallback().getMap().getHeight()+200;
        float aspect = mapHeight / mapWidth;
        this.setSize(600,(int) (600*aspect));
        backbuffer = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = backbuffer.createGraphics();
        //dc = new DebugComponent(ai);

        //this.add(dc);
       // dc.setVisible(true);

    }

    @Override
    public void paint(Graphics g){
        AlphaComposite threatComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        //AlphaComposite graphComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        int w = backbuffer.getWidth();
        int h = backbuffer.getHeight();
        //bufferGraphics.setComposite(graphComposite);
        //bufferGraphics.drawImage(losImage, 0, 0, w,h, null);
        bufferGraphics.setComposite(threatComposite);
        bufferGraphics.drawImage(threatImage, 0, 0, w,h, null);
       //bufferGraphics.setComposite(graphComposite);
        //bufferGraphics.drawImage(graphImage, 0, 0, w,h, null);

        g.drawImage(backbuffer, 0, 0, getWidth(), getHeight(), null);
    }

    public void setLosImage(BufferedImage bu) {
        losImage = bu;
    }

    public void setMapTexture(BufferedImage bu) {
        mapTexture = bu;
    }

    public void setThreatImage(BufferedImage threatMap) {
        threatImage= threatMap;
    }

    public void setGraphImage(BufferedImage graphImage) {
        this.graphImage = graphImage;
    }
}
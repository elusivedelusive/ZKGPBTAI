package ZKGPBTAI.gui;

import ZKGPBTAI.Main;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jonatan on 14-Dec-15.
 */
public class DebugComponent extends JComponent {
    Main parent;
    public DebugComponent(Main doh){
        this.parent = doh;
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;

        //applyRenderHints(g);

        parent.getCallback().getGame().sendTextMessage("DRAWING", 0);
        float[][] influenceMap = parent.influenceManager.im.getMyInfluence();

        for (int i = 0; i < parent.influenceManager.im.width; i++) {
            for (int j = 0; j < parent.influenceManager.im.height; j++) {
                float power = (influenceMap[i][j] > 0)?influenceMap[i][j]/1000: 1f;
                if(power > 0)
                    g.setColor(new Color(0f, power, 0f));
                else
                    g.setColor(new Color(power, 0f, 0f));
                //g.fillOval((i * 10) - 10, (j * 10) - 10, 2 * 10, 2 * 10);
                g.fillRect((i * 10) + 10, (j*10) + 10,10,10);
                g.setColor(Color.black);
                g.drawRect((i*10) + 10,(j*10) + 10,10,10);
            }
        }

        this.setBackground(Color.white);
    }

    private final static RenderingHints textRenderHints = new RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    private final static RenderingHints imageRenderHints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private final static RenderingHints colorRenderHints = new RenderingHints(
            RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    private final static RenderingHints interpolationRenderHints = new RenderingHints(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    private final static RenderingHints renderHints = new RenderingHints(
            RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    public static void applyRenderHints(Graphics2D g2d) {
        g2d.setRenderingHints(textRenderHints);
        g2d.setRenderingHints(imageRenderHints);
        g2d.setRenderingHints(colorRenderHints);
        g2d.setRenderingHints(interpolationRenderHints);
        g2d.setRenderingHints(renderHints);
    }

}

package ZKGPBTAI.influence_map;

import ZKGPBTAI.Main;
import ZKGPBTAI.Manager;
import ZKGPBTAI.gui.AdditiveComposite;
import com.springrts.ai.oo.clb.OOAICallback;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Jonatan on 03-Dec-15.
 */
public class InfluenceManager extends Manager {


    protected OOAICallback callback;
    public InfluenceMap im;
    BufferedImage threatmap;
    Graphics2D threatGraphics;
    public Main parent;

    public InfluenceManager(Main doh) {
        callback = doh.getCallback();
        im = new InfluenceMap(callback, this);
        threatmap = new BufferedImage(callback.getMap().getWidth(), callback.getMap().getHeight(), BufferedImage.TYPE_INT_ARGB);
        parent = doh;
        threatGraphics = threatmap.createGraphics();
        setInfluenceManager(this);
    }


    @Override
    public String getModuleName() {
        return "Influence Manager";
    }

    @Override
    public int update(int frame) {
        try {
            im.fadeInfluence();
        } catch (Exception e) {
            callback.getGame().sendTextMessage(getModuleName() + " 1 " + e.getMessage(), 0);
        }
        try {
            if (frame % 50 == 0) {
                im.updateInfluence();
                paintThreatMap();
            }
        } catch (Exception e) {
            callback.getGame().sendTextMessage(getModuleName() + " 2 " + e.getMessage(), 0);
        }

        return 0;
    }

    private void paintThreatMap() {

        int w = threatmap.getWidth();
        int h = threatmap.getHeight();

        threatGraphics.setBackground(new Color(0, 0, 0, 0));
        threatGraphics.clearRect(0, 0, w, h);
        threatGraphics.setComposite(new AdditiveComposite());

        float[][] influenceMap = im.getInfluenceMap();

        for (int i = 0; i < im.width; i++) {
            for (int j = 0; j < im.height; j++) {
                if (influenceMap[i][j] > 0f)
                    threatGraphics.setColor(new Color(0f, Math.min(1f, influenceMap[i][j] / 10000), 0f));
                else if (influenceMap[i][j] < 0f) {
                    threatGraphics.setColor(new Color(Math.min(1f, Math.abs(influenceMap[i][j] / 10000)), 0f, 0f));
                } else
                    threatGraphics.setColor(Color.black);
                threatGraphics.fillRect((i * 10) + 10, (j * 10) + 10, 10, 10);
            }
        }

/*        float[][] vulnerabilityMap = im.getModifiedVulnerabilityMap();
        for (int i = 0; i < im.width; i++) {
            for (int j = 0; j < im.height; j++) {
                if (vulnerabilityMap[i][j] > 0f)
                    threatGraphics.setColor(new Color(0f, Math.min(1f, vulnerabilityMap[i][j] / 10000), 0f));
                else
                    threatGraphics.setColor(Color.black);
                threatGraphics.fillRect((i * 10) + 10, (j * 10) + 10, 10, 10);
            }
        }*/
    }

    public BufferedImage getThreatMap() {
        return this.threatmap;
    }
}

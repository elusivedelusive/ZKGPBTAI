package ZKGPBTAI.gui;

/**
 * Created by Jonatan on 14-Dec-15.
 */
import java.awt.*;
import java.awt.image.*;
public class AdditiveComposite implements Composite {
    public AdditiveComposite() {
        super();
    }
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints rh) {
        return new AdditiveCompositeContext(srcColorModel,dstColorModel);
    }

}

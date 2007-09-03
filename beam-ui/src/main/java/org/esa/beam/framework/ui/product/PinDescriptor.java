package org.esa.beam.framework.ui.product;

import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.Pin;
import org.esa.beam.framework.datamodel.PinSymbol;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.ui.PlacemarkDescriptor;

/**
 * Created by Marco Peters.
 *
 * @author Marco Peters
 * @version $Revision:$ $Date:$
 */
public class PinDescriptor implements PlacemarkDescriptor {

    public final static PinDescriptor INSTANCE = new PinDescriptor();

    private PinDescriptor() {
    }

    public String getShowLayerCommandId() {
        return "showPinOverlay";
    }

    public String getRoleName() {
        return "pin";
    }

    public String getRoleLabel() {
        return "pin";
    }

    public String getCursorIconResourcePath() {
        return "cursors/PinTool.gif";
    }

    public ProductNodeGroup<Pin> getPlacemarkGroup(Product product) {
        return product.getPinGroup();
    }

    public PinSymbol createDefaultSymbol() {
        return PinSymbol.createDefaultPinSymbol();
    }

    public PixelPos updatePixelPos(GeoCoding geoCoding, GeoPos geoPos, PixelPos pixelPos) {
        if(geoCoding == null || !geoCoding.canGetPixelPos()) {
            return pixelPos;
        }
        return geoCoding.getPixelPos(geoPos, pixelPos);
    }

    public GeoPos updateGeoPos(GeoCoding geoCoding, PixelPos pixelPos, GeoPos geoPos) {
        if(geoCoding == null || !geoCoding.canGetGeoPos()) {
            return geoPos;
        }
        return geoCoding.getGeoPos(pixelPos, geoPos);

    }

}

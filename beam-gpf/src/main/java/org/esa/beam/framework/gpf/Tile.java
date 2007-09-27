package org.esa.beam.framework.gpf;

import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;

import java.awt.Rectangle;

// todo (nf, 26.09.2007) - API revision:
// - rename class to Tile
// - IMPORTANT: If we enable this tile to pass out references to primitive arrays of
//   the AWT raster's internal data buffer, we also have to provide
//   getScanlineOffset() and getScanlineStride()
//   methods. Note that these are totally different from getOffsetX() and getOffsetY()
//   which are defined within the image's pixel coodinate systems while the former are
//   defined only for the primitive array references passed out.
//
//   <p>This is the simplest but also slowest way to modify sample data of a tile:</p>
//   <pre>
//     for (int y = 0; y < getHeight(); y++) {
//         for (int x = 0; x < getWidth(); x++) {
//             // compute sample value...
//             tile.setSample(x, y, sample);
//         }
//     }
//   </pre>
//
//   <p>More performance is gained if the sample data buffer is checked out and committed
//   (required after modification only):</p>
//
//   <pre>
//     ProductData samples = tile.getRawSampleData(); // check out
//     for (int y = 0; y < getHeight(); y++) {
//         for (int x = 0; x < getWidth(); x++) {
//             // compute sample value...
//             samples.setElemFloatAt(y * getWidth() + x, sample);
//             // ...
//         }
//     }
//     tile.setRawSampleData(samples); // commit
//   </pre>
//
//   <p>The the fastest way to read from or write to sample data is via the primitive sample arrays:</p>
//
//   <pre>
//     float[] samples = tile.getRawSamplesFloat();
//     float sample;
//     int offset = tile.getScanlineOffset();
//     for (int y = 0; y < getHeight(); y++) {
//         int index = offset;
//         for (int x = 0; x < getWidth(); x++) {
//             // compute sample value...
//             samples[index] = sample;
//             index++;
//         }
//         offset += tile.getScanlineStride();
//     }
//   </pre>
//
//   Currently, we pass out a primitive reference only if the array.length equals
//   the area of the getRectangle(), which is never the case if the requested rectangle is
//   not equal to the tile rectangle.
//

/**
 * A tile.
 */
public interface Tile {

    /**
     * Gets the {@code RasterDataNode} associated with this tile,
     * e.g. a {@link org.esa.beam.framework.datamodel.Band Band} or
     * {@link org.esa.beam.framework.datamodel.TiePointGrid TiePointGrid}.
     *
     * @return The {@code RasterDataNode} associated with this tile.
     */
    RasterDataNode getRasterDataNode();

    /**
     * Gets the tile rectangle in pixel coordinates within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     * Simply returns <code>new Rectangle(
     * {@link #getMinX() minX},
     * {@link #getMinY() minY},
     * {@link #getWidth() width},
     * {@link #getHeight() height})</code>.
     *
     * @return The tile rectangle in pixel coordinates.
     */
    Rectangle getRectangle();

    /**
     * Gets the minimum pixel X-coordinate within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     *
     * @return The minimum pixel X-coordinate.
     */
    int getMinX();

    /**
     * Gets the maximum pixel X-coordinate within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     *
     * @return The maximum pixel X-coordinate.
     */
    int getMaxX();

    /**
     * Gets the minimum pixel Y-coordinate within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     *
     * @return The minimum pixel Y-coordinate.
     */
    int getMinY();

    /**
     * Gets the maximum pixel Y-coordinate within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     *
     * @return The maximum pixel Y-coordinate.
     */
    int getMaxY();

    /**
     * Gets the width in pixels within the scene covered by the tile's {@link #getRasterDataNode RasterDataNode}.
     *
     * @return The width in pixels.
     */
    int getWidth();

    /**
     * Gets the height in pixels within the scene covered by the tile's {@link RasterDataNode}.
     *
     * @return The height in pixels.
     */
    int getHeight();

    /**
     * Gets the raw (unscaled, uncalibrated) sample data (e.g. detector counts) of this tile's underlying raster.
     * Let {@code x} and {@code y} be absolute pixel coordinates. The {@code index} to be used with the returned data
     * buffer is then computed as follows:
     * <pre>
     *   int lineOffset = tile.{@link #getScanlineOffset()};
     *   int lineStride = tile.{@link #getScanlineOffset()};
     *   int dx = x - tile.{@link #getMinX()};
     *   int dy = y - tile.{@link #getMinY()};
     *   int index = lineOffset + lineStride * dy + dx;
     *   // use index here to access raw sample data...
     * </pre>
     * Or more efficient using the standard imaging x,y-loops for relative tile pixel coordinates:
     * <pre>
     *   int lineStride = tile.{@link #getScanlineStride()};
     *   int lineOffset = tile.{@link #getScanlineOffset()};
     *   for (int y = 0; y &lt; tile.{@link #getHeight()}; y++) {
     *      int index = lineOffset;
     *      for (int x = 0; x &lt; tile.{@link #getWidth()}; x++) {
     *           // use index here to access raw sample data...
     *           index++;
     *       }
     *       lineOffset += lineStride;
     *   }
     * </pre>
     * Or the standard imaging x,y-loops for absolute tile pixel coordinates
     * <pre>
     *   int lineStride = tile.{@link #getScanlineStride()};
     *   int lineOffset = tile.{@link #getScanlineOffset()};
     *   for (int y = tile.{@link #getMinY()}; y &lt;= tile.{@link #getMaxY()}; y++) {
     *      int index = lineOffset;
     *      for (int x = tile.{@link #getMinX()}; x &lt;= tile.{@link #getMaxX()}; x++) {
     *           // use index here to access raw sample data...
     *           index++;
     *       }
     *       lineOffset += lineStride;
     *   }
     * </pre>
     *
     * @return the sample data
     */
    ProductData getRawSampleData();

    byte[] getRawSamplesByte();

    short[] getRawSamplesShort();

    int[] getRawSamplesInt();

    float[] getRawSamplesFloat();

    double[] getRawSamplesDouble();

    int getScanlineOffset();

    int getScanlineStride();

    int getSampleInt(int x, int y);

    void setSample(int x, int y, int sample);

    float getSampleFloat(int x, int y);

    void setSample(int x, int y, float sample);

    double getSampleDouble(int x, int y);

    void setSample(int x, int y, double sample);

    boolean getSampleBoolean(int x, int y);

    void setSample(int x, int y, boolean sample);

    /**
     * Checks if this is a target tile. Non-target tiles are read only.
     *
     * @return <code>true</code> if this is a target tile.
     */
    boolean isWritable();

}

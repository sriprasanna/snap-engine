package org.esa.beam.statistics.percentile.interpolated;

import org.esa.beam.apache.math3.LinearInterpolator;
import org.esa.beam.apache.math3.PolynomialSplineFunction;
import org.esa.beam.apache.math3.SplineInterpolator;

import java.util.ArrayList;

public class GapFiller {
    private final static String LINEAR = InterpolatedPercentileOp.P_CALCULATION_METHOD_LINEAR_INTERPOLATION;
    private final static String SPLINE = InterpolatedPercentileOp.P_CALCULATION_METHOD_SPLINE_INTERPOLATION;

    public static void fillGaps(float[] interpolationFloats, final String interpolationMethod, final float startValueFallback, final float endValueFallback) {
        fillStartAndEndWithFallback(interpolationFloats, startValueFallback, endValueFallback);

        ArrayList<Double> xList = new ArrayList<Double>();
        ArrayList<Double> yList = new ArrayList<Double>();

        for (int i = 0; i < interpolationFloats.length; i++) {
            float value = interpolationFloats[i];
            if (!Float.isNaN(value)) {
                xList.add((double) i);
                yList.add((double) value);
            }
        }

        double[] nx = new double[xList.size()];
        double[] ny = new double[yList.size()];

        for (int i = 0; i < xList.size(); i++) {
            nx[i] = xList.get(i);
            ny[i] = yList.get(i);
        }

        final PolynomialSplineFunction interpolate;

        if (LINEAR.equalsIgnoreCase(interpolationMethod) || nx.length < 3) {
            interpolate = LinearInterpolator.interpolate(nx, ny);
        } else if (SPLINE.equalsIgnoreCase(interpolationMethod)) {
            interpolate = SplineInterpolator.interpolate(nx, ny);
        } else {
            interpolate = null;
        }

        for (int i = 0; i < interpolationFloats.length; i++) {
            interpolationFloats[i] = (float) interpolate.value(i);
        }
    }

    public static void fillStartAndEndWithFallback(float[] interpolationFloats, final float startValueFallback, final float endValueFallback) {
        if (Float.isNaN(interpolationFloats[0])) {
            interpolationFloats[0] = startValueFallback;
        }
        final int lastIdx = interpolationFloats.length - 1;
        if (Float.isNaN(interpolationFloats[lastIdx])) {
            interpolationFloats[lastIdx] = endValueFallback;
        }
    }

}
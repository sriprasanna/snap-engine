/*
 * Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.binning;

import org.esa.beam.binning.aggregators.AggregatorAverage;
import org.esa.beam.binning.aggregators.AggregatorAverageML;
import org.esa.beam.binning.aggregators.AggregatorMinMax;
import org.esa.beam.binning.postprocessor.PPSelection;
import org.esa.beam.binning.support.ObservationImpl;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PostProcessorTest {

    @Test
    public void testBinningWithoutPostProcessor() throws IOException {
        MyVariableContext variableContext = new MyVariableContext("A");
        BinManager bman = new BinManager(variableContext,
                new AggregatorMinMax(variableContext, "A"));

        TemporalBin tbin = doBinning(bman);
        assertEquals(2, tbin.getFeatureValues().length);
        WritableVector resultVector = bman.createResultVector();
        assertEquals(2, resultVector.size());
        bman.computeResult(tbin, resultVector);
        assertEquals(2, resultVector.size());
        assertEquals(0.2f, resultVector.get(0), 1e-4);
        assertEquals(0.6f, resultVector.get(1), 1e-4);

        assertArrayEquals(new String[]{"A_min", "A_max"}, bman.getResultFeatureNames());
    }

    @Test
    public void testBinningWithPostProcessor() throws IOException {
        MyVariableContext variableContext = new MyVariableContext("A");
        AggregatorMinMax aggregator = new AggregatorMinMax(variableContext, "A");
        PPSelection.Config ppSelection = new PPSelection.Config("A_max");
        BinManager bman = new BinManager(variableContext, ppSelection, aggregator);

        TemporalBin tbin = doBinning(bman);
        assertEquals(2, tbin.getFeatureValues().length);
        WritableVector resultVector = bman.createResultVector();
        assertEquals(2, resultVector.size());
        bman.computeResult(tbin, resultVector);
        assertEquals(2, resultVector.size());
        assertEquals(0.6f, resultVector.get(0), 1e-4);

        assertArrayEquals(new String[]{"A_max"}, bman.getResultFeatureNames());
    }

    private TemporalBin doBinning(BinManager bman) {
        SpatialBin sbin;
        TemporalBin tbin;

        tbin = bman.createTemporalBin(0);

        sbin = bman.createSpatialBin(0);
        bman.aggregateSpatialBin(new ObservationImpl(0.0, 0.0, 0.0, 0.2f), sbin);
        bman.completeSpatialBin(sbin);
        bman.aggregateTemporalBin(sbin, tbin);

        sbin = bman.createSpatialBin(0);
        bman.aggregateSpatialBin(new ObservationImpl(0.0, 0.0, 0.0, 0.6f), sbin);
        bman.completeSpatialBin(sbin);
        bman.aggregateTemporalBin(sbin, tbin);

        sbin = bman.createSpatialBin(0);
        bman.aggregateSpatialBin(new ObservationImpl(0.0, 0.0, 0.0, 0.4f), sbin);
        bman.completeSpatialBin(sbin);
        bman.aggregateTemporalBin(sbin, tbin);

        bman.completeTemporalBin(tbin);

        assertEquals(3, tbin.getNumObs());

        Vector tVec = bman.getTemporalVector(tbin, 0);

        assertEquals(2, tVec.size());
        assertEquals(0.2f, tVec.get(0), 1e-5f);
        assertEquals(0.6f, tVec.get(1), 1e-5f);

        return tbin;
    }

    private static class Times100PortProcessor extends PostProcessor {

        private Times100PortProcessor(String... outputFeatureNames) {
            super(outputFeatureNames);
        }

        @Override
        public void compute(Vector outputVector, WritableVector postVector) {
            int size = outputVector.size();
            for (int i = 0; i < size; i++) {
                postVector.set(i, outputVector.get(i) * 100);
            }
        }
    }
}
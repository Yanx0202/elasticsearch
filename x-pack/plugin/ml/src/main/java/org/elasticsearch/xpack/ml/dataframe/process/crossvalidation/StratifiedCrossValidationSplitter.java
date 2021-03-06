/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.ml.dataframe.process.crossvalidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Given a dependent variable, randomly splits the dataset trying
 * to preserve the proportion of each class in the training sample.
 */
public class StratifiedCrossValidationSplitter extends AbstractReservoirCrossValidationSplitter {

    private final Map<String, SampleInfo> classSamples;

    public StratifiedCrossValidationSplitter(List<String> fieldNames, String dependentVariable, Map<String, Long> classCounts,
                                             double trainingPercent, long randomizeSeed) {
        super(fieldNames, dependentVariable, trainingPercent, randomizeSeed);
        this.classSamples = new HashMap<>();
        classCounts.entrySet().forEach(entry -> classSamples.put(entry.getKey(), new SampleInfo(entry.getValue())));
    }

    @Override
    protected SampleInfo getSampleInfo(String[] row) {
        String classValue = row[dependentVariableIndex];
        SampleInfo sample = classSamples.get(classValue);
        if (sample == null) {
            throw new IllegalStateException("Unknown class [" + classValue + "]; expected one of " + classSamples.keySet());
        }
        return sample;
    }
}

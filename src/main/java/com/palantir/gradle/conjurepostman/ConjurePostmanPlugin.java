/*
 * (c) Copyright 2017 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.gradle.conjurepostman;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ConjurePostmanPlugin implements Plugin<Project> {

    @Override
    public final void apply(Project project) {
        project.getTasks().create("conjurepostmanTask", ConjurePostmanTask.class);
    }
}

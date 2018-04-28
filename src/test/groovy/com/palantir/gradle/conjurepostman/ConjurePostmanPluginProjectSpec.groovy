/*
 * (c) Copyright 2017 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.gradle.conjurepostman

import nebula.test.PluginProjectSpec

/**
 * Tests for {@link ConjurePostmanPlugin}.
 */
class ConjurePostmanPluginProjectSpec extends PluginProjectSpec {

    @Override
    String getPluginName() {
        return "com.palantir.conjurepostman"
    }
}

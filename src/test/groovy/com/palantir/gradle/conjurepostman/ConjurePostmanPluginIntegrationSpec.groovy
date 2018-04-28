/*
 * (c) Copyright 2017 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.gradle.conjurepostman

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import spock.lang.Unroll

/**
 * Integration tests for {@link ConjurePostmanPlugin}.
 */
class ConjurePostmanPluginIntegrationSpec extends IntegrationSpec {

    def 'example test running a task'() {
        setup:
        buildFile << '''
            apply plugin: 'com.palantir.conjurepostman'
        '''

        when:
        ExecutionResult result = runTasksSuccessfully('conjurepostmanTask')

        then:
        result.success
    }

    @Unroll
    def 'runs on version of gradle: #version'() {
        setup:
        buildFile << '''
            apply plugin: 'com.palantir.conjurepostman'
        '''

        when:
        gradleVersion = version
        fork = true
        ExecutionResult result = runTasksSuccessfully('conjurepostmanTask')

        then:
        result.success

        where:
        version << ['4.1', '4.0', '3.5', '3.4']
    }
}

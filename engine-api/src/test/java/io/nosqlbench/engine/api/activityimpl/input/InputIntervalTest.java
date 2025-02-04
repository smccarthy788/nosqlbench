/*
 * Copyright (c) 2022 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nosqlbench.engine.api.activityimpl.input;

import io.nosqlbench.engine.api.activityapi.cyclelog.buffers.results.CycleSegment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InputIntervalTest {

    @Test
    public void testBasicInterval() {
        InputInterval ii = new InputInterval(3,6);

        CycleSegment s1 = ii.getInputSegment(1);
        assertThat(s1).isNotNull();
        assertThat(s1.isExhausted()).isFalse();
        long v1 = s1.nextCycle();
        assertThat(v1).isEqualTo(3);

        CycleSegment s2 = ii.getInputSegment(2);
        assertThat(s2).isNotNull();
        assertThat(s2.isExhausted()).isFalse();
        long v2 = s2.nextCycle();
        assertThat(v2).isEqualTo(4);
        assertThat(s2.isExhausted()).isFalse();
        long v3 = s2.nextCycle();
        assertThat(v3).isEqualTo(5);
        assertThat(s2.isExhausted()).isTrue();
        long v4 = s2.nextCycle();
        assertThat(v4).isLessThan(0);

        assertThat(ii.getInputSegment(1)).isNull();
    }

}

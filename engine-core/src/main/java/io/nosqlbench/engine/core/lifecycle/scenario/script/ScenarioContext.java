/*
 * Copyright (c) 2022-2023 nosqlbench
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
package io.nosqlbench.engine.core.lifecycle.scenario.script;

import io.nosqlbench.api.config.LabeledScenarioContext;
import io.nosqlbench.api.config.NBLabels;
import io.nosqlbench.engine.api.scripting.ScriptEnvBuffer;
import io.nosqlbench.engine.core.lifecycle.scenario.ScenarioController;

public class ScenarioContext extends ScriptEnvBuffer implements LabeledScenarioContext {

    private final ScenarioController sc;
    private final String contextName;

    public ScenarioContext(String contextName, ScenarioController sc) {
        this.contextName = contextName;
        this.sc = sc;
    }

    public String getContextName() {
        return this.contextName;
    }

    @Override
    public Object getAttribute(String name) {
        Object o = super.getAttribute(name);
        return o;
    }

    @Override
    public Object getAttribute(String name, int scope) {
        Object o = super.getAttribute(name, scope);
        return o;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        super.setAttribute(name, value, scope);
    }

    @Override
    public NBLabels getLabels() {
        return NBLabels.forKV("scenario", this.contextName);
    }
}

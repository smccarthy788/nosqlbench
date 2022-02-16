package io.nosqlbench.adapter.cqld4.opmappers;

import com.datastax.dse.driver.api.core.graph.DseGraph;
import com.datastax.oss.driver.api.core.CqlSession;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.nosqlbench.adapter.cqld4.opdispensers.Cqld4FluentGraphOpDispenser;
import io.nosqlbench.engine.api.activityimpl.OpDispenser;
import io.nosqlbench.engine.api.activityimpl.OpMapper;
import io.nosqlbench.engine.api.activityimpl.uniform.flowtypes.Op;
import io.nosqlbench.engine.api.templating.ParsedOp;
import io.nosqlbench.virtdata.core.bindings.Bindings;
import io.nosqlbench.virtdata.core.bindings.BindingsTemplate;
import io.nosqlbench.virtdata.core.templates.ParsedTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.function.Supplier;

public class Cqld4FluentGraphOpMapper implements OpMapper<Op> {
    private final static Logger logger = LogManager.getLogger(Cqld4FluentGraphOpMapper.class);

    private final LongFunction<CqlSession> sessionFunc;
    private GraphTraversalSource gtsPlaceHolder;

    public Cqld4FluentGraphOpMapper(LongFunction<CqlSession> sessionFunc) {
        this.sessionFunc = sessionFunc;
    }

    @Override
    public OpDispenser<? extends Op> apply(ParsedOp cmd) {
        GraphTraversalSource g = DseGraph.g;

        ParsedTemplate fluent = cmd.getAsTemplate("fluent").orElseThrow();
        String scriptBodyWithRawVarRefs = fluent.getPositionalStatement();

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

        cmd.getOptionalStaticValue("imports", List.class).ifPresent(
            l -> {
                ArrayList<String> stringList = new ArrayList<>();
                l.forEach(o -> stringList.add(o.toString()));
                String[] verifiedClasses = expandClassNames(l);
                ImportCustomizer importer = new ImportCustomizer();
                importer.addImports(verifiedClasses);
                compilerConfiguration.addCompilationCustomizers(importer);
            }
        );

        Supplier<Script> supplier = () -> {
            groovy.lang.Binding groovyBindings = new Binding(new LinkedHashMap<String, Object>(Map.of("g", g)));
            GroovyShell gshell = new GroovyShell(groovyBindings, compilerConfiguration);
            return gshell.parse(scriptBodyWithRawVarRefs);
        };

        LongFunction<? extends String> graphnameFunc = cmd.getAsRequiredFunction("graphname");
        Bindings virtdataBindings = new BindingsTemplate(fluent.getBindPoints()).resolveBindings();

        return new Cqld4FluentGraphOpDispenser(cmd, graphnameFunc, sessionFunc, virtdataBindings, supplier);
    }

    private String[] expandClassNames(List l) {
        ClassLoader loader = Cqld4FluentGraphOpMapper.class.getClassLoader();

        List<String> classNames = new ArrayList<>();
        for (Object name : l) {
            String candidateName = name.toString();
            if (candidateName.endsWith(".*")) {
                throw new RuntimeException("You can not use wildcard package imports like '" + candidateName + "'");
            }
            try {
                loader.loadClass(candidateName);
                classNames.add(candidateName);
                logger.debug("added import " + candidateName);
            } catch (Exception e) {
                throw new RuntimeException("Class '" + candidateName + "' was not found for fluent imports.");
            }
        }
        return classNames.toArray(new String[0]);
    }
}

package org.netbeans.gradle.model.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.netbeans.gradle.model.GenericProjectProperties;
import org.netbeans.gradle.model.ProjectInfoBuilder;
import org.netbeans.gradle.model.util.SerializationUtils;

public final class DynamicModelLoader implements ToolingModelBuilder {
    private final ModelQueryInput input;

    public DynamicModelLoader(ModelQueryInput input) {
        if (input == null) throw new NullPointerException("input");

        this.input = input;
    }

    public boolean canBuild(String modelName) {
        return modelName.equals(ModelQueryOutputRef.class.getName());
    }

    public Object buildAll(String modelName, Project project) {
        if (!canBuild(modelName)) {
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }

        Map<Object, Object> projectInfoResults
                = new HashMap<Object, Object>(2 * input.getProjectInfoRequests().size());
        for (Map.Entry<?, ProjectInfoBuilder<?>> entry: input.getProjectInfoRequests().entrySet()) {
            Object result = entry.getValue().getProjectInfo(project);
            if (result != null) {
                projectInfoResults.put(entry.getKey(), result);
            }
        }

        GenericProjectProperties genericProperties = getGenericProperties(project);
        return new DefaultModelQueryOutputRef(new ModelQueryOutput(genericProperties, projectInfoResults));
    }

    private GenericProjectProperties getGenericProperties(Project project) {
        String name = project.getName();
        String path = project.getPath();
        File projectDir = project.getProjectDir();
        return new GenericProjectProperties(name, path, projectDir);
    }

    private static final class DefaultModelQueryOutputRef implements ModelQueryOutputRef {
        private final ModelQueryOutput modelQueryOutput;

        public DefaultModelQueryOutputRef(ModelQueryOutput modelQueryOutput) {
            this.modelQueryOutput = modelQueryOutput;
        }

        public byte[] getSerializedModelQueryOutput() {
            return SerializationUtils.serializeObject(modelQueryOutput);
        }
    }
}

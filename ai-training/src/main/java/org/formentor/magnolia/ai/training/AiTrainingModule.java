package org.formentor.magnolia.ai.training;

import info.magnolia.module.ModuleLifecycleContext;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is optional and represents the configuration for the ai-training module.
 * By exposing simple getter/setter/adder methods, this bean can be configured via content2bean
 * using the properties and node from <tt>config:/modules/ai-training</tt>.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 * See https://documentation.magnolia-cms.com/display/DOCS/Module+configuration for information about module configuration.
 */
@Setter
@Getter
public class AiTrainingModule implements info.magnolia.module.ModuleLifecycle {
    private String host;
    private String baseModel;
    private Azure azure;

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {

    }

    @Override
    public void stop(ModuleLifecycleContext moduleLifecycleContext) {

    }

    @Getter
    @Setter
    public static class Azure {
        private String host;
        private String apiVersion;
        private String baseModel;
    }

}

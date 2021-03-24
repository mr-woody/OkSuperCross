package com.woodys.supercross.plugin

import com.woodys.supercross.plugin.extension.DispatcherExtension
import com.woodys.supercross.plugin.service.IServiceGenerator
import com.woodys.supercross.plugin.service.StubServiceGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project

class SuperCrossPlugin implements Plugin<Project> {

    private IServiceGenerator stubServiceGenerator = new StubServiceGenerator()

    public static final String DISPATCHER_EXTENSION_NAME = "dispatcher"

    @Override
    void apply(Project project) {

        project.extensions.create(DISPATCHER_EXTENSION_NAME, DispatcherExtension)
        stubServiceGenerator.injectStubServiceToManifest(project)

    }
}


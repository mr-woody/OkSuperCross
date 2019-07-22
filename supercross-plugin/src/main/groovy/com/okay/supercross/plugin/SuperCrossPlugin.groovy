package com.okay.supercross.plugin

import com.android.build.gradle.AppExtension
import com.okay.supercross.plugin.extension.DispatcherExtension
import com.okay.supercross.plugin.service.IServiceGenerator
import com.okay.supercross.plugin.service.StubServiceGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project

class SuperCrossPlugin implements Plugin<Project> {

    private IServiceGenerator stubServiceGenerator = new StubServiceGenerator()

    public static final String DISPATCHER_EXTENSION_NAME = "dispatcher"

    @Override
    void apply(Project project) {

        project.extensions.create(DISPATCHER_EXTENSION_NAME, DispatcherExtension)

        def android = project.extensions.getByType(AppExtension)

        stubServiceGenerator.injectStubServiceToManifest(project)

        //注册一个Transform
        def classTransform = new SuperCrossTransform(project, stubServiceGenerator)

        android.registerTransform(classTransform)

        println("================apply router plugin==========")
    }

}


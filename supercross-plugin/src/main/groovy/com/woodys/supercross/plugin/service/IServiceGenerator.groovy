package com.woodys.supercross.plugin.service

import org.gradle.api.Project

public interface IServiceGenerator {

    void injectStubServiceToManifest(Project project)
}
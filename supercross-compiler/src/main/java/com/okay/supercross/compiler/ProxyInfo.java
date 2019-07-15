package com.okay.supercross.compiler;

import com.okay.supercross.annotation.RemoteServiceImpl;

import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ProxyInfo {
    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;
    private Set<String> methodNames;
    private String remoteServiceImplName;
    private String remoteServiceName;
    private String processId;

    public ProxyInfo(Elements elementUtils, TypeElement classElement, Set<String> methodNames, String processId) {
        this.typeElement = classElement;
        this.methodNames = methodNames;
        this.processId = processId;

        this.packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

        this.proxyClassName = (classElement.getSimpleName() + "Impl");

        this.remoteServiceName = (this.packageName + "." + classElement.getSimpleName());

        this.remoteServiceImplName = ((RemoteServiceImpl) classElement.getAnnotation(RemoteServiceImpl.class)).value();
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getProxyClassName() {
        return this.proxyClassName;
    }

    public TypeElement getTypeElement() {
        return this.typeElement;
    }

    public Set<String> getMethodNames() {
        return this.methodNames;
    }

    public String getRemoteServiceImplName() {
        return this.remoteServiceImplName;
    }

    public String getRemoteServiceName() {
        return this.remoteServiceName;
    }

    public String getProcessId() {
        return this.processId;
    }
}

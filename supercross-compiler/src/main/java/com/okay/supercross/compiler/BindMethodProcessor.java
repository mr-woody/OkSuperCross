package com.okay.supercross.compiler;

import com.okay.supercross.annotation.ProcessId;
import com.okay.supercross.annotation.RemoteServiceImpl;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BindMethodProcessor extends AbstractProcessor {
    Messager mMessager;
    Filer mFiler;
    Elements mElements;

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mMessager = processingEnv.getMessager();
        this.mFiler = processingEnv.getFiler();
        this.mElements = processingEnv.getElementUtils();
    }

    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet();
        annotations.add(RemoteServiceImpl.class.getCanonicalName());
        annotations.add(ProcessId.class.getCanonicalName());
        return annotations;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> remoteServiceImplElements = roundEnv.getElementsAnnotatedWith(RemoteServiceImpl.class);
        Set<? extends Element> processIdElements = roundEnv.getElementsAnnotatedWith(ProcessId.class);
        if ((remoteServiceImplElements.size() == 0) || (processIdElements.size() == 0)) {
            return false;
        }
        Map<String, ProxyInfo> mProxyMap = new HashMap();
        Map<String, String> processIds = new HashMap();
        for (Element element : processIdElements) {
            TypeElement typeElement = (TypeElement) element;
            String key = typeElement.getQualifiedName().toString();
            String value = ((ProcessId) typeElement.getAnnotation(ProcessId.class)).value();
            processIds.put(key, value);
        }

        for (Element element : remoteServiceImplElements) {
            TypeElement typeElement = (TypeElement) element;

            String targetClassName = typeElement.getQualifiedName().toString();

            List<? extends Element> methods = typeElement.getEnclosedElements();
            Set<String> methodNames = new HashSet();
            for (Element method : methods) {
                methodNames.add(method.getSimpleName().toString());
            }
            mProxyMap.put(targetClassName, new ProxyInfo(this.mElements, typeElement, methodNames, (String) processIds.get(targetClassName)));
        }

        if (mProxyMap.isEmpty()) {
            return false;
        }
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = (ProxyInfo) mProxyMap.get(key);

            TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(proxyInfo.getProxyClassName()).addModifiers(new Modifier[]{Modifier.PUBLIC});
            for (String method : proxyInfo.getMethodNames()) {
                ClassName remoteMethod = ClassName.get("com.okay.supercross.annotation.model", "Method", new String[0]);

                FieldSpec fieldSpec = FieldSpec.builder(remoteMethod, method, new Modifier[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC}).initializer("new $T($S, $S, $S, $S)", new Object[]{remoteMethod, proxyInfo.getRemoteServiceName(), proxyInfo.getRemoteServiceImplName(), method, proxyInfo.getProcessId()}).build();
                typeSpecBuilder.addField(fieldSpec);
            }
            TypeSpec taCls = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName() + ".impl", taCls).build();
            try {
                javaFile.writeTo(this.mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}

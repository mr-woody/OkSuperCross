package com.okay.supercross.compiler;

import com.okay.supercross.annotation.RegistApplication;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
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
public class ApplicationProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElements;

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RegistApplication.class);
        if (elements.size() == 0) {
            return false;
        }
        TypeElement typeElement = (TypeElement) elements.iterator().next();
        String targetClassName = typeElement.getQualifiedName().toString();
        String toClassName = targetClassName.replaceAll("\\.", "_");

        TypeSpec typeSpec = TypeSpec.classBuilder(toClassName).addModifiers(new Modifier[]{Modifier.PUBLIC}).build();
        JavaFile javaFile = JavaFile.builder("com.okay.supercross.apt", typeSpec).build();
        try {
            javaFile.writeTo(this.mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mMessager = processingEnv.getMessager();
        this.mFiler = processingEnv.getFiler();
        this.mElements = processingEnv.getElementUtils();
    }

    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet();
        annotations.add(RegistApplication.class.getCanonicalName());
        return annotations;
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}

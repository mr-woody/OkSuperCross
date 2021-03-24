
package com.woodys.supercross.plugin.service

import com.android.build.gradle.AppExtension
import com.woodys.supercross.plugin.extension.DispatcherExtension
import groovy.xml.MarkupBuilder
import org.gradle.api.Project

class StubServiceGenerator implements IServiceGenerator {

    def static final NAME = 'android:name'
    def static final PROCESS = 'android:process'
    def static final EXPORTED = 'android:exported'
    def static final ENABLED = 'android:enabled'
    def static final FALSE = 'false'
    def static final TRUE = 'true'

    def static final AUTHORITIES = "android:authorities"
    def static final DISPATCHER_AUTHORITY = "api.provider"

    def static final DISPATCHER_SERVICE = 'com.woodys.supercross.dispatcher.DispatcherService'
    def static final DISPTACHER_PROVIDER = 'com.woodys.supercross.dispatcher.DispatcherProvider'


    def dispatcher
    private String pkgName

    @Override
    void injectStubServiceToManifest(Project project) {

        def android = project.extensions.getByType(AppExtension)

        this.dispatcher = project.extensions.getByType(DispatcherExtension)

        project.afterEvaluate {
            android.applicationVariants.all { variant ->

                if (pkgName == null) {
                    pkgName = getPackageName(variant)
                }

                variant.outputs.each { output ->

                    output.processManifest.doLast {

                        //output.getProcessManifest().manifestOutputDirectory
                        output.processManifest.outputs.files.each { File file ->
                            //在gradle plugin 3.0.0之前，file是文件，且文件名为AndroidManifest.xml
                            //在gradle plugin 3.0.0之后，file是目录，且不包含AndroidManifest.xml，需要自己拼接
                            //除了目录和AndroidManifest.xml之外，还可能会包含manifest-merger-debug-report.txt等不相干的文件，过滤它
                            if ((file.name.equalsIgnoreCase("AndroidManifest.xml") && !file.isDirectory()) || file.isDirectory()) {
                                if (file.isDirectory()) {
                                    //3.0.0之后，自己拼接AndroidManifest.xml
                                    injectManifestFile(new File(file, "AndroidManifest.xml"))
                                } else {
                                    //3.0.0之前，直接使用
                                    injectManifestFile(file)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void injectManifestFile(File manifestFile) {
        //检测文件是否存在
        if (manifestFile != null && manifestFile.exists()) {
            String serviceManifest = addServiceItem(manifestFile.absolutePath)
            String newManifestContent = manifestFile.getText("UTF-8")
            int index = newManifestContent.lastIndexOf("</application>")
            newManifestContent = newManifestContent.substring(0, index) + serviceManifest + newManifestContent.substring(index)
            manifestFile.write(newManifestContent, 'UTF-8')

        } else {
            println "Attention!manifest file may not exist!"
        }
    }

    def getPackageName(variant) {
        if (null == variant) {
            return null
        }
        [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
    }

    //注意:闭包中只能调用static方法
    private String addServiceItem(String manifestPath) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.application {
            //之后，写入DispatcherService和DispatcherProvider
            def dispatcherProcess = dispatcher.process
            if (dispatcherProcess != null && dispatcherProcess.length() > 0) {
                service("${NAME}": DISPATCHER_SERVICE,
                        "${ENABLED}": "${TRUE}",
                        "${EXPORTED}": "${FALSE}",
                        "${PROCESS}": dispatcherProcess
                ){
                    'intent-filter' {
                        action("${NAME}":"${pkgName}.${getSimpleName(DISPATCHER_SERVICE)}")
                    }
                }


                provider(
                        "${AUTHORITIES}": getAuthority(),
                        "${EXPORTED}": "${FALSE}",
                        "${NAME}": DISPTACHER_PROVIDER,
                        "${ENABLED}": "${TRUE}",
                        "${PROCESS}": dispatcherProcess
                )

            } else {
                service("${NAME}": DISPATCHER_SERVICE,
                        "${ENABLED}": "${TRUE}",
                        "${EXPORTED}": "${FALSE}"
                ){
                    'intent-filter' {
                        action("${NAME}":"${pkgName}.${getSimpleName(DISPATCHER_SERVICE)}")
                    }
                }


                provider(
                        "${AUTHORITIES}": getAuthority(),
                        "${EXPORTED}": "${FALSE}",
                        "${NAME}": DISPTACHER_PROVIDER,
                        "${ENABLED}": "${TRUE}"
                )

            }

        }

        // 删除 application 标签
        def normalStr = writer.toString().replace("<application>", "").replace("</application>", "")

        return normalStr
    }

    private String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1)
    }

    private String getAuthority() {
        return pkgName + "." + DISPATCHER_AUTHORITY
    }

}
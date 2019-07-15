package com.okay.supercross.plugin.manifest


class ComponentReader implements IComponentReader {

    def manifest

    ComponentReader(String filePath) {
        manifest = new XmlSlurper().parse(filePath)
    }

    @Override
    void readActivities(Set<String> processNames) {
        manifest.application.activity.each {
            addProcess(processNames, it)
        }
    }

    @Override
    void readServices(Set<String> processNames) {
        manifest.application.service.each {
            addProcess(processNames, it)
        }
    }

    private void addProcess(Set<String> processNames, def it) {
        String processName = it.'@android:process'
        if (processName != null && processName.length() > 0) {
            processNames.add(processName)
        }
    }

    @Override
    void readBroadcastReceivers(Set<String> processNames) {
        manifest.application.receiver.each {
            addProcess(processNames, it)
        }
    }

    @Override
    void readProviders(Set<String> processNames) {
        manifest.application.provider.each {
            addProcess(processNames, it)
        }
    }
}
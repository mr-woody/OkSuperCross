package com.okay.supercross.plugin.manifest

interface IManifestParser {
    Set<String> getCustomProcessNames(String manifestPath)
}
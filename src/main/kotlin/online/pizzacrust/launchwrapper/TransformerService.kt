package online.pizzacrust.launchwrapper

import javassist.CtClass

interface TransformerService {

    fun runTransformers(ctClass: CtClass)

    fun getPackageExceptions() : List<String>

    fun containsInvalidPackage(name: String): Boolean {
        getPackageExceptions().forEach { packageName ->
            if (name.startsWith(packageName)) return true
        }
        return false
    }

}
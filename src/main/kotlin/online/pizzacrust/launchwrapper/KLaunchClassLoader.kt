package online.pizzacrust.launchwrapper

import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import java.net.URL
import java.net.URLClassLoader

class KLaunchClassLoader(urls: Array<URL>, val service: TransformerService, val classPool: ClassPool) : URLClassLoader(urls,
        null) {

    private val cached: MutableMap<String, Class<*>> = mutableMapOf()

    private fun kotlinGetCtClass(name: String): CtClass? {
        try {
            return classPool.getCtClass(name)
        } catch (e: NotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    override fun findClass(name: String?): Class<*> {
        if (name != null) {
            if (cached.containsKey(name)) return cached.get(name)!!
            if (!service.containsInvalidPackage(name)) {
                val ctClass = kotlinGetCtClass(name)
                if (ctClass != null) {
                    service.runTransformers(ctClass)
                    val clazz = ctClass.toClass(this)
                    cached.put(name, clazz)
                    return clazz
                }
            }
        }
        return super.findClass(name)
    }

}

class ExampleClass {

    fun test() {
        println("fail")
    }

}

fun exampleTransform(ctClass: CtClass) {
    ctClass.getDeclaredMethod("test").setBody("System.out.println(\"success\");")
}

fun main(vararg strings: String) {
    val classLoader: KLaunchClassLoader = KLaunchClassLoader(arrayOf(), Transformers, ClassPool.getDefault())
    Transformers.registerTransformer(::exampleTransform)
    val testClass = classLoader.loadClass(ExampleClass::class.qualifiedName)
    testClass.getDeclaredMethod("test").invoke(testClass.newInstance())
}
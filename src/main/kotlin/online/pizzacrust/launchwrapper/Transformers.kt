package online.pizzacrust.launchwrapper

import javassist.CtClass

object Transformers: TransformerService by ObjTransformService() {

    class ObjTransformService: TransformerService {
        override fun getPackageExceptions(): List<String> {
            return exceptions
        }

        override fun runTransformers(ctClass: CtClass) {
            transformers.forEach { transformer ->
                transformer.invoke(ctClass)
            }
        }
    }

    private val transformers: MutableList<(CtClass) -> Unit > = mutableListOf()
    private val exceptions: MutableList<String> = mutableListOf()

    fun registerTransformer(transformer: (CtClass) -> Unit) {
        if (!transformers.contains(transformer)) transformers.add(transformer)
    }

    fun registerPackageException(packageName: String) {
        if (!exceptions.contains(packageName)) exceptions.add(packageName)
    }

}
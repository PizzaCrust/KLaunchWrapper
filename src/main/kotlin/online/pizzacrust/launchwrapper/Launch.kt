package online.pizzacrust.launchwrapper

import javassist.ClassPool
import java.net.URLClassLoader

val classLoader: KLaunchClassLoader
            get() = KLaunchClassLoader((Tweaker::class.java.classLoader as URLClassLoader).urLs,
                    Transformers, ClassPool.getDefault())
val tweakers: MutableList<Tweaker> = mutableListOf()

private fun hasEmptyConstructor(clazz: Class<*>): Boolean {
    try {
        clazz.getConstructor()
    } catch (e: NoSuchMethodException) {
        return false
    }
    return true
}

fun main(vararg strings: String) {
    val listArgs = strings.toMutableList()
    val tweakClasses = mutableListOf<String>()
    var catchNext = false
    val removedIndexes = mutableListOf<Int>()
    var index = 0
    for (listArg in listArgs) {
        if (listArg.startsWith("--tweakClass", true)) { catchNext = true; removedIndexes
                .add(index); index++; continue; }
        if (catchNext) {
            tweakClasses.add(listArg)
            catchNext = false
            removedIndexes.add(index)
        }
        index++
    }
    var processedArgs = mutableListOf<String>()
    listArgs.forEachIndexed { index, string ->
        if (!removedIndexes.contains(index)) processedArgs.add(string)
    }
    println("[KLaunchWrapper] Received ${tweakClasses.size} tweak classes in arguments.")
    tweakClasses.forEach { name ->
        val clazz = Class.forName(name)
        if ((Tweaker::class.java.isAssignableFrom(clazz)) and (hasEmptyConstructor(clazz))) {
            tweakers.add(clazz.newInstance() as Tweaker)
        }
    }
    println("[KLaunchWrapper] Loaded ${tweakers.size} tweakers from tweak class arguments.")
    var launchTarget: String? = null
    tweakers.forEach { tweaker ->
        println("[KLaunchWrapper] Running tweaker: ${tweaker::class.java.simpleName}")
        tweaker.handleArguments(processedArgs.toTypedArray())
        tweaker.handleClassLoader(classLoader)
        launchTarget = tweaker.launchTarget
        processedArgs = tweaker.launchArguments.toMutableList()
    }
    println("[KLaunchWrapper] Running wrapped application (${launchTarget})...")
    val mainClass = classLoader.loadClass(launchTarget)
    mainClass.getDeclaredMethod("main", Array<String>::class.java).invoke(null, processedArgs.toTypedArray())
}
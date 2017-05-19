package online.pizzacrust.launchwrapper

interface Tweaker {

    fun handleArguments(args: Array<String>)

    fun handleClassLoader(cl: KLaunchClassLoader)

    val launchTarget: String

    val launchArguments: Array<String>

}
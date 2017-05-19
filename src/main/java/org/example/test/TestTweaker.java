package org.example.test;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import online.pizzacrust.launchwrapper.KLaunchClassLoader;
import online.pizzacrust.launchwrapper.LaunchKt;
import online.pizzacrust.launchwrapper.Transformers;
import online.pizzacrust.launchwrapper.Tweaker;

public class TestTweaker implements Tweaker {

    @Override
    public void handleArguments(@NotNull String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
    }

    @Override
    public void handleClassLoader(@NotNull KLaunchClassLoader cl) {
        Transformers.INSTANCE.registerTransformer((ctClass -> {
            try {
                ctClass.getDeclaredMethod("main").setBody("System.out.println(\"Success\");");
            } catch (CannotCompileException | NotFoundException e) {
                e.printStackTrace();
            }
            return Unit.INSTANCE;
        }));
    }

    @NotNull
    @Override
    public String getLaunchTarget() {
        return TestTarget.class.getName();
    }

    @NotNull
    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    public static void main(String... args) throws Exception {
        LaunchKt.main("lol", "--tweakClass", TestTweaker.class.getName(), "meow");
    }

}

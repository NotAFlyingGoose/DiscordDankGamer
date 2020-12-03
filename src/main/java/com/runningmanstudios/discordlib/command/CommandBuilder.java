package com.runningmanstudios.discordlib.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandBuilder {
    String name();
    String description();
    String[] usages() default {""};
    String[] aliases() default {""};
}

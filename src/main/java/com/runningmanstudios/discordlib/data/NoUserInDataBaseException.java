package com.runningmanstudios.discordlib.data;

public class NoUserInDataBaseException extends RuntimeException {

    public NoUserInDataBaseException(String guildId, String userId) {
        super("User #"+userId + " in guild #" + guildId + " does not exist within Database");
    }

    public NoUserInDataBaseException(String guildId, String userId, Throwable cause) {
        super("User #"+userId + " in guild #" + guildId + " does not exist within Database", cause);
    }

    public NoUserInDataBaseException(Throwable cause) {
        super(cause);
    }

    public NoUserInDataBaseException(String guildId, String userId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("User #"+userId + " in guild #" + guildId + " does not exist within Database", cause, enableSuppression, writableStackTrace);
    }

}

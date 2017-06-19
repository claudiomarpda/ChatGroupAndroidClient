package com.mz.chat.control;

/**
 * Created by mz on 17/06/17.
 * <p>
 * An interface for classes that deal with socket connection and stream.
 */
interface Connection {

    void disconnect();

    boolean isConnected();
}

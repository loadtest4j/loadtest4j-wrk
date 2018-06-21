package com.github.loadtest4j.drivers.wrk;

import java.io.InputStream;

class WrkLuaScript {
    protected static AutoDeletingTempFile create() {
        final InputStream scriptStream = WrkLuaScript.class.getResourceAsStream("/loadtest4j-wrk.lua");
        return AutoDeletingTempFile.create(scriptStream);
    }
}

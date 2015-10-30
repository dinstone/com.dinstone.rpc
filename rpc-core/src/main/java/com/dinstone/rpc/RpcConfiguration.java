
package com.dinstone.rpc;

import com.dinstone.rpc.serialize.SerializeType;

public class RpcConfiguration extends Configuration {

    /** service host name */
    private static final String SERVICE_HOST = "rpc.service.host";

    /** service port */
    private static final String SERVICE_PORT = "rpc.service.port";

    /** RPC protocol max length */
    private static final String MESSAGE_MAXLENGTH = "rpc.message.maxlength";

    /** serialize type */
    public static final String SERIALIZE_TYPE = "rpc.serialize.type";

    /** RPC protocol version */
    private static final String MESSAGE_TYPE = "rpc.message.type";

    public String getServiceHost() {
        return get(SERVICE_HOST);
    }

    public void setServiceHost(String host) {
        set(SERVICE_HOST, host);
    }

    public int getServicePort() {
        return getInt(SERVICE_PORT, 9958);
    }

    public void setServicePort(int port) {
        setInt(SERVICE_PORT, port);
    }

    public int getMessageMaxSize() {
        return getInt(MESSAGE_MAXLENGTH, Integer.MAX_VALUE);
    }

    public void setSerializeType(SerializeType type) {
        setInt(SERIALIZE_TYPE, type.getValue());
    }

    public SerializeType getSerializeType() {
        return SerializeType.valueOf(getInt(SERIALIZE_TYPE, SerializeType.JACKSON.getValue()));
    }

    public int getCallTimeout() {
        return getInt("rpc.call.timeout", 3000);
    }

}

package com.bitwise.bitmarket.common.protorpc;

import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

public interface PeerSession {
    RpcClientChannel getChannel();
    ClientRpcController getController();
    void shutdown();
}

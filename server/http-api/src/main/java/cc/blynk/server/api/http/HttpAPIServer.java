package cc.blynk.server.api.http;

import cc.blynk.core.http.rest.HandlerRegistry;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.handlers.HttpAndWebSocketUnificatorHandler;
import cc.blynk.server.api.http.logic.HttpAPILogic;
import cc.blynk.server.api.http.logic.ResetPasswordLogic;
import cc.blynk.server.api.http.logic.business.HttpBusinessAPILogic;
import cc.blynk.server.api.http.logic.ide.IDEAuthLogic;
import cc.blynk.server.core.BaseServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/12/2015.
 */
public class HttpAPIServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;
    public static final String WEBSOCKET_PATH = "/websocket";

    public HttpAPIServer(Holder holder) {
        super(holder.props.getIntProperty("http.port"), holder.transportTypeHolder);

        HandlerRegistry.register(new ResetPasswordLogic(holder.props, holder.userDao, holder.mailWrapper));
        HandlerRegistry.register(new HttpAPILogic(holder));
        HandlerRegistry.register(new HttpBusinessAPILogic(holder));
        HandlerRegistry.register(new IDEAuthLogic(holder.userDao, holder.redisClient));

        HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler = new HttpAndWebSocketUnificatorHandler(holder, port);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("HttpServerCodec", new HttpServerCodec());
                pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(65536, true));
                pipeline.addLast("HttpWebSocketUnificator", httpAndWebSocketUnificatorHandler);
            }
        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "HTTP API and WebSockets";
    }

    @Override
    public void close() {
        System.out.println("Shutting down HTTP API and WebSockets server...");
        super.close();
    }

}

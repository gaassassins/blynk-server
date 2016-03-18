package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.utils.ByteBufUtil.*;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.03.16.
 */
public class AddEnergyLogic {

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        int energyAmountToAdd = ParseUtil.parseInt(message.body, message.id);

        user.purchaseEnergy(energyAmountToAdd);
        ctx.writeAndFlush(ok(ctx, message.id), ctx.voidPromise());
    }

}
package com.github.lunatrius.ingameinfo.network.message;

import com.github.lunatrius.ingameinfo.tag.Tag;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class MessageSeed implements IMessage, IMessageHandler<MessageSeed, IMessage> {
    public long seed;

    public MessageSeed() {
        this.seed = 0;
    }

    public MessageSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.seed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.seed);
    }

    @Override
    public IMessage onMessage(MessageSeed message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            Tag.setSeed(message.seed);
        }

        return null;
    }
}

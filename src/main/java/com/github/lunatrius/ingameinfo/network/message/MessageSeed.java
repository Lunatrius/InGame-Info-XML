package com.github.lunatrius.ingameinfo.network.message;

import com.github.lunatrius.ingameinfo.tag.Tag;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSeed implements IMessage, IMessageHandler<MessageSeed, IMessage> {
    public long seed;

    public MessageSeed() {
        this.seed = 0;
    }

    public MessageSeed(final long seed) {
        this.seed = seed;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        this.seed = buf.readLong();
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeLong(this.seed);
    }

    @Override
    public IMessage onMessage(final MessageSeed message, final MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            Tag.setSeed(message.seed);
        }

        return null;
    }
}

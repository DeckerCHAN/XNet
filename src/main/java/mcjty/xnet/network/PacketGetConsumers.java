package mcjty.xnet.network;

import mcjty.lib.network.CommandHandler;
import mcjty.lib.network.PacketRequestListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.TileEntityController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

public class PacketGetConsumers extends PacketRequestListFromServer<PacketGetConsumers.SidedPos, PacketGetConsumers, PacketConsumersReady> {

    public PacketGetConsumers() {

    }

    public PacketGetConsumers(BlockPos pos) {
        super(XNet.MODID, pos, TileEntityController.CMD_GETCONSUMERS);
    }

    public static class Handler implements IMessageHandler<PacketGetConsumers, IMessage> {
        @Override
        public IMessage onMessage(PacketGetConsumers message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetConsumers message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            if(!(te instanceof CommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            CommandHandler commandHandler = (CommandHandler) te;
            List<SidedPos> list = commandHandler.executeWithResultList(message.command, message.args, Type.create(SidedPos.class));
            XNetMessages.INSTANCE.sendTo(new PacketConsumersReady(message.pos, TileEntityController.CLIENTCMD_CONSUMERSREADY, list), ctx.getServerHandler().player);
        }
    }

    public static class SidedPos {
        private final BlockPos pos;
        private final EnumFacing side;

        public SidedPos(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
            this.pos = pos;
            this.side = side;
        }

        @Nonnull
        public BlockPos getPos() {
            return pos;
        }

        @Nonnull
        public EnumFacing getSide() {
            return side;
        }
    }
}

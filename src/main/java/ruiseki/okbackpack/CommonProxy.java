package ruiseki.okbackpack;

import net.minecraftforge.common.MinecraftForge;

import ruiseki.okbackpack.common.event.ItemRenderEvent;
import ruiseki.okbackpack.common.network.PacketBackpackNBT;
import ruiseki.okbackpack.common.network.PacketQuickDraw;
import ruiseki.okbackpack.common.network.PacketSyncCarriedItem;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;

public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OKBackpack.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketBackpackNBT.class);
        packetHandler.register(PacketSyncCarriedItem.class);
        packetHandler.register(PacketQuickDraw.class);
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();;
        MinecraftForge.EVENT_BUS.register(new ItemRenderEvent());
    }
}

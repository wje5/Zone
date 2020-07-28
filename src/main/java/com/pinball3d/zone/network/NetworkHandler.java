package com.pinball3d.zone.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
	public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("zone");

	private static int nextID = 0;

	public NetworkHandler() {
		registerMessage(MessageWizardData.Handler.class, MessageWizardData.class, Side.SERVER);
		registerMessage(MessageBullet.Handler.class, MessageBullet.class, Side.SERVER);
		registerMessage(MessageOpenSphinx.Handler.class, MessageOpenSphinx.class, Side.SERVER);
		registerMessage(MessageShutdownSphinx.Handler.class, MessageShutdownSphinx.class, Side.SERVER);
		registerMessage(MessageConnectToNetwork.Handler.class, MessageConnectToNetwork.class, Side.SERVER);
		registerMessage(MessageTerminalConnectToNetwork.Handler.class, MessageTerminalConnectToNetwork.class,
				Side.SERVER);
		registerMessage(MessageTerminalRequestNetworkData.Handler.class, MessageTerminalRequestNetworkData.class,
				Side.SERVER);
		registerMessage(MessageSendNetworkDataToTerminal.Handler.class, MessageSendNetworkDataToTerminal.class,
				Side.CLIENT);
		registerMessage(MessageRequestValidNetworks.Handler.class, MessageRequestValidNetworks.class, Side.SERVER);
		registerMessage(MessageSendValidNetworkData.Handler.class, MessageSendValidNetworkData.class, Side.CLIENT);
		registerMessage(MessageChangePassword.Handler.class, MessageChangePassword.class, Side.SERVER);
		registerMessage(MessageChangeAdminPassword.Handler.class, MessageChangeAdminPassword.class, Side.SERVER);
		registerMessage(MessageChangeName.Handler.class, MessageChangeName.class, Side.SERVER);
		registerMessage(MessageOpenIOPanelGui.Handler.class, MessageOpenIOPanelGui.class, Side.SERVER);
		registerMessage(MessageIOPanelPageChange.Handler.class, MessageIOPanelPageChange.class, Side.SERVER);
		registerMessage(MessageUpdateIOPanelGui.Handler.class, MessageUpdateIOPanelGui.class, Side.SERVER);
		registerMessage(MessageIOPanelSearchChange.Handler.class, MessageIOPanelSearchChange.class, Side.SERVER);
		registerMessage(MessageIOPanelRequest.Handler.class, MessageIOPanelRequest.class, Side.SERVER);
		registerMessage(MessageIOPanelSendItemToStorage.Handler.class, MessageIOPanelSendItemToStorage.class,
				Side.SERVER);
		registerMessage(MessageRequestStorage.Handler.class, MessageRequestStorage.class, Side.SERVER);
		registerMessage(MessageSendStorageToClient.Handler.class, MessageSendStorageToClient.class, Side.CLIENT);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}

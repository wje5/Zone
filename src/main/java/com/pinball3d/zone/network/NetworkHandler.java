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
		registerMessage(MessageRegisterSphinx.Handler.class, MessageRegisterSphinx.class, Side.SERVER);
		registerMessage(MessageSendUUIDToClient.Handler.class, MessageSendUUIDToClient.class, Side.CLIENT);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}

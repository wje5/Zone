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
		registerMessage(MessageOpenSphinx.Handler.class, MessageOpenSphinx.class, Side.SERVER);
		registerMessage(MessageShutdownSphinx.Handler.class, MessageShutdownSphinx.class, Side.SERVER);
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
		registerMessage(MessageDisconnect.Handler.class, MessageDisconnect.class, Side.SERVER);
		registerMessage(MessageTerminalDisconnect.Handler.class, MessageTerminalDisconnect.class, Side.SERVER);
		registerMessage(MessagePlaySoundAtPos.Handler.class, MessagePlaySoundAtPos.class, Side.CLIENT);
		registerMessage(MessageTryConnectToNetwork.Handler.class, MessageTryConnectToNetwork.class, Side.SERVER);
		registerMessage(MessageConnectNetworkCallback.Handler.class, MessageConnectNetworkCallback.class, Side.CLIENT);
		registerMessage(MessageConnectNetworkCallbackWrong.Handler.class, MessageConnectNetworkCallbackWrong.class,
				Side.CLIENT);
		registerMessage(MessageRequestNetworkInfo.Handler.class, MessageRequestNetworkInfo.class, Side.SERVER);
		registerMessage(MessageSendNetworkInfoToClient.Handler.class, MessageSendNetworkInfoToClient.class,
				Side.CLIENT);
		registerMessage(MessageRequestNeedNetworkInfo.Handler.class, MessageRequestNeedNetworkInfo.class, Side.SERVER);
		registerMessage(MessageSendNeedNetworkInfoToClient.Handler.class, MessageSendNeedNetworkInfoToClient.class,
				Side.CLIENT);
		registerMessage(MessageDeleteNeedNetworkUnit.Handler.class, MessageDeleteNeedNetworkUnit.class, Side.SERVER);
		registerMessage(MessageComputeLogisticTime.Handler.class, MessageComputeLogisticTime.class, Side.SERVER);
		registerMessage(MessageSendLogisticTimeToClient.Handler.class, MessageSendLogisticTimeToClient.class,
				Side.CLIENT);
		registerMessage(MessageIOPanelTransferPlayerInventory.Handler.class,
				MessageIOPanelTransferPlayerInventory.class, Side.SERVER);
		registerMessage(MessageUpdateContainerIOPanel.Handler.class, MessageUpdateContainerIOPanel.class, Side.CLIENT);
		registerMessage(MessageErrorStorageFull.Handler.class, MessageErrorStorageFull.class, Side.CLIENT);
		registerMessage(MessageConnectionUpdate.Handler.class, MessageConnectionUpdate.class, Side.CLIENT);
		registerMessage(MessageConnectionRequest.Handler.class, MessageConnectionRequest.class, Side.SERVER);
		registerMessage(MessageConnectionNeedNetworkRequest.Handler.class, MessageConnectionNeedNetworkRequest.class,
				Side.SERVER);
		registerMessage(MessageConnectionControllerRequest.Handler.class, MessageConnectionControllerRequest.class,
				Side.SERVER);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}

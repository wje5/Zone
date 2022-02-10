package com.pinball3d.zone.network;

import com.pinball3d.zone.network.elite.MessageCloseElite;
import com.pinball3d.zone.network.elite.MessageRequestNetworks;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
	public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("zone");
	private static MessageZone.Handler handler = new MessageZone.Handler();
	private static int nextID = 0;

	public NetworkHandler() {
		registerMessage(MessageShutdownSphinx.Handler.class, MessageShutdownSphinx.class, Side.SERVER);
		registerMessage(MessageChangeName.Handler.class, MessageChangeName.class, Side.SERVER);
		registerMessage(MessageIOPanelRequest.Handler.class, MessageIOPanelRequest.class, Side.SERVER);
		registerMessage(MessageDisconnect.Handler.class, MessageDisconnect.class, Side.SERVER);
		registerMessage(MessagePlaySoundAtPos.Handler.class, MessagePlaySoundAtPos.class, Side.CLIENT);
		registerMessage(MessageConnectionUpdate.Handler.class, MessageConnectionUpdate.class, Side.CLIENT);
		registerMessage(MessageConnectionNeedNetworkRequest.Handler.class, MessageConnectionNeedNetworkRequest.class,
				Side.SERVER);
		registerMessage(MessageNewClass.Handler.class, MessageNewClass.class, Side.SERVER);
		registerMessage(MessageManageClassify.Handler.class, MessageManageClassify.class, Side.SERVER);
		registerMessage(MessageRenameClassify.Handler.class, MessageRenameClassify.class, Side.SERVER);
		registerMessage(MessageDeleteClassify.Handler.class, MessageDeleteClassify.class, Side.SERVER);
		registerMessage(MessageChangeGravatar.Handler.class, MessageChangeGravatar.class, Side.SERVER);
		registerMessage(MessageDeleteUser.Handler.class, MessageDeleteUser.class, Side.SERVER);
		registerMessage(MessageReviewUser.Handler.class, MessageReviewUser.class, Side.SERVER);
		registerMessage(MessageTransferAdmin.Handler.class, MessageTransferAdmin.class, Side.SERVER);
		registerMessage(MessageRescanRecipes.Handler.class, MessageRescanRecipes.class, Side.SERVER);
		registerMessage(MessageManageOreDictionaryPriority.Handler.class, MessageManageOreDictionaryPriority.class,
				Side.SERVER);
		registerMessage(MessageRescanRecipesFinish.Handler.class, MessageRescanRecipesFinish.class, Side.CLIENT);
		registerMessage(MessageDeleteOreDictionary.Handler.class, MessageDeleteOreDictionary.class, Side.SERVER);
		registerMessage(MessageUpdateCameraPos.Handler.class, MessageUpdateCameraPos.class, Side.SERVER);

		// ELITE
		registerMessage(MessageRequestNetworks.class, Side.SERVER);
		registerMessage(MessageRequestNetworks.PostBack.class, Side.CLIENT);
		registerMessage(MessageCloseElite.class, Side.SERVER);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}

	private static void registerMessage(Class<? extends MessageZone> requestMessageType, Side side) {
		registerMessage(handler, requestMessageType, side);
	}
}

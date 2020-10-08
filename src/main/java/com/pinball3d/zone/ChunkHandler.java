package com.pinball3d.zone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChunkHandler implements LoadingCallback {
	public static ChunkHandler instance;

	private HashMap<WorldPos, Ticket> map = new HashMap<WorldPos, Ticket>();

	public ChunkHandler() {
		ForgeChunkManager.setForcedChunkLoadingCallback(Zone.instance, this);
		instance = this;
	}

	@SubscribeEvent
	public void unloadWorld(WorldEvent.Unload evt) {
		this.unloadTickets(map, evt.getWorld().provider.getDimension());
	}

	private void unloadTickets(HashMap<?, Ticket> tickets, int dim) {
		Iterator<Ticket> it = tickets.values().iterator();
		while (it.hasNext()) {
			Ticket t = it.next();
			if (t.world.provider.getDimension() == dim) {
				it.remove();
			}
		}
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			switch (ticket.getType()) {
			case NORMAL:
				NBTTagCompound tag = ticket.getModData();
				WorldPos pos = WorldPos.load(tag);
				TileEntity tileentity = pos.getTileEntity();
				if (tileentity instanceof IChunkLoader) {
					IChunkLoader te = (IChunkLoader) tileentity;
					WorldPos loc = new WorldPos(tileentity);
					this.forceTicketChunks(ticket, te.getLoadChunks());
					this.addTicket(loc, ticket);
				} else {
					ForgeChunkManager.releaseTicket(ticket);
				}
				break;
			case ENTITY:
				break;
			}
		}
	}

	private void addTicket(WorldPos pos, Ticket ticket) {
		map.put(pos, ticket);
	}

	public void unloadChunks(WorldPos pos) {
		Ticket ticket = map.remove(pos);
		ForgeChunkManager.releaseTicket(ticket);
	}

	public void loadChunks(WorldPos pos) {
		Ticket ticket = map.get(pos);
		TileEntity te = pos.getTileEntity();
		if (te instanceof IChunkLoader) {
			if (ticket == null) {
				ticket = this.getNewTileTicket(pos);
				this.addTicket(pos, ticket);
			}
			this.forceTicketChunks(ticket, ((IChunkLoader) te).getLoadChunks());
		}
	}

	private Ticket getNewTileTicket(WorldPos pos) {
		Ticket ticket = ForgeChunkManager.requestTicket(Zone.instance, pos.getWorld(), ForgeChunkManager.Type.NORMAL);
		NBTTagCompound tag = ticket.getModData();
		pos.writeToNBT(tag);
		return ticket;
	}

	private void forceTicketChunks(Ticket ticket, Set<ChunkPos> chunks) {
		Set<ChunkPos> ticketChunks = ticket.getChunkList();
		for (ChunkPos coord : ticketChunks) {
			if (!chunks.contains(coord)) {
				ForgeChunkManager.unforceChunk(ticket, coord);
			}
		}
		for (ChunkPos coord : chunks) {
			if (!ticketChunks.contains(coord)) {
				ForgeChunkManager.forceChunk(ticket, coord);
			}
		}
	}

	public static interface IChunkLoader {
		public Set<ChunkPos> getLoadChunks();
	}
}

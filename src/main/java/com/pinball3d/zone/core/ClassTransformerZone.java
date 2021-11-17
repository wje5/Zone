package com.pinball3d.zone.core;

import java.util.Iterator;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformerZone implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		try {
			switch (transformedName) {
			case "net.minecraft.client.renderer.EntityRenderer":
				ClassReader reader = new ClassReader(basicClass);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				Iterator<MethodNode> it = node.methods.iterator();
				while (it.hasNext()) {
					MethodNode method = it.next();
					if (method.name.equals("updateCameraAndRender")
							|| method.name.equals("a") && "(FJ)V".equals(method.desc)) {
						ListIterator<AbstractInsnNode> it2 = method.instructions.iterator();
						LabelNode label = null;
						LineNumberNode line1027 = null;
						while (it2.hasNext()) {
							AbstractInsnNode n = it2.next();
							if (n instanceof MethodInsnNode) {
								MethodInsnNode m = (MethodInsnNode) n;
								if ((m.owner.equals("net/minecraft/client/renderer/GlStateManager")
										&& m.name.equals("clear")) || (m.owner.equals("bus") && m.name.equals("m"))) {
									label = (LabelNode) n.getPrevious().getPrevious().getPrevious();
								}
							} else if (n instanceof FieldInsnNode) {
								FieldInsnNode f = (FieldInsnNode) n;
								if ((f.owner.equals("net/minecraft/client/settings/GameSettings")
										&& f.name.equals("limitFramerate"))
										|| (f.owner.equals("bid") && f.name.equals("i"))) {
									line1027 = (LineNumberNode) n.getNext().getNext().getNext();
								}
							}
						}
						InsnList list = new InsnList();
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
								LoadingPluginZone.runtimeDeobf ? "bib" : "net/minecraft/client/Minecraft",
								LoadingPluginZone.runtimeDeobf ? "func_71410_x" : "getMinecraft",
								LoadingPluginZone.runtimeDeobf ? "()Lbib;" : "()Lnet/minecraft/client/Minecraft;",
								false));
						list.add(new FieldInsnNode(Opcodes.GETFIELD,
								LoadingPluginZone.runtimeDeobf ? "bib" : "net/minecraft/client/Minecraft",
								LoadingPluginZone.runtimeDeobf ? "m" : "currentScreen",
								LoadingPluginZone.runtimeDeobf ? "Lblk;" : "Lnet/minecraft/client/gui/GuiScreen;"));
						list.add(new TypeInsnNode(Opcodes.INSTANCEOF,
								"com/pinball3d/zone/sphinx/elite/EliteMainwindow"));
						list.add(new JumpInsnNode(Opcodes.IFNE, label));
						method.instructions.insert(line1027, list);
					}
				}
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				node.accept(writer);
				return writer.toByteArray();
			case "net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer":
				reader = new ClassReader(basicClass);
				node = new ClassNode();
				reader.accept(node, 0);
				it = node.methods.iterator();
				while (it.hasNext()) {
					MethodNode method = it.next();
					if (method.name.equals("render")) {
						ListIterator<AbstractInsnNode> it2 = method.instructions.iterator();
						while (it2.hasNext()) {
							AbstractInsnNode n = it2.next();
							if (n instanceof MethodInsnNode) {
								MethodInsnNode m = (MethodInsnNode) n;
								if (m.name.equals("shouldSideBeRendered")
										|| m.owner.equals("awt") && m.name.equals("c")) {
									m.setOpcode(Opcodes.INVOKESTATIC);
									m.owner = "com/pinball3d/zone/sphinx/elite/map/MapRenderManager";
									m.name = "shouldSideBeRendered";
									m.desc = LoadingPluginZone.runtimeDeobf ? "(Lawt;Lamy;Let;Lfa;)Z"
											: "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z";
									m.itf = false;
									break;
								}
							}
						}
					}
				}
				writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				node.accept(writer);
				return writer.toByteArray();
			case "net.minecraft.server.management.PlayerList":
				reader = new ClassReader(basicClass);
				node = new ClassNode();
				reader.accept(node, 0);
				it = node.methods.iterator();
				while (it.hasNext()) {
					MethodNode method = it.next();
					if (method.name.equals("serverUpdateMovingPlayer")
							|| method.name.equals("d") && method.desc != null && "(Loq;)V".equals(method.desc)) {
						ListIterator<AbstractInsnNode> it2 = method.instructions.iterator();
						while (it2.hasNext()) {
							AbstractInsnNode n = it2.next();
							if (n instanceof VarInsnNode) {
								it2.next();
								it2.remove();
								it2.next();
								it2.remove();
								it2.next();
								it2.remove();
								it2.next();
								it2.remove();
								method.instructions
										.insert(n,
												new MethodInsnNode(Opcodes.INVOKESTATIC,
														"com/pinball3d/zone/sphinx/elite/map/ServerChunkHelper",
														"updateCameraPos",
														LoadingPluginZone.runtimeDeobf ? "(Loq;)V"
																: "(Lnet/minecraft/entity/player/EntityPlayerMP;)V",
														false));
							}
						}
					}
				}
				writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				node.accept(writer);
				return writer.toByteArray();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return basicClass;
		}
		return basicClass;
	}
}

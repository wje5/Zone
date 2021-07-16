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
//					if (method.name.equals("renderWorld") || (method.name.equals("b") && method.signature != null
//							&& method.signature.equals("(FJ)V"))) {
//						LabelNode label = new LabelNode();
//						InsnList list = new InsnList();
//						list.add(label);
//						list.add(new InsnNode(Opcodes.RETURN));
//						method.instructions.insert(method.instructions.getLast(), list);
//						InsnList list2 = new InsnList();
//						list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
//								LoadingPluginZone.runtimeDeobf ? "bib;" : "net/minecraft/client/Minecraft",
//								LoadingPluginZone.runtimeDeobf ? "b" : "getMinecraft",
//								LoadingPluginZone.runtimeDeobf ? "()Lbib;" : "()Lnet/minecraft/client/Minecraft;"));
//						list2.add(new FieldInsnNode(Opcodes.GETFIELD,
//								LoadingPluginZone.runtimeDeobf ? "bib" : "net/minecraft/client/Minecraft",
//								LoadingPluginZone.runtimeDeobf ? "m" : "currentScreen",
//								LoadingPluginZone.runtimeDeobf ? "Lblk;" : "Lnet/minecraft/client/gui/GuiScreen;"));
//						list2.add(new TypeInsnNode(Opcodes.INSTANCEOF,
//								"com/pinball3d/zone/sphinx/elite/EliteMainwindow"));
//						list2.add(new JumpInsnNode(Opcodes.IFNE, label));
//						method.instructions.insertBefore(method.instructions.getFirst(), list2);
//					}
					if (method.name.equals("updateCameraAndRender")
							|| method.name.equals("a") && method.signature.equals("(FJ)V")) {
						ListIterator<AbstractInsnNode> it2 = method.instructions.iterator();
//						LabelNode label = (LabelNode) method.instructions.get(548);
//						LineNumberNode line1027 = (LineNumberNode) method.instructions.get(322);
						LabelNode label = null;
						LineNumberNode line1027 = null;
						while (it2.hasNext()) {
							AbstractInsnNode n = it2.next();
							System.out.println(n);
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
//							if (n instanceof LineNumberNode) {
//								LineNumberNode line = (LineNumberNode) n;
//								if (line.line == 1027) {
//									line1027 = line;
//								} else if (line.line == 1076) {
//									label = (LabelNode) n.getPrevious();
//								}
//							}
						}
						InsnList list = new InsnList();
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
								LoadingPluginZone.runtimeDeobf ? "bib;" : "net/minecraft/client/Minecraft",
								LoadingPluginZone.runtimeDeobf ? "b" : "getMinecraft",
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
			case "net.minecraft.client.renderer.BlockModelRenderer":
				reader = new ClassReader(basicClass);
				node = new ClassNode();
				reader.accept(node, 0);
				it = node.methods.iterator();
				while (it.hasNext()) {
					MethodNode method = it.next();
					if (method.name.equals("renderModelSmooth") || (method.name.equals("b"))) {
						LabelNode label = new LabelNode();
						InsnList list = new InsnList();
//						list.add(label);
//						list.add(new InsnNode(Opcodes.RETURN));
//						method.instructions.insert(method.instructions.getLast(), list);
//						InsnList list2 = new InsnList();
//						list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
//								LoadingPluginZone.runtimeDeobf ? "bib;" : "net/minecraft/client/Minecraft",
//								LoadingPluginZone.runtimeDeobf ? "b" : "getMinecraft",
//								LoadingPluginZone.runtimeDeobf ? "()Lbib;" : "()Lnet/minecraft/client/Minecraft;"));
//						list2.add(new FieldInsnNode(Opcodes.GETFIELD,
//								LoadingPluginZone.runtimeDeobf ? "bib" : "net/minecraft/client/Minecraft",
//								LoadingPluginZone.runtimeDeobf ? "m" : "currentScreen",
//								LoadingPluginZone.runtimeDeobf ? "Lblk;" : "Lnet/minecraft/client/gui/GuiScreen;"));
//						list2.add(new TypeInsnNode(Opcodes.INSTANCEOF,
//								"com/pinball3d/zone/sphinx/elite/EliteMainwindow"));
//						list2.add(new JumpInsnNode(Opcodes.IFNE, label));
//						method.instructions.insertBefore(method.instructions.getFirst(), list2);
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

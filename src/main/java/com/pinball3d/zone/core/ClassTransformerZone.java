package com.pinball3d.zone.core;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformerZone implements IClassTransformer {
	@SuppressWarnings("deprecation")
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		try {
			if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
				ClassReader reader = new ClassReader(basicClass);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				Iterator<MethodNode> it = node.methods.iterator();
				while (it.hasNext()) {
					MethodNode method = it.next();
					if (method.name.equals("renderWorld") || (method.name.equals("b") && method.signature != null
							&& method.signature.equals("(FJ)V"))) {
						LabelNode label = new LabelNode();
						InsnList list = new InsnList();
						list.add(label);
						list.add(new InsnNode(Opcodes.RETURN));
						method.instructions.insert(method.instructions.getLast(), list);
						InsnList list2 = new InsnList();
						list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
								LoadingPluginZone.runtimeDeobf ? "bib;" : "net/minecraft/client/Minecraft",
								LoadingPluginZone.runtimeDeobf ? "b" : "getMinecraft",
								LoadingPluginZone.runtimeDeobf ? "()Lbib;" : "()Lnet/minecraft/client/Minecraft;"));
						list2.add(new FieldInsnNode(Opcodes.GETFIELD,
								LoadingPluginZone.runtimeDeobf ? "bib" : "net/minecraft/client/Minecraft",
								LoadingPluginZone.runtimeDeobf ? "m" : "currentScreen",
								LoadingPluginZone.runtimeDeobf ? "Lblk;" : "Lnet/minecraft/client/gui/GuiScreen;"));
						list2.add(new TypeInsnNode(Opcodes.INSTANCEOF,
								"com/pinball3d/zone/sphinx/elite/EliteMainwindow"));
						list2.add(new JumpInsnNode(Opcodes.IFNE, label));
						method.instructions.insertBefore(method.instructions.getFirst(), list2);
					}
				}
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				node.accept(writer);
				return writer.toByteArray();
			}
		} catch (Throwable e) {
			return basicClass;
		}
		return basicClass;
	}
}

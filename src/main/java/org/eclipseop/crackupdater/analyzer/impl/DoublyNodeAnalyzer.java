package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundClass;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AnalyzerInformation(expectedFields = {"key", "previous", "next"})
public class DoublyNodeAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    FoundClass node = FoundStuff.findIdentifiedClass("Node");
    return classNodes.stream()
        .filter(cn -> cn.superName.equals(node.getClassNode().name))
        .filter(cn -> cn.fieldCount(cn.getWrappedName(), true) == 2)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access) && fn.desc.equals("J"))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("key", fn)));

    FieldInsnNode previous =
        identifiedClass.methods.stream()
            .filter(mn -> !Modifier.isStatic(mn.access))
            .filter(mn -> !mn.name.equals("<init>"))
            .mapMulti(
                (BiConsumer<MethodNode, Consumer<AbstractInsnNode>>)
                    (methodNode, consumer) ->
                        Arrays.stream(methodNode.instructions.toArray()).forEach(consumer))
            .filter(ain -> ain.opcode() == Opcodes.GETFIELD)
            .map(ain -> (FieldInsnNode) ain)
            .findFirst()
            .orElse(null);

    getFoundClass().addFoundField(FoundField.from(identifiedClass, "previous", previous));

    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access) && !fn.desc.equals("J"))
        .filter(fn -> !fn.name.equals(previous.name))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("next", fn)));
  }
}

package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundField;
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

@AnalyzerInformation(expectedFields = {"formatted", "raw"})
public class NamePairAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> cn.interfaces.stream().anyMatch(p -> p.contains("Comparable")))
        .filter(cn -> cn.fieldCount("Ljava/lang/String;", true) == 2)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    FieldInsnNode previous =
        identifiedClass.methods.stream()
            .filter(mn -> !Modifier.isStatic(mn.access))
            .filter(mn -> mn.name.equals("hashCode"))
            .mapMulti(
                (BiConsumer<MethodNode, Consumer<AbstractInsnNode>>)
                    (methodNode, consumer) ->
                        Arrays.stream(methodNode.instructions.toArray()).forEach(consumer))
            .filter(ain -> ain.opcode() == Opcodes.GETFIELD)
            .map(ain -> (FieldInsnNode) ain)
            .findFirst()
            .orElse(null);

    getFoundClass().addFoundField(FoundField.from(identifiedClass, "formatted", previous));

    identifiedClass.fields.stream()
        .filter(mn -> !Modifier.isStatic(mn.access))
        .filter(fn -> !fn.name.equals(previous.name))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("raw", fn)));
  }
}

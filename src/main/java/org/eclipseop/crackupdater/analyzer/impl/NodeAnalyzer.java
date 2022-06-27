package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

@AnalyzerInformation(expectedFields = {"key", "previous", "next"})
public class NodeAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(ClassNode::ownerless)
        .filter(cn -> cn.fieldCount("J") == 1)
        .filter(cn -> cn.fieldCount(cn.getWrappedName()) == 2)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access) && fn.desc.equals("J"))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("key", fn)));

    // TODO: 6/8/2022 fix to adjust for fake params
    MethodNode method = identifiedClass.getMethod("()Z");
    FieldInsnNode previousFin =
        Arrays.stream(method.instructions.toArray())
            .filter(ain -> ain.opcode() == Opcodes.GETFIELD)
            .map(ain -> (FieldInsnNode) ain)
            .findFirst()
            .orElse(null);
    getFoundClass().addFoundField(FoundField.from(identifiedClass, "previous", previousFin));

    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access) && !fn.desc.equals("J"))
        .filter(fn -> !fn.name.equals(previousFin.name))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("next", fn)));
  }
}

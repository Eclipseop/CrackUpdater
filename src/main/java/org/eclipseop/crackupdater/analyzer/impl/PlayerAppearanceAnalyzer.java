package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.Mask;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.List;

@AnalyzerInformation(expectedFields = {
    "female",
    "transformedNpcId",
    "equipmentIds",
    "ids"
})
public class PlayerAppearanceAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(ClassNode::ownerless)
        .filter(cn -> cn.fieldCount("[I") == 2)
        .filter(cn -> cn.fieldCount("Z") == 2)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access))
        .filter(fn -> fn.desc.equals("Z"))
        .filter(fn -> Modifier.isPublic(fn.access))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("female", fn)));

    identifiedClass.fields.stream()
        .filter(fn -> !Modifier.isStatic(fn.access))
        .filter(fn -> fn.desc.equals("I"))
        .filter(fn -> Modifier.isPublic(fn.access))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("transformedNpcId", fn)));

    findIds(identifiedClass);
  }

  private void findIds(ClassNode classNode) {
    MethodNode method =
        classNode.methods.stream()
            .filter(mn -> !Modifier.isStatic(mn.access))
            .filter(mn -> mn.desc.endsWith(")I"))
            .findFirst()
            .orElse(null);

    List<AbstractInsnNode> follow = MethodNodeUtil.follow(method.instructions);
    List<AbstractInsnNode> insnNodes =
        follow.stream()
            .filter(ain -> ain.opcode() == Opcodes.IRETURN)
            .toList();

    List<AbstractInsnNode> prev = Mask.prev(insnNodes.get(0), Mask.ISHL, Mask.ICONST_5, Mask.GETFIELD);
    FieldInsnNode equipmentsId = null;
    for (AbstractInsnNode abstractInsnNode : prev) {
      if (abstractInsnNode instanceof FieldInsnNode fin) {
        equipmentsId = fin;
        getFoundClass().addFoundField(FoundField.from(classNode, "equipmentIds", fin));
        break;
      }
    }

    FieldInsnNode finalEquipmentsId = equipmentsId;
    classNode.fields.stream()
            .filter(fn -> !Modifier.isStatic(fn.access))
            .filter(fn -> fn.desc.equals("[I"))
            .filter(fn -> !fn.name.equals(finalEquipmentsId.name))
            .findFirst()
            .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("ids", fn)));
  }
}

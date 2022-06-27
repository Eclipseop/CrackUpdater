package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.List;

@AnalyzerInformation(expectedFields = {
    "prayerIcon",
    "skullIcon",
    "actions",
    "combatLevel",
    "skillLevel",
    "team",
    "hidden",
    "namePair",
    "playerAppearance"
})
public class PlayerAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> Modifier.isFinal(cn.access))
        .filter(cn -> cn.superName.equals(FoundStuff.findIdentifiedClass("PathingEntity").getClassNode().name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    findConstructorFields(identifiedClass);

    identifiedClass.fields.stream()
            .filter(fn -> fn.desc.equals(FoundStuff.findIdentifiedClass("NamePair").getClassNode().getWrappedName()))
            .findFirst()
            .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("namePair", fn)));

    identifiedClass.fields.stream()
            .filter(fn -> fn.desc.equals(FoundStuff.findIdentifiedClass("PlayerAppearance").getClassNode().getWrappedName()))
            .findFirst()
            .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("playerAppearance", fn)));
  }

  private void findConstructorFields(ClassNode identifiedClass) {
    MethodNode init = identifiedClass.methods.stream().filter(mn -> mn.name.equals("<init>")).findFirst().orElse(null);

    List<AbstractInsnNode> follow = MethodNodeUtil.follow(init.instructions);
    List<FieldInsnNode> fins = follow.stream()
            .filter(ain -> ain.opcode() == Opcodes.PUTFIELD)
            .map(ain -> (FieldInsnNode) ain).toList();
    for (int i = 0; i < fins.size(); i++) {
      FieldInsnNode fin = fins.get(i);

      String name = switch (i) {
        case 0 -> "prayerIcon";
        case 1 -> "skullIcon";
        case 2 -> "actions";
        case 3 -> "combatLevel";
        case 4 -> "skillLevel";
        case 8 -> "team";
        case 9 -> "hidden";
        default -> null;
      };

      if (name == null) continue;
      getFoundClass().addFoundField(FoundField.from(identifiedClass, name, fin));
    }
  }
}

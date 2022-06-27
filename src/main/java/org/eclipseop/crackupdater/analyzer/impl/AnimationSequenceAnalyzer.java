package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
import org.eclipseop.crackupdater.util.found.FoundClass;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@AnalyzerInformation(expectedFields = {
    "loopOffset",
    "stretch",
    "priority",
    "offHand",
    "mainHand",
    "maxLoops",
    "animatingPrecedence",
    "walkingPrecedence",
    "replayMode"
})
public class AnimationSequenceAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    FoundClass doublyNode = FoundStuff.findIdentifiedClass("DoublyNode");

    return classNodes.stream()
        .filter(cn -> cn.fieldCount("[I") == 5)
        .filter(cn -> cn.superName.equals(doublyNode.getClassNode().name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    MethodNode init =
            identifiedClass.methods.stream()
                    .filter(mn -> mn.name.equals("<init>"))
                    .findFirst()
                    .orElse(null);

    List<AbstractInsnNode> follow = MethodNodeUtil.follow(init.instructions);
    List<FieldInsnNode> fins = follow.stream()
            .filter(ain -> ain.opcode() == Opcodes.PUTFIELD)
            .map(ain -> (FieldInsnNode) ain).toList();
    for (int i = 0; i < fins.size(); i++) {
      FieldInsnNode fin = fins.get(i);
      String name = switch (i) {
        case 3 -> "loopOffset";
        case 4 -> "stretch";
        case 5 -> "priority";
        case 6 -> "offHand";
        case 7 -> "mainHand";
        case 8 -> "maxLoops";
        case 9 -> "animatingPrecedence";
        case 10 -> "walkingPrecedence";
        case 11 -> "replayMode";
        default -> null;
      };
      if (name == null) continue;
      getFoundClass().addFoundField(FoundField.from(identifiedClass, name, fin));

    }
  }
}

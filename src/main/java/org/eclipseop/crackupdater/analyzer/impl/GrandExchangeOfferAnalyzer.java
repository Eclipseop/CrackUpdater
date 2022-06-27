package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@AnalyzerInformation(expectedFields = {"metadata",
    "itemId",
    "itemPrice",
    "itemQuantity",
    "transferred",
    "spent"
})
public class GrandExchangeOfferAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> cn.fieldCount("I") == 5)
        .filter(cn -> cn.fieldCount("B") == 1)
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
        case 0 -> "metadata";
        case 1 -> "itemId";
        case 2 -> "itemPrice";
        case 3 -> "itemQuantity";
        case 4 -> "transferred";
        case 5 -> "spent";
        default -> null;
      };
      if (name == null) continue;
      getFoundClass().addFoundField(FoundField.from(identifiedClass, name, fin));
    }

  }
}

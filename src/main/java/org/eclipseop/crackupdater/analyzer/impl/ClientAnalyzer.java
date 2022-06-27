package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AnalyzerInformation(expectedFields = {
    "loginState",
    "loginResponse1",
    "loginResponse2",
    "loginResponse3",
    "username",
    "password",
    "loginWorldSelectorOpen",
    "players",
    "localPlayer",
    "grandExchangeOffers",
    "mouseRecorder"
})
public class ClientAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream().filter(cn -> cn.name.equals("client")).findFirst().orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    findBasicFields(classNodes);
    findLoginFields(classNodes);
  }

  private void findLoginFields(List<ClassNode> classNodes) {
    MethodNode methodNode =
        classNodes.stream()
            .filter(cn -> cn.fieldCount("Ljava/lang/String;", false) == 9)
            .mapMulti(
                (BiConsumer<ClassNode, Consumer<MethodNode>>)
                    (classNode, consumer) -> classNode.methods.forEach(consumer))
            .filter(mn -> mn.name.equals("<clinit>"))
            .findFirst()
            .orElse(null);

    List<AbstractInsnNode> follow = MethodNodeUtil.follow(methodNode.instructions);
    List<FieldInsnNode> fins = follow.stream()
            .filter(ain -> ain.opcode() == Opcodes.PUTSTATIC)
            .map(ain -> (FieldInsnNode) ain).toList();
    for (int i = 0; i < fins.size(); i++) {
      FieldInsnNode fin = fins.get(i);
      String name = switch (i) {
        case 6 -> "loginState";
        case 8 -> "loginResponse1";
        case 9 -> "loginResponse2";
        case 10 -> "loginResponse3";
        case 11 -> "username";
        case 12 -> "password";
        case 21 -> "loginWorldSelectorOpen";
        default -> null;
      };
      if (name == null) continue;;
      getFoundClass().addFoundField(FoundField.from(fin.method.owner, name, fin));
    }

  }

  private void findBasicFields(List<ClassNode> classNodes) {
    List<FieldNode> allFields =
        classNodes.stream()
            .mapMulti(
                (BiConsumer<ClassNode, Consumer<FieldNode>>)
                    (classNode, consumer) -> classNode.fields.forEach(consumer))
            .toList();

    // TODO: 6/11/2022 add Npcs, interface comp
    for (FieldNode field : allFields) {
      String name = null;
      if (field.desc.equals(
          "[" + FoundStuff.findIdentifiedClass("Player").getClassNode().getWrappedName())) {
        name = "players";
      } else if (field.desc.equals(
          FoundStuff.findIdentifiedClass("Player").getClassNode().getWrappedName())) {
        name = "localPlayer";
      } else if (field.desc.equals(
          "["
              + FoundStuff.findIdentifiedClass("GrandExchangeOffer")
                  .getClassNode()
                  .getWrappedName())) {
        name = "grandExchangeOffers";
      } else if (field.desc.equals(
          FoundStuff.findIdentifiedClass("MouseRecorder").getClassNode().getWrappedName())) {
        name = "mouseRecorder";
      }

      if (name == null) continue;
      getFoundClass().addFoundField(new FoundField(name, field));
    }
  }
}

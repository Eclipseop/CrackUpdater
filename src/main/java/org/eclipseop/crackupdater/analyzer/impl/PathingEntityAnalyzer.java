package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.Mask;
import org.eclipseop.crackupdater.util.MethodNodeUtil;
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

@AnalyzerInformation(expectedFields = {"walkingStance",
    "overheadText",
    "hitsplatTypes",
    "hitsplats",
    "hitsplatCycles",
    "hitsplatIds",
    "specialHitsplats",
    "targetIndex",
    "stance",
    "stanceFrame",
    "animation",
    "animationFrame",
    "animationFrameCycle",
    "animationDelay",
    "effect",
    "effectFrame",
    "routeWaypointCount",
    "routeWaypointsX",
    "routeWaypointsY"
})
public class PathingEntityAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> Modifier.isAbstract(cn.access))
        .filter(cn -> cn.superName.equals(FoundStuff.findIdentifiedClass("Entity").getClassNode().name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    findHitsplatCount(identifiedClass);
    findPositions(classNodes, identifiedClass);

    MethodNode init = identifiedClass.methods.stream().filter(mn -> mn.name.equals("<init>")).findFirst().orElse(null);
    List<AbstractInsnNode> follow = MethodNodeUtil.follow(init.instructions);

    List<FieldInsnNode> fins = follow.stream()
            .filter(ain -> ain.opcode() == Opcodes.PUTFIELD)
            .map(ain -> (FieldInsnNode) ain).toList();
    for (int i = 0; i < fins.size(); i++) {
      FieldInsnNode fin = fins.get(i);
      String name = switch (i) {
        case 5 ->  "walkingStance";
        case 17 -> "overheadText";
        case 23 -> "hitsplatTypes";
        case 24 -> "hitsplats";
        case 25 -> "hitsplatCycles";
        case 26 -> "hitsplatIds";
        case 27 -> "specialHitsplats";
        case 29 -> "targetIndex";
        case 32 -> "stance";
        case 33 -> "stanceFrame";
        case 35 -> "animation";
        case 36 -> "animationFrame";
        case 37 -> "animationFrameCycle";
        case 38 -> "animationDelay";
        case 40 -> "effect";
        case 41 -> "effectFrame";
        case 49 -> "routeWaypointCount";
        case 50 -> "routeWaypointsX";
        case 51 -> "routeWaypointsY";
        default -> null;
      };

      if (name == null) continue;
      getFoundClass().addFoundField(FoundField.from(identifiedClass, name, fin));
    }

  }

  private void findHitsplatCount(ClassNode classNode) {
    AbstractInsnNode iremAin =
        classNode.methods.stream()
            .filter(mn -> mn.desc.startsWith("(IIIIII)V"))
            .filter(mn -> mn.instructions.toArray()[0].opcode() == Opcodes.ICONST_1)
            .mapMulti(
                (BiConsumer<MethodNode, Consumer<AbstractInsnNode>>)
                    (methodNode, consumer) -> {
                      Arrays.stream(methodNode.instructions.toArray()).forEach(consumer);
                    })
            .filter(ain -> ain.opcode() == Opcodes.IREM)
            .findFirst()
            .orElse(null);

    List<AbstractInsnNode> next = Mask.next(iremAin, Mask.PUTFIELD);
    getFoundClass()
        .addFoundField(FoundField.from(classNode, "hitsplatCount", (FieldInsnNode) next.get(0)));
  }

  private void findPositions(List<ClassNode> classNodes, ClassNode identifiedClass) {
    List<FieldInsnNode> positionFins =
        classNodes.stream()
            .mapMulti(
                (BiConsumer<ClassNode, Consumer<MethodNode>>)
                    (classNode, consumer) -> classNode.methods.forEach(consumer))
            .filter(mn -> Modifier.isFinal(mn.access) && Modifier.isStatic(mn.access))
            .filter(
                mn ->
                    mn.desc.startsWith("(" + identifiedClass.getWrappedName())
                        && mn.desc.endsWith(")V"))
            .filter(mn -> mn.count(Opcodes.GETFIELD) == 2)
            .findAny()
            .stream()
            .mapMulti(
                (BiConsumer<MethodNode, Consumer<AbstractInsnNode>>)
                    (methodNode, consumer) ->
                        Arrays.stream(methodNode.instructions.toArray()).forEach(consumer))
            .filter(ain -> ain instanceof FieldInsnNode)
            .map(ain -> (FieldInsnNode) ain)
            .toList();

    for (int i = 0; i < positionFins.size(); i++) {
      String name = i == 0 ? "absoluteX" : "absoluteY";
      getFoundClass().addFoundField(FoundField.from(identifiedClass, name, positionFins.get(i)));
    }
  }
}

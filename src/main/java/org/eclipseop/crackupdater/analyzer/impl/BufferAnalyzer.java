package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AnalyzerInformation(expectedFields = {
    "caret", "payload"
})
public class BufferAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> cn.superName.equals(FoundStuff.findIdentifiedClass("Node").getClassNode().name))
        .filter(cn -> cn.fieldCount("I") == 1)
        .filter(cn -> cn.fieldCount("[B") == 1)
        .mapMulti(
            (BiConsumer<ClassNode, Consumer<MethodNode>>)
                (cn, consumer) -> cn.methods.forEach(consumer))
        .filter(mn -> mn.name.equals("<init>"))
        .filter(mn -> mn.desc.equals("([B)V"))
        .map(mn -> mn.owner)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    for (FieldNode fn : identifiedClass.fields) {
      if (Modifier.isStatic(fn.access)) continue;

      String name = switch (fn.desc) {
        case "I" -> "caret";
        case "[B" -> "payload";
        default -> null;
      };
      getFoundClass().addFoundField(new FoundField(name, fn));
    }
  }
}

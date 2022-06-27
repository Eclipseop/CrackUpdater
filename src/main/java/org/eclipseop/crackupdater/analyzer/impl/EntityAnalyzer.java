package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundClass;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.List;

@AnalyzerInformation(expectedFields = {"height"})
public class EntityAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    FoundClass doublyNode = FoundStuff.findIdentifiedClass("DoublyNode");

    return classNodes.stream()
        .filter(cn -> Modifier.isAbstract(cn.access))
        .filter(cn -> cn.superName.equals(doublyNode.getClassNode().name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    FieldNode heightFn =
        identifiedClass.fields.stream()
            .filter(fn -> !Modifier.isStatic(fn.access))
            .filter(fn -> fn.desc.equals("I"))
            .findFirst()
            .orElse(null);

    getFoundClass().addFoundField(new FoundField("height", heightFn));
  }
}

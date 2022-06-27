package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@AnalyzerInformation(expectedFields = {})
public class WorldAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(ClassNode::ownerless)
        .filter(cn -> cn.fieldCount("I") == 5)
        .filter(cn -> cn.fieldCount("Ljava/lang/String;") == 2)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {}
}

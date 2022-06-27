package org.eclipseop.crackupdater.analyzer.impl;

import org.eclipseop.crackupdater.analyzer.Analyzer;
import org.eclipseop.crackupdater.analyzer.AnalyzerInformation;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@AnalyzerInformation(expectedFields = {"canvas"})
public class GameEngineAnalyzer extends Analyzer {

  @Override
  public ClassNode findClass(List<ClassNode> classNodes) {
    return classNodes.stream()
        .filter(cn -> cn.superName.equals("java/applet/Applet"))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass) {
    identifiedClass.fields.stream()
        .filter(fn -> fn.desc.equals("Ljava/awt/Canvas;"))
        .findFirst()
        .ifPresent(fn -> getFoundClass().addFoundField(new FoundField("canvas", fn)));
  }
}

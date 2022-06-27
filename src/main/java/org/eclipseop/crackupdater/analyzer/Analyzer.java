package org.eclipseop.crackupdater.analyzer;

import org.eclipseop.crackupdater.util.found.FoundClass;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public abstract class Analyzer {

  private FoundClass foundClass;

  public abstract ClassNode findClass(List<ClassNode> classNodes);

  public abstract void findHooks(List<ClassNode> classNodes, ClassNode identifiedClass);

  public FoundClass getFoundClass() {
    return foundClass;
  }

  public void run(List<ClassNode> classNodes) {
    long startTime = System.currentTimeMillis();

    ClassNode identifiedClass = findClass(classNodes);
    if (identifiedClass == null) {
      System.out.println("Failed to identify " + getClass().getName());
      return;
    }

    AnalyzerInformation analyzerInformation =
        this.getClass().getAnnotation(AnalyzerInformation.class);

    FoundClass foundClass =
        new FoundClass(
            getClass().getSimpleName().replace("Analyzer", ""),
            identifiedClass,
            analyzerInformation.expectedFields());
    this.foundClass = foundClass;
    FoundStuff.add(foundClass);

    findHooks(classNodes, identifiedClass);
    foundClass.setRuntime(System.currentTimeMillis() - startTime);
  }
}

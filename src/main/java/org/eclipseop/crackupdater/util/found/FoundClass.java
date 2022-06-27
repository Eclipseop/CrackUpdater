package org.eclipseop.crackupdater.util.found;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoundClass {

  private final String name;
  private final ClassNode classNode;
  private final List<FoundField> foundFields;
  private final String[] expectedFields;

  private long runtime;

  public FoundClass(
      String name, ClassNode classNode, String[] expectedFields, FoundField... foundFields) {
    this.name = name;
    this.classNode = classNode;
    this.expectedFields = expectedFields;
    this.foundFields = new ArrayList<>();
    Collections.addAll(this.foundFields, foundFields);
  }

  public ClassNode getClassNode() {
    return classNode;
  }

  public List<FoundField> getFoundFields() {
    return foundFields;
  }

  public String getName() {
    return name;
  }

  public void addFoundField(FoundField foundField) {
    foundFields.add(foundField);
  }

  public long getRuntime() {
    return runtime;
  }

  public void setRuntime(long runtime) {
    this.runtime = runtime;
  }

  public String[] getExpectedFields() {
    return expectedFields;
  }

  public List<String> getMissingFields() {
    List<String> list = new ArrayList<>();
    List<String> foundFields = this.foundFields.stream().map(FoundField::getName).toList();

    for (String expectedField : expectedFields) {
      if (foundFields.contains(expectedField)) continue;
      list.add(expectedField);
    }

    return list;
  }
}

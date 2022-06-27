package org.eclipseop.crackupdater.util.found;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class FoundField {
  private final String name;
  private final FieldNode fieldNode;
  private int multiplier;

  public FoundField(String name, FieldNode fieldNode) {
    this.name = name;
    this.fieldNode = fieldNode;
  }

  public static FoundField from(List<ClassNode> classNodes, String name, FieldInsnNode fin) {
    FieldNode found = classNodes.stream()
            .filter(cn -> cn.name.equals(fin.owner))
            .mapMulti((BiConsumer<ClassNode, Consumer<FieldNode>>) (classNode, consumer) -> classNode.fields.forEach(consumer))
            .filter(fn -> fn.name.equals(fin.name))
            .findFirst().orElse(null);

    return new FoundField(name, found);
  }

  public static FoundField from(ClassNode classNode, String name, FieldInsnNode fin) {
    FieldNode found = classNode.fields.stream().filter(fn -> fin.name.equals(fn.name)).findFirst().orElse(null);
    return new FoundField(name, found);
  }

  public String getName() {
    return name;
  }

  public FieldNode getFieldNode() {
    return fieldNode;
  }

  public int getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(int multiplier) {
    this.multiplier = multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (FoundField) obj;
    return Objects.equals(this.name, that.name) &&
            Objects.equals(this.fieldNode, that.fieldNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, fieldNode);
  }

  @Override
  public String toString() {
    return "FoundField[" +
            "name=" + name + ", " +
            "fieldNode=" + fieldNode + ']';
  }

}

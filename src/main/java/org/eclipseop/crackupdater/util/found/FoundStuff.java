package org.eclipseop.crackupdater.util.found;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FoundStuff {

  private static final List<FoundClass> foundClasses = new ArrayList<>();

  public static List<FoundClass> getFoundClasses() {
    return foundClasses;
  }

  public static void add(FoundClass foundClass) {
    foundClasses.add(foundClass);
  }

  public static FoundClass findIdentifiedClass(String name) {
    return foundClasses.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
  }

  // class.field
  // TODO: 6/11/2022 rewrite
  public static FoundField findIdentifiedField(String fullName) {
    for (FoundClass foundClass : foundClasses) {
      for (FoundField foundField : foundClass.getFoundFields()) {
        if (foundField.getFieldNode().name.equals(fullName.split("\\.")[1])
            && foundField.getFieldNode().owner.name.equals(fullName.split("\\.")[0])) {
          return foundField;
        }
      }
    }

    return null;
  }

  public static void print() {
    foundClasses.sort(Comparator.comparing(FoundClass::getName));

    for (FoundClass foundClass : foundClasses) {
      System.out.println("& " + foundClass.getName() + " - " + foundClass.getClassNode().name);

      for (FoundField foundField : foundClass.getFoundFields()) {
        String fieldMessage =
            "* "
                + formatDesc(foundField.getFieldNode().desc)
                + " "
                + foundField.getName()
                + " - "
                + foundField.getFieldNode().owner
                + "."
                + foundField.getFieldNode().name;

        if (foundField.getMultiplier() != 0) {
          fieldMessage += " * " + foundField.getMultiplier();
        }

        System.out.println(fieldMessage);
      }

      for (String missingField : foundClass.getMissingFields()) {
        System.out.println("X MISSING - " + missingField);
      }
      System.out.println("+ Took " + ((double) foundClass.getRuntime() / 1000) + "s\n");
    }
  }

  // TODO: 6/11/2022 rewrite
  private static String formatDesc(String desc) {
    String temp = desc;

    if (temp.endsWith(";")) {
      temp = temp.replaceAll("L", "").replaceAll(";", "");
    }

    temp =
        temp.replaceAll("I", "int")
            .replaceAll("J", "long")
            .replaceAll("B", "byte")
            .replaceAll("Z", "boolean");

    while (temp.startsWith("[")) {
      temp = temp.substring(1) + "[]";
    }

    while (temp.contains("/")) {
      temp = temp.substring(temp.indexOf("/") + 1);
    }

    return temp;
  }
}

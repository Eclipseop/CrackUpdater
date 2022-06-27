package org.eclipseop.crackupdater.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JarUtil {

  public static List<ClassNode> unpack(JarFile jar) {
    return jar.stream()
        .filter(p -> p.getName().endsWith(".class") && !p.getName().startsWith("org/"))
        .map(
            jarEntry -> {
              try {
                ClassReader cr = new ClassReader(jar.getInputStream(jarEntry));
                ClassNode classNode = new ClassNode();

                cr.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                return classNode;
              } catch (IOException e) {
                e.printStackTrace();
              }
              return null;
            })
        .collect(Collectors.toList());
  }
}

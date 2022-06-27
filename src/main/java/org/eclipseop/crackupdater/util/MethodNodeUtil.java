package org.eclipseop.crackupdater.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

import java.util.LinkedList;
import java.util.List;

public class MethodNodeUtil {

//  public static List<AbstractInsnNode> follow(InsnList list) {
//    return follow(list.getFirst(), Collections.emptyList());
//  }

  public static List<AbstractInsnNode> follow(AbstractInsnNode startNode, List<AbstractInsnNode> visited) {
    List<AbstractInsnNode> temp = new LinkedList<>();

    AbstractInsnNode current = startNode;
    while (current != null) {
      System.out.println(visited.size());
      if (visited.contains(current)) {
        //System.out.println("Visited size: " + visited.size());
        break;
      }
      if (current.opcode() == Opcodes.ARETURN || current.opcode() == Opcodes.RETURN || current.opcode() == Opcodes.IRETURN) {
        temp.add(current);
        break;
      }

      if (current.opcode() == Opcodes.GOTO) {
        temp.add(current);
        current = ((JumpInsnNode) current).label.getNext();
        continue;
      }

      if (current instanceof JumpInsnNode jin) {
        temp.add(current);
        List<AbstractInsnNode> follow = follow(jin.label.getNext(), temp);
        temp.addAll(follow);
        current = current.getNext();
        continue;
      }

      //System.out.println(current);
      temp.add(current);
      current = current.getNext();
    }
    return temp;
  }

  // this kinda works :)
//  List<AbstractInsnNode> temp = new LinkedList<>();
//

    public static List<AbstractInsnNode> follow(InsnList list) {
      List<AbstractInsnNode> temp = new LinkedList<>();
      AbstractInsnNode current = list.getFirst();
      while (current != null) {
        if (current.opcode() == Opcodes.RETURN) {
          temp.add(current);
          break;
        }

        if (current.opcode() == -1) {
          current = current.getNext();
          continue;
        }

        temp.add(current);

        if (current.opcode() == Opcodes.GOTO || current.opcode() == Opcodes.IF_ICMPGE || current.opcode() == Opcodes.IF_ACMPNE) {
          current = ((JumpInsnNode) current).label.getNext();
          continue;
        }

        current = current.getNext();
      }
      return temp;

    }

}
